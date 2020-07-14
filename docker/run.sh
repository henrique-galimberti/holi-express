#!/usr/bin/env bash

set -e

export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

docker-compose -f $DIR/docker-compose.yml stop

docker-compose -f $DIR/docker-compose.yml up -d redis postgres
echo "Waiting for databases to init..."
sleep 30

docker-compose -f $DIR/docker-compose.yml up