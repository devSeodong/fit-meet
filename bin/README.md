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

### 🔹 작업 전

1. 최신 코드 가져오기

   ```bash
   git pull origin main
   ```

### 🔹 작업 후

1. 변경 사항 확인

   ```bash
   git status
   git diff
   ```

2. 필요한 파일만 스테이징

   ```bash
   git add <파일명>
   # 정말 확실한 경우에만
   git add .
   ```

3. 의미 있는 커밋 메시지 작성

   ```bash
   git commit -m "[feat] 회원가입 API 구현"
   git commit -m "[fix] 로그인 예외 처리 수정"
   git commit -m "[docs] README 업데이트"
   ```

4. 충돌 방지 — push하기 전 다시 최신 코드 Pull

   ```bash
   git pull origin main
   ```

5. 이상 없으면 Push

   ```bash
   git push origin main
   ```

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
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
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

* `@ApiResponses`

  * 성공/오류 응답 코드 상세히 작성
  * 프론트 개발 시 큰 도움이 됨

> 새로운 REST API 작성 시 필수 순서
> **Controller 메서드 생성 → Swagger 어노테이션 작성 → Commit**

---

## 🔚 마무리

* **컨벤션 통일**
* **main 브랜치 규칙 준수**
* **Swagger로 문서화**

