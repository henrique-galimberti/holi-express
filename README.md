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

Every API call will go through an API Gateway, which is currently responsible for: 
* Load Balancing 
* Fault Tolerance (through circuit-breaker)
* Service Discovery
* Authentication and Session Handling

The stack used in this project contains:
* Java
* Vert.x
* PostgreSQL
* Redis
* Gradle
* JUnit
* Docker

## Usage (Base Requirements + User Stories)
#### Register user (Base Requirement):
REQUEST:
```sh
POST /api/user/add HTTP/1.1
Content-Type: application/json

{
    "username": "henrique.galimberti",
    "password": "123456",
    "name": "Henrique Galimberti"
}
```
RESPONSE:
```sh
{
  "message": "user_added",
  "id": 5
}
```
EXPLANATION:
This is a straight forward method. It just stores the user.
The only validation done here is username uniqueness.
#### Register product (Base Requirement):
REQUEST:
```sh
POST /api/product/add HTTP/1.1
Content-Type: application/json
Cookie: vertx-web.session=6944d0ccd5c7a4cf6e15f59c1f4a1792

{
    "product": {
        "image": "data:image/png;base64,iVBORw0KGgoAAAA...",
        "name": "Cafeteira Oster Prima Latte 2",
        "price": 950.00,
        "sellerId": 1,
        "type": "Eletrodoméstico"
    },
    "stock": 5
}
```
RESPONSE:
```sh
{
  "message": "product_added",
  "id": 2
}
```
EXPLANATION:
This method will store the product and 
then call stock microservice to update the product stock.
It also requires previous authentication.
#### Buy product (Base Requirement):
```sh
POST /api/order/add HTTP/1.1
Content-Type: application/json
Cookie: vertx-web.session=bcbf269ccb8b174727b919dbbf1bb315

{
    "buyerId": 1,
    "productId": 10,
    "value": 999.99
}
```
RESPONSE:
```sh
{
  "message": "product_out_of_stock"
}
```
EXPLANATION:
The method simply registers an order with a 'payment_pending' state.
In order for it to advance in its flow, it would be necessary to register
a payment, then the payment api would notify our microservice and it would then
notify the order to advance to delivery state (which would also need a notification to then complete the order).
It also validates product stock (and decreases it in every order). 
#### Compare prices (User Story #2):
```sh
GET /api/product/compare HTTP/1.1
Content-Type: application/json

{
    "id": 2,
    "image": "data:image/png;base64,iVBORw0KGgoAAAA...",
    "name": "Cafeteira Oster",
    "price": 999.99,
    "sellerId": 1,
    "type": "Eletrodoméstico"
}
```
RESPONSE:
```sh
[
  {
    "id": 3,
    "sellerId": 1,
    "name": "Cafeteira Tramontina",
    "price": 800.0,
    "image": "data:image/png;base64,iVBORw0KGgoAAAA...",
    "type": "Eletrodoméstico"
  },
  {
    "id": 4,
    "sellerId": 1,
    "name": "Cafeteira Oster Prima Latte",
    "price": 900.0,
    "image": "data:image/png;base64,iVBORw0KGgoAAAA...",
    "type": "Eletrodoméstico"
  }
]
```
EXPLANATION:
The method fetches some products with the same type of our product (param) and lower price.
It then sorts then by number of matching words with our product name.
Before returning, it limits our result to 10 products. 
#### Authentication (User Story #5):
REQUEST:
```sh
POST /auth HTTP/1.1
Content-Type: application/json

{
    "username": "henrique.galimberti",
    "password": "123456"
}
```
RESPONSE (success):
```sh
set-cookie vertx-web.session=061a8bde735fe29274fe4a4533a9d8f8; Path=/

{
  "message" : "authentication_success",
  "user-principal" : {
    "username" : "henrique.galimberti"
  }
}
```
RESPONSE (failure):
```sh
{
  "error": "Invalid username/password"
}
```
EXPLANATION:
A simple JDBCAuth.

## Disclaimer
As per instructions:

"There’s no right or wrong, we want to evaluate your 
way of thinking, your java knowledge, libs and frameworks
 that you use, your architectural choices and software design skills."

So, in this project it was aimed to fulfill the base requirements as well as 
showing the skills described in instructions.

That being said, there are several features that were not addressed in this solution, as per example:
* RDBMS otimizations (indexes, foreign keys, ...)
* Complementary fields for entities (such as address for users, description for products and so on)
* Front-end ;)
* Payment and delivery integration (which will lead to a never completing order)
* Other (maybe essential) features for an e-commerce that were not in the base requirements


## Progress
![api-gateway](https://progress-bar.dev/100?title=api-gateway)
![product-microservice](https://progress-bar.dev/100?title=product-microservice)
![order-microservice](https://progress-bar.dev/100?title=order-microservice)
![user-microservice](https://progress-bar.dev/100?title=user-microservice)
![payment-microservice](https://progress-bar.dev/100?title=payment-microservice)
![authorization-service](https://progress-bar.dev/100?title=authorization-service)
![redis-cache](https://progress-bar.dev/0?title=redis-cache)
![docker](https://progress-bar.dev/80?title=docker)
![logging](https://progress-bar.dev/100?title=logging)
![monitoring](https://progress-bar.dev/0?title=monitoring)
![tests](https://progress-bar.dev/5?title=tests)