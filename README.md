# gate

주차장 관리 프로그램의 게이트 시스템입니다.

## 개요
- Spring Boot 기반의 게이트 관리 시스템
- REST API를 통한 차량 입출차 및 결제 차량 관리 기능 제공
- 결제 완료 차량 정보 관리 및 게이트 제어

## Features
- 차량 입차 처리
- 차량 출차 처리
- 결제 완료 차량 정보 관리
- 결제 여부 확인
- 결제 차량 목록 관리

## API Endpoints

### 1. 차량 입차
```http
POST /api/v1/parking/entrance
```

<details>
<summary><strong>Request/Response</strong></summary>

#### Request
```json
{
  "plate": "12가 3456"          // 차량 번호
}
```

#### Response
```json
{
  "plate": "12가 3456",                 // 차량 번호
  "car_status": "entry",                // 차량 상태
  "message": "입차 완료",               // 메시지
  "isPaid": true,                       // 결제 여부
  "time": "2024-02-20T14:30:00"        // 처리 시간
}
```
</details>

### 2. 차량 출차
```http
PATCH /api/v1/parking/{plate}/departure
```

<details>
<summary><strong>Response</strong></summary>

```json
{
  "plate": "12가 3456",
  "car_status": "exit",
  "message": "출차 완료",
  "isPaid": false,
  "time": "2024-02-20T16:30:00"
}
```
</details>

### 3. 결제 완료 차량 등록
```http
POST /api/v1/paid-vehicles
```

<details>
<summary><strong>Request/Response</strong></summary>

#### Request
```json
{
  "plate": "12가 3456"          // 차량 번호
}
```

#### Response
```json
{
  "status": "success"
}
```
</details>

### 4. 결제 여부 조회
```http
GET /api/v1/paid-vehicles/{plate}
```

<details>
<summary><strong>Response</strong></summary>

```json
{
  "plate": "12가 3456",
  "car_status": "check",
  "message": "결제 완료된 차량입니다",
  "isPaid": true,
  "time": "2024-02-20T14:30:00"
}
```
</details>

### 5. 결제 완료 차량 삭제
```http
DELETE /api/v1/paid-vehicles/{plate}
```

<details>
<summary><strong>Response</strong></summary>

```json
{
    "status": "success"
}
```
</details>