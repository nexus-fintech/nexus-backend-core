# **Nexus Finance \- Backend Core 🏦**

[![CI](https://github.com/nexus-fintech/nexus-backend-core/actions/workflows/ci.yml/badge.svg)](https://github.com/nexus-fintech/nexus-backend-core/actions/workflows/ci.yml)

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

## **🧪 Pruebas**

55 pruebas automatizadas (JUnit 5, Mockito y MockMvc) con **74 % de cobertura de líneas** (92 % en el contexto de préstamos). Corren en cada push con GitHub Actions y el build falla si la cobertura baja del 70 %.

| Nivel | Qué verifica |
| :---- | :---- |
| **Dominio** | `Money` y su redondeo, ciclo de vida del préstamo, validación de comandos y objetos de valor del cliente |
| **Amortización** | Cuota fija del sistema francés (10,000 al 12 % anual en 12 meses: 888.49), capital que suma exacto, préstamos al 0 % y fechas de vencimiento |
| **Servicios** | Casos de uso de clientes y préstamos con Mockito, y la traducción del ACL hacia el motor de riesgo |
| **Seguridad** | Hash con BCrypt y emisión y validación de JWT |
| **API (integración)** | Flujo real por HTTP con la cadena de seguridad y H2: login, alta de cliente, solicitud y aprobación del préstamo, y las respuestas 400, 401, 404 y 409 |

```bash
mvn verify   # pruebas + reporte de cobertura en target/site/jacoco
```

## **🚀 Despliegue Local (Docker)**

El proyecto está diseñado para ser orquestado junto con su base de datos y el motor de riesgo.

```bash
# Desde la raíz del proyecto (donde está docker-compose.yml)
docker-compose up --build
```

### **Documentación API (Swagger)**

Una vez levantado, accede a la documentación interactiva:

* 👉 **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## **👤 Autor**

**Angel Antonio Cancho Corilla** \- *Software Engineer (Java/Angular Specialist)*