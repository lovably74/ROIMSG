# 운영 환경 배포 가이드 (초안)

운영 환경은 고가용성과 보안을 우선합니다. 아래 항목을 참조하여 IaC 매니페스트를 추가하세요.

## 권장 아키텍처
- Kubernetes 혹은 클라우드 매니지드 서비스 (EKS/GKE/AKS 등)
- 관리형 PostgreSQL/Redis (Aurora, Cloud SQL, Memorystore 등)
- 로깅 및 모니터링: Prometheus, Grafana, ELK 등

## TODO
- [ ] Kubernetes 매니페스트 작성 (`infrastructure/prod/k8s/`)
- [ ] Helm 차트 또는 Kustomize 템플릿 정의
- [ ] 운영용 CI/CD 파이프라인 명세
- [ ] 보안 그룹/방화벽 정책 문서화

> **참고**: 운영 서버에서는 Docker Compose를 사용하지 않습니다.
