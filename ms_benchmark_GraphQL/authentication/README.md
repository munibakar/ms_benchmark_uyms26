# Authentication Microservice

Netflix Clone Authentication Microservice - JWT tabanlı kullanıcı kimlik doğrulama servisi.

## Özellikler

- ✅ Kullanıcı kaydı (Register)
- ✅ Kullanıcı girişi (Login)
- ✅ Google OAuth2 ile giriş
- ✅ JWT token tabanlı kimlik doğrulama
- ✅ BCrypt şifre hashleme
- ✅ PostgreSQL veritabanı
- ✅ Spring Security entegrasyonu
- ✅ Global exception handling
- ✅ Input validation
- ✅ Actuator health checks

## Teknolojiler

- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Token)
- Google OAuth2 Client
- Lombok
- Maven

## API Endpoints

### Public Endpoints (Kimlik doğrulama gerektirmez)

#### Kullanıcı Kaydı
```http
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Kullanıcı Girişi
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Google ile Giriş
```http
POST /auth/google
Content-Type: application/json

{
  "idToken": "google-id-token-here"
}
```

### Response Format

Başarılı kimlik doğrulama yanıtı:
```json
{
  "token": "jwt-token-here",
  "user": {
    "id": 1,
    "userId": 1,
    "email": "user@example.com",
    "isGoogleUser": false,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

**Not:** Profil bilgileri (isim, soyisim, phone, profilePicture) artık Authentication servisinde tutulmuyor. Bu bilgiler için User Service'i kullanın.

## Kurulum

### Gereksinimler
- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### Adımlar

1. Repository'yi klonlayın:
```bash
git clone <repository-url>
cd authentication
```

2. `.env.example` dosyasını `.env` olarak kopyalayın ve gerekli değerleri doldurun:
```bash
cp .env.example .env
```

3. PostgreSQL veritabanını oluşturun:
```sql
CREATE DATABASE authentication_db;
```

4. Uygulamayı çalıştırın:
```bash
mvn spring-boot:run
```

Uygulama `http://localhost:8081` adresinde çalışacaktır.

## Environment Variables

| Variable | Açıklama | Default |
|----------|----------|---------|
| `SERVER_PORT` | Sunucu portu | 8081 |
| `DB_HOST` | PostgreSQL host | localhost |
| `DB_PORT` | PostgreSQL port | 5432 |
| `DB_NAME` | Veritabanı adı | authentication_db |
| `DB_USER` | Veritabanı kullanıcı adı | user |
| `DB_PASSWORD` | Veritabanı şifresi | password |
| `JWT_SECRET` | JWT secret key (min 256-bit) | - |
| `GOOGLE_CLIENT_ID` | Google OAuth2 Client ID | - |

## Docker

### Docker Compose ile çalıştırma

```bash
docker-compose up -d
```

### Dockerfile ile build etme

```bash
docker build -t authentication-service .
docker run -p 8081:8081 authentication-service
```

## Geliştirme

### Build
```bash
mvn clean install
```

### Test
```bash
mvn test
```

### Package
```bash
mvn package
```

## Güvenlik

- Şifreler BCrypt ile hashlenmiştir
- JWT tokenları HMAC-SHA256 algoritması ile imzalanmıştır
- CORS yapılandırılmıştır
- Session yönetimi STATELESS'tir
- Input validation yapılmaktadır

## Veritabanı Şeması

### Users Tablosu

| Kolon            | Tip           | Açıklama                              |
|------------------|---------------|---------------------------------------|
| id               | BIGINT        | Primary key (auto-increment)          |
| user_id          | BIGINT        | Unique user identifier                |
| email            | VARCHAR(100)  | Unique email address                  |
| password         | VARCHAR(255)  | BCrypt hashed password                |
| is_google_user   | BOOLEAN       | Google OAuth kullanıcı mı?            |
| active_token     | TEXT          | Aktif JWT token                       |
| token_expires_at | TIMESTAMP     | Token son kullanma tarihi             |
| created_at       | TIMESTAMP     | Kayıt oluşturma tarihi                |
| updated_at       | TIMESTAMP     | Son güncelleme tarihi                 |
| deleted_at       | TIMESTAMP     | Soft delete tarihi (nullable)         |

**Not:** Bu servis sadece authentication (kimlik doğrulama) ile ilgilenir. Kullanıcı profil bilgileri (isim, soyisim, telefon, profil resmi) User Service'de tutulur.

## Migration

Eğer mevcut bir veritabanınız varsa ve profil bilgilerini User Service'e taşımak istiyorsanız, `MIGRATION_GUIDE.md` dosyasına bakın.

## Lisans

MIT

