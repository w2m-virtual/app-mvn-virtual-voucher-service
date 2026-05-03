# app-mvn-virtual-voucher-service

W2M Virtual · voucher service (estado **S9 VOUCHER_DELIVERED**).

Tras una reserva confirmada y un pago capturado, este servicio emite el voucher:
genera un **PDF** con OpenPDF y lo envía por **email** (SMTP) al titular. Si el envío falla
el voucher queda persistido con estado `FAILED_DELIVERY` para descarga directa.

## Stack

- Spring Boot 3.5.5, Java 24
- Hexagonal puro (módulo `voucher` dominio + `app` bootstrap)
- OpenPDF 2.0.3
- spring-boot-starter-mail
- Persistencia in-memory (`ConcurrentHashMap`)
- mailpit como SMTP de desarrollo

## Arrancar

```bash
docker compose up -d           # mailpit en 1025 (SMTP) + 8025 (web UI)
mvn -DskipTests package
java -jar app/target/app-0.1.0-SNAPSHOT.jar
```

Servicio en http://localhost:8085. Mailpit web UI en http://localhost:8025.

## Endpoints

| Verb | Path | Descripción |
| --- | --- | --- |
| POST | `/api/vouchers/issue` | Genera PDF + envía email + persiste |
| GET | `/api/vouchers/{voucherId}` | Metadatos del voucher (sin bytes) |
| GET | `/api/vouchers/{voucherId}/pdf` | Descarga el PDF (`application/pdf`) |
| GET | `/api/vouchers/by-booking/{bookingId}` | Voucher asociado a una reserva |
| GET | `/actuator/health` | Health probe |

## Configuración relevante

```yaml
spring.mail.host: ${MAIL_HOST:localhost}
spring.mail.port: ${MAIL_PORT:1025}
voucher.from:     noreply@w2m-virtual.local
server.port:      8085
```
