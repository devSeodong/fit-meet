# fit-meet

## 🤝 협업 규칙

이 레포지토리는 **브랜치를 나누지 않고 `main` 브랜치에 직접 작업을 반영**합니다.
협업 효율을 위해 아래 규칙을 꼭 지켜주세요.

---

## 1️⃣ 코드 컨벤션 (Code Convention)

### 공통 규칙

* (팀에서 확정 후 작성)

---

## 2️⃣ Git 사용 규칙

> 브랜치를 사용하지 않기 때문에, **반드시 아래 순서 준수!**

### 🔹 포크하는 법

1. Fork할 레파지토리에서 new Fork 생성하여 본인 레파지토리로 이관
2. STS에 git clone 후
   ```bash
   git remote add master {상위 레파지토리}
   git remote add origin {본인 레파지토리}
   ```

### 🔹 작업 전

1. 최신 Master 코드 가져오기

   ```bash
   git pull master main
   ```

### 🔹 작업 후

1. 변경 사항 확인

   ```bash
   git status
   git diff
   ```

2. 충돌 방지 — 변경 된 코드 Stash

   ```bash
   git stash push -m "message"
   git stash save "message"
   git stash
   ```
   => 셋 중 선택 자유

   ```bash
   git stash list
   ```
   => stash 목록 확인

3. 충돌 방지 — 최신 코드 Pull

   ```bash
   git pull master main
   ```

4. 충돌 방지 — Stash 해놓은 내 변경사항 다시 반영

   ```bash
   git stash apply stash@{index}
   ```
   => 만약 Merge 해야할 상황있다면 Merge 후 Pull Request 시 메시지 남겨놓기

5. 필요한 파일만 스테이징

   ```bash
   git add <파일명>
   # 정말 확실한 경우에만
   git add .
   ```

6. 의미 있는 커밋 메시지 작성

   ```bash
   git commit -m "[feat] 회원가입 API 구현"
   git commit -m "[fix] 로그인 예외 처리 수정"
   git commit -m "[docs] README 업데이트"
   ```

7. 이상 없으면 Push

   ```bash
   git push origin main
   ```

8. Origin에 Push 된 변경 사항 Master로 Pull Request
   

> ⚠️ **main이 배포 기준 브랜치입니다.**
> 빌드/실행 안 되는 코드는 절대 올리지 말아주세요 🙏

---

## 3️⃣ Swagger 작성 가이드 (REST API 문서화 필수)

모든 **REST API 엔드포인트에는 Swagger 어노테이션을 반드시 작성**합니다.
(SpringDoc 기준 예시)

### Controller 예시

```java
@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Operation(
        summary = "회원 가입",
        description = "이메일, 비밀번호, 이름을 받아 신규 사용자를 생성합니다."
    )
    @PostMapping
    public ResponseEntity<UserResponseDto> signUp(
        @RequestBody UserSignUpRequestDto request
    ) {
        ...
    }
}
```

### Swagger 작성 체크리스트

* `@Tag`
  → 컨트롤러 단위 그룹화
  (예: `"User"`, `"Auth"`, `"Meal"`, `"Challenge"`)

* `@Operation`

  * `summary`: API 한 줄 요약
  * `description`: 세부 설명

> 새로운 REST API 작성 시 필수 순서
> **Controller 메서드 생성 → Swagger 어노테이션 작성 → Commit**

---

# 📦 4️⃣ 공통 Response 규칙 (API 응답 통일)

FitMeet의 모든 REST API 응답은 아래 **단일 Response 포맷**을 사용합니다.
프론트는 항상 동일한 형식으로 응답을 받을 수 있으며,
성공/실패 여부는 `code`로 구분합니다.

---

## ✅ Response 공통 구조

```json
{
  "code": 0,
  "msg": "SUCCESS",
  "data": { ... }
}
```

### ✔ 필드 설명

| 필드명         | 설명                                    |
| ----------- | ------------------------------------- |
| `code`  | 0 = 성공<br>그 외 = ErrorCode에서 정의한 에러 코드 |
| `msg` | 성공/실패 메시지                             |
| `data`      | 실제 데이터 (실패 시 null)                    |

---

## 🟢 성공 응답 예시

```json
{
  "code": 0,
  "msg": "SUCCESS",
  "data": {
    "id": 1,
    "email": "test@test.com",
    "name": "김서형"
  }
}
```

---

## 🔴 실패(에러) 응답 예시

예: 존재하지 않는 이메일로 조회한 경우

```json
{
  "code": 2001,
  "msg": "유저를 찾을 수 없습니다.",
  "data": null
}
```

예: JWT 토큰 만료

```json
{
  "code": 1002,
  "msg": "만료된 토큰입니다.",
  "data": null
}
```

예: DB 중복키 (이메일 중복 등)

```json
{
  "code": 8001,
  "msg": "이미 존재하는 데이터입니다.",
  "data": null
}
```

---

## 🧩 ErrorCode 규칙 (code 체계)

| 구분               | 코드 범위         | 설명               |
| ---------------- | ------------- | ---------------- |
| 성공               | `0`           | 정상 처리            |
| 인증/인가            | `1000 ~ 1999` | JWT 오류, 로그인 오류 등 |
| 유저 도메인           | `2000 ~ 2999` | 회원 관련 오류         |
| 기타 도메인(식단/챌린지 등) | `3000 ~ 3999` | 필요 시 추가          |
| DB 제약 오류         | `8000 ~ 8999` | DuplicateKey 등   |
| 서버 오류            | `9000 ~ 9999` | 예상치 못한 내부 오류     |

> 상세 코드는 `/global/error/ErrorCode.java` 참고

---

## ⚙️ GlobalExceptionHandler

모든 예외는 프로젝트 공통 `GlobalExceptionHandler` 에서 처리되며,
ErrorCode 기반의 통일된 Response 구조로 변환됩니다.

핸들링되는 주요 예외:

* `CustomException` (비즈니스 예외)
* `ExpiredJwtException` / `JwtException`
* `DuplicateKeyException` (DB 중복)
* `BadCredentialsException` (로그인 실패)
* `AccessDeniedException` (권한 없음)
* 기타 모든 예외
  
---

## 🔚 마무리

* **컨벤션 통일**
* **main 브랜치 규칙 준수**
* **Swagger로 문서화**
* **공통 Response 규칙 (API 응답 통일)**

