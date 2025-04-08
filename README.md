📊 Microservicio Report-SERVICE - Spring Boot

📖 Descripción

El Microservicio Report es un componente del sistema de gestión de citas médicas desarrollado en Spring Boot. Este microservicio se encarga específicamente de la generación y gestión de reportes, siguiendo la arquitectura de microservicios con bases de datos independientes.

🚀 Tecnologías Utilizadas
•	Java 17
•	Spring Boot 3.2.2 (Web, Data JPA, Security, Cloud Config, Eureka Client)
•	PostgreSQL 14
•	Docker & Docker Compose
•	Maven

📦 Dependencias
•	spring-boot-starter-actuator - Monitoreo y métricas
•	spring-boot-starter-data-jpa - Persistencia de datos
•	spring-boot-starter-web - API REST
•	spring-boot-starter-security - Seguridad
•	spring-cloud-starter-netflix-eureka-client - Registro de servicio
•	spring-cloud-starter-config - Configuración centralizada
•	postgresql - Base de datos
•	lombok - Reducción de código boilerplate
•	springdoc-openapi - Documentación API

⸻

🏗️ Arquitectura

Este microservicio sigue el patrón “Database per Service”, donde cada microservicio tiene su propia base de datos independiente. Este enfoque proporciona:
•	Aislamiento de datos: Los datos de reportes se mantienen en una base de datos dedicada
•	Escalabilidad independiente: El servicio puede escalar según sus propias necesidades
•	Resiliencia: Los fallos en otros servicios no afectan la disponibilidad de los datos de reportes

🛠️ Configuración del Entorno

Antes de ejecutar el microservicio, asegúrate de tener instalado:

✅ Docker y Docker Compose
✅ JDK 17 o superior
✅ Maven
✅ IntelliJ IDEA

⸻

⚙️ Pasos para ejecutar el proyecto

🔹 1. Configurar el Config Server (Opcional)
cd config-server
mvn clean package -DskipTests
cd ..

🔹 2. Compilar el proyecto
mvn clean package -DskipTests

🔹 3. Levantar con Docker Compose
docker-compose up -d

🔹 4. Verificar el estado de los servicios
docker-compose ps

🔹 5. Comprobar la salud del servicio
curl http://localhost:8083/actuator/health

🔹 6. Swagger
http://localhost:8083/api/swagger-ui/index.html

Config server (UP)
Hacer click en el botón RUN de IntelliJ

🔹 7. Ejecutar en modo desarrollo (Opcional)

📡 Endpoints API REST

Una vez iniciado el servicio, accede a la documentación Swagger:

🔹 Swagger UI
🔹 OpenAPI JSON

👤 Autor

📝 Carlos Lott

📜 Licencia

Este proyecto está bajo la licencia MIT.

