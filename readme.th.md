[English](./readme.md) | [**ภาษาไทย**](./readme.th.md)

---

# แพลตฟอร์มจองร้านอาหาร

![Version](https://img.shields.io/badge/version-0.1.0-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)

แพลตฟอร์มการจองร้านอาหารแบบครบวงจรที่พัฒนาด้วย Spring Boot และ Kafka โดยใช้สถาปัตยกรรมไมโครเซอร์วิส ระบบประกอบด้วยหลายบริการที่สื่อสารกันผ่านการส่งข้อความ Kafka เพื่อให้บริการระบบจัดการร้านอาหารและการจองแบบครบวงจร

## สารบัญ
- [ภาพรวมโปรเจค](#ภาพรวมโปรเจค)
- [สถาปัตยกรรม](#สถาปัตยกรรม)
- [โครงสร้างโปรเจค](#โครงสร้างโปรเจค)
- [ความต้องการเบื้องต้น](#ความต้องการเบื้องต้น)
- [เริ่มต้นอย่างรวดเร็ว](#เริ่มต้นอย่างรวดเร็ว)
- [API อ้างอิง](#api-อ้างอิง)
  - [บริการผู้ใช้](#บริการผู้ใช้-port-8081)
  - [บริการร้านอาหาร](#บริการร้านอาหาร-port-8082)
  - [บริการการจอง](#บริการการจอง-port-8083)
- [หัวข้อ Kafka](#หัวข้อ-kafka)
- [คอลเลคชัน Postman](#คอลเลคชัน-postman)
- [การพัฒนา](#การพัฒนา)
- [การทดสอบ](#การทดสอบ)
- [การพัฒนาในอนาคต](#การพัฒนาในอนาคต)
- [คู่มือการใช้งาน JWT Authentication](#คู่มือการใช้งาน-jwt-authentication-flow)
- [การมีส่วนร่วม](#การมีส่วนร่วม)
- [ลิขสิทธิ์](#ลิขสิทธิ์)

## ภาพรวมโปรเจค

แพลตฟอร์มจองร้านอาหารช่วยให้สามารถ:
- จัดการข้อมูลร้านอาหารพร้อมรายละเอียดและเวลาทำการ
- จัดการโต๊ะพร้อมอัปเดตสถานะแบบเรียลไทม์
- ตรวจสอบตัวตนผู้ใช้และจัดการโปรไฟล์
- ระบบการจองขั้นสูงพร้อมขั้นตอนการยืนยัน
- ค้นหาร้านอาหารตามเงื่อนไขการจอง
- จัดการคิวสำหรับลูกค้าที่เดินเข้ามาใช้บริการโดยไม่มีการจอง
- สถาปัตยกรรมแบบ Event-driven สำหรับการอัปเดตแบบเรียลไทม์ระหว่างบริการต่างๆ

## สถาปัตยกรรม

![architecture-diagram](architecture-diagram.png)

แพลตฟอร์มนี้ใช้สถาปัตยกรรมไมโครเซอร์วิสประกอบด้วย:

- **บริการผู้ใช้ (User Service)**: การตรวจสอบตัวตน, การลงทะเบียนผู้ใช้, และการจัดการโปรไฟล์
- **บริการร้านอาหาร (Restaurant Service)**: ข้อมูลร้านอาหาร, โต๊ะ, และเวลาทำการ
- **บริการการจอง (Reservation Service)**: การจองและการจัดตารางเวลา
- **Kafka**: Event bus สำหรับการสื่อสารระหว่างบริการ
- **ฐานข้อมูล MySQL**: ฐานข้อมูลแยกสำหรับแต่ละบริการ

## โครงสร้างโปรเจค

โปรเจคนี้ใช้โครงสร้างแพ็คเกจมาตรฐานสำหรับแอปพลิเคชัน Spring Boot เพื่อจัดระเบียบโค้ดตามความรับผิดชอบ แพ็คเกจหลักที่ใช้ในแต่ละบริการ (เช่น user-service, restaurant-service, reservation-service) มีดังนี้:

* **`api`**:
    * **วัตถุประสงค์**: จัดการคำขอ HTTP ขาเข้าและส่งการตอบสนอง HTTP กลับ ทำหน้าที่เป็นจุดเข้าสำหรับไคลเอนต์ภายนอก (เช่น เว็บเบราว์เซอร์, แอปพลิเคชันมือถือ, หรือบริการอื่นๆ)
    * **แพ็คเกจย่อยทั่วไป**:
        * `controllers`: ประกอบด้วยคลาส Spring MVC Controller (เช่น `UserController`, `RestaurantController`, `ReservationController`) ที่แมปเอนด์พอยต์ URL ไปยังเมธอด, จัดการพารามิเตอร์/เนื้อหาคำขอ, เรียกใช้เลเยอร์ `service` สำหรับการดำเนินการทางธุรกิจ, และส่งข้อมูล (มักเป็น DTO) กลับไปยังไคลเอนต์

* **`config`**:
    * **วัตถุประสงค์**: ประกอบด้วยคลาส Configuration ต่างๆ สำหรับตั้งค่าและปรับแต่งพฤติกรรมของแอปพลิเคชัน
    * **ตัวอย่าง**: การตั้งค่า Spring Security (`SecurityConfig`), การตั้งค่าการเชื่อมต่อ Kafka (`KafkaProducerConfig`, `KafkaConsumerConfig`), การเริ่มต้นข้อมูล (`DataInitializer`), การตั้งค่า Web MVC (`WebConfig`, เช่น การเพิ่ม `CurrentUserArgumentResolver`), การกำหนดตารางเวลา (`SchedulingConfig`) คลาสเหล่านี้กำหนด *วิธี* ที่ส่วนต่างๆ ของแอปพลิเคชันถูกกำหนดค่าและเชื่อมต่อเข้าด้วยกัน

* **`domain`**:
    * **วัตถุประสงค์**: แสดงถึงแก่นของแอปพลิเคชัน ประกอบด้วยตรรกะทางธุรกิจและโมเดลข้อมูล
    * **แพ็คเกจย่อยทั่วไป**:
        * `models`: ประกอบด้วยคลาส Entity หรือ Domain Model (โดยทั่วไปเป็น JPA Entity ที่แมปไปยังตารางฐานข้อมูล) ที่แสดงถึงโครงสร้างข้อมูลหลักที่แอปพลิเคชันทำงานด้วย (เช่น `User`, `Profile`, `Restaurant`, `RestaurantTable`, `Reservation`)
        * `repositories`: ประกอบด้วยอินเทอร์เฟซ (มักขยายจาก Spring Data JPA repositories เช่น `JpaRepository`) ที่กำหนดเมธอดสำหรับโต้ตอบกับฐานข้อมูล (เช่น การบันทึก, การค้นหา, การอัปเดต, การลบข้อมูล) สำหรับ Domain Models (เช่น `UserRepository`, `RestaurantRepository`, `ReservationRepository`)

* **`dto` (Data Transfer Object)**:
    * **วัตถุประสงค์**: ประกอบด้วยออบเจ็กต์ Java แบบธรรมดา (POJOs) ที่ออกแบบมาเฉพาะสำหรับ "การถ่ายโอนข้อมูล" ระหว่างเลเยอร์ต่างๆ ของแอปพลิเคชัน โดยเฉพาะระหว่างเลเยอร์ `service` และ `api` (controllers) หรือแม้แต่ระหว่างไมโครเซอร์วิส
    * **ประโยชน์**:
        * **การแยกส่วน**: ช่วยแยก Domain Models ภายในออกจากโครงสร้างข้อมูลที่เปิดเผยหรือรับจากภายนอก (API Contract) ซึ่งหมายความว่าการเปลี่ยนแปลง Domain Model จะไม่ส่งผลกระทบโดยตรงต่อ API (และในทางกลับกัน)
        * **การจัดรูปแบบข้อมูล**: อนุญาตให้ปรับแต่งโครงสร้างข้อมูลสำหรับกรณีการใช้งานเฉพาะหรือเอนด์พอยต์ API (เช่น การส่งข้อมูลผู้ใช้บางส่วนใน `UserDTO` แต่รับข้อมูลการลงทะเบียนที่จำเป็นใน `UserRegistrationRequest`)
        * **การตรวจสอบความถูกต้อง**: มักใช้กับ Validation Annotations (เช่น `@NotBlank`, `@Email`, `@Min`) เพื่อตรวจสอบข้อมูลขาเข้าจากไคลเอนต์ที่เลเยอร์ Controller
    * **ตัวอย่าง**: `UserDTO`, `ProfileDTO`, `LoginRequest`, `RestaurantDTO`, `TableDTO`, `ReservationCreateRequest`

* **`service`**:
    * **วัตถุประสงค์**: ประกอบด้วยตรรกะทางธุรกิจหลักของแอปพลิเคชัน คลาสในเลเยอร์บริการจัดการการเรียกใช้ repositories เพื่อจัดการข้อมูลและถูกเรียกโดย controllers เพื่อตอบสนองคำขอของผู้ใช้ (เช่น `UserService`, `RestaurantService`, `ReservationService`)

* **`kafka`**:
    * **วัตถุประสงค์**: จัดการการโต้ตอบกับ Apache Kafka สำหรับการสื่อสารแบบ event-driven
    * **แพ็คเกจย่อยทั่วไป**:
        * `producers`: คลาสที่รับผิดชอบการส่งข้อความ (เหตุการณ์) ไปยังหัวข้อ Kafka (เช่น `UserEventProducer`)
        * `consumers`: คลาสที่รับผิดชอบการรับและประมวลผลข้อความ (เหตุการณ์) จากหัวข้อ Kafka (เช่น `RestaurantEventConsumer`)

* **`security`**:
    * **วัตถุประสงค์**: เก็บคลาสที่เกี่ยวข้องกับความปลอดภัยของแอปพลิเคชัน เช่น การจัดการ JWT, การตรวจสอบตัวตนผู้ใช้, และตรรกะการอนุญาต (เช่น `JwtTokenProvider`, `JwtAuthorizationFilter`, `CustomUserDetailsService`)

* **`exception`**:
    * **วัตถุประสงค์**: ประกอบด้วยคลาสสำหรับการจัดการข้อยกเว้นแบบรวมศูนย์ (เช่น `GlobalExceptionHandler`) และข้อยกเว้นเฉพาะแอปพลิเคชัน (เช่น `EntityNotFoundException`)

* **`utils`**:
    * **วัตถุประสงค์**: ประกอบด้วยคลาสยูทิลิตี้ที่อาจใช้ร่วมกันในส่วนต่างๆ ของแอปพลิเคชัน (เช่น `DateTimeUtils`, `SpatialUtils`)

(หมายเหตุ: แพ็คเกจย่อยทั้งหมดอาจไม่มีอยู่ในทุกบริการ ขึ้นอยู่กับความซับซ้อนและความต้องการเฉพาะของบริการนั้น)

## ความต้องการเบื้องต้น

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

การทำเช่นนี้จะเริ่มต้น:
- Zookeeper
- Kafka broker
- ฐานข้อมูล MySQL
- Kafdrop (Kafka UI)

### 3. เข้าถึงบริการ

- **บริการผู้ใช้**: http://localhost:8081
- **บริการร้านอาหาร**: http://localhost:8082
- **บริการการจอง**: http://localhost:8083
- **Kafdrop UI**: http://localhost:9000

## API อ้างอิง

### เอกสาร API ด้วย Swagger UI

ทุกบริการมีเอกสาร API แบบโต้ตอบโดยใช้ Swagger UI:

- **Swagger UI บริการผู้ใช้**: http://localhost:8081/swagger-ui.html
- **Swagger UI บริการร้านอาหาร**: http://localhost:8082/swagger-ui.html
- **Swagger UI บริการการจอง**: http://localhost:8083/swagger-ui.html

Swagger UI ให้อินเทอร์เฟซที่เป็นมิตรกับผู้ใช้เพื่อสำรวจและทดสอบเอนด์พอยต์ API รวมถึง:
- คำอธิบายเอนด์พอยต์โดยละเอียด
- พารามิเตอร์คำขอและสคีมา
- รูปแบบการตอบสนองและรหัสสถานะ
- ข้อกำหนดในการตรวจสอบตัวตน
- ความสามารถในการทดสอบแบบโต้ตอบ

### บริการผู้ใช้ (Port 8081)

#### การตรวจสอบตัวตน

| เอนด์พอยต์ | วิธีการ | คำอธิบาย | ต้องการการตรวจสอบตัวตน |
|----------|--------|-------------|---------------|
| `/api/auth/login` | POST | ตรวจสอบตัวตนผู้ใช้และรับโทเค็น JWT | ไม่ |

**ตัวอย่างคำขอเข้าสู่ระบบ:**
```json
{
  "username": "user",
  "password": "password123"
}
```

**ตัวอย่างการตอบสนองการเข้าสู่ระบบ:**
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

| เอนด์พอยต์ | วิธีการ | คำอธิบาย | ต้องการการตรวจสอบตัวตน |
|----------|--------|-------------|---------------|
| `/api/users/register` | POST | ลงทะเบียนผู้ใช้ใหม่ | ไม่ |
| `/api/users/me` | GET | ดูข้อมูลผู้ใช้ปัจจุบัน | ใช่ |
| `/api/users/{id}` | GET | ดูผู้ใช้ตาม ID | ใช่ (ผู้ดูแลระบบหรือตัวเอง) |
| `/api/users` | GET | ดูผู้ใช้ทั้งหมด | ใช่ (ผู้ดูแลระบบ) |
| `/api/users/{id}` | DELETE | ลบผู้ใช้ | ใช่ (ผู้ดูแลระบบ) |

#### การจัดการโปรไฟล์

| เอนด์พอยต์ | วิธีการ | คำอธิบาย | ต้องการการตรวจสอบตัวตน |
|----------|--------|-------------|---------------|
| `/api/users/{id}/profile` | GET | ดูโปรไฟล์ผู้ใช้ | ใช่ (ผู้ดูแลระบบหรือตัวเอง) |
| `/api/users/{id}/profile` | PUT | อัปเดตโปรไฟล์ผู้ใช้ | ใช่ (ผู้ดูแลระบบหรือตัวเอง) |

### บริการร้านอาหาร (Port 8082)

#### ร้านอาหาร

| เอนด์พอยต์ | วิธีการ | คำอธิบาย | ต้องการการตรวจสอบตัวตน |
|----------|--------|-------------|---------------|
| `/api/restaurants/public/all` | GET | ดูร้านอาหารที่เปิดให้บริการทั้งหมด | ไม่ |
| `/api/restaurants/public/{id}` | GET | ดูร้านอาหารตาม ID | ไม่ |
| `/api/restaurants/public/search` | GET | ค้นหาร้านอาหารตามเกณฑ์ | ไม่ |
| `/api/restaurants/public/nearby` | GET | ค้นหาร้านอาหารใกล้เคียง | ไม่ |
| `/api/restaurants` | POST | สร้างร้านอาหารใหม่ | ใช่ |
| `/api/restaurants/{id}` | PUT | อัปเดตร้านอาหาร | ใช่ (เจ้าของ) |
| `/api/restaurants/{id}/active` | PATCH | สลับสถานะการใช้งานของร้านอาหาร | ใช่ (เจ้าของ) |

#### โต๊ะ

| เอนด์พอยต์ | วิธีการ | คำอธิบาย | ต้องการการตรวจสอบตัวตน |
|----------|--------|-------------|---------------|
| `/api/restaurants/{restaurantId}/tables/public` | GET | ดูโต๊ะทั้งหมดของร้านอาหาร | ไม่ |
| `/api/restaurants/{restaurantId}/tables/public/available` | GET | ดูโต๊ะที่ว่าง | ไม่ |
| `/api/restaurants/{restaurantId}/tables` | POST | เพิ่มโต๊ะไปยังร้านอาหาร | ใช่ (เจ้าของ) |
| `/api/restaurants/{restaurantId}/tables/{tableId}` | PUT | อัปเดตโต๊ะ | ใช่ (เจ้าของ) |
| `/api/restaurants/{restaurantId}/tables/{tableId}/status` | PATCH | อัปเดตสถานะโต๊ะ | ใช่ (เจ้าของ) |

#### เวลาทำการ

| เอนด์พอยต์ | วิธีการ | คำอธิบาย | ต้องการการตรวจสอบตัวตน |
|----------|--------|-------------|---------------|
| `/api/restaurants/{restaurantId}/operating-hours/public` | GET | ดูเวลาทำการ | ไม่ |
| `/api/restaurants/{restaurantId}/operating-hours` | PUT | อัปเดตเวลาทำการ | ใช่ (เจ้าของ) |

### บริการการจอง (Port 8083)

#### การจอง

| เอนด์พอยต์ | วิธีการ | คำอธิบาย | ต้องการการตรวจสอบตัวตน |
|----------|--------|-------------|---------------|
| `/api/reservations` | POST | สร้างการจองพร้อมรายการเมนูที่เลือก (ตัวเลือก) | ใช่ |
| `/api/reservations/user` | GET | ดูการจองของผู้ใช้ | ใช่ |
| `/api/reservations/restaurant/{restaurantId}` | GET | ดูการจองของร้านอาหาร | ใช่ (เจ้าของ) |
| `/api/reservations/{id}` | GET | ดูการจองตาม ID | ใช่ |
| `/api/reservations/{id}` | PUT | อัปเดตการจอง (เฉพาะผู้ใช้ที่สร้างการจอง) | ใช่ |
| `/api/reservations/{id}/confirm` | POST | ยืนยันการจอง (เฉพาะผู้ใช้ที่สร้างการจอง) | ใช่ |
| `/api/reservations/{id}/cancel` | POST | ยกเลิกการจอง (เฉพาะผู้ใช้ที่สร้างการจองหรือเจ้าของร้านอาหาร) | ใช่ |
| `/api/reservations/{id}/menu-items` | POST | เพิ่มรายการเมนูไปยังการจอง (เฉพาะผู้ใช้ที่สร้างการจอง) | ใช่ |

#### การจัดการเมนู

| เอนด์พอยต์ | วิธีการ | คำอธิบาย | ต้องการการตรวจสอบตัวตน |
|----------|--------|-------------|---------------|
| `/api/menus/restaurants/{restaurantId}/categories` | GET | ดูหมวดหมู่เมนูทั้งหมดพร้อมรายการ | ไม่ |
| `/api/menus/categories/{categoryId}` | GET | ดูหมวดหมู่เมนูพร้อมรายการ | ไม่ |
| `/api/menus/restaurants/{restaurantId}/items` | GET | ดูรายการเมนูทั้งหมดของร้านอาหาร | ไม่ |
| `/api/menus/items/{itemId}` | GET | ดูรายการเมนูตาม ID | ไม่ |
| `/api/menus/restaurants/{restaurantId}/search` | GET | ค้นหารายการเมนูตามชื่อ/คำอธิบาย | ไม่ |

#### การค้นหาร้านอาหาร

| เอนด์พอยต์ | วิธีการ | คำอธิบาย | ต้องการการตรวจสอบตัวตน |
|----------|--------|-------------|---------------|
| `/api/restaurants/search` | POST | ค้นหาร้านอาหารที่ว่างตาม ID, ชื่อ, หรือเกณฑ์ | ไม่ |

**ตัวอย่างการค้นหาตาม ID:**
```json
{
  "restaurantId": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
}
```

**ตัวอย่างการค้นหาตามชื่อ:**
```json
{
  "restaurantName": "Restaurant"
}
```

**ตัวอย่างการค้นหาตามเกณฑ์:**
```json
{
  "date": "2025-04-15",
  "time": "19:00:00",
  "partySize": 4,
  "cuisineType": "Italian"
}
```

#### การจัดการตารางเวลา

| เอนด์พอยต์ | วิธีการ | คำอธิบาย | ต้องการการตรวจสอบตัวตน |
|----------|--------|-------------|---------------|
| `/api/schedules/restaurant/{restaurantId}` | GET | ดูตารางเวลาของร้านอาหาร | ไม่ |
| `/api/schedules/restaurant/{restaurantId}/date/{date}` | PUT | อัปเดตตารางเวลาสำหรับวันหนึ่ง | ใช่ (เจ้าของ) |

## หัวข้อ Kafka

หัวข้อ Kafka ต่อไปนี้ใช้สำหรับการสื่อสารระหว่างบริการ:

### หัวข้อบริการผู้ใช้
- `user-events`: เหตุการณ์ทั่วไปที่เกี่ยวข้องกับผู้ใช้
- `user-registration`: เหตุการณ์การลงทะเบียนผู้ใช้
- `user-login`: เหตุการณ์การเข้าสู่ระบบของผู้ใช้
- `user-profile`: เหตุการณ์การอัปเดตโปรไฟล์ผู้ใช้

### หัวข้อบริการร้านอาหาร
- `restaurant-events`: เหตุการณ์ทั่วไปที่เกี่ยวข้องกับร้านอาหาร
- `restaurant-update`: เหตุการณ์การอัปเดตข้อมูลร้านอาหาร
- `table-status`: เหตุการณ์การเปลี่ยนสถานะโต๊ะ
- `capacity-change`: เหตุการณ์การเปลี่ยนความจุของร้านอาหาร
- `restaurant-search-request`: เหตุการณ์คำขอค้นหาร้านอาหาร
- `restaurant-search-response`: เหตุการณ์การตอบสนองการค้นหาร้านอาหาร

### หัวข้อบริการการจอง
- `reservation-events`: เหตุการณ์การจองทั่วไป
- `reservation-create`: เหตุการณ์การสร้างการจอง
- `reservation-update`: เหตุการณ์การอัปเดตการจอง
- `reservation-cancel`: เหตุการณ์การยกเลิกการจอง
- `menu-item-events`: เหตุการณ์การอัปเดตรายการเมนู
- `menu-category-events`: เหตุการณ์การอัปเดตหมวดหมู่เมนู
- `kitchen-events`: เหตุการณ์ทั่วไปที่เกี่ยวข้องกับครัว

## คอลเลคชัน Postman

ไฟล์คอลเลคชัน Postman (`PostmanTesting.json`) รวมอยู่ด้วยสำหรับการทดสอบ API ที่มีอยู่ทั้งหมด รวมทั้งฟังก์ชันการค้นหาร้านอาหาร

วิธีใช้คอลเลคชันนี้:

1. นำเข้าคอลเลคชันลงใน Postman
2. ตั้งค่าตัวแปรสภาพแวดล้อม:
   - `base_url_user`: http://localhost:8081
   - `base_url_restaurant`: http://localhost:8082
   - `base_url_reservation`: http://localhost:8083

## การพัฒนา

### การเพิ่มโค้ดที่กำหนดเอง

แต่ละบริการได้รับการออกแบบให้เป็นอิสระและสามารถขยายแยกกันได้:

- **บริการผู้ใช้**: เพิ่มคุณสมบัติผู้ใช้ที่กำหนดเองในไดเรกทอรี `user-service`
- **บริการร้านอาหาร**: เพิ่มคุณสมบัติร้านอาหารในไดเรกทอรี `restaurant-service`
- **บริการการจอง**: เพิ่มคุณสมบัติการจองในไดเรกทอรี `reservation-service`

### การสร้างบริการแต่ละบริการ

คุณสามารถสร้างและรันบริการแต่ละบริการได้:

```bash
cd user-service
mvn spring-boot:run
```

### การเข้าถึงฐานข้อมูล

- **ฐานข้อมูลบริการผู้ใช้**: localhost:3306
- **ฐานข้อมูลบริการร้านอาหาร**: localhost:3307
- **ฐานข้อมูลบริการการจอง**: localhost:3308

## การพัฒนาในอนาคต

1. บริการการแจ้งเตือน
   - การแจ้งเตือนทางอีเมล
   - การแจ้งเตือนทาง SMS
   - การแจ้งเตือนแบบพุช

2. บริการการชำระเงิน
   - การประมวลผลการชำระเงิน
   - การจัดการการคืนเงิน
   - ประวัติการชำระเงิน

3. แดชบอร์ดวิเคราะห์
   - ข้อมูลเชิงลึกทางธุรกิจ
   - ข้อมูลเชิงลึกของลูกค้า
   - เมตริกประสิทธิภาพ

# คู่มือการใช้งาน JWT Authentication Flow

คู่มือนี้อธิบายวิธีการทำงานของการตรวจสอบตัวตนในแพลตฟอร์มจองร้านอาหารและวิธีการใช้งาน JWT authentication ในแอปพลิเคชันไคลเอนต์อย่างเหมาะสม

## ภาพรวม

แพลตฟอร์มใช้ JSON Web Tokens (JWT) สำหรับการตรวจสอบตัวตน โดยมีขั้นตอนดังนี้:

1. ผู้ใช้เข้าสู่ระบบด้วยชื่อผู้ใช้/รหัสผ่านไปยังบริการผู้ใช้
2. บริการผู้ใช้ตรวจสอบข้อมูลประจำตัวและส่งคืนโทเค็น JWT
3. ไคลเอนต์รวมโทเค็นนี้ในส่วนหัว `Authorization` สำหรับคำขอต่อๆ ไป
4. บริการตรวจสอบโทเค็นและดึงข้อมูลผู้ใช้สำหรับการอนุญาต

## แผนภาพขั้นตอนการตรวจสอบตัวตน

```
┌─────────┐                ┌─────────────┐                  ┌──────────────────┐
│  Client │                │ User Service │                  │ Protected Service │
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
     │                            │                                   │ Validate token
     │                            │                                   │ Extract user info
     │                            │                                   │ Check permissions
     │                            │                                   │
     │ 200 OK                     │                                   │
     │ {requested data}           │                                   │
     │ ◄───────────────────────────────────────────────────────────── │
     │                            │                                   │
```

## รายละเอียดการใช้งาน

### 1. คำขอการตรวจสอบตัวตน

**เอนด์พอยต์:** `POST /api/auth/login`

**เนื้อหาคำขอ:**
```json
{
  "username": "user123",
  "password": "password123"
}
```

**การตอบสนอง (สำเร็จ):**
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

**การตอบสนอง (ล้มเหลว):**
```json
{
  "success": false,
  "message": "Invalid username or password",
  "errorCode": "AUTHENTICATION_ERROR"
}
```

### 2. การใช้โทเค็น JWT

หลังจากได้รับโทเค็น ให้รวมไว้ในส่วนหัว `Authorization` สำหรับคำขอต่อๆ ไป:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoiMWEyYjNjNGQiLCJpYXQiOjE2MTYxNTkwMjIsImV4cCI6MTYxNjI0NTQyMn0.signature
```

### 3. โครงสร้างโทเค็น JWT

โทเค็น JWT ประกอบด้วยสามส่วน:
- **ส่วนหัว (Header):** ประกอบด้วยประเภทโทเค็นและอัลกอริทึม
- **เพย์โหลด (Payload):** ประกอบด้วยข้อมูลอ้าง (claims) เกี่ยวกับผู้ใช้
- **ลายเซ็น (Signature):** ตรวจสอบว่าโทเค็นไม่ถูกแก้ไข

เพย์โหลด JWT ของแพลตฟอร์มประกอบด้วย:
- `sub`: ชื่อผู้ใช้
- `userId`: ID เฉพาะของผู้ใช้
- `iat`: ประทับเวลาที่ออก
- `exp`: ประทับเวลาหมดอายุ

### 4. การหมดอายุของโทเค็น

โทเค็นมีอายุ 24 ชั่วโมง (กำหนดค่าได้ใน `application.properties` ด้วย `jwt.expiration`) หลังจากหมดอายุ ไคลเอนต์ต้องขอโทเค็นใหม่โดยเข้าสู่ระบบอีกครั้ง

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

// ทำคำขอที่ต้องการการตรวจสอบตัวตน
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
2. ดึงโทเค็นจากการตอบสนอง
3. ตั้งค่าตัวแปรสภาพแวดล้อม `authToken`
4. ใช้โทเค็นในคำขอต่อๆ ไปด้วยส่วนหัว Authorization:
   - ประเภท: Bearer Token
   - โทเค็น: `{{authToken}}`

### 6. แนวทางปฏิบัติด้านความปลอดภัยที่ดีที่สุด

1. **เก็บโทเค็นอย่างปลอดภัย:**
   - เว็บ: ใช้คุกกี้ HttpOnly หรือที่เก็บข้อมูลที่ปลอดภัย
   - มือถือ: ใช้ Keychain (iOS) หรือ Encrypted SharedPreferences (Android)

2. **ใช้งานการรีเฟรชโทเค็น:**
   - เลือกใช้กลไกโทเค็นรีเฟรชสำหรับประสบการณ์ผู้ใช้ที่ราบรื่น

3. **จัดการการหมดอายุ:**
   - ตรวจจับการตอบสนอง 401 Unauthorized
   - เปลี่ยนเส้นทางไปยังหน้าเข้าสู่ระบบเมื่อโทเค็นหมดอายุ

4. **ออกจากระบบ:**
   - ล้างโทเค็นที่เก็บไว้ฝั่งไคลเอนต์
   - เลือกใช้การทำให้โทเค็นไม่ถูกต้องฝั่งเซิร์ฟเวอร์

5. **HTTPS เท่านั้น:**
   - ใช้ HTTPS เสมอในการผลิตเพื่อป้องกันโทเค็นในการส่งผ่าน

## บทบาทและสิทธิ์ของผู้ใช้

แพลตฟอร์มมีระบบควบคุมการเข้าถึงตามบทบาท:

1. **บทบาท USER**:
   - `user:read`: สามารถอ่านข้อมูลผู้ใช้ของตนเอง
   - `profile:read`: สามารถอ่านข้อมูลโปรไฟล์ของตนเอง
   - `profile:write`: สามารถอัปเดตข้อมูลโปรไฟล์ของตนเอง
   - `restaurant:read`: สามารถอ่านข้อมูลร้านอาหาร

2. **บทบาท ADMIN**:
   - สิทธิ์ USER ทั้งหมด
   - `user:write`: สามารถสร้าง/อัปเดตข้อมูลผู้ใช้
   - `user:delete`: สามารถลบข้อมูลผู้ใช้

3. **บทบาท RESTAURANT_OWNER** (กำหนดในบริบทของร้านอาหาร):
   - ขึ้นอยู่กับ `ownerId` ของร้านอาหารที่ตรงกับ ID ของผู้ใช้ปัจจุบัน
   - สามารถจัดการร้านอาหาร, โต๊ะ ฯลฯ ของตนเอง

## การทดสอบการตรวจสอบตัวตน

คุณสามารถใช้คอลเลคชัน Postman ที่รวมอยู่เพื่อทดสอบการตรวจสอบตัวตน:

1. ใช้คำขอ "Login User" เพื่อตรวจสอบตัวตน
2. คอลเลคชันตั้งค่าไว้เพื่อดึงโทเค็น JWT โดยอัตโนมัติและเก็บไว้ในตัวแปร `authToken`
3. คำขอที่ต้องการการตรวจสอบตัวตนถัดไปทั้งหมดจะใช้โทเค็นนี้

## การแก้ไขปัญหา

ปัญหาการตรวจสอบตัวตนทั่วไป:

1. **401 Unauthorized**:
   - โทเค็นหายไป, หมดอายุ, หรือไม่ถูกต้อง
   - วิธีแก้ไข: ตรวจสอบตัวตนอีกครั้งและรับโทเค็นใหม่

2. **403 Forbidden**:
   - โทเค็นถูกต้องแต่ผู้ใช้ขาดสิทธิ์
   - ตรวจสอบว่าผู้ใช้มีบทบาทหรือสิทธิ์ที่จำเป็นหรือไม่

3. **โทเค็นไม่ถูกส่งอย่างถูกต้อง**:
   - ตรวจสอบว่ารูปแบบส่วนหัว Authorization ถูกต้อง: `Bearer <token>`
   - ตรวจสอบช่องว่างหรือปัญหาการจัดรูปแบบ

4. **ปัญหา Cross-Origin**:
   - หากใช้ไคลเอนต์เว็บ ตรวจสอบว่า CORS ได้รับการกำหนดค่าอย่างถูกต้อง
   - ตรวจสอบคำขอ OPTIONS preflight

## ลิขสิทธิ์

โปรเจคนี้อยู่ภายใต้สัญญาอนุญาต MIT