# ecommerce-product-service

Product Service para sistema de ecommerce.

## Características

- Spring Boot 2.5.7 con Java 11
- Base de datos: H2 (dev) / MySQL (stage/prod)
- Service Discovery: Eureka Client
- Actuator para health checks

## Endpoints

```
GET    /product-service/api/products       - Listar productos
GET    /product-service/api/products/{id}  - Obtener producto
POST   /product-service/api/products       - Crear producto
PUT    /product-service/api/products       - Actualizar producto
DELETE /product-service/api/products/{id}  - Eliminar producto

GET    /product-service/api/categories     - Listar categorías
GET    /product-service/api/categories/{id}
POST   /product-service/api/categories
PUT    /product-service/api/categories
DELETE /product-service/api/categories/{id}
```

## Testing

### Unit Tests (38)
- ProductServiceImpl: 10 tests
- CategoryServiceImpl: 10 tests
- ProductMappingHelper: 10 tests
- CategoryMappingHelper: 8 tests

### Integration Tests (19)
- ProductServiceIntegrationTest: 9 tests
- CategoryServiceIntegrationTest: 9 tests
- ApiExceptionHandlerTest: 1 test

```bash
./mvnw test
```

## Ejecutar

```bash
./mvnw spring-boot:run
```

Service corre en http://localhost:8500/product-service
