# WorkFlowHub

레거시 Java 업무 경험을 Spring Boot/JPA 구조로 확장하기 위해 만든 백엔드 포트폴리오 프로젝트입니다.

기존 게시판 CRUD 프로젝트를 기반으로 업무 배정, 상태 변경, 승인/반려, 이력 로그 기능을 추가했습니다. 대형 서비스를 만드는 것이 목적이 아니라, Spring Boot와 JPA를 사용해 계층 구조, 트랜잭션, 권한 검증, 검색/페이징, N+1 개선, 테스트 코드를 직접 구현해보는 데 초점을 두었습니다.

## 프로젝트 목적

기존에는 Java 기반 업무 로직과 SQL 중심의 개발 경험이 있었고, 이 프로젝트에서는 그 경험을 Spring Boot 방식으로 다시 구성했습니다.

중점적으로 확인한 부분은 다음과 같습니다.

- Controller, Service, Repository 계층 분리
- JPA 엔티티 매핑과 연관관계
- `@Transactional`을 사용한 상태 변경과 이력 저장의 정합성 보장
- 역할별 권한 검증
- 업무 상태 변경 로그 관리
- 검색/페이징 처리
- `@EntityGraph`를 활용한 N+1 개선
- 핵심 서비스 로직 테스트

## 기술 스택

- Java 21
- Spring Boot 3.5.14
- Spring Web
- Spring Data JPA
- H2 Database
- Thymeleaf
- Lombok
- Gradle
- JUnit 5 / AssertJ

## 주요 기능

### 게시판 기능

기존 게시판 CRUD 기능을 유지했습니다.

- 게시글 목록 조회
- 게시글 작성
- 게시글 저장

게시판은 프로젝트의 메인 기능이라기보다, 기존 프로젝트를 확장했다는 기반 기능으로 두었습니다.

### 업무 관리 기능

- 업무 생성
- 업무 단건 조회
- 업무 목록 조회
- 상태/담당자/작성자/키워드/마감일 기준 검색
- 페이징
- 업무 배정
- 업무 상태 변경
- 업무 승인/반려
- 업무 상태 변경 이력 조회

### 화면 기능

백엔드 API만으로 끝내지 않고, 기능 흐름을 브라우저에서 확인할 수 있도록 Thymeleaf 화면을 추가했습니다.

- 로그인 화면
- 회원가입 화면
- 관리자 업무 현황 대시보드
- 업무 목록/검색/페이징 화면
- 업무 상세 및 이력 조회 화면
- 업무 생성 화면
- 상세 화면에서 업무 배정, 상태 변경, 승인/반려 처리

화면은 포트폴리오 시연 목적의 단순 관리 화면입니다. 프론트엔드 완성도보다는 Spring MVC에서 Controller가 Model에 데이터를 담고 Thymeleaf가 이를 출력하는 흐름을 확인하는 데 초점을 두었습니다.

### 업무 상태

업무 상태는 아래 값으로 관리합니다.

```text
CREATED
ASSIGNED
IN_PROGRESS
SUBMITTED
APPROVED
REJECTED
```

담당자가 변경할 수 있는 상태 흐름은 아래와 같이 제한했습니다.

```text
ASSIGNED -> IN_PROGRESS
IN_PROGRESS -> SUBMITTED
```

승인/반려는 제출된 업무만 가능합니다.

```text
SUBMITTED -> APPROVED
SUBMITTED -> REJECTED
```

## 권한 정책

역할은 3가지로 구분했습니다.

```text
ADMIN
MANAGER
USER
```

| 역할 | 가능 작업 |
|---|---|
| ADMIN | 업무 생성, 배정, 승인, 반려 |
| MANAGER | 업무 생성, 배정, 승인, 반려 |
| USER | 본인에게 배정된 업무 상태 변경 |

현재 프로젝트는 Spring Security 인증까지 붙인 구조는 아닙니다. 대신 API 요청 DTO 또는 화면 세션에 포함된 사용자 ID로 `User` 엔티티를 조회하고, 해당 사용자의 `role` 값을 기준으로 권한을 검증합니다.

예를 들어 업무 생성 시 `createdBy` 사용자가 `ADMIN` 또는 `MANAGER`가 아니면 예외가 발생합니다.

## 핵심 구현 포인트

### 1. 트랜잭션

업무 생성, 배정, 상태 변경, 승인/반려 시 `WorkTask` 변경과 `TaskHistory` 저장을 하나의 `@Transactional` 안에서 처리했습니다.

이렇게 처리한 이유는 업무 상태는 바뀌었는데 이력은 저장되지 않거나, 반대로 이력만 저장되는 상황을 막기 위해서입니다.

```text
WorkTask 상태 변경
+ TaskHistory 저장
= 하나의 트랜잭션으로 처리
```

### 2. 상태 변경 이력

상태가 변경될 때마다 `TaskHistory`에 이력을 저장합니다.

저장하는 정보는 다음과 같습니다.

| 항목 | 설명 |
|---|---|
| task | 대상 업무 |
| beforeStatus | 변경 전 상태 |
| afterStatus | 변경 후 상태 |
| changedBy | 처리자 |
| comment | 처리 코멘트 |
| createdAt | 처리 일시 |

예시 흐름:

```text
CREATED -> ASSIGNED
ASSIGNED -> IN_PROGRESS
IN_PROGRESS -> SUBMITTED
SUBMITTED -> APPROVED
```

각 단계마다 이력이 쌓이도록 구현했습니다.

### 3. 검색/페이징

업무 목록은 `Pageable`을 사용해 페이징 처리했습니다.

검색 조건은 아래와 같습니다.

- 상태
- 담당자
- 작성자
- 키워드
- 마감일 시작일
- 마감일 종료일

요청 예시:

```http
GET /tasks?status=ASSIGNED&page=0&size=10
GET /tasks?assignedTo=user01&page=0&size=10
GET /tasks?createdBy=manager01&page=0&size=10
GET /tasks?keyword=report&page=0&size=10
GET /tasks?dueDateFrom=2026-07-01&dueDateTo=2026-07-31&page=0&size=10
```

Repository에서는 JPQL을 사용해 검색값이 없으면 해당 조건을 무시하도록 구성했습니다.

### 4. N+1 개선

업무 목록 응답을 만들 때 작성자와 담당자 정보가 필요합니다.

```java
task.getCreatedBy().getUserId()
task.getAssignedTo().getUserId()
```

이때 연관 엔티티를 하나씩 늦게 조회하면 추가 쿼리가 여러 번 발생할 수 있습니다. 이를 줄이기 위해 업무 목록 조회 메서드에 `@EntityGraph`를 적용했습니다.

```java
@EntityGraph(attributePaths = {"createdBy", "assignedTo"})
```

즉 `WorkTask`를 조회할 때 `createdBy`, `assignedTo`도 함께 조회하도록 하여 N+1 가능성을 줄였습니다.

### 5. 예외 응답 공통 처리

`@RestControllerAdvice`를 사용해 서비스 계층에서 발생하는 예외 응답을 공통 형식으로 정리했습니다.

예외 응답 예시:

```json
{
  "status": 400,
  "message": "Only ADMIN or MANAGER can perform this action."
}
```

### 6. 테스트 코드

`WorkTaskServiceTest`에서 핵심 서비스 로직을 검증했습니다.

- 매니저의 업무 생성 성공
- 일반 사용자의 업무 생성 실패
- 상태 변경 시 이력 저장

테스트는 기존 DB 데이터에 의존하지 않고, 테스트 안에서 필요한 사용자와 업무를 직접 생성한 뒤 검증하도록 작성했습니다.

### 7. 시연용 로그인/회원가입

Spring Security를 적용하지 않고, 학습 범위에 맞춰 `HttpSession` 기반의 간단한 로그인/로그아웃 흐름을 구현했습니다.

- 회원가입 시 `TB_USER`에 사용자 저장
- 로그인 시 사용자 ID와 비밀번호 확인
- 로그인 성공 시 세션에 `loginUserId`, `loginUserRole` 저장
- 업무 화면에서 로그인 사용자 표시
- 업무 생성/배정/승인 폼에서 로그인 사용자 ID를 기본값으로 사용

이 기능은 보안 구현이 목적이 아니라, 화면에서 사용자 흐름을 시연하기 위한 보조 기능입니다.

## API 명세

### 업무 API

| 기능 | Method | URL |
|---|---|---|
| 업무 생성 | POST | `/tasks` |
| 업무 단건 조회 | GET | `/tasks/{taskId}` |
| 업무 목록 조회/검색 | GET | `/tasks` |
| 업무 배정 | PATCH | `/tasks/{taskId}/assign` |
| 업무 상태 변경 | PATCH | `/tasks/{taskId}/status` |
| 업무 승인 | PATCH | `/tasks/{taskId}/approve` |
| 업무 반려 | PATCH | `/tasks/{taskId}/reject` |
| 업무 이력 조회 | GET | `/tasks/{taskId}/histories` |

### 화면 URL

| 기능 | Method | URL |
|---|---|---|
| 로그인 화면 | GET | `/login` |
| 로그인 처리 | POST | `/login` |
| 로그아웃 | POST | `/logout` |
| 회원가입 화면 | GET | `/users/register` |
| 회원가입 처리 | POST | `/users/register` |
| 관리자 현황 | GET | `/tasks/admin` |
| 업무 목록 화면 | GET | `/tasks/page` |
| 업무 상세 화면 | GET | `/tasks/page/{taskId}` |
| 업무 생성 화면 | GET | `/tasks/page/new` |
| 업무 생성 처리 | POST | `/tasks/page` |
| 화면 업무 배정 | POST | `/tasks/page/{taskId}/assign` |
| 화면 상태 변경 | POST | `/tasks/page/{taskId}/status` |
| 화면 업무 승인 | POST | `/tasks/page/{taskId}/approve` |
| 화면 업무 반려 | POST | `/tasks/page/{taskId}/reject` |

### 업무 생성 요청

```http
POST /tasks
Content-Type: application/json
```

```json
{
  "title": "Monthly report",
  "content": "Create monthly sales report",
  "priority": "HIGH",
  "dueDate": "2026-07-31",
  "createdBy": "manager01",
  "assignedTo": "user01"
}
```

### 업무 배정 요청

```http
PATCH /tasks/{taskId}/assign
Content-Type: application/json
```

```json
{
  "assigneeId": "user01",
  "managerId": "manager01",
  "comment": "Assign task to user01"
}
```

### 업무 상태 변경 요청

```http
PATCH /tasks/{taskId}/status
Content-Type: application/json
```

```json
{
  "status": "IN_PROGRESS",
  "changedBy": "user01",
  "comment": "Start task"
}
```

### 승인/반려 요청

```http
PATCH /tasks/{taskId}/approve
PATCH /tasks/{taskId}/reject
Content-Type: application/json
```

```json
{
  "reviewerId": "manager01",
  "comment": "Approved"
}
```

## 테이블 구조

### TB_USER

| 컬럼 | 설명 |
|---|---|
| user_id | 사용자 ID |
| passwd | 비밀번호 |
| user_name | 사용자 이름 |
| role | 사용자 역할 |

### TB_WORK_TASK

| 컬럼 | 설명 |
|---|---|
| task_id | 업무 ID |
| title | 제목 |
| content | 내용 |
| status | 업무 상태 |
| priority | 우선순위 |
| due_date | 마감일 |
| created_by | 생성자 |
| assigned_to | 담당자 |
| created_at | 생성일 |
| updated_at | 수정일 |

### TB_TASK_HISTORY

| 컬럼 | 설명 |
|---|---|
| history_id | 이력 ID |
| task_id | 업무 ID |
| before_status | 변경 전 상태 |
| after_status | 변경 후 상태 |
| changed_by | 처리자 |
| comment | 처리 코멘트 |
| created_at | 처리 일시 |

### TB_BOARD

| 컬럼 | 설명 |
|---|---|
| id | 게시글 ID |
| title | 제목 |
| content | 내용 |
| author | 작성자 |

## 실행 방법

Windows:

```bash
./gradlew.bat bootRun
```

H2 Console:

```text
http://localhost:8080/h2-console
```

H2 접속 정보:

```text
JDBC URL: jdbc:h2:file:./data/workflowhub;AUTO_SERVER=TRUE
User Name: sa
Password:
```

브라우저에서 확인할 수 있는 주요 화면:

```text
http://localhost:8080/login
http://localhost:8080/tasks/admin
http://localhost:8080/tasks/page
http://localhost:8080/tasks/page/new
```

시연용 기본 계정 예시:

```text
manager01 / 1234
user01 / 1234
```

## 시연 흐름

아래 순서로 실행하면 프로젝트의 핵심 기능을 한 번에 확인할 수 있습니다.

1. `/login`에서 매니저 계정으로 로그인
2. `/tasks/page/new`에서 업무 생성
3. 업무 상세 화면에서 담당자 배정
4. 담당자 계정으로 로그인 후 상태를 `IN_PROGRESS`, `SUBMITTED`로 변경
5. 매니저 계정으로 다시 로그인 후 승인 또는 반려
6. 업무 상세 화면에서 `TaskHistory` 이력 확인
7. `/tasks/admin`에서 전체 현황 확인

## 테스트 실행

Windows:

```bash
./gradlew.bat test
```

현재 Windows 환경에서 Gradle 테스트 결과 폴더가 잠기는 경우가 있어 IntelliJ 테스트 러너로 `WorkTaskServiceTest` 실행을 확인했습니다.

## 트러블슈팅

### 1. H2 컬럼명 불일치

초기 구현 중 `User` 엔티티의 ID 컬럼명과 `schema.sql`의 컬럼명이 달라 조회 시 컬럼을 찾지 못하는 문제가 있었습니다.

엔티티 매핑과 스키마의 컬럼명을 `user_id` 기준으로 통일해 해결했습니다.

### 2. Pageable Import 오류

검색/페이징 구현 중 IntelliJ 자동 import로 `java.awt.print.Pageable`이 들어가 Repository 생성이 실패했습니다.

Spring Data JPA 페이징에는 `org.springframework.data.domain.Pageable`을 사용해야 하므로 import를 수정해 해결했습니다.

### 3. N+1 개선

업무 목록 조회 후 DTO 변환 과정에서 작성자와 담당자 엔티티 접근으로 추가 쿼리가 발생할 수 있었습니다.

Repository 조회 메서드에 `@EntityGraph(attributePaths = {"createdBy", "assignedTo"})`를 적용해 목록 조회 시 필요한 사용자 엔티티를 함께 조회하도록 개선했습니다.

### 4. 화면 검색 조건 유지

업무 목록 화면은 검색 조건을 URL 쿼리 파라미터로 전달하는 GET 방식으로 구성했습니다.

```text
/tasks/page?status=REJECTED&assignedTo=user01&keyword=report
```

검색 조건을 `WorkTaskSearchCondition` DTO로 받고, Controller에서 다시 Model에 담아 Thymeleaf 화면에 전달하여 선택한 검색값이 유지되도록 처리했습니다.

## 이후 보완 예정

- README에 화면 캡처 추가
- 테스트 케이스 추가
- Spring Security 적용 검토
