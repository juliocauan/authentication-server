docker compose -f "docker-compose-dev.yml" down
docker volume rm --force VOLUME auth-dev
docker image rm authentication-dev-server:latest
docker image prune --force