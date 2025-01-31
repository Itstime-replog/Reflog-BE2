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
















