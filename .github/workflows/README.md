# GitHub Actions Workflows

Este directorio contiene los workflows de CI/CD para Product Service.

## Workflows Disponibles

### 1. `ci.yml` - Pipeline Continuo
- **Trigger**: Push/PR a cualquier rama
- **Objetivo**: Build y tests básicos

### 2. `dev.yml` - Pipeline de Desarrollo
- **Trigger**: Push/PR a `develop` o `feature/*`
- **Objetivo**: 
  - Build de aplicación
  - Pruebas unitarias
  - Pruebas de integración
  - Build de imagen Docker
  - Push a Docker Hub (tag: `dev-latest`)

### 3. `stage.yml` - Pipeline de Staging
- **Trigger**: Push a `release/*` o PR a `main`
- **Objetivo**:
  - Build de aplicación
  - Todas las pruebas
  - Build de imagen Docker
  - Push a Docker Hub (tag: `stage-latest`)
  - Pruebas E2E
  - Pruebas de rendimiento con Locust

### 4. `prod.yml` - Pipeline de Producción
- **Trigger**: Push a `main`
- **Objetivo**:
  - Build de aplicación
  - Todas las pruebas
  - Build de imagen Docker
  - Push a Docker Hub (tag: `latest`)
  - Generación automática de Release Notes
  - Deploy a Kubernetes (comentado por ahora)

### 5. `performance.yml` - Pruebas de Rendimiento
- **Trigger**: Manual o Push a `release/*`
- **Objetivo**:
  - Ejecutar Locust contra el servicio
  - Generar reportes de rendimiento

## Secrets Requeridos

Configurar estos secrets en GitHub:

- `DOCKER_USERNAME`: Usuario de Docker Hub
- `DOCKER_PASSWORD`: Contraseña de Docker Hub

## Configuración de Kubernetes

**IMPORTANTE**: Los deployments a Kubernetes están comentados porque se ejecutan localmente con Minikube.

Ver `../SETUP_MINIKUBE.md` para instrucciones de despliegue local.

Si deseas desplegar desde GitHub Actions, necesitas:
1. Un clúster Kubernetes accesible (GKE, EKS, AKS, o similar)
2. Configurar el secret `KUBE_CONFIG` con el kubeconfig del cluster
3. Descomentar las secciones de Kubernetes en los workflows

## Ejecutar Workflows Localmente

Puedes probar los workflows usando [act](https://github.com/nektos/act):

```bash
act -j build-and-test -W .github/workflows/dev.yml
```

## Estructura de Tags de Docker

- `dev-latest`: Última versión en develop
- `stage-latest`: Última versión en staging
- `latest`: Última versión en producción
- `{commit-sha}`: Versión específica por commit

