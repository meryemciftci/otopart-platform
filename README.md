# OtoPart Platform

Otomotiv parça e-ticaret ve akıllı teslimat platformu.  
Spring Boot 3.2 · PostgreSQL · Redis · JWT · Groq AI

---

## Modüller & Geliştirme Yol Haritası

| # | Modül | Açıklama |  
|---|-------|----------|
| 1 | **Auth** | Kayıt / Giriş / JWT / Usta (B2B) kaydı / DBS 
| 2 | **Ürün & Kategori** | CRUD, arama, lastik/şase filtresi, etiketler 
| 3 | **Tedarikçi** | 50 tedarikçi entegrasyonu, otomatik sipariş iletimi 
| 4 | **Teslimat Slotları** | 09-12-15-17 slot sistemi, Ankara/şehir dışı mantığı
| 5 | **Garaj** | Şase ekleme, araç uyumluluk filtresi, garaj etiketi
| 6 | **Sipariş** | Sepet → Sipariş → Tedarikçi iletimi 
| 7 | **Sadakat Puanı** | 1 TL = 1 puan, ücretsiz kargo, ilk 3 sipariş 
| 8 | **Kupon & İndirim** | İlk sipariş kuponu, toplu alım, kampanya etiketleri 
| 9 | **B2B / Usta** | %10 iskonto, DBS tanımlama, toplu sipariş 
| 10 | **Kurye & Kargo** | Motokurye atama, konum takibi, kargo entegrasyonu 
| 11 | **AI Asistan** | Groq – şase sorgula, sepete ekle, canlı destek 
| 12 | **Ödeme** | Kredi kartı taksit, DBS, banka kredisi yönlendirme
| 13 | **Admin Panel** | Dashboard, raporlar, tedarikçi yönetimi 

---

## Proje Yapısı

```
src/main/java/com/otopart/
├── config/              # Security, App, Swagger config
├── security/            # JWT filter & service
├── shared/
│   ├── enums/           # UserRole, OrderStatus, DeliveryType, City...
│   ├── response/        # ApiResponse wrapper
│   └── util/            # BaseEntity
└── domain/
    ├── user/            # Auth, kullanıcı
    ├── vehicle/         # Garaj, şase, uyumluluk
    ├── product/         # Ürün, kategori
    ├── supplier/        # Tedarikçi
    ├── order/           # Sipariş
    ├── delivery/        # Teslimat slotları, kurye
    ├── loyalty/         # Puan sistemi
    ├── coupon/          # Kupon & kampanya
    └── payment/         # Ödeme
```

---

## Çalıştırma

### 1. Veritabanını başlat
```bash
docker-compose up -d postgres redis
```

### 2. Uygulamayı başlat
```bash
./mvnw spring-boot:run
```

### 3. Swagger UI
```
http://localhost:8080/api/swagger-ui.html
```

---

## Ortam Değişkenleri

```env
DB_USERNAME=otopart
DB_PASSWORD=otopart123
REDIS_HOST=localhost
JWT_SECRET=change-this-in-production-min32chars!!
GROQ_API_KEY=your_groq_api_key
```

---

## Teknoloji Yığını

- Java 17, Spring Boot 3.2
- PostgreSQL 15, Flyway migration
- Redis (cache & session)
- JWT (jjwt), Spring Security
- Swagger / OpenAPI 3
- Groq AI (Llama3) – şase analizi & canlı destek
- MapStruct, Lombok
- Docker Compose
