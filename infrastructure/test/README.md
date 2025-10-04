# 테스트 서버 Docker Compose

테스트(스테이징) 서버에서 PostgreSQL과 Redis를 빠르게 기동하기 위한 설정입니다. 개발 환경에서는 사용하지 않습니다.

## 사전 조건
- Docker Engine 20.10 이상
- Docker Compose v2.20 이상

## 실행
```bash
docker compose -f infrastructure/test/docker-compose.yml up -d
```

## 종료
```bash
docker compose -f infrastructure/test/docker-compose.yml down
```

## 노트
- 데이터는 Docker 볼륨 `postgres-data`, `redis-data`에 저장됩니다.
- 운영 환경은 별도의 Kubernetes/매니지드 서비스를 사용하세요.
