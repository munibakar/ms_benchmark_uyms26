# Profile Service - README

Netflix klonu için profil yönetimi mikroservisi. Kullanıcıların ana hesaplarına bağlı profilleri yönetir.

## 📋 Özellikler

- ✅ Profil oluşturma, güncelleme, silme
- ✅ Plan bazlı profil limit kontrolü (BASIC: 1, STANDARD: 2, PREMIUM: 5)
- ✅ PIN korumalı profiller
- ✅ Çocuk profilleri için olgunluk seviyesi kontrolü
- ✅ Varsayılan profil yönetimi
- ✅ Soft delete desteği
- ✅ User Service ve Subscription Service entegrasyonu

## 🏗️ Teknoloji Stack

- **Java**: 17
- **Spring Boot**: 3.5.7
- **Spring Cloud**: 2025.0.0
- **PostgreSQL**: 16
- **Spring Data JPA**: ORM
- **Spring Cloud Config**: Merkezi konfigürasyon
- **Eureka Client**: Service discovery
- **OpenFeign**: Servisler arası iletişim
- **Spring Security Crypto**: PIN şifreleme

## 🚀 Başlatma

### Yerel Ortamda Çalıştırma

1. **Önkoşullar:**
   - Java 17
   - Maven 3.9+
   - PostgreSQL 16
   - Eureka Naming Server çalışıyor olmalı
   - Config Server çalışıyor olmalı

2. **Veritabanını oluşturun:**
```sql
CREATE DATABASE profile_service_db;
```

3. **Uygulamayı başlat:**
```bash
cd profile-service
mvn spring-boot:run
```

### Docker ile Çalıştırma

```bash
cd profile-service
docker-compose up -d
```

## 📍 API Endpoints

Tüm endpoint'ler API Gateway üzerinden erişilebilir:

**Base URL:** `http://localhost:8765/profile-service/api/profiles`

### Public Endpoints (Token gerektirmez)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/profiles/health` | Health check |

### Protected Endpoints (JWT token gerektirir)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/profiles` | Profil oluştur |
| GET | `/api/profiles/account/{accountId}` | Aktif profilleri listele |
| GET | `/api/profiles/{profileId}` | Profil getir |
| GET | `/api/profiles/{profileId}/account/{accountId}` | Profil getir (account kontrolü ile) |
| PUT | `/api/profiles/{profileId}/account/{accountId}` | Profil güncelle |
| DELETE | `/api/profiles/{profileId}/account/{accountId}` | Profil sil |
| GET | `/api/profiles/account/{accountId}/default` | Varsayılan profil getir |
| GET | `/api/profiles/account/{accountId}/count` | Profil sayısı getir |

## 📚 Test

Postman ile test etmek için detaylı kılavuz: [POSTMAN_TEST_GUIDE.md](./POSTMAN_TEST_GUIDE.md)

Postman Collection: [Profile_Service.postman_collection.json](./Profile_Service.postman_collection.json)

## 🔧 Konfigürasyon

Merkezi konfigürasyon: `git-localconfig-repo/profile-service.properties`

- Database ayarları
- PIN konfigürasyonu (min/max uzunluk)
- Varsayılan değerler (language, maturity-level)
- Subscription status kontrolü

**Not:** `maxProfiles` artık subscription service'ten dinamik olarak alınıyor.

## 🔗 Servisler Arası İletişim

- **User Service**: Kullanıcı hesabının aktif olup olmadığını kontrol eder
- **Subscription Service**: Aktif abonelik ve plan bilgisini alır (maxProfiles buradan gelir)

## 📝 Veri Modeli

```java
Profile {
    id: Long
    accountId: Long  // User Service'teki userId
    profileName: String
    avatarUrl: String
    isChildProfile: Boolean
    maturityLevel: String (ALL, PG, PG13, R, NC17)
    language: String (tr, en, fr, de, etc.)
    isPinProtected: Boolean
    pinHash: String (BCrypt encrypted)
    isActive: Boolean
    isDefault: Boolean
    createdAt: LocalDateTime
    updatedAt: LocalDateTime
    deletedAt: LocalDateTime (soft delete)
}
```

## 🎯 İş Kuralları

1. **Profil Limit Kontrolü:**
   - BASIC plan: Maksimum 1 profil
   - STANDARD plan: Maksimum 2 profil
   - PREMIUM plan: Maksimum 5 profil
   - Limit subscription service'ten dinamik alınır

2. **Varsayılan Profil:**
   - İlk profil otomatik olarak varsayılan yapılır
   - Bir hesapta sadece bir varsayılan profil olabilir
   - Varsayılan profil değiştirildiğinde eski varsayılan kaldırılır

3. **Profil Silme:**
   - En az bir profil kalmalı (son profil silinemez)
   - Varsayılan profil silinirse, başka bir profil varsayılan yapılır
   - Soft delete kullanılır (deletedAt set edilir)

4. **PIN Koruması:**
   - PIN 4-8 karakter arasında olmalı
   - BCrypt ile şifrelenir
   - PIN hash'i profile'da tutulur

## 🐛 Sorun Giderme

Sorun yaşıyorsanız [POSTMAN_TEST_GUIDE.md](./POSTMAN_TEST_GUIDE.md) dosyasındaki "Sorun Giderme" bölümüne bakın.
