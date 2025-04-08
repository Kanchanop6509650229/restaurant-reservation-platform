# แพลตฟอร์มจองร้านอาหาร

![Version](https://img.shields.io/badge/version-0.1.0-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)

แพลตฟอร์มสำหรับการจองร้านอาหารที่ครบวงจร พัฒนาด้วยสถาปัตยกรรมไมโครเซอร์วิส (Microservices) โดยใช้ Spring Boot และ Kafka แอปพลิเคชันประกอบด้วยบริการหลายส่วนที่สื่อสารกันผ่าน Kafka messaging เพื่อให้บริการระบบจัดการร้านอาหารและการจองที่ครบถ้วน

## สารบัญ
- [ภาพรวมโปรเจค](#ภาพรวมโปรเจค)
- [สถาปัตยกรรม](#สถาปัตยกรรม)
- [โครงสร้างโปรเจค](#โครงสร้างโปรเจค)
- [ข้อกำหนดเบื้องต้น](#ข้อกำหนดเบื้องต้น)
- [เริ่มต้นอย่างรวดเร็ว](#เริ่มต้นอย่างรวดเร็ว)
- [API อ้างอิง](#api-อ้างอิง)
  - [User Service](#user-service-port-8081)
  - [Restaurant Service](#restaurant-service-port-8082)
  - [Reservation Service](#reservation-service-port-8083)
- [Kafka Topics](#kafka-topics)
- [Postman Collection](#postman-collection)
- [การพัฒนา](#การพัฒนา)
- [การทดสอบ](#การทดสอบ)
- [การพัฒนาในอนาคต](#การพัฒนาในอนาคต)
- [คู่มือการใช้งานระบบยืนยันตัวตน JWT](#คู่มือการใช้งานระบบยืนยันตัวตน-jwt)
- [การมีส่วนร่วม](#การมีส่วนร่วม)
- [ลิขสิทธิ์](#ลิขสิทธิ์)

## ภาพรวมโปรเจค

แพลตฟอร์มจองร้านอาหารนี้ช่วยให้สามารถ:
- จัดการข้อมูลร้านอาหารพร้อมรายละเอียดและเวลาทำการ
- จัดการโต๊ะพร้อมอัพเดทสถานะแบบเรียลไทม์
- ระบบยืนยันตัวตนผู้ใช้และการจัดการโปรไฟล์
- ระบบการจองขั้นสูงพร้อมขั้นตอนการยืนยัน
- ระบบจัดการคิวสำหรับลูกค้าที่เดินเข้ามาใช้บริการ
- สถาปัตยกรรมแบบ Event-driven สำหรับอัพเดทแบบเรียลไทม์ระหว่างบริการต่างๆ

## สถาปัตยกรรม

![architecture-diagram](architecture-diagram.png)

แพลตฟอร์มนี้ใช้สถาปัตยกรรมไมโครเซอร์วิสประกอบด้วย:

- **User Service**: ระบบยืนยันตัวตน, การลงทะเบียนผู้ใช้, และการจัดการโปรไฟล์
- **Restaurant Service**: ข้อมูลร้านอาหาร, โต๊ะ, และเวลาทำการ
- **Reservation Service**: การจอง, คิว, และการจัดตารางเวลา
- **Kafka**: ระบบส่งข้อความสำหรับการสื่อสารระหว่างเซอร์วิส
- **MySQL Databases**: ฐานข้อมูลแยกสำหรับแต่ละเซอร์วิส

## โครงสร้างโปรเจค

โปรเจคนี้ใช้โครงสร้างแพ็คเกจมาตรฐานสำหรับแอปพลิเคชัน Spring Boot เพื่อจัดระเบียบโค้ดตามความรับผิดชอบ แพ็คเกจหลักที่ใช้ในแต่ละเซอร์วิส (เช่น user-service, restaurant-service, reservation-service) มีดังนี้:

* **`api`**:
    * **วัตถุประสงค์**: จัดการคำขอ HTTP ขาเข้าและส่งคืนการตอบสนอง HTTP ทำหน้าที่เป็นจุดเข้าสำหรับไคลเอนต์ภายนอก (เช่น เว็บเบราว์เซอร์, แอปมือถือ, หรือบริการอื่นๆ)
    * **ซับแพ็คเกจทั่วไป**:
        * `controllers`: มีคลาส Spring MVC Controller (เช่น `UserController`, `RestaurantController`, `ReservationController`) ที่เชื่อมโยง URL endpoints กับเมธอด, จัดการพารามิเตอร์คำขอ/เนื้อหา, เรียกใช้เลเยอร์ `service` สำหรับการดำเนินการทางธุรกิจ และส่งข้อมูลกลับ (มักเป็น DTOs) ไปยังไคลเอนต์

* **`config`**:
    * **วัตถุประสงค์**: มีคลาส Configuration ต่างๆ สำหรับการตั้งค่าและปรับแต่งพฤติกรรมของแอปพลิเคชัน
    * **ตัวอย่าง**: การตั้งค่า Spring Security (`SecurityConfig`), การตั้งค่าการเชื่อมต่อ Kafka (`KafkaProducerConfig`, `KafkaConsumerConfig`), การเริ่มต้นข้อมูล (`DataInitializer`), การตั้งค่า Web MVC (`WebConfig`, เช่น การเพิ่ม `CurrentUserArgumentResolver`), การจัดตารางเวลา (`SchedulingConfig`) คลาสเหล่านี้กำหนด *วิธี* ที่ส่วนต่างๆ ของแอปพลิเคชันถูกกำหนดค่าและเชื่อมต่อเข้าด้วยกัน

* **`domain`**:
    * **วัตถุประสงค์**: แทนแกนหลักของแอปพลิเคชัน ประกอบด้วยโลจิกทางธุรกิจและโมเดลข้อมูล
    * **ซับแพ็คเกจทั่วไป**:
        * `models`: มีคลาส Entity หรือ Domain Model (โดยทั่วไปเป็น JPA Entities ที่แมปกับตารางในฐานข้อมูล) ที่แทนโครงสร้างข้อมูลหลักที่แอปพลิเคชันทำงานด้วย (เช่น `User`, `Profile`, `Restaurant`, `RestaurantTable`, `Reservation`)
        * `repositories`: มีอินเทอร์เฟซ (มักขยายจาก Spring Data JPA repositories เช่น `JpaRepository`) ที่กำหนดเมธอดสำหรับการโต้ตอบกับฐานข้อมูล (เช่น การบันทึก, การค้นหา, การอัปเดต, การลบข้อมูล) สำหรับ Domain Models (เช่น `UserRepository`, `RestaurantRepository`, `ReservationRepository`)

* **`dto` (Data Transfer Object)**:
    * **วัตถุประสงค์**: มี Plain Old Java Objects (POJOs) ที่ออกแบบมาโดยเฉพาะสำหรับ "การถ่ายโอนข้อมูล" ระหว่างเลเยอร์ต่างๆ ของแอปพลิเคชัน โดยเฉพาะระหว่างเลเยอร์ `service` และ `api` (controllers) หรือแม้แต่ระหว่างไมโครเซอร์วิส
    * **ประโยชน์**:
        * **การแยกส่วน**: ช่วยแยก Domain Models ภายในออกจากโครงสร้างข้อมูลที่แสดงออกหรือรับมาจากภายนอก (API Contract) นี่หมายความว่าการเปลี่ยนแปลงใน Domain Model ไม่ส่งผลกระทบโดยตรงต่อ API (และในทางกลับกัน)
        * **การปรับรูปข้อมูล**: อนุญาตให้ปรับแต่งโครงสร้างข้อมูลสำหรับกรณีการใช้งานเฉพาะหรือ API endpoints (เช่น การส่งข้อมูลผู้ใช้บางส่วนใน `UserDTO` แต่รับข้อมูลการลงทะเบียนที่จำเป็นใน `UserRegistrationRequest`)
        * **การตรวจสอบความถูกต้อง**: มักใช้กับ Validation Annotations (เช่น `@NotBlank`, `@Email`, `@Min`) เพื่อตรวจสอบข้อมูลขาเข้าจากไคลเอนต์ที่เลเยอร์ Controller
    * **ตัวอย่าง**: `UserDTO`, `ProfileDTO`, `LoginRequest`, `RestaurantDTO`, `TableDTO`, `ReservationCreateRequest`

* **`service`**:
    * **วัตถุประสงค์**: มีโลจิกทางธุรกิจหลักของแอปพลิเคชัน คลาสในเลเยอร์ service จัดการการเรียก repositories เพื่อจัดการข้อมูลและถูกเรียกโดย controllers เพื่อตอบสนองคำขอของผู้ใช้ (เช่น `UserService`, `RestaurantService`, `ReservationService`)

* **`kafka`**:
    * **วัตถุประสงค์**: จัดการการโต้ตอบกับ Apache Kafka สำหรับการสื่อสารแบบ event-driven
    * **ซับแพ็คเกจทั่วไป**:
        * `producers`: คลาสที่รับผิดชอบการส่งข้อความ (events) ไปยัง Kafka topics (เช่น `UserEventProducer`)
        * `consumers`: คลาสที่รับผิดชอบการรับและประมวลผลข้อความ (events) จาก Kafka topics (เช่น `RestaurantEventConsumer`)

* **`security`**:
    * **วัตถุประสงค์**: มีคลาสที่เกี่ยวข้องกับความปลอดภัยของแอปพลิเคชัน เช่น การจัดการ JWT, การยืนยันตัวตนผู้ใช้, และโลจิกการอนุญาต (เช่น `JwtTokenProvider`, `JwtAuthorizationFilter`, `CustomUserDetailsService`)

* **`exception`**:
    * **วัตถุประสงค์**: มีคลาสสำหรับการจัดการข้อยกเว้นแบบรวมศูนย์ (เช่น `GlobalExceptionHandler`) และข้อยกเว้นเฉพาะแอปพลิเคชัน (เช่น `EntityNotFoundException`)

* **`utils`**:
    * **วัตถุประสงค์**: มีคลาสยูทิลิตี้ที่อาจใช้ร่วมกันในส่วนต่างๆ ของแอปพลิเคชัน (เช่น `DateTimeUtils`, `SpatialUtils`)

(หมายเหตุ: ไม่จำเป็นต้องมีซับแพ็คเกจทั้งหมดในทุกเซอร์วิส ขึ้นอยู่กับความซับซ้อนและความต้องการเฉพาะของเซอร์วิสนั้นๆ)

## ข้อกำหนดเบื้องต้น

- Java 17+
- Docker และ Docker Compose
- Maven

## เริ่มต้นอย่างรวดเร็ว

### 1. สร้างโปรเจค

```bash
mvn clean package
```

### 2. เริ่มต้นโครงสร้างพื้นฐานด้วย Docker Compose

```bash
cd kafka-infrastructure
docker-compose up -d
```

การเริ่มต้นนี้จะเปิดใช้งาน:
- Zookeeper
- Kafka broker
- ฐานข้อมูล MySQL
- Kafdrop (Kafka UI)

### 3. เข้าถึงบริการ

- **User Service**: http://localhost:8081
- **Restaurant Service**: http://localhost:8082
- **Reservation Service**: http://localhost:8083
- **Kafdrop UI**: http://localhost:9000

## API อ้างอิง

### User Service (Port 8081)

#### การยืนยันตัวตน

| Endpoint | วิธี | คำอธิบาย | ต้องการการยืนยันตัวตน |
|----------|--------|-------------|---------------|
| `/api/auth/login` | POST | ยืนยันตัวตนผู้ใช้และรับโทเค็น JWT | ไม่ |

**ตัวอย่างคำขอเข้าสู่ระบบ:**
```json
{
  "username": "user",
  "password": "password123"
}
```

**ตัวอย่างการตอบกลับเข้าสู่ระบบ:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": "1a2b3c4d-5e6f-7g8h-9i0j",
    "message": "Authentication successful"
  }
}
```

#### การจัดการผู้ใช้

| Endpoint | วิธี | คำอธิบาย | ต้องการการยืนยันตัวตน |
|----------|--------|-------------|---------------|
| `/api/users/register` | POST | ลงทะเบียนผู้ใช้ใหม่ | ไม่ |
| `/api/users/me` | GET | รับข้อมูลผู้ใช้ปัจจุบัน | ใช่ |
| `/api/users/{id}` | GET | รับผู้ใช้ตาม ID | ใช่ (แอดมินหรือตัวเอง) |
| `/api/users` | GET | รับผู้ใช้ทั้งหมด | ใช่ (แอดมิน) |
| `/api/users/{id}` | DELETE | ลบผู้ใช้ | ใช่ (แอดมิน) |

#### การจัดการโปรไฟล์

| Endpoint | วิธี | คำอธิบาย | ต้องการการยืนยันตัวตน |
|----------|--------|-------------|---------------|
| `/api/users/{id}/profile` | GET | รับโปรไฟล์ผู้ใช้ | ใช่ (แอดมินหรือตัวเอง) |
| `/api/users/{id}/profile` | PUT | อัปเดตโปรไฟล์ผู้ใช้ | ใช่ (แอดมินหรือตัวเอง) |

### Restaurant Service (Port 8082)

#### ร้านอาหาร

| Endpoint | วิธี | คำอธิบาย | ต้องการการยืนยันตัวตน |
|----------|--------|-------------|---------------|
| `/api/restaurants/public/all` | GET | รับร้านอาหารที่เปิดใช้งานทั้งหมด | ไม่ |
| `/api/restaurants/public/{id}` | GET | รับร้านอาหารตาม ID | ไม่ |
| `/api/restaurants/public/search` | GET | ค้นหาร้านอาหารตามเกณฑ์ | ไม่ |
| `/api/restaurants/public/nearby` | GET | ค้นหาร้านอาหารใกล้เคียง | ไม่ |
| `/api/restaurants` | POST | สร้างร้านอาหารใหม่ | ใช่ |
| `/api/restaurants/{id}` | PUT | อัปเดตร้านอาหาร | ใช่ (เจ้าของ) |
| `/api/restaurants/{id}/active` | PATCH | สลับสถานะเปิดใช้งานของร้านอาหาร | ใช่ (เจ้าของ) |

#### โต๊ะ

| Endpoint | วิธี | คำอธิบาย | ต้องการการยืนยันตัวตน |
|----------|--------|-------------|---------------|
| `/api/restaurants/{restaurantId}/tables/public` | GET | รับโต๊ะทั้งหมดของร้านอาหาร | ไม่ |
| `/api/restaurants/{restaurantId}/tables/public/available` | GET | รับโต๊ะที่ว่าง | ไม่ |
| `/api/restaurants/{restaurantId}/tables` | POST | เพิ่มโต๊ะให้ร้านอาหาร | ใช่ (เจ้าของ) |
| `/api/restaurants/{restaurantId}/tables/{tableId}` | PUT | อัปเดตโต๊ะ | ใช่ (เจ้าของ) |
| `/api/restaurants/{restaurantId}/tables/{tableId}/status` | PATCH | อัปเดตสถานะโต๊ะ | ใช่ (เจ้าของ) |

#### เวลาทำการ

| Endpoint | วิธี | คำอธิบาย | ต้องการการยืนยันตัวตน |
|----------|--------|-------------|---------------|
| `/api/restaurants/{restaurantId}/operating-hours/public` | GET | รับเวลาทำการ | ไม่ |
| `/api/restaurants/{restaurantId}/operating-hours` | PUT | อัปเดตเวลาทำการ | ใช่ (เจ้าของ) |

### Reservation Service (Port 8083)

#### การจอง

| Endpoint | วิธี | คำอธิบาย | ต้องการการยืนยันตัวตน |
|----------|--------|-------------|---------------|
| `/api/reservations/availability` | GET | ตรวจสอบความพร้อมใช้งานของโต๊ะ | ไม่ |
| `/api/reservations` | POST | สร้างการจอง | ใช่ |
| `/api/reservations/user` | GET | รับการจองของผู้ใช้ | ใช่ |
| `/api/reservations/restaurant/{restaurantId}` | GET | รับการจองของร้านอาหาร | ใช่ (เจ้าของ) |
| `/api/reservations/{id}` | GET | รับการจองตาม ID | ใช่ |
| `/api/reservations/{id}` | PUT | อัปเดตการจอง | ใช่ |
| `/api/reservations/{id}/confirm` | POST | ยืนยันการจอง | ใช่ |
| `/api/reservations/{id}/cancel` | POST | ยกเลิกการจอง | ใช่ |

#### การจัดการตารางเวลา

| Endpoint | วิธี | คำอธิบาย | ต้องการการยืนยันตัวตน |
|----------|--------|-------------|---------------|
| `/api/schedules/restaurant/{restaurantId}` | GET | รับตารางเวลาของร้านอาหาร | ไม่ |
| `/api/schedules/restaurant/{restaurantId}/date/{date}` | PUT | อัปเดตตารางเวลาสำหรับวันหนึ่ง | ใช่ (เจ้าของ) |

## Kafka Topics

Kafka topics ต่อไปนี้ใช้สำหรับการสื่อสารระหว่างเซอร์วิส:

### User Service Topics
- `user-events`: เหตุการณ์ทั่วไปที่เกี่ยวข้องกับผู้ใช้
- `user-registration`: เหตุการณ์การลงทะเบียนผู้ใช้
- `user-login`: เหตุการณ์การเข้าสู่ระบบของผู้ใช้
- `user-profile`: เหตุการณ์การอัปเดตโปรไฟล์ผู้ใช้

### Restaurant Service Topics
- `restaurant-events`: เหตุการณ์ทั่วไปที่เกี่ยวข้องกับร้านอาหาร
- `restaurant-update`: เหตุการณ์การอัปเดตข้อมูลร้านอาหาร
- `table-status`: เหตุการณ์การเปลี่ยนสถานะโต๊ะ
- `capacity-change`: เหตุการณ์การเปลี่ยนแปลงความจุของร้านอาหาร

### Reservation Service Topics
- `reservation-events`: เหตุการณ์การจองทั่วไป
- `reservation-create`: เหตุการณ์การสร้างการจอง
- `reservation-update`: เหตุการณ์การอัปเดตการจอง
- `reservation-cancel`: เหตุการณ์การยกเลิกการจอง

## Postman Collection

ไฟล์ Postman collection (`PostmanTesting.json`) มีให้สำหรับการทดสอบ API ที่มีทั้งหมด การใช้งาน:

1. นำเข้า collection เข้า Postman
2. ตั้งค่าตัวแปรสภาพแวดล้อม:
   - `baseUrlUser`: http://localhost:8081
   - `baseUrlRestaurant`: http://localhost:8082
   - `baseUrlReservation`: http://localhost:8083

## การพัฒนา

### การเพิ่มโค้ดที่กำหนดเอง

แต่ละเซอร์วิสได้รับการออกแบบให้เป็นอิสระและสามารถขยายแยกกันได้:

- **User Service**: เพิ่มฟีเจอร์ผู้ใช้ที่กำหนดเองในไดเรกทอรี `user-service`
- **Restaurant Service**: เพิ่มฟีเจอร์ร้านอาหารในไดเรกทอรี `restaurant-service`
- **Reservation Service**: เพิ่มฟีเจอร์การจองในไดเรกทอรี `reservation-service`

### การสร้างเซอร์วิสแยก

คุณสามารถสร้างและเรียกใช้เซอร์วิสแต่ละตัวได้:

```bash
cd user-service
mvn spring-boot:run
```

### การเข้าถึงฐานข้อมูล

- **User Service DB**: localhost:3306
- **Restaurant Service DB**: localhost:3307
- **Reservation Service DB**: localhost:3308

## การพัฒนาในอนาคต

1. Notification Service
   - การแจ้งเตือนทางอีเมล
   - การแจ้งเตือนทาง SMS
   - การแจ้งเตือนแบบ Push

2. Payment Service
   - การประมวลผลการชำระเงิน
   - การจัดการการคืนเงิน
   - ประวัติการชำระเงิน

3. Analytics Dashboard
   - ข้อมูลเชิงลึกทางธุรกิจ
   - ข้อมูลเชิงลึกของลูกค้า
   - ตัวชี้วัดประสิทธิภาพ

# คู่มือการใช้งานระบบยืนยันตัวตน JWT

คู่มือนี้อธิบายวิธีการทำงานของการยืนยันตัวตนในแพลตฟอร์มจองร้านอาหารและวิธีการใช้งานการยืนยันตัวตน JWT ในแอปพลิเคชันไคลเอนต์อย่างถูกต้อง

## ภาพรวม

แพลตฟอร์มใช้ JSON Web Tokens (JWT) สำหรับการยืนยันตัวตน กระบวนการยืนยันตัวตนคือ:

1. ผู้ใช้เข้าสู่ระบบด้วยชื่อผู้ใช้/รหัสผ่านไปยัง User Service
2. User Service ตรวจสอบข้อมูลและส่งคืนโทเค็น JWT
3. ไคลเอนต์รวมโทเค็นนี้ในส่วนหัว `Authorization` สำหรับคำขอต่อไป
4. เซอร์วิสตรวจสอบโทเค็นและแยกข้อมูลผู้ใช้สำหรับการอนุญาต

## แผนภาพกระบวนการยืนยันตัวตน

```
┌─────────┐                ┌─────────────┐                  ┌──────────────────┐
│ ไคลเอนต์ │                │ User Service │                  │ Protected Service │
└────┬────┘                └──────┬──────┘                  └─────────┬────────┘
     │                            │                                   │
     │ POST /api/auth/login       │                                   │
     │ {username, password}       │                                   │
     │ ─────────────────────────► │                                   │
     │                            │                                   │
     │ 200 OK                     │                                   │
     │ {token, userId}            │                                   │
     │ ◄───────────────────────── │                                   │
     │                            │                                   │
     │ GET /api/some-endpoint     │                                   │
     │ Authorization: Bearer token│                                   │
     │ ─────────────────────────────────────────────────────────────► │
     │                            │                                   │
     │                            │                                   │ ตรวจสอบโทเค็น
     │                            │                                   │ แยกข้อมูลผู้ใช้
     │                            │                                   │ ตรวจสอบสิทธิ์
     │                            │                                   │
     │ 200 OK                     │                                   │
     │ {requested data}           │                                   │
     │ ◄───────────────────────────────────────────────────────────── │
     │                            │                                   │
```

## รายละเอียดการใช้งาน

### 1. คำขอยืนยันตัวตน

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "user123",
  "password": "password123"
}
```

**Response (สำเร็จ):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoiMWEyYjNjNGQiLCJpYXQiOjE2MTYxNTkwMjIsImV4cCI6MTYxNjI0NTQyMn0.signature",
    "userId": "1a2b3c4d-5e6f-7g8h-9i0j",
    "message": "Authentication successful"
  }
}
```

**Response (ล้มเหลว):**
```json
{
  "success": false,
  "message": "Invalid username or password",
  "errorCode": "AUTHENTICATION_ERROR"
}
```

### 2. การใช้โทเค็น JWT

หลังจากได้รับโทเค็น ให้รวมไว้ในส่วนหัว `Authorization` สำหรับคำขอต่อไป:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoiMWEyYjNjNGQiLCJpYXQiOjE2MTYxNTkwMjIsImV4cCI6MTYxNjI0NTQyMn0.signature
```

### 3. โครงสร้างโทเค็น JWT

โทเค็น JWT ประกอบด้วยสามส่วน:
- **Header:** มีประเภทโทเค็นและอัลกอริทึม
- **Payload:** มีคำอ้าง (ข้อมูล) เกี่ยวกับผู้ใช้
- **Signature:** ตรวจสอบว่าโทเค็นไม่ถูกแก้ไข

Payload ของโทเค็น JWT แพลตฟอร์มรวมถึง:
- `sub`: ชื่อผู้ใช้
- `userId`: ID เฉพาะของผู้ใช้
- `iat`: Timestamp ที่ออก
- `exp`: Timestamp หมดอายุ

### 4. การหมดอายุของโทเค็น

โทเค็นใช้ได้ 24 ชั่วโมง (กำหนดค่าได้ใน `application.properties` ด้วย `jwt.expiration`) หลังจากหมดอายุ ไคลเอนต์ต้องขอโทเค็นใหม่โดยเข้าสู่ระบบอีกครั้ง

### 5. การใช้งานในไคลเอนต์ประเภทต่างๆ

#### เว็บแอปพลิเคชัน (JavaScript)
```javascript
// ฟังก์ชันเข้าสู่ระบบ
async function login(username, password) {
  const response = await fetch('http://localhost:8081/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, password })
  });
  
  const data = await response.json();
  if (data.success) {
    // เก็บโทเค็นใน localStorage หรือ sessionStorage
    localStorage.setItem('token', data.data.token);
    localStorage.setItem('userId', data.data.userId);
    return true;
  }
  return false;
}

// การทำคำขอที่ต้องยืนยันตัวตน
async function fetchRestaurants() {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8082/api/restaurants', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
}
```

#### แอปพลิเคชันมือถือ
```swift
// ตัวอย่าง Swift (iOS)
func login(username: String, password: String, completion: @escaping (Bool, String?) -> Void) {
    let url = URL(string: "http://localhost:8081/api/auth/login")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.addValue("application/json", forHTTPHeaderField: "Content-Type")
    
    let body: [String: Any] = ["username": username, "password": password]
    request.httpBody = try? JSONSerialization.data(withJSONObject: body)
    
    URLSession.shared.dataTask(with: request) { data, response, error in
        guard let data = data, error == nil else {
            completion(false, nil)
            return
        }
        
        if let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
           let success = json["success"] as? Bool,
           success,
           let data = json["data"] as? [String: Any],
           let token = data["token"] as? String {
            
            // เก็บโทเค็นอย่างปลอดภัย
            KeychainManager.save(token, forKey: "authToken")
            completion(true, token)
        } else {
            completion(false, nil)
        }
    }.resume()
}
```

#### Postman
1. ทำคำขอ POST ไปยัง `/api/auth/login` ด้วยข้อมูลประจำตัว
2. แยกโทเค็นจากการตอบสนอง
3. ตั้งค่าตัวแปรสภาพแวดล้อม `authToken`
4. ใช้โทเค็นในคำขอต่อไปด้วยส่วนหัว Authorization:
   - ประเภท: Bearer Token
   - โทเค็น: `{{authToken}}`

### 6. แนวปฏิบัติด้านความปลอดภัยที่ดีที่สุด

1. **เก็บโทเค็นอย่างปลอดภัย:**
   - เว็บ: ใช้คุกกี้ HttpOnly หรือการจัดเก็บที่ปลอดภัย
   - มือถือ: ใช้ Keychain (iOS) หรือ Encrypted SharedPreferences (Android)

2. **ใช้การรีเฟรชโทเค็น:**
   - มีตัวเลือกในการใช้กลไก refresh token สำหรับประสบการณ์ผู้ใช้ที่ราบรื่น

3. **จัดการการหมดอายุ:**
   - ตรวจสอบการตอบสนอง 401 Unauthorized
   - เปลี่ยนเส้นทางไปยังหน้าเข้าสู่ระบบเมื่อโทเค็นหมดอายุ

4. **ออกจากระบบ:**
   - ล้างโทเค็นที่เก็บไว้ในฝั่งไคลเอนต์
   - มีตัวเลือกในการใช้การทำให้โทเค็นเป็นโมฆะฝั่งเซิร์ฟเวอร์

5. **HTTPS เท่านั้น:**
   - ใช้ HTTPS เสมอในการผลิตเพื่อปกป้องโทเค็นในการส่งผ่าน

## บทบาทผู้ใช้และสิทธิ์

แพลตฟอร์มมีระบบการควบคุมการเข้าถึงตามบทบาท:

1. **บทบาท USER**:
   - `user:read`: สามารถอ่านข้อมูลผู้ใช้ของตัวเอง
   - `profile:read`: สามารถอ่านข้อมูลโปรไฟล์ของตัวเอง
   - `profile:write`: สามารถอัปเดตข้อมูลโปรไฟล์ของตัวเอง
   - `restaurant:read`: สามารถอ่านข้อมูลร้านอาหาร

2. **บทบาท ADMIN**:
   - สิทธิ์ของ USER ทั้งหมด
   - `user:write`: สามารถสร้าง/อัปเดตข้อมูลผู้ใช้
   - `user:delete`: สามารถลบข้อมูลผู้ใช้

3. **บทบาท RESTAURANT_OWNER** (กำหนดในบริบทของร้านอาหาร):
   - ขึ้นอยู่กับ `ownerId` ของร้านอาหารที่ตรงกับ ID ของผู้ใช้ปัจจุบัน
   - สามารถจัดการร้านอาหารของตัวเอง, โต๊ะ, ฯลฯ

## การทดสอบการยืนยันตัวตน

คุณสามารถใช้ Postman collection ที่ให้มาเพื่อทดสอบการยืนยันตัวตน:

1. ใช้คำขอ "Login User" เพื่อยืนยันตัวตน
2. collection ถูกตั้งค่าให้แยกโทเค็น JWT โดยอัตโนมัติและเก็บไว้ในตัวแปร `authToken`
3. คำขอที่ต้องยืนยันตัวตนทั้งหมดในภายหลังจะใช้โทเค็นนี้

## การแก้ไขปัญหา

ปัญหาการยืนยันตัวตนทั่วไป:

1. **401 Unauthorized**:
   - โทเค็นหายไป หมดอายุ หรือไม่ถูกต้อง
   - วิธีแก้ไข: ยืนยันตัวตนใหม่และรับโทเค็นใหม่

2. **403 Forbidden**:
   - โทเค็นถูกต้องแต่ผู้ใช้ขาดสิทธิ์
   - ตรวจสอบว่าผู้ใช้มีบทบาทหรือสิทธิ์ที่จำเป็นหรือไม่

3. **โทเค็นไม่ถูกส่งอย่างถูกต้อง**:
   - ตรวจสอบให้แน่ใจว่ารูปแบบส่วนหัว Authorization ถูกต้อง: `Bearer <token>`
   - ตรวจสอบปัญหาเกี่ยวกับช่องว่างหรือการจัดรูปแบบ

4. **ปัญหา Cross-Origin**:
   - หากใช้ไคลเอนต์เว็บ ตรวจสอบให้แน่ใจว่า CORS ถูกกำหนดค่าอย่างถูกต้อง
   - ตรวจสอบคำขอ OPTIONS แบบ preflight

## ลิขสิทธิ์

โปรเจคนี้ได้รับอนุญาตภายใต้ MIT License