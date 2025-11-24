# **Nexus Finance \- Backend Core 🏦**

**Núcleo transaccional de la plataforma Nexus Finance. Gestiona el ciclo de vida de clientes y créditos mediante una arquitectura distribuida y orientada al dominio.**

## **🏗️ Arquitectura y Diseño**

Este proyecto sigue estrictamente los principios de **Domain-Driven Design (DDD)** y **Clean Architecture** (Puertos y Adaptadores).

### **Estructura de Bounded Contexts**

El sistema está modulado en contextos delimitados para garantizar alta cohesión:

1. **Client Context:** Gestión de perfiles e identidad.  
   * Uso de **Value Objects** (Dni, EmailAddress) para validación intrínseca.  
2. **Loan Context:** Motor financiero.  
   * **CQRS:** Separación de Comandos (Escritura) y Queries (Lectura).  
   * **Integration (ACL):** Capa anticorrupción para comunicarse con el *Risk Engine* (Python).  
   * **Domain Services:** Cálculo de cronogramas de pago (Sistema de Amortización Francés).

## **🛠️ Stack Tecnológico**

| Componente | Tecnología | Versión | Propósito |
| :---- | :---- | :---- | :---- |
| **Lenguaje** | Java | **21 (LTS)** | Uso de Records, Pattern Matching y Virtual Threads. |
| **Framework** | Spring Boot | **3.4.x** | Inyección de dependencias y configuración automática. |
| **Persistencia** | Spring Data JPA | \- | Abstracción de acceso a datos. |
| **Base de Datos** | MySQL | **8.0** | Almacenamiento relacional robusto. |
| **Integración** | Spring Cloud OpenFeign | \- | Cliente HTTP declarativo para microservicios. |
| **Build Tool** | Maven | 3.9+ | Gestión de dependencias y ciclo de vida. |

## **✨ Funcionalidades Clave Implementadas**

### **👥 Gestión de Clientes**

* Registro de clientes con validación fuerte de datos.  
* Búsqueda optimizada por correo electrónico.

### **💰 Gestión de Préstamos (Loan Lifecycle)**

1. **Solicitud (Request):** Registro de intención de crédito.  
2. **Evaluación de Riesgo Híbrida:**  
   * El sistema consulta al microservicio **Nexus Risk Engine** (Python) antes de aprobar.  
   * Implementa un **Gateway/ACL** para traducir los modelos de dominio Java a DTOs externos.  
3. **Aprobación y Amortización:**  
   * Si el riesgo es aceptable, el sistema calcula automáticamente la **Tabla de Amortización (Sistema Francés)**.  
   * Generación de cuotas (Capital \+ Interés) con precisión BigDecimal.  
   * Transición de estado a ACTIVE de forma atómica.

## **🚀 Despliegue Local (Docker)**

El proyecto está diseñado para ser orquestado junto con su base de datos y el motor de riesgo.

```bash
# Desde la raíz del proyecto (donde está docker-compose.yml)
docker-compose up --build
```

### **Documentación API (Swagger)**

Una vez levantado, accede a la documentación interactiva:

* 👉 **Swagger UI:** [https://localhost:8080/swagger-ui.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui.html)

## **👤 Autor**

**Angel Antonio Cancho Corilla** \- *Software Engineer (Java/Angular Specialist)*