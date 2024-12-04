# Stop all docker containers
docker ps -q | xargs -L1 docker stop
test -z "$(docker ps -q 2>/dev/null)" && osascript -e 'quit app "Docker"'
# Soft start docker
open --background -a Docker
