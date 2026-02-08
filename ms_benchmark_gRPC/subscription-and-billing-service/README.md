# Subscription and Billing Service

Netflix klonu için abonelik ve fatura yönetimi mikroservisi.

## Özellikler

- 🎯 **Abonelik Yönetimi**: Kullanıcılar abonelik satın alabilir, iptal edebilir
- 💳 **Ödeme Yönetimi**: Ödeme yöntemleri ekleme ve silme
- 📄 **Fatura Geçmişi**: Kullanıcıların tüm ödemeleri kayıt altında
- 📦 **Abonelik Planları**: FREE, BASIC, STANDARD, PREMIUM planları
- 🔄 **Otomatik Yenileme**: Aboneliklerin otomatik yenilenmesi
- 💰 **Aylık/Yıllık Ödeme**: Farklı faturalama dönemleri

## Teknoloji Stack

- **Java**: 17
- **Spring Boot**: 3.5.7
- **Spring Cloud**: 2025.0.0
- **PostgreSQL**: 16
- **Spring Data JPA**: ORM
- **Spring Cloud Config**: Merkezi konfigürasyon
- **Eureka Client**: Service discovery
- **OpenFeign**: Servisler arası iletişim
- **Lombok**: Boilerplate reduction

## API Endpoints

### Public Endpoints (Token gerektirmez)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/subscription/plans` | Tüm abonelik planlarını listele |
| GET | `/api/subscription/health` | Health check |

### Protected Endpoints (JWT token gerektirir)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/subscription/my-subscription` | Aktif aboneliği getir |
| GET | `/api/subscription/my-subscriptions` | Tüm abonelikleri getir |
| POST | `/api/subscription/subscribe` | Yeni abonelik satın al |
| PUT | `/api/subscription/cancel` | Aboneliği iptal et |
| GET | `/api/billing/history` | Fatura geçmişi |
| GET | `/api/billing/successful-payments` | Başarılı ödemeler |
| GET | `/api/payment/methods` | Ödeme yöntemlerini listele |
| POST | `/api/payment/methods` | Ödeme yöntemi ekle |
| DELETE | `/api/payment/methods/{id}` | Ödeme yöntemi sil |

## Database Schema

### subscription_plans
Abonelik planları (FREE, BASIC, STANDARD, PREMIUM)

### subscriptions
Kullanıcı abonelikleri

### billing_history
Ödeme ve fatura geçmişi

### payment_methods
Kullanıcıların kayıtlı ödeme yöntemleri

## Kurulum

### Docker ile Çalıştırma

```bash
# Docker network oluştur (ilk kez çalıştırıyorsanız)
docker network create microservices-network

# Servisi başlat
docker-compose up --build -d

# Logları izle
docker-compose logs -f
```

### Manuel Çalıştırma

```bash
# Maven build
mvn clean install

# Çalıştır
java -jar target/subscription-and-billing-service-0.0.1-SNAPSHOT.jar
```

## Çevre Değişkenleri

- `CONFIG_SERVER_URI`: Config server URL'i (default: http://localhost:8888)
- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE`: Eureka server URL'i
- `DB_HOST`: PostgreSQL host
- `DB_PORT`: PostgreSQL port (default: 5435)
- `DB_NAME`: Database adı
- `DB_USER`: Database kullanıcısı
- `DB_PASSWORD`: Database şifresi
- `SERVER_PORT`: Servis portu (default: 9100)

## Servis Entegrasyonu

Bu servis şu servislerle entegre çalışır:

- **Config Server**: Konfigürasyon yönetimi
- **Eureka Server**: Service discovery
- **API Gateway**: Dış dünyaya açılım
- **User Service**: Kullanıcı bilgileri

## Güvenlik

- ✅ Gateway verification filter (sadece API Gateway'den istek kabul eder)
- ✅ JWT token validation (API Gateway'de)
- ✅ Ödeme bilgileri token olarak saklanır (güvenli)
- ✅ Soft delete (veri kaybı önlenir)

## Geliştirme

```bash
# Hot reload için DevTools aktif
mvn spring-boot:run
```

## Lisans

Bu proje eğitim amaçlıdır.




