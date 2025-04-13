[English](./README.md) | [**ภาษาไทย**](./README.th.md)

---

# บริการจองร้านอาหาร (Reservation Service)

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)

บริการจองร้านอาหารเป็นส่วนหนึ่งของสถาปัตยกรรมไมโครเซอร์วิสของแพลตฟอร์มจองร้านอาหาร ทำหน้าที่จัดการทุกแง่มุมของการจองร้านอาหาร รวมถึงการสร้าง การยืนยัน การยกเลิก และการอัปเดตข้อมูลการจอง

## ภาพรวม

บริการจองร้านอาหารเป็นองค์ประกอบหลักของแพลตฟอร์มจองร้านอาหาร รับผิดชอบการจัดการวงจรชีวิตการจองทั้งหมด บริการนี้จัดการการสร้างการจอง การยืนยัน การแก้ไข การยกเลิก และการเสร็จสิ้นการจอง รวมถึงการจัดโต๊ะและการเลือกรายการอาหาร

## คุณสมบัติ

- **การจัดการวงจรชีวิตการจองอย่างครบถ้วน**
  - สร้าง ยืนยัน อัปเดต และยกเลิกการจอง
  - การเปลี่ยนสถานะโดยอัตโนมัติตามเวลา
  - การติดตามประวัติอย่างครอบคลุมสำหรับการเปลี่ยนแปลงทั้งหมด

- **การจัดการโต๊ะ**
  - จัดโต๊ะอัตโนมัติตามขนาดของกลุ่มและความพร้อมใช้งาน
  - ติดตามสถานะโต๊ะและซิงโครไนซ์ข้อมูล
  - คืนสถานะโต๊ะเมื่อการจองเสร็จสิ้นหรือถูกยกเลิก

- **การผสานรวมเมนู**
  - เพิ่มรายการอาหารในการจอง
  - รักษาแคชของรายการอาหารจาก Kitchen Service ในเครื่อง
  - ตรวจสอบว่ารายการอาหารเป็นของร้านอาหารที่ถูกต้อง

- **การจัดการโควตา**
  - ติดตามและบังคับใช้โควตาการจองตามช่วงเวลา
  - ป้องกันการจองเกินในช่วงเวลาที่มีความต้องการสูง

- **การจัดกำหนดการ**
  - ประมวลผลการจองที่หมดอายุโดยอัตโนมัติ
  - ทำเครื่องหมายการจองที่ผ่านไปแล้วว่าเสร็จสิ้น
  - ทำความสะอาดข้อมูลการจองเก่า

## เทคโนโลยีที่ใช้

- **Java 17**
- **Spring Boot** - เฟรมเวิร์กหลักของแอปพลิเคชัน
- **Spring Data JPA** - การปฏิสัมพันธ์กับฐานข้อมูล
- **Spring Kafka** - การส่งข้อความแบบ Event-driven
- **Spring Security** - การพิสูจน์ตัวตนและการอนุญาต
- **MySQL** - ฐานข้อมูลหลัก (สามารถกำหนดค่าได้)
- **H2** - ฐานข้อมูลในหน่วยความจำสำหรับการพัฒนาและทดสอบ
- **JWT** - สำหรับการพิสูจน์ตัวตนที่ปลอดภัย
- **Swagger/OpenAPI** - เอกสาร API

## สถาปัตยกรรม

บริการจองร้านอาหารใช้สถาปัตยกรรมแบบเป็นชั้น:

- **ชั้น API**: REST controllers สำหรับจัดการคำขอ HTTP
- **ชั้น Service**: ตรรกะทางธุรกิจและการจัดการธุรกรรม
- **ชั้น Repository**: การเข้าถึงข้อมูลและการเก็บรักษาข้อมูล
- **ชั้น Domain**: โมเดลเอนทิตีและกฎทางธุรกิจ
- **ชั้น Kafka**: ผู้ผลิตและผู้บริโภคเหตุการณ์สำหรับการสื่อสารระหว่างบริการ

## ข้อกำหนดเบื้องต้น

- JDK 17
- Maven 3.6+
- MySQL 8+ (หรือฐานข้อมูลที่เข้ากันได้)
- Apache Kafka 3.0+

## กระบวนการจอง

### การสร้างการจอง

1. **การตรวจสอบคำขอ**
   - ตรวจสอบข้อมูลนำเข้าของผู้ใช้ (ขนาดกลุ่ม, วันที่/เวลา, ฯลฯ)
   - ตรวจสอบว่าร้านอาหารมีอยู่และใช้งานอยู่
   - ตรวจสอบว่าเวลาที่จองอยู่ในเวลาทำการของร้านอาหาร (ผ่าน Kafka)

2. **การจัดโต๊ะ**
   - ส่งคำขอเพื่อหาโต๊ะที่ว่าง (ผ่าน Kafka)
   - รับการตอบกลับการจัดโต๊ะ
   - หากไม่มีโต๊ะว่าง จะปฏิเสธการจอง

3. **การสร้างการจอง**
   - สร้างบันทึกการจองด้วยสถานะ PENDING
   - กำหนดเส้นตายการยืนยัน (ค่าเริ่มต้น: 15 นาที)
   - อัปเดตโควตาการจองสำหรับช่วงเวลานั้น
   - เผยแพร่เหตุการณ์การสร้างการจอง (ผ่าน Kafka)

4. **การประมวลผลรายการอาหาร** (ตัวเลือก)
   - ตรวจสอบว่ารายการอาหารเป็นของร้านอาหาร
   - เพิ่มรายการอาหารที่เลือกลงในการจอง

### การยืนยันการจอง

1. **การตรวจสอบการอนุญาต**
   - ตรวจสอบว่าผู้ใช้เป็นผู้สร้างการจอง

2. **การอัปเดตสถานะ**
   - เปลี่ยนสถานะจาก PENDING เป็น CONFIRMED
   - บันทึกเวลาที่ยืนยัน
   - สร้างบันทึกประวัติ

3. **การเผยแพร่เหตุการณ์**
   - เผยแพร่เหตุการณ์การยืนยันการจอง (ผ่าน Kafka)

### การประมวลผลอัตโนมัติ

1. **การจองที่หมดอายุ**
   - งานที่กำหนดเวลาทำงานทุกนาที
   - ระบุการจองที่เลยกำหนดเวลายืนยัน
   - ยกเลิกการจองที่หมดอายุ
   - คืนโต๊ะที่จัดไว้
   - อัปเดตโควตาการจอง

2. **การจองที่เสร็จสิ้น**
   - ทำเครื่องหมายการจองที่ยืนยันและผ่านไปแล้วว่าเสร็จสิ้นโดยอัตโนมัติ
   - คืนโต๊ะสำหรับการจองที่เสร็จสิ้น
   - อัปเดตสถานะโต๊ะ (ผ่าน Kafka)

## การกำหนดค่า

คัดลอกไฟล์ properties ตัวอย่างเพื่อสร้างการกำหนดค่าของคุณ:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

อัปเดตคุณสมบัติต่อไปนี้ด้วยค่าเฉพาะสภาพแวดล้อมของคุณ:

- รายละเอียดการเชื่อมต่อฐานข้อมูล
- ที่อยู่ Kafka broker
- JWT secret
- คุณสมบัติเฉพาะบริการ

ตัวเลือกการกำหนดค่าที่สำคัญ:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_service
spring.datasource.username=your_username
spring.datasource.password=your_password

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=reservation-service-group

# JWT
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Reservation Settings
reservation.max-future-days=90
reservation.default-session-length-minutes=120
reservation.min-advance-booking-minutes=60
reservation.max-party-size=20
reservation.confirmation-expiration-minutes=15
```

## การสร้างบริการ

```bash
mvn clean package
```

การทำเช่นนี้จะ:
1. คอมไพล์โค้ด
2. เรียกใช้การทดสอบ
3. สร้างไฟล์ JAR ในไดเรกทอรี `target`

## การเรียกใช้บริการ

### การใช้ Maven

```bash
mvn spring-boot:run
```

### การใช้ Java

```bash
java -jar target/reservation-service-0.0.1-SNAPSHOT.jar
```

### การใช้ Docker

```bash
# สร้าง Docker image
docker build -t reservation-service .

# รัน container
docker run -p 8083:8083 reservation-service
```

## API Endpoints

บริการเปิดเผย API endpoints ต่อไปนี้:

### Health Endpoints

| วิธี | Endpoint | คำอธิบาย | ต้องการการพิสูจน์ตัวตน | บริการที่เชื่อมต่อ |
|--------|----------|-------------|--------------|-------------------|
| GET | `/api/health` | การตรวจสอบสุขภาพพื้นฐาน | ไม่ | ไม่มี (ภายใน) |
| GET | `/api/health/details` | ข้อมูลสุขภาพโดยละเอียด | ไม่ | ไม่มี (ภายใน) |

### การจัดการการจอง

| วิธี | Endpoint | คำอธิบาย | ต้องการการพิสูจน์ตัวตน | บริการที่เชื่อมต่อ |
|--------|----------|-------------|--------------|-------------------|
| GET | `/api/reservations/user` | รับการจองของผู้ใช้ปัจจุบัน | ใช่ | User Service |
| GET | `/api/reservations/restaurant/{restaurantId}` | รับการจองของร้านอาหาร | ใช่ (แอดมิน/เจ้าของ) | Restaurant Service |
| GET | `/api/reservations/{id}` | รับการจองเฉพาะ | ใช่ | ไม่มี (ภายใน) |
| POST | `/api/reservations` | สร้างการจองใหม่ | ใช่ | Restaurant Service, Table Service |
| PUT | `/api/reservations/{id}` | อัปเดตการจอง | ใช่ | Restaurant Service, Table Service |
| POST | `/api/reservations/{id}/confirm` | ยืนยันการจอง | ใช่ | Restaurant Service, Table Service |
| POST | `/api/reservations/{id}/cancel` | ยกเลิกการจอง | ใช่ | Restaurant Service, Table Service |
| POST | `/api/reservations/{id}/menu-items` | เพิ่มรายการอาหารในการจอง | ใช่ | Kitchen Service |

### การจัดการเมนู

| วิธี | Endpoint | คำอธิบาย | ต้องการการพิสูจน์ตัวตน | บริการที่เชื่อมต่อ |
|--------|----------|-------------|--------------|-------------------|
| GET | `/api/menus/restaurants/{restaurantId}/categories` | รับหมวดหมู่เมนูร้านอาหาร | ไม่ | Kitchen Service |
| GET | `/api/menus/categories/{categoryId}` | รับหมวดหมู่เมนูเฉพาะ | ไม่ | Kitchen Service |
| GET | `/api/menus/restaurants/{restaurantId}/items` | รับรายการอาหารทั้งหมดสำหรับร้านอาหาร | ไม่ | Kitchen Service |
| GET | `/api/menus/items/{itemId}` | รับรายการอาหารเฉพาะ | ไม่ | Kitchen Service |
| GET | `/api/menus/restaurants/{restaurantId}/search` | ค้นหารายการอาหาร | ไม่ | Kitchen Service |

### การค้นหาร้านอาหาร

| วิธี | Endpoint | คำอธิบาย | ต้องการการพิสูจน์ตัวตน | บริการที่เชื่อมต่อ |
|--------|----------|-------------|--------------|-------------------|
| POST | `/api/restaurants/search` | ค้นหาร้านอาหารที่มีอยู่ | ไม่ | Restaurant Service |

### การจัดการตารางเวลา

| วิธี | Endpoint | คำอธิบาย | ต้องการการพิสูจน์ตัวตน | บริการที่เชื่อมต่อ |
|--------|----------|-------------|--------------|-------------------|
| GET | `/api/schedules/restaurant/{restaurantId}` | รับตารางเวลาร้านอาหาร | ไม่ | Restaurant Service |
| PUT | `/api/schedules/restaurant/{restaurantId}/date/{date}` | อัปเดตตารางเวลาสำหรับวันที่ | ใช่ (แอดมิน/เจ้าของ) | Restaurant Service |

## การพิสูจน์ตัวตน

บริการใช้การพิสูจน์ตัวตนแบบ JWT endpoints ที่ได้รับการป้องกันต้องการโทเค็น JWT ที่ถูกต้องในส่วนหัว Authorization:

```
Authorization: Bearer <jwt_token>
```

## การสื่อสารด้วย Event

บริการสื่อสารกับไมโครเซอร์วิสอื่นโดยใช้ Kafka:

### Events ที่เผยแพร่

- `ReservationCreatedEvent`
- `ReservationConfirmedEvent`
- `ReservationCancelledEvent`
- `ReservationModifiedEvent`
- `TableAssignedEvent`
- `TableStatusChangedEvent`
- `FindAvailableTableRequestEvent`
- `RestaurantValidationRequestEvent`
- `RestaurantSearchRequestEvent`

### Events ที่บริโภค

- `MenuItemEvent`
- `UserEvent`
- `FindAvailableTableResponseEvent`
- `RestaurantValidationResponseEvent`
- `RestaurantSearchResponseEvent`
- `ReservationTimeValidationResponseEvent`
- `RestaurantOwnershipResponseEvent`

## เอกสาร

เอกสาร API มีให้ผ่าน Swagger UI ที่:

```
http://localhost:8083/swagger-ui.html
```

## งานที่กำหนดเวลา

บริการรวมงานที่กำหนดเวลาหลายอย่าง:

- **ประมวลผลการจองที่หมดอายุ**: ทำงานทุกนาทีเพื่อจัดการการจองที่เลยกำหนดเวลายืนยัน
- **ทำความสะอาดข้อมูลเก่า**: ทำงานทุกวันเพื่อเก็บถาวรหรือลบข้อมูลการจองเก่า
- **อัปเดตสถานะโต๊ะ**: คืนโต๊ะโดยอัตโนมัติสำหรับการจองที่เสร็จสิ้น

## ความปลอดภัย

ทุก endpoint ได้รับการป้องกันด้วยการพิสูจน์ตัวตนแบบ JWT ผู้ใช้สามารถเท่านั้น:
- ดูการจองของตัวเอง
- แก้ไขหรือยกเลิกการจองที่พวกเขาสร้าง
- เจ้าของร้านอาหารสามารถดูการจองทั้งหมดสำหรับร้านอาหารของพวกเขา

## การจัดการข้อผิดพลาด

บริการให้การตอบสนองข้อผิดพลาดที่เป็นมาตรฐานด้วย:

- รหัสสถานะ HTTP
- ข้อความแสดงข้อผิดพลาด
- รหัสข้อผิดพลาด
- ID คำขอสำหรับการติดตาม

## แนวทางการพัฒนา

### การเพิ่ม Endpoints ใหม่

1. สร้างคลาส DTO สำหรับคำขอ/การตอบสนอง
2. เพิ่มเมธอด controller ด้วยคำอธิบายประกอบที่เหมาะสม
3. ดำเนินการตรรกะบริการ
4. เพิ่มการทดสอบแบบบูรณาการ

### การเพิ่ม Events ใหม่

1. กำหนดคลาสเหตุการณ์ในโมดูลทั่วไป
2. กำหนดค่า Kafka producer หรือ consumer
3. เพิ่มตรรกะการจัดการเหตุการณ์ในบริการที่เหมาะสม
4. อัปเดตการแมปประเภทใน KafkaProducerConfig หรือ KafkaConsumerConfig

## โครงสร้างโปรเจกต์

```
reservation-service/
├── src/main/java/com/restaurant/reservation/
│   ├── api/controllers/         # REST controllers
│   ├── config/                  # การกำหนดค่าแอปพลิเคชัน
│   ├── domain/                  # โมเดลโดเมนและ repositories
│   │   ├── models/              # คลาสเอนทิตี
│   │   └── repositories/        # JPA repositories
│   ├── dto/                     # Data Transfer Objects
│   ├── exception/               # ข้อยกเว้นที่กำหนดเอง
│   ├── filters/                 # ตัวกรอง HTTP
│   ├── kafka/                   # การกำหนดค่า Kafka
│   │   ├── consumers/           # Kafka consumers
│   │   └── producers/           # Kafka producers
│   ├── security/                # การกำหนดค่าความปลอดภัย
│   └── service/                 # บริการตรรกะทางธุรกิจ
└── src/main/resources/          # ทรัพยากรแอปพลิเคชัน
    └── application.properties   # การกำหนดค่าแอปพลิเคชัน
```

## ใบอนุญาต

MIT License

Copyright (c) 2025 Restaurant Reservation Platform

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.