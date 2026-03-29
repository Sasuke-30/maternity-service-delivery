# Maternity Service Delivery System

Spring Boot API for maternity ward operations:
- patient registration
- appointment booking with conflict detection
- ward bed management
- staff and shifts
- antenatal and delivery records
- high-risk alerts

## Default Login Credentials

HTTP Basic auth is enabled for all `/api/**` routes.

- Username: `admin@hospital.local`
- Password: `Admin@123`

Secondary user:
- Username: `doctor@hospital.local`
- Password: `Doctor@123`

## Local Run

Use Java 17 and Maven:

```bash
mvn spring-boot:run
```

API base:
- `http://localhost:8080/api`

Health:
- `http://localhost:8080/actuator/health`

## Render Deploy

This repo includes:
- `Dockerfile`
- `render.yaml`

Render will expose the app on a public URL after deployment.

