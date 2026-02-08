import { ApolloServer } from '@apollo/server';
import { expressMiddleware } from '@apollo/server/express4';
import { ApolloGateway, IntrospectAndCompose, RemoteGraphQLDataSource } from '@apollo/gateway';
import express from 'express';
import cors from 'cors';
import http from 'http';

/**
 * Apollo Federation Gateway
 * 
 * Bu gateway, tüm mikroservisleri (subgraph) tek bir GraphQL endpoint'i altında birleştirir.
 * Client tek bir sorgu ile birden fazla servisten veri alabilir.
 * 
 * Mimari:
 * Client -> Apollo Gateway -> Subgraphs (User, Profile, Subscription, Content, Video, Auth)
 */

// Subgraph URL'leri (environment variables veya default değerler)
// NOT: Authentication servisi federasyonun parçası değil - güvenlik nedeniyle ayrı tutulur
const SUBGRAPH_URLS = {
  user: process.env.USER_SERVICE_URL || 'http://user-service:9000/graphql',
  profile: process.env.PROFILE_SERVICE_URL || 'http://profile-service:9001/graphql',
  subscription: process.env.SUBSCRIPTION_SERVICE_URL || 'http://subscription-and-billing-service:9100/graphql',
  content: process.env.CONTENT_SERVICE_URL || 'http://content-management-service:9200/graphql',
  video: process.env.VIDEO_SERVICE_URL || 'http://video-streaming-service:9300/graphql',
  authentication: process.env.AUTH_SERVICE_URL || 'http://authentication-service:8000/graphql',
};

// Retry configuration
const MAX_RETRIES = 15;
const INITIAL_DELAY_MS = parseInt(process.env.STARTUP_DELAY_MS) || 30000;

async function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * Her deneme için yeni bir ApolloGateway instance'ı oluştur
 * Bu kritik çünkü başarısız olan gateway nesnesi tekrar kullanılamaz
 */
function createGateway() {
  return new ApolloGateway({
    supergraphSdl: new IntrospectAndCompose({
      subgraphs: [
        { name: 'user', url: SUBGRAPH_URLS.user },
        { name: 'profile', url: SUBGRAPH_URLS.profile },
        { name: 'subscription', url: SUBGRAPH_URLS.subscription },
        { name: 'content', url: SUBGRAPH_URLS.content },
        { name: 'video', url: SUBGRAPH_URLS.video },
        { name: 'authentication', url: SUBGRAPH_URLS.authentication },
      ],
      subgraphHealthCheck: false,
    }),
    buildService({ name, url }) {
      return new RemoteGraphQLDataSource({
        url,
        willSendRequest({ request, context }) {
          request.http.headers.set('X-Gateway-Request', 'true');
          if (context.token) {
            request.http.headers.set('Authorization', context.token);
          }
        },
      });
    },
    pollIntervalInMs: process.env.NODE_ENV === 'production' ? undefined : 10000,
  });
}

async function startServer() {
  const gateway = createGateway();

  const app = express();
  const httpServer = http.createServer(app);

  // Health endpoint for Docker healthcheck (GET request)
  app.get('/health', (req, res) => {
    res.status(200).json({ status: 'healthy', timestamp: new Date().toISOString() });
  });

  // Root endpoint for basic check
  app.get('/', (req, res) => {
    res.status(200).json({
      service: 'Apollo Federation Gateway',
      status: 'running',
      subgraphs: Object.keys(SUBGRAPH_URLS)
    });
  });

  const server = new ApolloServer({
    gateway,
  });

  await server.start();

  app.use(
    '/graphql',
    cors(),
    express.json(),
    expressMiddleware(server, {
      context: async ({ req }) => {
        const token = req.headers.authorization || '';
        return { token };
      },
    })
  );

  const port = parseInt(process.env.PORT) || 4000;

  await new Promise((resolve) => httpServer.listen({ port }, resolve));

  console.log('');
  console.log('╔═══════════════════════════════════════════════════════════════╗');
  console.log('║         Apollo Federation Gateway - Started                    ║');
  console.log('╚═══════════════════════════════════════════════════════════════╝');
  console.log('');
  console.log(`🚀 Gateway ready at http://localhost:${port}/`);
  console.log(`🏥 Health endpoint: http://localhost:${port}/health`);
  console.log('');
  console.log('📡 Connected Subgraphs:');
  Object.entries(SUBGRAPH_URLS).forEach(([name, url]) => {
    console.log(`   - ${name.padEnd(12)} : ${url}`);
  });
  console.log('');
  console.log('📊 Federation Query Örneği:');
  console.log('   query GetUserDashboard($userId: ID!) {');
  console.log('       user(id: $userId) {');
  console.log('           email');
  console.log('           firstName');
  console.log('           profiles { profileName }');
  console.log('           subscription { status }');
  console.log('           billingHistory { amount }');
  console.log('       }');
  console.log('   }');
  console.log('');
  console.log('🔗 GraphQL Playground: http://localhost:' + port + '/graphql');
  console.log('');
}

// Gateway'i retry mekanizması ile başlat
async function startWithRetry() {
  console.log(`⏳ Waiting ${INITIAL_DELAY_MS / 1000}s for subgraphs to be ready...`);
  await sleep(INITIAL_DELAY_MS);

  for (let attempt = 1; attempt <= MAX_RETRIES; attempt++) {
    try {
      console.log(`🔄 Attempt ${attempt}/${MAX_RETRIES} to start gateway...`);
      await startServer();
      return;
    } catch (err) {
      console.error(`❌ Attempt ${attempt} failed:`, err.message);

      if (attempt < MAX_RETRIES) {
        const delayMs = Math.min(10000 * attempt, 60000);
        console.log(`⏳ Retrying in ${delayMs / 1000}s...`);
        await sleep(delayMs);
      } else {
        console.error('❌ All retry attempts exhausted. Exiting.');
        process.exit(1);
      }
    }
  }
}

startWithRetry();
