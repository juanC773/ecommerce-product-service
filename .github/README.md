# GitHub Actions para Product Service

Este directorio contiene los workflows de CI/CD específicos para el Product Service.

## Estructura

Cada microservicio es un **repositorio Git independiente**, por lo que tiene su propio `.github/workflows/`.

## Workflows

### `ci-cd-dev.yml`
Pipeline para el ambiente de desarrollo que:
1. Compila y ejecuta pruebas
2. Construye y publica imagen Docker
3. Despliega en Kubernetes (dev)

## Configuración Requerida

### Secrets en GitHub

1. **AZURE_CREDENTIALS**: Service Principal JSON de Azure
2. **AZURE_RESOURCE_GROUP**: Nombre del resource group
3. **AZURE_AKS_CLUSTER**: Nombre del cluster AKS
4. **INFRA_REPO**: Repositorio donde está `ecommerce-infra` (ej: `usuario/ecommerce-infra`)
5. **INFRA_REPO_TOKEN**: Token para acceder al repo de infraestructura (opcional si es público o mismo org)

### Repositorio de Infraestructura

Los manifests de Kubernetes deben estar en un repositorio separado `ecommerce-infra` con la estructura:
```
ecommerce-infra/
  k8s/
    product-service/
      deployment.yaml
      configmap.yaml
    namespace.yaml
    ...
```

Si el repositorio de infraestructura está en la misma organización y es privado, puedes omitir el token.

## Uso

El workflow se ejecuta automáticamente al hacer push a las ramas `develop` o `dev`.

También puedes ejecutarlo manualmente desde la pestaña "Actions" en GitHub.


test ahora con el cambio en recursos