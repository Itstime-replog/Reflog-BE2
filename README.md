##  서비스명
메타 인지 회고 서비스, Reflog (Reflog : *Reflect: 성찰하다 + Log: 기록하다*)

## 서비스 소개
> 리플로그의 핵심 가치: 개인의 성장 , 간편함, 공감(서로의 학습 경험 공유)하는 서비스입니다.
> 
![image](https://github.com/user-attachments/assets/00cb07e2-289b-4ad2-8b1b-e7235d3cdf70)

![Slide 16_9 - 15](https://github.com/user-attachments/assets/9f133179-2b86-4e2a-b675-83f352669d23)


## System Architecture
![Slide 16_9 - 23](https://github.com/user-attachments/assets/35918f4d-472c-4c0a-98b3-ce9f49891fa9)

## 📜 ERD
![Slide 16_9 - 22](https://github.com/user-attachments/assets/67ccaed0-65fc-43b0-bf34-ab27a9ae4bea)

<br><br>
## 🗂️ Package
```
├── 🗂️ Dockerfile
├── 🗂️ server-yml
├── 🗂️ org.Reflog.Reflog
│   └── 🗂️ Reflog
│       ├── 💽 ReflogApplication
│       │   ├── 🗂️ ai
│       │   │   ├── 📂 dto
│       │   │   └── 📂 service
│       │   ├── 🗂️ common
│       │   │   ├── 📂 annotation
│       │   │   ├── 📂 code
│       │   │   ├── 📂 exception
│       │   │   └── 📂 resolver
│       │   ├── 🗂️ community
│       │   │   ├── 📂 controller
│       │   │   ├── 📂 domain
│       │   │   ├── 📂 dto
│       │   │   ├── 📂 repository
│       │   │   └── 📂 service
│       │   ├── 🗂️ comment
│       │   ├── 🗂️ email
│       │   ├── 🗂️ retrospect
│       │   ├── 🗂️ schedule
│       │   ├── 🗂️ todolist
│       │   ├── 🗂️ member
│       │   ├── 🗂️ mission
│       │   ├── 🗂️ notification
│       │   ├── 🗂️ oauth
│       │   │   ├── 📂 domain
│       │   │   ├── 📂 handler
│       │   │   ├── 📂 info
│       │   │   └── 📂 token
```
<br><br>

## ⚒️ Tech Stack
### Back-end
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-%6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-%6DB33F?logo=springsecurity&logoColor=white)
![Spring JPA](https://img.shields.io/badge/Spring%20JPA-%6DB33F?logo=&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?logo=amazonec2&logoColor=white)
![Amazon RDS](https://img.shields.io/badge/Amazon%20RDS-527FFF?logo=amazonRDS&logoColor=white)
![Amazon S3](https://img.shields.io/badge/Amazon%20S3-FC390E?logo=amazons3&logoColor=white) <br>
![Github Actions](https://img.shields.io/badge/Github%20Actions-2088FF?logo=githubactions&logoColor=white) ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-00465B?logo=&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-512BD4?logo=&logoColor=white) <br>

### Co-Work Tool
![Github](https://img.shields.io/badge/Github-181717?logo=github&logoColor=white)
![figma](https://img.shields.io/badge/Figma-F24E1E?logo=figma&logoColor=white)
![notion](https://img.shields.io/badge/Notion-000000?logo=notion&logoColor=white)

### 💻 기술 스택 선정 이유

- **Spring Boot**
    - 스프링 프레임워크 기반으로 자동 설정과 스타터 의존성을 제공해 빠른 웹 애플리케이션 개발과 배포를 가능하게 합니다.
- **JPA (Java Persistence API)**
    - 객체 지향 프로그래밍 방식으로 데이터베이스와 상호작용을 할 수 있게 하며, SQL 작성 없이 데이터를 관리할 수 있는 표준 ORM 기술입니다.
- **Querydsl**
    - 타입 안정성을 보장하며, 컴파일 시점에서 오류를 잡아주는 기능을 통해 복잡한 쿼리를 직관적이고 효율적으로 작성할 수 있습니다.
- **AWS RDS (MySQL)**
    - 관리형 MySQL 데이터베이스 서비스로, 유지보수와 패치 관리를 AWS에서 담당하여 안정성과 확장성을 제공합니다.
- **AWS S3 Storage**
    - 대량의 데이터를 안전하게 저장하고, 간편하게 액세스할 수 있는 객체 스토리지 서비스로, 높은 가용성과 보안을 제공합니다.
- **Java 17**
    - 장기 지원(LTS) 버전으로, 최신 기능과 보안 패치, 성능 최적화를 제공하며 안정적인 개발 환경을 제공합니다.
- **Spring Security**
    - 스프링 기반 애플리케이션의 보안을 강화하며, 인증과 권한 부여, CSRF 방어 등을 포함한 종합적인 보안 솔루션을 제공합니다.
- **JWT (JSON Web Tokens)**
    - 상태를 저장하지 않는 인증 시스템으로, 클라이언트와 서버 간 정보를 간단하고 안전하게 교환할 수 있습니다.
- **GitHub Actions**
    - 코드 빌드, 테스트, 배포를 자동화하여 CI/CD 워크플로우를 간소화하고 개발 생산성을 높입니다.
- **Spring Scheduler**
    - 스프링 프레임워크 내에서 주기적인 작업을 쉽게 설정하고 관리할 수 있게 해주는 스케줄링 기능입니다.
- **Swagger**
    - RESTful API의 설계, 빌드, 문서화를 지원하며 프론트와 백엔드 개발자 간의 협업과 API 이해를 증진시키는 소프트웨어 프레임워크입니다.
- **Gmail API**
    - Gmail을 통해 이메일 전송 및 관리를 구현하며, 안정적인 이메일 통신을 가능하게 합니다.
- **Server-Sent Events (SSE)**
    - 클라이언트와 서버 간 실시간 이벤트 기반 통신을 제공하여 알림과 같은 실시간 기능을 구현할 수 있습니다.

## 📌 Convention
### Commit Convention
[커밋 컨벤션 보러가기](https://ebony-booklet-855.notion.site/18c84ddcb3c380abb776fa44c61c0b24?pvs=4)

| Tag      | Description                                         |
|----------|-----------------------------------------------------|
| `feat`   | Commits that add a new feature.                     |
| `fix`    | Commits that fix a bug.                             |
| `hotfix` | Fix an urgent bug in issue or QA.                   |
| `build`  | Commits that affect build components.               |
| `chore`  | Miscellaneous commits.                              |
| `style`  | Commits for code styling or format.                 |
| `docs`   | Commits that affect documentation only.             |
| `test`   | Commits that add missing tests or correcting existing tests. |
| `refactor`| Commits for code refactoring.                      |

### Naming Convention
- 파일 : CamelCase + SnakeCase
- 클래스명 : PascalCase
- 함수/변수명 : CamelCase

### Branch Naming Convention
- main
- develop
- feature/Issue#
- refactor/Issue#


















