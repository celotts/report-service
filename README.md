Microservicio - Sistema Médico

Este proyecto es un entorno de microservicios construido con Spring Boot, Eureka, Config Server, Docker Compose y PostgreSQL.

🏁 Servicios incluidos

registry-service: Servicio de descubrimiento Eureka.

config-service: Servidor centralizado de configuración.

patient-service: Microservicio de pacientes.

report-service: Microservicio de reportes.

patient-db: Base de datos PostgreSQL para pacientes.

🚀 Requisitos

Docker y Docker Compose

Java 17+

Maven (si vas a compilar manualmente)

📁 Estructura del Proyecto

medical-system/
├── config-service/
├── registry-service/
├── patient-service/
├── report-service/
├── infra-docker/git 
│   ├── docker-compose.yml
│   ├── .env*
│   └── scripts/

📦 Compilar microservicios (si no usás precompilados)

Desde la raíz del proyecto:

for service in registry-service config-service patient-service report-service; do
cd $service && mvn clean package -DskipTests && cd ..
done

🐳 Levantar entorno

Desde el directorio infra-docker/:

cd infra-docker

# Asegúrate que los .env existan con las variables necesarias
ls .env*  # deberías ver 6 o más

# Levanta todos los servicios
docker compose up -d --build

✅ Verificación

Eureka Dashboard: http://localhost:8761

Config Server Health: http://localhost:7777/actuator/health

Patient Service Health: http://localhost:8081/actuator/health

Report Service Health: http://localhost:8083/actuator/health

🔍 Logs útiles

docker compose logs -f config-service
docker compose logs -f registry-service

🧪 Variables de entorno

Las variables están divididas en varios archivos .env:

.env.dev: Configuración del entorno de desarrollo.

.env.secrets: Tokens, contraseñas, secretos.

.env.config-service, .env.patient-service, etc.: Variables específicas por microservicio.

🧼 Apagar todo

docker compose down -v

💡 Siguientes pasos propuestos

Añadir API Gateway (Spring Cloud Gateway)

Implementar seguridad con OAuth2 / Keycloak

Monitoreo con Prometheus + Grafana

Logging centralizado con ELK o Loki

Testing con Postman o RestAssured

