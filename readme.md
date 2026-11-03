<img width="1000" height="153" alt="image" src="https://github.com/user-attachments/assets/6c40572d-6989-46bb-bc80-cd89e4ebe36d" /># Spring Cloud OpenFeign: Declarative Inter-Service Communication

## 1. Project Overview

This project demonstrates inter-service communication between two independent Spring Boot microservices using **Spring Cloud OpenFeign**.

We implement a simple Producer–Consumer architecture using two services:

1. **Product Service (Producer):** Provides product information through REST APIs.
2. **Order Service (Consumer):** Consumes product information from Product Service using Spring Cloud OpenFeign and generates an order response.

The primary objective is to understand how microservices communicate with each other using a declarative REST client.

Unlike RestTemplate, where we manually write HTTP communication code, OpenFeign allows us to define a Java interface that Spring automatically implements at runtime.

## 2. Technologies Used

* Java 17
* Spring Boot 3.5.6
* Spring Cloud 2025.0.0
* Spring Web
* Spring Cloud OpenFeign
* Maven
* Postman

## 3. Project Architecture

The project consists of two independent Spring Boot applications.

| Service         | Role     | Port | Responsibility                                  |
| --------------- | -------- | ---- | ----------------------------------------------- |
| Product Service | Producer | 8081 | Provides product information                    |
| Order Service   | Consumer | 8082 | Consumes product information and creates orders |

### Communication Architecture

```text
                  CLIENT / POSTMAN
                         |
                         |
                         | GET /api/orders/101
                         |
                         v
                +----------------------+
                |    ORDER SERVICE     |
                |      Port 8082       |
                |                      |
                |   OrderController   |
                |          |           |
                |          v           |
                |    ProductClient     |
                |    (@FeignClient)    |
                +----------------------+
                         |
                         |
                         | HTTP GET
                         | /api/products/101
                         |
                         v
                +----------------------+
                |   PRODUCT SERVICE    |
                |      Port 8081       |
                |                      |
                |   ProductController |
                +----------------------+
                         |
                         |
                         | JSON Response
                         |
                         v
                   ORDER SERVICE
                         |
                         |
                         v
                       CLIENT
```

## 4. Project Structure

### Product Service (Producer)

```text
product-service
|
|-- src/main/java
|   |
|   |-- com.example.productservice
|       |
|       |-- ProductServiceApplication.java
|       |
|       |-- controller
|       |   |
|       |   |-- ProductController.java
|       |
|       |-- model
|           |
|           |-- Product.java
|
|-- src/main/resources
|   |
|   |-- application.properties
|
|-- pom.xml
```

### Order Service (Consumer)

```text
order-service
|
|-- src/main/java
|   |
|   |-- com.example.orderservice
|       |
|       |-- OrderServiceApplication.java
|       |
|       |-- client
|       |   |
|       |   |-- ProductClient.java
|       |
|       |-- controller
|       |   |
|       |   |-- OrderController.java
|       |
|       |-- model
|           |
|           |-- Product.java
|           |-- OrderResponse.java
|
|-- src/main/resources
|   |
|   |-- application.properties
|
|-- pom.xml
```

## 5. Dependencies

### Product Service

Product Service requires Spring Web.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### Order Service

Order Service requires Spring Web and Spring Cloud OpenFeign.

First, configure the Spring Cloud BOM inside `dependencyManagement`.

```xml
<dependencyManagement>
    <dependencies>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2025.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

    </dependencies>
</dependencyManagement>
```

Add the following dependencies:

```xml
<dependencies>

    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Cloud OpenFeign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>

</dependencies>
```

**Note:** The Spring Cloud BOM manages compatible Spring Cloud dependency versions. No separate version is required for the OpenFeign starter.

## 6. Product Service (Producer)

Product Service exposes a REST API to provide product information.

### API Endpoint

```http
GET http://localhost:8081/api/products/{id}
```

Example:

```http
GET http://localhost:8081/api/products/101
```

### Sample Response

```json
{
    "id": 101,
    "name": "Laptop",
    "price": 55000.0
}
```

### Configuration

`application.properties`

```properties
spring.application.name=product-service
server.port=8081
```

Product Service does not require any OpenFeign dependency or configuration.

It simply exposes REST APIs that can be consumed by other microservices.

## 7. Order Service (Consumer)

Order Service consumes product information from Product Service using Spring Cloud OpenFeign.

### Step 1: Enable Feign Clients

In `OrderServiceApplication.java`, add:

```java
@EnableFeignClients
```

Complete implementation:

```java
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(
            OrderServiceApplication.class,
            args
        );
    }
}
```

The `@EnableFeignClients` annotation enables scanning and registration of Feign client interfaces.

### Step 2: Create ProductClient Interface

`ProductClient.java`

```java
@FeignClient(
    name = "product-service",
    url = "${product.service.url}"
)
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    Product getProduct(@PathVariable("id") int id);

}
```

The `@FeignClient` annotation declares a REST client for Product Service.

The interface method:

```java
Product getProduct(int id);
```

represents the REST API call to Product Service.

OpenFeign automatically generates the implementation of this interface at runtime.

### Step 3: Configure application.properties

```properties
spring.application.name=order-service

server.port=8082

# Product Service URL
product.service.url=http://localhost:8081
```

### Step 4: Inject ProductClient into OrderController

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final ProductClient productClient;

    public OrderController(ProductClient productClient) {

        this.productClient = productClient;
    }

    @GetMapping("/{productId}")
    public OrderResponse createOrder(
            @PathVariable int productId) {

        // Calling Product Service using OpenFeign
        Product product = productClient.getProduct(productId);

        int quantity = 2;

        double totalAmount =
                product.getPrice() * quantity;

        return new OrderResponse(
                1001,
                product,
                quantity,
                totalAmount
        );
    }
}
```

The following statement performs inter-service communication:

```java
Product product = productClient.getProduct(productId);
```

OpenFeign internally sends an HTTP GET request to Product Service and converts the JSON response into a Product Java object.

## 8. Execution Steps

Both Spring Boot applications must run independently.

### Step 1: Start Product Service

Run:

`ProductServiceApplication.java`

Product Service starts on port 8081.

### Step 2: Start Order Service

Run:

`OrderServiceApplication.java`

Order Service starts on port 8082.

### Step 3: Test Product Service

Open Postman:

```http
GET http://localhost:8081/api/products/101
```

Expected response:

```json
{
    "id": 101,
    "name": "Laptop",
    "price": 55000.0
}
```

### Step 4: Test Order Service

Open Postman:

```http
GET http://localhost:8082/api/orders/101
```

Expected response:

```json
{
    "orderId": 1001,
    "product": {
        "id": 101,
        "name": "Laptop",
        "price": 55000.0
    },
    "quantity": 2,
    "totalAmount": 110000.0
}
```

## 9. Execution Flow

When the client calls:

```http
GET http://localhost:8082/api/orders/101
```

The following operations take place:

1. Client sends a request to Order Service.
2. OrderController receives product ID 101.
3. OrderController invokes `productClient.getProduct(101)`.
4. OpenFeign internally generates and executes an HTTP GET request.
5. Product Service receives the request.
6. ProductController creates a Product object.
7. Product Service converts the Product object into JSON.
8. OpenFeign receives the JSON response.
9. OpenFeign converts JSON into a Product Java object.
10. Order Service calculates the total order amount.
11. Order Service returns the final order response to the client.

## 10. Is Eureka Server Required?

No. Eureka Server is not required in this implementation.

We have configured the Product Service URL explicitly:

```java
@FeignClient(
    name = "product-service",
    url = "${product.service.url}"
)
```

Therefore, OpenFeign directly communicates with Product Service.

### When Do We Need Eureka Server?

In a larger microservices application, multiple instances of a service may run on different servers or ports.

Spring Cloud Netflix Eureka provides service registration and discovery.

For example:

```text
                EUREKA SERVER
                 Port 8761
                      |
          -------------------------
          |           |           |
          v           v           v
      Product-1   Product-2   Product-3
       :8081       :8083       :8084
          |
          |
      Order Service
          |
          |
      ProductClient
```

Instead of specifying the URL explicitly, we can configure:

```java
@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    Product getProduct(@PathVariable("id") int id);

}
```

In this configuration:

* Eureka Server maintains the service registry.
* Product Service registers itself with Eureka.
* Order Service discovers Product Service.
* Spring Cloud LoadBalancer selects an available instance.

**Important:** When using Eureka-based discovery, do not specify the fixed `url` attribute in `@FeignClient`.

## 11. RestTemplate vs Spring Cloud OpenFeign

| Feature                    | RestTemplate                      | OpenFeign                                   |
| -------------------------- | --------------------------------- | ------------------------------------------- |
| Communication              | HTTP                              | HTTP                                        |
| Programming style          | Imperative                        | Declarative                                 |
| Implementation             | Java statements                   | Java interface                              |
| HTTP GET                   | `getForObject()`                  | `@GetMapping`                               |
| HTTP POST                  | `postForObject()`                 | `@PostMapping`                              |
| JSON conversion            | Automatic                         | Automatic                                   |
| Boilerplate code           | More                              | Less                                        |
| Eureka integration         | Requires additional configuration | Supported                                   |
| Client-side load balancing | Requires integration              | Supported through Spring Cloud LoadBalancer |

### RestTemplate Example

```java
Product product = restTemplate.getForObject(
    "http://localhost:8081/api/products/101",
    Product.class
);
```

### OpenFeign Example

```java
Product product = productClient.getProduct(101);
```

OpenFeign reduces boilerplate code and makes inter-service communication easier to understand and maintain.

## 12. Important Concepts Covered

* Microservices Architecture
* Producer–Consumer Architecture
* Inter-Service Communication
* Declarative REST Client
* Spring Cloud OpenFeign
* `@FeignClient`
* `@EnableFeignClients`
* Dependency Injection
* REST APIs
* JSON Serialization and Deserialization
* Service Discovery (conceptual introduction)
* Eureka Integration (advanced concept)

## 13. Practical Assignments

1. Add an API in Product Service to retrieve all products.
2. Create a POST API in Product Service to add a new product.
3. Use `@PostMapping` in ProductClient to invoke the POST API.
4. Add quantity as a request parameter to the Order Service API.
5. Implement exception handling when Product Service is unavailable.
6. Return HTTP 404 when a product does not exist.
7. Configure OpenFeign connection and read timeouts.
8. Integrate Eureka Server for service discovery.
9. Run multiple Product Service instances.
10. Integrate Spring Cloud LoadBalancer.

## 14. Learning Outcomes

After completing this practical, students will be able to:

1. Understand declarative inter-service communication.
2. Create and configure Feign Client interfaces.
3. Enable Feign Clients in Spring Boot applications.
4. Invoke REST APIs of another microservice without manually writing HTTP communication code.
5. Understand automatic JSON-to-Java object conversion.
6. Differentiate between RestTemplate and OpenFeign.
7. Understand how OpenFeign integrates with Eureka for service discovery.

## 15. References

* [Spring Cloud OpenFeign Documentation](https://docs.spring.io/spring-cloud-openfeign/reference/index.html)
* [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
* [Spring Boot Documentation](https://docs.spring.io/spring-boot/index.html)

---

**Author:** Atul Kumar Uttam

**Project:** Inter-Service Communication Using Spring Cloud OpenFeign

**Technologies:** Java | Spring Boot | Spring Cloud | OpenFeign | Microservices
