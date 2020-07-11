# Tech Challenge: Marketplace Platform
HoliExpress is a big company in the delivery business. They hired us to build a revolutionary new
platform, an e-commerce marketplace platform where users can publish products for sale and
also buying other users products. We are expecting a huge number of users from day one. So we
need to build a high scalable, reliable and high performance platform. So we are choosing you to
help building this solution.

## Architecture
The solution will be splitted in tiny microservices which will be responsible for processing 
single entities operations. Every microservice consists of a main Verticle that deploys a REST 
endpoint and registers it on service discovery.

Every API call will go through an API Gateway, which is currently responsible for load balancing 
and controlling fault tolerance (through circuit-breaker), as well as service discovery.

The stack used in this project contains:
* Java
* Vert.x
* PostgreSQL
* Redis (kind of pending)
* Gradle
* JUnit (pending)
* Docker (pending)

## Progress
![api-gateway](https://progress-bar.dev/90?title=api-gateway)
![product-microservice](https://progress-bar.dev/80?title=product-microservice)
![order-microservice](https://progress-bar.dev/10?title=order-microservice)
![user-microservice](https://progress-bar.dev/10?title=user-microservice)
![authorization-service](https://progress-bar.dev/0?title=authorization-service)
![redis-cache](https://progress-bar.dev/0?title=redis-cache)
![docker](https://progress-bar.dev/0?title=docker)
![logging](https://progress-bar.dev/0?title=logging)
![monitoring](https://progress-bar.dev/0?title=monitoring)
![tests](https://progress-bar.dev/0?title=tests)