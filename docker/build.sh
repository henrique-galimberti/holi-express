#!/usr/bin/env bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

docker build -t "holi-express/api-gateway" $DIR/../api-gateway
docker build -t "holi-express/order-microservice" $DIR/../order-microservice
docker build -t "holi-express/payment-microservice" $DIR/../payment-microservice
docker build -t "holi-express/product-microservice" $DIR/../product-microservice
docker build -t "holi-express/stock-microservice" $DIR/../stock-microservice
docker build -t "holi-express/user-microservice" $DIR/../user-microservice