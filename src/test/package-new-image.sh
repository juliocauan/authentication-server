docker compose -f "docker-compose.yml" down
docker volume rm --force VOLUME auth-db-dev
docker image prune --force
bash src/test/test-and-package.sh
docker compose -f "docker-compose.yml" up -d --build