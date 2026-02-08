# Profile Service - Postman Test Kılavuzu

Bu dokümantasyon, Profile Service endpoint'lerini Postman ile test etmek için gerekli tüm bilgileri içerir.

## 📋 İçindekiler

1. [Önkoşullar](#önkoşullar)
2. [Token Alma](#token-alma)
3. [Profile Service Endpoint'leri](#profile-service-endpointleri)
4. [Test Senaryoları](#test-senaryoları)
5. [Hata Durumları](#hata-durumları)

---

## 🔐 Önkoşullar

### 1. Servislerin Çalışıyor Olması

Aşağıdaki servislerin çalışıyor olması gerekiyor:
- ✅ API Gateway (port: 8765)
- ✅ Profile Service (port: 9001)
- ✅ User Service (port: 9000)
- ✅ Subscription Service (port: 9100)
- ✅ Authentication Service (port: 8000)
- ✅ Eureka Naming Server (port: 8761)
- ✅ Config Server (port: 8888)

### 2. Base URL

Tüm istekler API Gateway üzerinden yapılmalıdır:

```
Base URL: http://localhost:8765/profile-service/api/profiles
```

**ÖNEMLİ:** Profile Service'e direkt erişim yok! Tüm istekler API Gateway üzerinden geçmelidir.

---

## 🔑 Token Alma

Profile Service endpoint'leri (health check hariç) JWT token gerektirir.

### Adım 1: Kullanıcı Kaydı (Register)

**Endpoint:** `POST http://localhost:8765/authentication/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Ahmet",
  "lastName": "Yılmaz"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "userId": 1,
    "email": "test@example.com",
    "isGoogleUser": false,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

**Postman'de:**
1. Yeni bir request oluşturun
2. Method: `POST`
3. URL: `http://localhost:8765/authentication/api/auth/register`
4. Headers tab'ında: `Content-Type: application/json`
5. Body tab'ında: `raw` → `JSON` seçin ve yukarıdaki JSON'ı yapıştırın
6. Send'e tıklayın
7. Response'tan `token` değerini kopyalayın

### Adım 2: Giriş Yapma (Login) - Alternatif

**Endpoint:** `POST http://localhost:8765/authentication/api/auth/login`

**Request Body:**
```json
{
  "email": "test@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "userId": 1,
    "email": "test@example.com",
    ...
  }
}
```

### Adım 3: Token'ı Postman Collection Variable'a Kaydetme

1. Postman'de **Variables** tab'ına gidin
2. Collection variable oluşturun:
   - Variable Name: `jwt_token`
   - Initial Value: Token değerini yapıştırın
3. Veya **Tests** tab'ında otomatik kaydetme için:

```javascript
// Login response'unda token'ı otomatik kaydet
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.collectionVariables.set("jwt_token", jsonData.token);
    pm.collectionVariables.set("user_id", jsonData.user.userId);
    console.log("Token saved:", jsonData.token);
}
```

---

## 📍 Profile Service Endpoint'leri

### 1. Health Check (Public - Token Gerekmez)

**Endpoint:** `GET http://localhost:8765/profile-service/api/profiles/health`

**Headers:** Yok

**Response (200 OK):**
```
Profile Service is running
```

---

### 2. Profil Oluşturma

**Endpoint:** `POST http://localhost:8765/profile-service/api/profiles`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{jwt_token}}
```

**Request Body:**
```json
{
  "accountId": 1,
  "profileName": "Ahmet Profili",
  "avatarUrl": "https://example.com/avatar.jpg",
  "isChildProfile": false,
  "maturityLevel": "ALL",
  "language": "tr",
  "isPinProtected": false,
  "isDefault": true
}
```

**Örnek Request (Çocuk Profili):**
```json
{
  "accountId": 1,
  "profileName": "Çocuk Profili",
  "isChildProfile": true,
  "maturityLevel": "PG",
  "language": "tr",
  "isPinProtected": true,
  "pin": "1234",
  "isDefault": false
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "accountId": 1,
  "profileName": "Ahmet Profili",
  "avatarUrl": "https://example.com/avatar.jpg",
  "isChildProfile": false,
  "maturityLevel": "ALL",
  "language": "tr",
  "isPinProtected": false,
  "isActive": true,
  "isDefault": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

**Önemli Notlar:**
- `accountId` User Service'teki `userId` ile aynı olmalıdır
- Kullanıcının aktif bir aboneliği olmalıdır
- Plan limitine göre profil sayısı kontrol edilir (BASIC: 1, STANDARD: 2, PREMIUM: 5)
- İlk profil otomatik olarak varsayılan profil yapılır

---

### 3. Hesaba Göre Aktif Profilleri Listeleme

**Endpoint:** `GET http://localhost:8765/profile-service/api/profiles/account/{accountId}`

**Headers:**
```
Authorization: Bearer {{jwt_token}}
```

**Örnek URL:**
```
GET http://localhost:8765/profile-service/api/profiles/account/1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "accountId": 1,
    "profileName": "Ahmet Profili",
    "avatarUrl": "https://example.com/avatar.jpg",
    "isChildProfile": false,
    "maturityLevel": "ALL",
    "language": "tr",
    "isPinProtected": false,
    "isActive": true,
    "isDefault": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  },
  {
    "id": 2,
    "accountId": 1,
    "profileName": "Ayşe Profili",
    "isChildProfile": false,
    "maturityLevel": "ALL",
    "language": "tr",
    "isPinProtected": false,
    "isActive": true,
    "isDefault": false,
    "createdAt": "2024-01-01T10:30:00",
    "updatedAt": "2024-01-01T10:30:00"
  }
]
```

---

### 4. Profil ID'ye Göre Profil Getirme

**Endpoint:** `GET http://localhost:8765/profile-service/api/profiles/{profileId}`

**Headers:**
```
Authorization: Bearer {{jwt_token}}
```

**Örnek URL:**
```
GET http://localhost:8765/profile-service/api/profiles/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "accountId": 1,
  "profileName": "Ahmet Profili",
  "avatarUrl": "https://example.com/avatar.jpg",
  "isChildProfile": false,
  "maturityLevel": "ALL",
  "language": "tr",
  "isPinProtected": false,
  "isActive": true,
  "isDefault": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

---

### 5. Profil Güncelleme

**Endpoint:** `PUT http://localhost:8765/profile-service/api/profiles/{profileId}/account/{accountId}`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{jwt_token}}
```

**Örnek URL:**
```
PUT http://localhost:8765/profile-service/api/profiles/1/account/1
```

**Request Body (Kısmi Güncelleme):**
```json
{
  "profileName": "Yeni Profil Adı",
  "avatarUrl": "https://example.com/new-avatar.jpg",
  "language": "en",
  "isDefault": true
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "accountId": 1,
  "profileName": "Yeni Profil Adı",
  "avatarUrl": "https://example.com/new-avatar.jpg",
  "isChildProfile": false,
  "maturityLevel": "ALL",
  "language": "en",
  "isPinProtected": false,
  "isActive": true,
  "isDefault": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T11:00:00"
}
```

---

### 6. Profil Silme (Soft Delete)

**Endpoint:** `DELETE http://localhost:8765/profile-service/api/profiles/{profileId}/account/{accountId}`

**Headers:**
```
Authorization: Bearer {{jwt_token}}
```

**Örnek URL:**
```
DELETE http://localhost:8765/profile-service/api/profiles/2/account/1
```

**Response (204 No Content):** Boş body

**Önemli:** 
- Son profil silinemez (en az 1 profil kalmalı)
- Varsayılan profil silinirse, başka bir profil varsayılan yapılır

---

### 7. Varsayılan Profili Getirme

**Endpoint:** `GET http://localhost:8765/profile-service/api/profiles/account/{accountId}/default`

**Headers:**
```
Authorization: Bearer {{jwt_token}}
```

**Örnek URL:**
```
GET http://localhost:8765/profile-service/api/profiles/account/1/default
```

**Response (200 OK):**
```json
{
  "id": 1,
  "accountId": 1,
  "profileName": "Ahmet Profili",
  "isChildProfile": false,
  "maturityLevel": "ALL",
  "language": "tr",
  "isPinProtected": false,
  "isActive": true,
  "isDefault": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

---

### 8. Profil Sayısını Getirme

**Endpoint:** `GET http://localhost:8765/profile-service/api/profiles/account/{accountId}/count`

**Headers:**
```
Authorization: Bearer {{jwt_token}}
```

**Örnek URL:**
```
GET http://localhost:8765/profile-service/api/profiles/account/1/count
```

**Response (200 OK):**
```json
3
```

---

## 🧪 Test Senaryoları

### Senaryo 1: Yeni Kullanıcı - İlk Profil Oluşturma

1. **Kullanıcı kaydı yap:**
   ```
   POST /authentication/api/auth/register
   ```

2. **Abonelik satın al:** (Subscription Service'ten)
   ```
   POST /subscription/api/subscription/subscribe
   Body: { "planName": "BASIC", "billingCycle": "MONTHLY", "paymentMethodId": 1 }
   ```

3. **İlk profili oluştur:**
   ```
   POST /profile-service/api/profiles
   Body: {
     "accountId": 1,
     "profileName": "Ana Profil",
     "isDefault": true
   }
   ```
   - ✅ Varsayılan profil otomatik oluşturulmalı
   - ✅ İlk profil olduğu için `isDefault: true` olmalı

---

### Senaryo 2: BASIC Plan - Profil Limit Kontrolü

1. **BASIC planlı kullanıcı için 2. profil oluşturmayı dene:**
   ```
   POST /profile-service/api/profiles
   Body: {
     "accountId": 1,
     "profileName": "İkinci Profil"
   }
   ```
   - ❌ **Hata Beklenir:** "Maximum profile limit reached for plan BASIC. Maximum allowed: 1"

---

### Senaryo 3: PREMIUM Plan - 5 Profil Oluşturma

1. **PREMIUM planlı kullanıcı için 5 profil oluştur:**
   ```
   POST /profile-service/api/profiles
   Body: { "accountId": 2, "profileName": "Profil 1" }
   ...
   POST /profile-service/api/profiles
   Body: { "accountId": 2, "profileName": "Profil 5" }
   ```
   - ✅ 5 profil başarıyla oluşturulmalı

2. **6. profili oluşturmayı dene:**
   - ❌ **Hata Beklenir:** "Maximum profile limit reached for plan PREMIUM. Maximum allowed: 5"

---

### Senaryo 4: PIN Korumalı Profil Oluşturma

```
POST /profile-service/api/profiles
Body: {
  "accountId": 1,
  "profileName": "PIN Korumalı Profil",
  "isPinProtected": true,
  "pin": "1234"
}
```
- ✅ PIN şifrelenmiş şekilde kaydedilmeli (`pinHash` field'ı dolu olmalı)
- ✅ PIN 4-8 karakter arasında olmalı

**Geçersiz PIN Testleri:**
- `"pin": "12"` → ❌ "PIN must be between 4 and 8 characters"
- `"pin": "123456789"` → ❌ "PIN must be between 4 and 8 characters"

---

### Senaryo 5: Varsayılan Profil Değiştirme

1. **Mevcut varsayılan profil:**
   ```
   GET /profile-service/api/profiles/account/1/default
   ```

2. **Başka bir profili varsayılan yap:**
   ```
   PUT /profile-service/api/profiles/2/account/1
   Body: { "isDefault": true }
   ```

3. **Kontrol et:**
   ```
   GET /profile-service/api/profiles/account/1/default
   ```
   - ✅ Yeni varsayılan profil `id: 2` olmalı
   - ✅ Eski varsayılan profil `isDefault: false` olmalı

---

### Senaryo 6: Son Profili Silme Hatası

1. **Tek profil varsa silmeyi dene:**
   ```
   DELETE /profile-service/api/profiles/1/account/1
   ```
   - ❌ **Hata Beklenir:** "Cannot delete the last profile. At least one profile must remain."

---

## ❌ Hata Durumları

### 401 Unauthorized

**Sebep:** Token eksik veya geçersiz

**Çözüm:**
- Token'ı kontrol edin: `Authorization: Bearer {{jwt_token}}`
- Token'ın süresi dolmuş olabilir, yeniden login yapın

**Response:**
```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing token"
}
```

---

### 403 Forbidden

**Sebep:** Direkt servis erişimi (API Gateway bypass edilmeye çalışılmış)

**Çözüm:** Tüm istekleri API Gateway üzerinden yapın (`http://localhost:8765/profile-service/...`)

**Response:**
```json
{
  "error": "Forbidden",
  "message": "Direct service access is not allowed. Please use API Gateway."
}
```

---

### 400 Bad Request

**Örnek 1: Profil limiti aşıldı**
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Maximum profile limit reached for plan BASIC. Maximum allowed: 1",
  "path": "/profile-service/api/profiles"
}
```

**Örnek 2: Aktif abonelik yok**
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "No active subscription found",
  "path": "/profile-service/api/profiles"
}
```

**Örnek 3: PIN uzunluk hatası**
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "PIN must be between 4 and 8 characters",
  "path": "/profile-service/api/profiles"
}
```

**Örnek 4: Validation hatası**
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "profileName": "Profile name is required",
    "accountId": "Account ID is required"
  },
  "path": "/profile-service/api/profiles"
}
```

---

### 404 Not Found

**Örnek 1: Profil bulunamadı**
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Profile not found for profile ID: 999",
  "path": "/profile-service/api/profiles/999"
}
```

**Örnek 2: Varsayılan profil bulunamadı**
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Default profile not found for account ID: 1",
  "path": "/profile-service/api/profiles/account/1/default"
}
```

---

## 📝 Postman Collection Örneği

Postman'de Collection oluştururken aşağıdaki yapıyı kullanabilirsiniz:

### Collection Structure

```
Profile Service Tests
├── 1. Authentication
│   ├── Register User
│   └── Login User
├── 2. Profile Management
│   ├── Create Profile
│   ├── Get All Profiles by Account
│   ├── Get Profile by ID
│   ├── Get Profile by ID and Account
│   ├── Update Profile
│   ├── Delete Profile
│   ├── Get Default Profile
│   └── Get Profile Count
└── 3. Health Check
    └── Health Check (Public)
```

### Collection Variables

Collection seviyesinde şu variable'ları oluşturun:

| Variable Name | Initial Value | Current Value |
|--------------|---------------|---------------|
| `base_url` | `http://localhost:8765` | `http://localhost:8765` |
| `jwt_token` | (boş - login sonrası doldurulacak) | |
| `user_id` | (boş - login sonrası doldurulacak) | |
| `account_id` | `1` | `1` |
| `profile_id` | (boş - create sonrası doldurulacak) | |

### Request URL Format

```
{{base_url}}/profile-service/api/profiles
{{base_url}}/profile-service/api/profiles/account/{{account_id}}
{{base_url}}/profile-service/api/profiles/{{profile_id}}
```

### Authorization Header

Tüm protected endpoint'lerde:

**Type:** `Bearer Token`

**Token:** `{{jwt_token}}`

Veya **Headers** tab'ında manuel:

```
Authorization: Bearer {{jwt_token}}
```

---

## 🔄 Test Akışı Örneği

### Tam Test Senaryosu

1. **Kullanıcı Kaydı**
   ```
   POST {{base_url}}/authentication/api/auth/register
   → Token al
   ```

2. **Abonelik Satın Alma** (Subscription Service)
   ```
   POST {{base_url}}/subscription/api/subscription/subscribe
   Headers: Authorization: Bearer {{jwt_token}}
   Body: { "planName": "STANDARD", "billingCycle": "MONTHLY", "paymentMethodId": 1 }
   ```

3. **İlk Profil Oluşturma**
   ```
   POST {{base_url}}/profile-service/api/profiles
   Headers: Authorization: Bearer {{jwt_token}}
   Body: { "accountId": {{user_id}}, "profileName": "Ana Profil", "isDefault": true }
   → profile_id kaydet
   ```

4. **Profilleri Listeleme**
   ```
   GET {{base_url}}/profile-service/api/profiles/account/{{user_id}}
   Headers: Authorization: Bearer {{jwt_token}}
   → 1 profil görmeli
   ```

5. **İkinci Profil Oluşturma** (STANDARD plan 2 profil destekler)
   ```
   POST {{base_url}}/profile-service/api/profiles
   Headers: Authorization: Bearer {{jwt_token}}
   Body: { "accountId": {{user_id}}, "profileName": "İkinci Profil" }
   ```

6. **Varsayılan Profili Değiştirme**
   ```
   PUT {{base_url}}/profile-service/api/profiles/{{profile_id}}/account/{{user_id}}
   Headers: Authorization: Bearer {{jwt_token}}
   Body: { "isDefault": true }
   ```

7. **Profil Güncelleme**
   ```
   PUT {{base_url}}/profile-service/api/profiles/{{profile_id}}/account/{{user_id}}
   Headers: Authorization: Bearer {{jwt_token}}
   Body: { "profileName": "Güncellenmiş Profil Adı", "language": "en" }
   ```

8. **Profil Sayısını Kontrol Etme**
   ```
   GET {{base_url}}/profile-service/api/profiles/account/{{user_id}}/count
   Headers: Authorization: Bearer {{jwt_token}}
   → 2 dönmeli
   ```

---

## 💡 İpuçları

1. **Token Otomatik Kaydetme:**
   Login request'inin **Tests** tab'ına şunu ekleyin:
   ```javascript
   if (pm.response.code === 200) {
       var jsonData = pm.response.json();
       pm.collectionVariables.set("jwt_token", jsonData.token);
       pm.collectionVariables.set("user_id", jsonData.user.userId);
   }
   ```

2. **Profile ID Otomatik Kaydetme:**
   Create Profile request'inin **Tests** tab'ına:
   ```javascript
   if (pm.response.code === 201) {
       var jsonData = pm.response.json();
       pm.collectionVariables.set("profile_id", jsonData.id);
   }
   ```

3. **Environment Variables:**
   Farklı ortamlar için (local, dev, prod) Environment oluşturun:
   - `local_base_url`: `http://localhost:8765`
   - `dev_base_url`: `http://dev-api.example.com`

4. **Pre-request Scripts:**
   Token kontrolü için:
   ```javascript
   if (!pm.collectionVariables.get("jwt_token")) {
       console.log("Warning: JWT token not set. Please login first.");
   }
   ```

---

## 🐛 Sorun Giderme

### Problem: 401 Unauthorized

**Kontrol Listesi:**
- ✅ Token doğru mu? (`Bearer ` prefix'i var mı?)
- ✅ Token süresi dolmuş mu? (Yeniden login yapın)
- ✅ Header adı doğru mu? (`Authorization` - büyük/küçük harf duyarlı)

### Problem: 403 Forbidden

**Kontrol Listesi:**
- ✅ API Gateway üzerinden mi erişiyorsunuz? (`http://localhost:8765/profile-service/...`)
- ✅ Direkt servis portuna mı erişmeye çalışıyorsunuz? (`http://localhost:9001/...` - ❌ Yapmayın!)

### Problem: 500 Internal Server Error

**Kontrol Listesi:**
- ✅ User Service çalışıyor mu?
- ✅ Subscription Service çalışıyor mu?
- ✅ Eureka'da servisler kayıtlı mı? (`http://localhost:8761`)
- ✅ Database bağlantısı var mı?

### Problem: Servisler Arası İletişim Hatası

**Logları kontrol edin:**
- Profile Service logları: `docker logs profile-service`
- User Service logları: `docker logs user-service-1`
- Subscription Service logları: `docker logs subscription-and-billing-service`

---

## 📚 Örnek Postman Collection JSON

Postman Collection import için hazır JSON dosyası da oluşturabilirim. İsterseniz hazırlayabilirim.

**Test etmeye hazır!** 🚀
