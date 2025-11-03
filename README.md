# ecommerce-product-service.

Product Service para sistema de ecommerce.

## Características

- Spring Boot 2.5.7 con Java 11
- Base de datos: H2 (dev) / MySQL (stage/prod)
- Service Discovery: Eureka Client
- Actuator para health checks
- **Soft Delete:** Los productos no se eliminan físicamente
- **Categorías Reservadas:** "Deleted" y "No category" protegidas
- **Validaciones:** Campos requeridos en productos

## Endpoints

```
GET    /product-service/api/products       - Listar productos (sin eliminados)
GET    /product-service/api/products/{id}  - Obtener producto (sin eliminados)
POST   /product-service/api/products       - Crear producto
PUT    /product-service/api/products       - Actualizar producto
DELETE /product-service/api/products/{id}  - Soft delete de producto

GET    /product-service/api/categories     - Listar categorías (sin reservadas)
GET    /product-service/api/categories/{id}- Obtener categoría (sin reservadas)
POST   /product-service/api/categories     - Crear categoría
PUT    /product-service/api/categories     - Actualizar categoría
DELETE /product-service/api/categories/{id}- Eliminar categoría (migra productos)
```

## Testing

### Unit Tests (21) ✅
- ProductServiceImpl: 10 tests
- CategoryServiceImpl: 11 tests

### Integration Tests (19) ✅
- ProductServiceIntegrationTest: 10 tests
- CategoryServiceIntegrationTest: 8 tests

### Helper & Exception Tests (17) ✅
- ProductMappingHelper: 5 tests
- CategoryMappingHelper: 6 tests
- ApiExceptionHandler: 6 tests

**Total: 57 tests - Todos pasando ✅**

```bash
./mvnw test
```

## Ejecutar

```bash
# Opción 1: Directamente
./mvnw spring-boot:run

# Opción 2: Compilar y ejecutar
./mvnw clean package
java -jar target/product-service-v0.1.0.jar
```

Service corre en: `http://localhost:8500/product-service`

## Pruebas en Postman

Ver `POSTMAN_TESTS.md` para guía completa de pruebas o importar `ProductService.postman_collection.json`

### Endpoints Críticos a Probar:

1. **GET** `/api/categories` → Debe mostrar SOLO 3 categorías (sin Deleted/No category)
2. **GET** `/api/products` → Debe mostrar productos sin eliminados
3. **DELETE** `/api/products/1` → Soft delete (producto ya no aparece en lista)
4. **DELETE** `/api/categories/2` → Migra productos a "No category"
5. **DELETE** `/api/categories/5` → Debe fallar (categoría reservada)

## Funcionalidades Implementadas

✅ **Soft Delete:** Los productos eliminados se mueven a categoría "Deleted"  
✅ **Categorías Reservadas:** "Deleted" y "No category" protegidas del sistema  
✅ **Migración Automática:** Al eliminar categoría, productos → "No category"  
✅ **Validaciones:** Campos requeridos en productos  
✅ **Queries Personalizados:** Exclusión de eliminados/reservados  
...