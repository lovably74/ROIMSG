# ROIMSG 배포 환경 개요

ROIMSG 플랫폼은 환경별로 서로 다른 배포 전략을 사용합니다. 개발자는 각 환경에 맞는 구성을 참고해 주세요.

## 개발 환경 (로컬)
- Docker를 사용하지 않습니다.
- 개발자는 로컬에 PostgreSQL과 Redis를 직접 설치하거나 사내 공용 인스턴스를 사용합니다.
- `.env` 파일의 데이터베이스 및 캐시 연결 정보를 로컬 설치 정보로 맞춰 주세요.
- 초기 스키마는 `scripts/setup-database.js` 혹은 `scripts/sql/*.sql`로 적용합니다.

## 테스트 서버 (스테이징)
- 독립된 테스트 서버에서는 Docker Compose로 의존 서비스를 구성합니다.
- 사용 파일: `infrastructure/test/docker-compose.yml`
- 실행 예시:
  ```bash
  docker compose -f infrastructure/test/docker-compose.yml up -d
  docker compose -f infrastructure/test/docker-compose.yml down
  ```
- 테스트 서버 배포 파이프라인에서만 Docker 권한이 필요합니다.

## 운영 서버
- 운영 환경은 Docker Compose 대신 Kubernetes 또는 관리형 서비스 사용을 권장합니다.
- 향후 `infrastructure/prod/` 디렉터리에 배포 매니페스트를 추가합니다.
- 운영 환경에서는 고가용성 DB, Redis 클러스터, 모니터링( Prometheus/Grafana ) 등을 별도로 구성하세요.

## 디렉터리 구조
```
infrastructure/
├── README.md                 # 환경별 개요 (현재 문서)
├── test/
│   └── docker-compose.yml    # 테스트 서버 전용 Docker Compose
└── prod/                     # 운영 배포 매니페스트(추가 예정)
```
