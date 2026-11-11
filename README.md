# Monitoreo de signos vitales con Kafka y Kafka Streams

Proyecto de grado GRA-00905 (UTS). Esqueleto de punta a punta: el simulador publica lecturas, Kafka Streams aplica la regla A (umbral crítico) y publica alertas, y el módulo de persistencia las consume. TimescaleDB y Grafana ya quedan levantados y listos para las siguientes etapas.

## Estructura

| Módulo | Qué hace |
|---|---|
| `common` | Eventos (`LecturaVital`, `Alerta`), topics, puntajes NEWS2 y serialización JSON |
| `simulator` | Genera lecturas por paciente y las publica en `lecturas` |
| `processor` | Topología de Kafka Streams: lee `lecturas`, publica en `alertas` |
| `persistence` | Consume ambos topics (por ahora solo los registra) |
| `docker/` | Esquema de TimescaleDB y datasource de Grafana |

## Primer arranque (PowerShell)

1. Crear el archivo de variables y cambiar las contraseñas:
   ```powershell
   Copy-Item .env.example .env
   notepad .env
   ```
2. Levantar la infraestructura:
   ```powershell
   docker compose up -d
   docker compose logs kafka-init
   ```
   Debe terminar con `topics listos`. Kafka UI queda en http://localhost:8085 y Grafana en http://localhost:3000.
3. Abrir la carpeta en IntelliJ IDEA (archivo `pom.xml` de la raíz) y esperar a que Maven importe las dependencias.
4. Ejecutar las pruebas: `PuntajeTest` y `TopologiaVitalesTest`.
5. Ejecutar, en este orden, `ProcesadorApplication`, `PersistenciaApplication` y `SimuladorApplication`.
6. A los 30 segundos el simulador pone en crisis al paciente `P-0001`: en Kafka UI deben aparecer mensajes en el topic `alertas`, y la consola de persistencia registrará las alertas.

## Pendiente (por requisito)

- RF-03 y RF-04: anomalías configurables y bitácora de inyecciones (simulador).
- RF-12 y RF-13: reglas B y C con ventanas (ver los TODO en `TopologiaVitales`).
- RF-15 y RF-16: enfriamiento y periodo de gracia.
- RF-19 a RF-23: escritura en TimescaleDB, idempotente y por lotes.
- RF-25 y RF-26: tableros de Grafana.
- Decidir con un ADR: garantía de procesamiento y serialización.
