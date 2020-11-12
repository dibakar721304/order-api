# order-api description
An API where a valid user can place an order.
The order-api allows a client to use a REST-ful API to place orders against a simple product catalog of fruit and to query those orders. The order-api validates the items ordered and the quantities available. It also maintains inventory quantities as orders are placed. It stores orders that fail due to one or more items in the order being unknown or unavailable.

# Work flow
The order-api is a spring boot application backed by an in-memory database (H2). The product catalog has been preloaded (src/main/resources/schema.sql and data.sql). Two accounts have also been preloaded. Restarting the application clears the database of any orders placed and reinitializes the inventory and accounts tables.

Order placement and querying is secured by API key. Each client has his own API key, and can retrieve his own orders.

## The data model
An `Order` belongs to an `Account` and can have zero or more `OrderItem`s. An order can be in one of two statuses:
* open: all SKUs on the order are in stock and the order has been placed.
* failed: one or more SKUs are not available in sufficient quantities to fulfill the order. An `OrderItem` without a price or an extended price indicates that the requested SKU is not available. Any `Order` without `OrderItems` is also set to "failed" status.

`Inventory` maintains a record of the current product catalog: SKU, price, and available quantity.

`Order`s are mapped using Hibernate and accessed using JPA. `Inventory` and `Account`s are POJOs accessed using direct JDBC.

# How to run it
The order-api is built using maven and runs as a standalone JAR or we can use postman to hit API.To build and run locally:

cd into the project root and build the project:

`mvn clean install`

Launch the application:

`mvn spring-boot:run`

The order-api runs on http://localhost:8080.

# How to use the API
The API is secured by API key. Two keys are provided:

* Password1: API key for account The User1.
* Password2: API key for account The User2.

The key must be provided in a custom request header named `order-api-key`.

## Available Endpoints


### /api/v1/orders (GET)
Returns a list of orders this account has placed.

Example:
`curl -H "order-api-key: Password1" http://localhost:8080/api/v1/orders`

The list can also be filtered to return orders in a given status (`open` or `failed`); e.g.,

`curl -H "order-api-key: Password1" http://localhost:8080/api/v1/orders?status=open`

### /api/v1/orders (POST)
Creates a new order.

Example:
````
curl -H "Content-Type: application/json" \
-H "order-api-key: Password1" http://localhost:8080/api/v1/orders \
-d '{"orderItems": [{"sku": "Orange", "qty":2}, {"sku": "Bananas", "qty":4}]}'
````

### /api/v1/orders/{id} (GET)
Returns the order with the given id. If the order doesn't exist (or doesn't belong to the existing account), the response is a 404.

Example:

`curl -H "order-api-key: Password1" http://localhost:8080/api/v1/orders/1`

To view the complete order, request expansion of the `orderItems` collection:

`curl -H "order-api-key: Password1" http://localhost:8080/api/v1/orders/1?expand=orderItems`


