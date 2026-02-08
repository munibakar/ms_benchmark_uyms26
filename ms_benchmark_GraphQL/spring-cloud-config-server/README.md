# Spring Cloud Config Server

Bu servis, mikroservislerin konfigürasyon dosyalarını merkezi olarak yönetmek için Spring Cloud Config Server kullanır.

## Özellikler

- ✅ Merkezi konfigürasyon yönetimi
- ✅ Local Git repository desteği
- ✅ Environment-specific konfigürasyonlar
- ✅ Hot reload (config değişikliklerinde otomatik yenileme)
- ✅ Docker desteği

## Nasıl Çalışır?

1. Config Server, `git-localconfig-repo` klasöründeki `.properties` veya `.yml` dosyalarını okur
2. Mikroservisler başlatılırken Config Server'a bağlanır
3. Config Server, mikroservise özel konfigürasyonları döner
4. Mikroservis bu konfigürasyonlarla başlatılır

## Konfigürasyon Dosyaları

Konfigürasyon dosyaları `git-localconfig-repo` klasöründe tutulur. Dosya isimlendirme formatı:

```
{application-name}.properties
{application-name}-{profile}.properties
```

Örnek:
- `authentication.properties` - authentication mikroservisi için default config
- `authentication-dev.properties` - authentication mikroservisi için dev profile config
- `authentication-prod.properties` - authentication mikroservisi için production profile config

## Çalıştırma

### Local olarak

```bash
cd spring-cloud-config-server
mvn spring-boot:run
```

### Docker ile

```bash
cd spring-cloud-config-server
docker-compose up --build
```

### Authentication servisi ile birlikte

```bash
cd authentication
docker-compose up --build
```

## Endpoints

- **Config Server**: http://localhost:8888
- **Health Check**: http://localhost:8888/actuator/health
- **Config Endpoint**: http://localhost:8888/{application}/{profile}

Örnek:
```bash
# authentication servisi için config'i görüntüle
curl http://localhost:8888/authentication/default

# authentication servisi için prod profile config'i görüntüle
curl http://localhost:8888/authentication/prod
```

## Environment Variables

| Variable | Default | Açıklama |
|----------|---------|----------|
| `SERVER_PORT` | `8888` | Config Server portu |
| `GIT_REPO_PATH` | `../git-localconfig-repo` | Git repository path'i |

## Mikroservislerde Kullanım

Mikroservisler Config Server'ı kullanmak için:

1. **pom.xml**'e dependency ekle:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

2. **bootstrap.yml** oluştur:
```yaml
spring:
  application:
    name: authentication
  cloud:
    config:
      enabled: true
      uri: http://localhost:8888
      fail-fast: true
```

3. **git-localconfig-repo**'da `{application-name}.properties` dosyası oluştur

## Troubleshooting

### Config Server'a bağlanamıyor

1. Config Server'ın çalıştığından emin olun:
```bash
curl http://localhost:8888/actuator/health
```

2. Docker network'ü kontrol edin:
```bash
docker network ls
docker network inspect authentication-network
```

### Config dosyası bulunamıyor

1. Dosya isminin doğru olduğundan emin olun (application name ile eşleşmeli)
2. Git repo path'ini kontrol edin
3. Config Server loglarını inceleyin:
```bash
docker logs config-server
```

## Güvenlik Notları

⚠️ Production ortamında:
- Config Server'ı basic auth veya OAuth2 ile koruyun
- Hassas bilgileri encrypt edin
- Git repository'yi özel tutun
- Network güvenliğini sağlayın

## İlgili Dokümantasyon

- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)
- [Spring Cloud Config Server Reference](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)

