# User Service - Netflix Clone Microservices

User Service, kullanıcı profil bilgilerini yöneten mikroservistir. Authentication Service ile entegre çalışır ve kullanıcı kayıt işlemi sırasında OpenFeign üzerinden çağrılır.

## 📋 Özellikler

- ✅ Kullanıcı profil yönetimi (CRUD işlemleri)
- ✅ Authentication Service ile OpenFeign entegrasyonu
- ✅ Spring Cloud Config Server entegrasyonu
- ✅ Eureka Service Discovery
- ✅ PostgreSQL veritabanı
- ✅ RESTful API
- ✅ Docker desteği
- ✅ Soft delete özelliği
- ✅ JPA Auditing (CreatedDate, LastModifiedDate)

## 🏗️ Teknoloji Stack

- **Java**: 17
- **Spring Boot**: 3.5.6
- **Spring Cloud**: 2023.0.0
- **Database**: PostgreSQL 16
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## 📦 Bağımlılıklar

- Spring Boot Web
- Spring Data JPA
- Spring Cloud Config Client
- Spring Cloud Netflix Eureka Client
- Spring Cloud OpenFeign
- PostgreSQL Driver
- Lombok
- Spring Validation
- Spring Boot Actuator

## 🚀 Başlatma

### Yerel Ortamda Çalıştırma

1. **Önkoşullar:**
   - Java 17
   - Maven 3.9+
   - PostgreSQL 16
   - Spring Cloud Config Server çalışıyor olmalı
   - Eureka Naming Server çalışıyor olmalı

2. **Uygulamayı başlat:**
```bash
cd user-service
mvn spring-boot:run
```

### Docker ile Çalıştırma

1. **Docker image oluştur:**
```bash
docker-compose build
```

2. **Servisleri başlat:**
```bash
docker-compose up -d
```

3. **Logları izle:**
```bash
docker-compose logs -f user-service
```

4. **Servisleri durdur:**
```bash
docker-compose down
```

## 📍 Endpoints

### User Profile Endpoints

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/users/profile` | Yeni kullanıcı profili oluştur (Auth Service tarafından çağrılır) |
| GET | `/api/users/profile/{userId}` | Kullanıcı profilini getir (User ID ile) |
| GET | `/api/users/profile/email/{email}` | Kullanıcı profilini getir (Email ile) |
| PUT | `/api/users/profile/{userId}` | Kullanıcı profilini güncelle |
| DELETE | `/api/users/profile/{userId}` | Kullanıcı profilini sil (soft delete) |
| GET | `/api/users/profiles` | Tüm kullanıcı profillerini listele |
| GET | `/api/users/health` | Health check |

### Örnek Request/Response

**POST /api/users/profile**
```json
// Request
{
  "userId": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe"
}

// Response (201 Created)
{
  "id": 1,
  "userId": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": null,
  "dateOfBirth": null,
  "profilePictureUrl": null,
  "bio": null,
  "country": null,
  "city": null,
  "address": null,
  "postalCode": null,
  "isActive": true,
  "isVerified": false,
  "createdAt": "2024-01-01T12:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

**PUT /api/users/profile/1**
```json
// Request
{
  "firstName": "Jane",
  "lastName": "Smith",
  "phoneNumber": "+90 555 123 4567",
  "country": "Turkey",
  "city": "Istanbul"
}

// Response (200 OK)
{
  "id": 1,
  "userId": 1,
  "email": "user@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "phoneNumber": "+90 555 123 4567",
  "country": "Turkey",
  "city": "Istanbul",
  ...
}
```

## 🔧 Konfigürasyon

### Config Server (user.properties)

User Service, konfigürasyon bilgilerini Spring Cloud Config Server'dan alır. Konfigürasyon dosyası: `git-localconfig-repo/user.properties`

**Önemli Konfigürasyonlar:**
- `server.port`: 8001
- `spring.datasource.url`: PostgreSQL bağlantı URL'i
- Database bağlantı havuzu ayarları
- JPA ve Hibernate ayarları
- Logging seviyeleri
- CORS ayarları

### Eureka Discovery

User Service, Eureka Server'a `user-service` ismiyle kayıt olur. Diğer servisler bu isimle User Service'e erişebilir.

### Database Schema

**user_profiles** tablosu:
- `id`: Primary Key (Auto Increment)
- `user_id`: Authentication Service'ten gelen kullanıcı ID'si (Unique)
- `email`: Kullanıcı email'i (Unique)
- `first_name`: İsim
- `last_name`: Soyisim
- `phone_number`: Telefon numarası
- `date_of_birth`: Doğum tarihi
- `profile_picture_url`: Profil resmi URL'i
- `bio`: Kullanıcı hakkında bilgi
- `country`: Ülke
- `city`: Şehir
- `address`: Adres
- `postal_code`: Posta kodu
- `is_active`: Aktif mi? (Boolean)
- `is_verified`: Doğrulanmış mı? (Boolean)
- `created_at`: Oluşturulma tarihi (Auto)
- `updated_at`: Güncellenme tarihi (Auto)
- `deleted_at`: Silinme tarihi (Soft Delete)

## 🔗 Authentication Service Entegrasyonu

Authentication Service, kullanıcı kayıt işlemi sırasında User Service'e OpenFeign ile bir istek gönderir:

1. Kullanıcı `/api/auth/register` endpoint'ine kayıt olur
2. Auth Service, kullanıcıyı kendi veritabanına kaydeder
3. Auth Service, OpenFeign kullanarak User Service'in `/api/users/profile` endpoint'ine istek gönderir
4. User Service, kullanıcı profili oluşturur
5. Her iki işlem de başarılı olursa, kullanıcıya "kayıt başarılı" yanıtı döner

## 🧪 Test

### Health Check
```bash
curl http://localhost:8001/api/users/health
```

### Kullanıcı Profili Oluşturma
```bash
curl -X POST http://localhost:8001/api/users/profile \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Kullanıcı Profili Getirme
```bash
curl http://localhost:8001/api/users/profile/1
```

## 📊 Actuator Endpoints

- `/actuator/health`: Sağlık durumu
- `/actuator/info`: Uygulama bilgileri
- `/actuator/metrics`: Metrikler

## 🐛 Hata Ayıklama

### Logları görüntüle
```bash
docker-compose logs -f user-service
```

### Database'e bağlan
```bash
docker exec -it user-service-db psql -U user -d user_service_db
```

### Container içine gir
```bash
docker exec -it user-service sh
```

## 🔐 Güvenlik

- Hassas bilgiler environment variables ile yönetilir
- Database şifreleri production ortamında güçlü olmalıdır
- CORS ayarları production ortamında kısıtlanmalıdır

## 📝 Notlar

- User Service, port 8001'de çalışır
- PostgreSQL, port 5433'te expose edilir (host:5433 -> container:5432)
- Soft delete kullanılır, veriler fiziksel olarak silinmez
- JPA Auditing aktiftir, `createdAt` ve `updatedAt` otomatik güncellenir

## 🤝 Bağımlı Servisler

1. **Spring Cloud Config Server** (port 8888)
2. **Eureka Naming Server** (port 8761)
3. **PostgreSQL Database** (port 5433)

## 📚 Mimari

```
┌─────────────────────┐
│  Authentication     │
│     Service         │
│   (port: 8000)      │
└──────────┬──────────┘
           │
           │ OpenFeign
           │ /api/users/profile
           ▼
┌─────────────────────┐       ┌──────────────────┐
│   User Service      │──────▶│   PostgreSQL     │
│   (port: 8001)      │       │   (port: 5433)   │
└──────────┬──────────┘       └──────────────────┘
           │
           ├──────▶ Config Server (port: 8888)
           │
           └──────▶ Eureka Server (port: 8761)
```

## 👨‍💻 Geliştirici

Bu mikroservis Netflix Clone projesi kapsamında geliştirilmiştir.

