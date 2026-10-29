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

## **🔐 Seguridad**

* **Registro público solo para clientes.** El alta crea siempre una cuenta `ROLE_CLIENT`, aunque la petición pida otro rol. El primer administrador se crea al arrancar desde `NEXUS_ADMIN_USERNAME` y `NEXUS_ADMIN_PASSWORD`.
* **Cada cliente ve y cambia solo lo suyo.** Perfil, préstamos y solicitudes pasan por `@PreAuthorize` con guardias de propiedad (`ClientAccessGuard`, `LoanAccessGuard`). Aprobar préstamos y listar usuarios y roles es del administrador. Un préstamo solo se pide para un cliente que existe.
* **Login sin pistas.** Un usuario inexistente y una contraseña errónea responden igual: `401` con el mismo mensaje y en el mismo tiempo (se compara contra un hash señuelo).
* **Sin secretos en el repositorio.** La clave JWT y las credenciales de la base llegan por variables de entorno, y la aplicación no arranca con una clave de menos de 32 bytes.
* **Lo demás:** el token nunca se escribe en los logs, CORS acepta solo los orígenes configurados y de actuator solo `/actuator/health` es público.

## **🧪 Pruebas**

68 pruebas automatizadas (JUnit 5, Mockito y MockMvc) con **83 % de cobertura de líneas** (96 % en el contexto de préstamos). Corren en cada push con GitHub Actions y el build falla si la cobertura baja del 70 %.

| Nivel | Qué verifica |
| :---- | :---- |
| **Dominio** | `Money` y su redondeo, ciclo de vida del préstamo, validación de comandos y objetos de valor del cliente |
| **Amortización** | Cuota fija del sistema francés (10,000 al 12 % anual en 12 meses: 888.49), capital que suma exacto, préstamos al 0 % y fechas de vencimiento |
| **Servicios** | Casos de uso de clientes y préstamos con Mockito, y la traducción del ACL hacia el motor de riesgo |
| **Seguridad** | Hash con BCrypt, emisión y validación de JWT, y que la aplicación no arranque con una clave débil |
| **API (integración)** | Flujo real por HTTP con la cadena de seguridad y H2: login, alta de cliente, solicitud y aprobación del préstamo, y las respuestas 400, 401, 404 y 409 |
| **Acceso (integración)** | El registro no da `ROLE_ADMIN`, el login fallido no revela si el usuario existe, un cliente no lee, cambia ni pide préstamos a nombre de otro, usuarios y roles solo para el admin, el primer admin desde la configuración, CORS por origen y el token fuera de los logs |

```bash
mvn verify   # pruebas + reporte de cobertura en target/site/jacoco
```

## **⚙️ Configuración**

| Variable | Obligatoria | Para qué |
| :---- | :---- | :---- |
| `JWT_SECRET` | Sí | Firma los tokens. 32 caracteres aleatorios o más, p. ej. `openssl rand -hex 32` |
| `DB_USERNAME`, `DB_PASSWORD` | Sí | Credenciales de MySQL |
| `SPRING_DATASOURCE_URL` | No | URL de la base; por defecto `jdbc:mysql://localhost:3306/nexusdb` |
| `RISK_ENGINE_URL` | No | API del motor de riesgo; por defecto `http://localhost:8000/api/v1` |
| `NEXUS_ADMIN_USERNAME`, `NEXUS_ADMIN_PASSWORD` | No | Primer administrador, creado al arrancar si ese usuario no existe |
| `CORS_ALLOWED_ORIGINS` | No | Orígenes del navegador separados por comas; por defecto `http://localhost:4200` |

## **🚀 Despliegue Local**

La plataforma completa (MySQL, este backend y el motor de riesgo) se levanta con Docker Compose desde [nexus-finance-platform](https://github.com/nexus-fintech/nexus-finance-platform). Para correr solo el backend contra un MySQL local:

```bash
export JWT_SECRET=$(openssl rand -hex 32) DB_USERNAME=nexus DB_PASSWORD=...
export NEXUS_ADMIN_USERNAME=admin NEXUS_ADMIN_PASSWORD=...   # opcional: el primer administrador
mvn spring-boot:run
```

### **Documentación API (Swagger)**

Una vez levantado, accede a la documentación interactiva:

* 👉 **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## **👤 Autor**

**Angel Antonio Cancho Corilla** \- *Software Engineer (Java/Angular Specialist)*