# API Gateway - Netflix Clone Microservices

Spring Cloud Gateway kullanarak tüm mikroservisleri tek bir endpoint üzerinden erişilebilir hale getiren API Gateway servisi.

## 📋 Özellikler

- ✅ Spring Cloud Gateway
- ✅ Eureka Service Discovery
- ✅ Otomatik Load Balancing
- ✅ Dynamic Routing
- ✅ JWT Authentication
- ✅ **🔒 Gateway Güvenlik Sistemi (Tüm istekler API Gateway'den geçer)** 🆕
- ✅ **Kapsamlı Request Logging & Monitoring** 🆕
- ✅ **Request Tracking (Distributed Tracing)** 🆕
- ✅ **Performance Metrics & Analytics** 🆕
- ✅ Health Check
- ✅ Docker desteği

## 🏗️ Teknoloji Stack

- **Java**: 17
- **Spring Boot**: 3.5.7
- **Spring Cloud**: 2025.0.0
- **Spring Cloud Gateway**: WebFlux
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## 📦 Bağımlılıklar

- Spring Cloud Gateway (WebFlux)
- Spring Cloud Netflix Eureka Client
- Spring Boot Actuator

## 🚀 Başlatma

### Yerel Ortamda Çalıştırma

1. **Önkoşullar:**
   - Java 17
   - Maven 3.9+
   - Eureka Naming Server çalışıyor olmalı

2. **Uygulamayı başlat:**
```bash
cd api-gateway
mvn spring-boot:run
```

### Docker ile Çalıştırma

1. **Docker image oluştur:**
```bash
docker-compose build
```

2. **Servisi başlat:**
```bash
docker-compose up -d
```

3. **Logları izle:**
```bash
docker-compose logs -f api-gateway
```

4. **Servisi durdur:**
```bash
docker-compose down
```

## 📍 Erişim

- **API Gateway**: http://localhost:8765
- **Health Check**: http://localhost:8765/actuator/health
- **Gateway Routes**: http://localhost:8765/actuator/gateway/routes

## 🔗 Route Yapısı

API Gateway, Eureka'dan otomatik olarak servisleri bulur ve route'lar oluşturur:

```
API Gateway (8765)
├── /user-service/** → user-service (3 instance, load balanced)
│   ├── /user-service/api/users/health
│   ├── /user-service/api/users/profiles
│   └── /user-service/api/users/profile/{id}
└── /authentication/** → authentication-service
    ├── /authentication/api/auth/register
    ├── /authentication/api/auth/login
    └── /authentication/api/auth/health
```

## 🧪 Test

### Health Check
```bash
curl http://localhost:8765/actuator/health
```

### Route'ları Listele
```bash
curl http://localhost:8765/actuator/gateway/routes
```

### User Service Test (Load Balanced)
```bash
# Her istek farklı user-service instance'ına gider
curl http://localhost:8765/user-service/api/users/health
curl http://localhost:8765/user-service/api/users/health
curl http://localhost:8765/user-service/api/users/health
```

### Authentication Service Test
```bash
# Kullanıcı kayıt
curl -X POST http://localhost:8765/authentication/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

## 🔧 Konfigürasyon

### application.properties

```properties
# Application Name
spring.application.name=api-gateway

# Server Port
server.port=8765

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

# Otomatik Discovery
spring.cloud.gateway.server.webflux.discovery.locator.enabled=true
spring.cloud.gateway.server.webflux.discovery.locator.lower-case-service-id=true
```

### Çevre Değişkenleri (Docker)

- `SERVER_PORT`: Server portu (default: 8765)
- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE`: Eureka server URL'i

## 📊 Load Balancing

API Gateway, Spring Cloud LoadBalancer kullanarak otomatik load balancing yapar:

1. **Eureka'dan Servis Listesi**: Tüm instance'ları alır
2. **Round Robin**: Varsayılan dağıtım algoritması
3. **Health Check**: Sadece sağlıklı instance'lara istek gönderir

### Load Balancing Test:
```bash
# 10 istek gönder
for i in {1..10}; do
  echo "Request $i:"
  curl -s http://localhost:8765/user-service/api/users/health
  echo ""
  sleep 1
done
```

## 🐛 Hata Ayıklama

### Logları görüntüle
```bash
docker logs -f api-gateway
```

### Route'ları kontrol et
```bash
curl http://localhost:8765/actuator/gateway/routes | jq
```

### Eureka Dashboard
```
http://localhost:8761
```

## 🔒 Güvenlik: Tüm İstekler API Gateway'den Geçer (YENİ!)

**ÖNEMLİ:** Mikroservisler artık **SADECE API Gateway üzerinden erişilebilir**. Direkt servis erişimi engellenmiştir.

### Güvenlik Katmanları:

#### 1️⃣ Docker Network Isolation
- Servis portları host'a expose edilmemiş
- Sadece Docker network içinden erişilebilir
- `localhost:8000` ❌ (Authentication Service)
- `localhost:9000` ❌ (User Service)
- `localhost:8765` ✅ (API Gateway - Tek Giriş Noktası)

#### 2️⃣ Gateway Verification Header
- Her istek `X-Gateway-Request: true` header'ı içerir
- Servisler bu header'ı kontrol eder
- Header yoksa 403 Forbidden döner

### Erişim Örnekleri:

```bash
# ❌ YANLIŞ: Direkt servis erişimi (ÇALIŞMAZ)
curl http://localhost:8000/api/auth/login
# Sonuç: Connection refused

# ✅ DOĞRU: API Gateway üzerinden
curl http://localhost:8765/authentication/api/auth/login
# Sonuç: Normal API yanıtı
```

### Detaylı Bilgi:
👉 **[../GATEWAY_SECURITY_SETUP.md](../GATEWAY_SECURITY_SETUP.md)** - Kapsamlı güvenlik dokümantasyonu

## 🔐 CORS Konfigürasyonu

API Gateway, tüm servislere CORS desteği sağlar. İhtiyaç halinde `application.properties` dosyasına eklenebilir:

```properties
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origins=*
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-headers=*
```

## 📝 Notlar

- API Gateway, Eureka'dan otomatik olarak servisleri bulur
- Yeni servis eklendiğinde otomatik route oluşur
- Load balancing otomatik çalışır
- Health check ile sağlıklı instance'lar belirlenir

## 📊 Logging & Monitoring (YENİ!)

API Gateway artık **TÜM istekleri** otomatik olarak logluyor ve izliyor! 

### Özellikler:
- 📨 **Global Logging**: Tüm HTTP request/response'ları detaylı loglar
- 🔍 **Request Tracking**: Her isteğe unique ID (GW-xxx) atar
- ⚡ **Performance Monitoring**: İstek sürelerini ölçer ve istatistik tutar
- 🎯 **Endpoint Analytics**: Endpoint bazlı performans metrikleri
- 🐌 **Slow Request Detection**: Yavaş istekleri otomatik tespit eder
- 🔐 **Security**: Sensitive bilgileri (Authorization, Cookie) maskeler

### Detaylı Bilgi:
Kapsamlı logging ve monitoring dokümantasyonu için:
👉 **[LOGGING_MONITORING_GUIDE.md](LOGGING_MONITORING_GUIDE.md)**

### Örnek Loglar:
```
📨 INCOMING REQUEST
🆔 Request ID: GW-1730000000000-a1b2c3d4
⏰ Timestamp: 2025-10-26 14:30:45.123
🔹 Method: GET
🔹 Path: /user-service/api/users/profile/123
🌐 Client IP: 192.168.1.100

📤 OUTGOING RESPONSE
📊 Status: 200 ✅ SUCCESS
⏱️  Duration: 234 ms
```

### Log Dosyaları:
```
logs/
├── api-gateway.log    # Genel loglar
├── requests.log       # Request/Response logları
├── metrics.log        # Performance metrikleri
└── errors.log         # Sadece hatalar
```

## 🚢 Production

Production ortamında:
- ✅ **Gateway Security** (Aktif - Tüm istekler API Gateway'den geçer)
- ✅ **JWT Authentication** (Aktif)
- ✅ **Request Logging** (Aktif)
- ✅ **Performance Monitoring** (Aktif)
- ⚙️ Caffeine cache ekleyin
- ⚙️ Rate limiting ekleyin
- ⚙️ Circuit breaker pattern ekleyin

## 📚 İlgili Dokümantasyonlar

- 🔒 [Gateway Security Setup](../GATEWAY_SECURITY_SETUP.md) - Güvenlik yapılandırması
- 📊 [Logging & Monitoring Guide](LOGGING_MONITORING_GUIDE.md) - Log ve monitoring
- 🔐 [API Gateway Auth Setup](API_GATEWAY_AUTH_SETUP.md) - Authentication yapılandırması
- 🐳 [Docker Compose Usage](../DOCKER_COMPOSE_USAGE.md) - Docker kullanımı
- ⚙️ [Spring Cloud Config Setup](../SPRING_CLOUD_CONFIG_SETUP.md) - Config server

