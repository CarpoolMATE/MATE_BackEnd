## 🙋‍♀️🙋‍♂️ Team

|전민서|김현준|
|:---:|:---:|
|[@JEON-MIN-SEO](https://github.com/JEON-MIN-SEO)|[@kimhyunjun-55](https://github.com/kimhyunjun-55)|@|
|기획 및 BE|BE||
<br>

# MATE Backend Architecture  
![MATE Backend Architecture](https://github.com/user-attachments/assets/4d7594b6-f964-4d9d-a504-cdd97d62e479)  

## 🏗️ 아키텍처 개요  
MATE 백엔드 시스템의 전체적인 구조를 나타낸 다이어그램입니다.  
**CI/CD 자동화, AWS 클라우드 인프라, Docker 기반 컨테이너 운영, 모니터링 및 로깅 시스템**을 포함하고 있습니다..  

---

## 🛠️ 기술 스택
| 분류          | 기술 스택 |
|--------------|------------------------------------------------|
| **언어**      | Java 17, Spring Boot 3.4.0 |
| **CI/CD**    | GitHub Actions, Docker Hub |
| **서버**      | AWS EC2, Amazon S3, Amazon RDS |
| **컨테이너**  | Docker, Docker Compose |
| **웹 서버**  | Nginx |
| **캐시**      | Redis |
| **보안**      | Spring Security |
| **모니터링**  | Prometheus, Grafana |
| **로깅**      | ELK Stack (Elasticsearch, Logstash, Kibana) |

---

## 🔄 **CI/CD 파이프라인**
### 1️⃣ **개발 브랜치 전략**
- `main` → **CI/CD (자동 배포)**
- `develop` → **CI (빌드 및 테스트)**
- `featureBranch` → **새로운 기능 개발**

### 2️⃣ **배포 과정**
1. `develop` 브랜치에서 코드 푸시 시 **CI (빌드 및 테스트)** 실행  
2. `main` 브랜치로 머지되면 **CI/CD 실행 → `.jar` 파일 빌드 → Docker Hub로 푸시**  
3. AWS EC2에서 **Docker Compose로 컨테이너 실행 및 배포**  

---

## ⚙️ **서버 인프라 구성**
- **AWS EC2**: 애플리케이션이 실행되는 서버  
- **Amazon S3**: 파일 저장소  
- **Amazon RDS**: 데이터베이스 (MySQL)  
- **Docker & Docker Compose**: 컨테이너화된 서비스 운영  

---

## 📡 **모니터링 및 로깅**
| 시스템       | 역할 |
|------------|-----------------------------------------------|
| **Prometheus** | 애플리케이션 성능 모니터링 |
| **Grafana**    | Prometheus 데이터를 시각화 |
| **Elasticsearch** | 로그 저장 및 검색 |
| **Logstash** | 로그 수집 및 변환 |
| **Kibana** | 로그 데이터 시각화 |
<details>
  <summary>Grafana 모니터링(클릭해서 보기)</summary>
  ![Image](https://github.com/user-attachments/assets/861b16a7-ac68-4480-b3c1-215b61238250)
</details>
---

## 📌 **기능 구성**
| 모듈 | 설명 |
|------|--------------------------------------------------|
| `login` | 사용자 인증 및 로그인 기능 (Spring Security) |
| `file` | 파일 업로드 및 다운로드 (Amazon S3 연동) |
| `logging` | 서버 로그 관리 (ELK Stack 활용) |
| `others` | 기타 비즈니스 로직 및 유틸리티 기능 |

---

## 🚀 **배포 아키텍처 흐름**
1. **개발 환경**에서 코드 작성 후 GitHub에 푸시  
2. GitHub Actions에서 **CI/CD 실행**  
3. `.jar` 빌드 후 **Docker Hub에 컨테이너 이미지 업로드**  
4. AWS EC2에서 Docker Compose로 **배포 및 실행**  
5. 애플리케이션 모니터링 및 로깅 (Prometheus, Grafana, ELK)  

---
## ⏳ 앞으로 해야 할 일 (TODO List)

- [ ] **CI/CD 개선** (무중단 배포)
- [x] **ELK Stack 구축** ✔️ (완료)
- [x] **API 문서화 (Swagger/OpenAPI 추가)** ✔️ (완료)
- [ ] **인덱스 쿼리 작성**
- [ ] **DB Partitioning** (Redis 캐싱을위한)
- [ ] **비밀번호 찾기 이메일 발신**
- [ ] **전체적인 코드개선 및 리팩토링**

