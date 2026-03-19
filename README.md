# WasherDryerBackend

A Java-based RESTful backend for managing laundry machine reservations. Users can register, log in, view available washers/dryers, reserve machines, and report issues. Email reminders are sent automatically via a scheduled job when a reservation ends.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 21+ (tested with JDK 25) |
| MySQL | 8.0+ or AWS RDS MySQL (local dev: use Docker Compose) |
| VSCode | Latest |

> Tomcat 10.1 and Maven 3 are bundled in `tomcat/` and `maven/` — no separate installs needed for local dev.

**Required environment variable** (set once, VSCode tasks depend on it):

```powershell
# PowerShell — run once, then restart VSCode
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Path\To\Your\JDK', 'User')
```

To find your JDK path: `(Get-Command java).Source` — strip `\bin\java.exe` from the result.

**Required VSCode Extensions:**

| Extension | Purpose |
|-----------|---------|
| Extension Pack for Java (Microsoft) | Language support, debugger, Maven |
| XML (Red Hat) | Syntax support for `web.xml`, `pom.xml` |

---

## Quick Start (local dev)

### 1. Run setup script

Downloads Maven and Tomcat into the project folder and creates `.env` from the template:

```bash
# macOS / Linux
./setup.sh

# Windows
setup.bat
```

### 2. Configure credentials

Edit `.env` with your values:

```bash
DB_HOST=your-rds-endpoint    # required
DB_USER=your-username        # required
DB_PASS=your-password        # required
DB_PORT=3306                 # optional, defaults to 3306
DB_NAME=washerproject        # optional, defaults to washerproject

SMTP_FROM=your@gmail.com     # required for email notifications
SMTP_PASSWORD=your-app-password # required — use a Gmail App Password

MANAGER_EMAIL=manager@example.com  # receives machine issue reports and sensor alerts
CORS_ORIGIN=http://localhost:3000  # allowed frontend origin

# YoLink sensor integration (optional — app runs in manual mode if omitted)
YOLINK_CLIENT_ID=your-yolink-client-id
YOLINK_SECRET_KEY=your-yolink-secret-key
YOLINK_HOME_ID=your-yolink-home-id

# RabbitMQ (required if YoLink is enabled; defaults work with docker compose)
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASS=guest

# Heartbeat monitoring (optional; defaults shown)
HEARTBEAT_CHECK_INTERVAL_MINUTES=5   # how often to check for silent devices
HEARTBEAT_THRESHOLD_MINUTES=10       # minutes of silence before alerting
```

> `.env` is gitignored — never commit it.

### 3. Build

```bash
./maven/bin/mvn clean package
```

Or press `Ctrl+Shift+B` in VSCode.

### 4. Run & Debug

Press `F5` — VSCode will:
1. Start Tomcat with debug port 8000 open
2. Wait until server is ready
3. Auto-attach the Java debugger

On first startup, **Flyway automatically creates all database tables** — no manual `Init DB` step needed.

The app is available at `http://localhost:8080/washer`.

To stop Tomcat: **Terminal → Run Task → Stop Tomcat**.

---

## Sensor Mode (YoLink Integration)

The app has two operating modes:

- **Manual mode** (default) — users manually update machine status via the API.
- **Sensor mode** — vibration sensors automatically detect when a machine starts/stops.

### Architecture

```
YoLink Cloud (MQTT)
       │  yl-home/{homeId}/+/report
       ▼
YoLinkSubscriber  ──heartbeat + vibration events──▶  RabbitMQPublisher
                                                              │
                                                     durable queue "machine-events"
                                                              │
                                                     MachineStatusConsumer
                                                              │
                                                       MySQLConnection
                                                    (updateCondition, updateDeviceHeartbeat)
```

**Sensor mode activates automatically** when `YOLINK_CLIENT_ID` is set in `.env`. If the env var is absent or any component fails to connect, the app falls back to manual mode and logs a warning — it never fails to start.

### How vibration detection works

1. YoLink vibration sensor emits an MQTT event when the machine turns on (`alert`) or stops (`normal`).
2. `YoLinkSubscriber` maps: `alert` → `vibrating`, `normal` → `stopped`, and publishes to RabbitMQ.
3. `MachineStatusConsumer` reads the event and updates the machine's `item_condition` in the DB:
   - `vibrating` (if not already `start`) → sets condition to `start`, auto-assigns any reserved user.
   - `stopped` (if currently `start`) → sets condition to `done`.
4. Every MQTT message also publishes a `heartbeat` event to track that the sensor is alive.

### Heartbeat monitoring

A background thread runs every `HEARTBEAT_CHECK_INTERVAL_MINUTES` (default: 5). It queries for devices that have been silent for more than `HEARTBEAT_THRESHOLD_MINUTES` (default: 10). If a silent device's machine is stuck in `start`, an alert email is sent to `MANAGER_EMAIL`.

### Pairing a sensor with a machine

Set `device_id` on an `item` row to the YoLink device's ID. Machines without a `device_id` always use manual mode regardless of whether sensor mode is globally active.

---

## Docker Compose (full stack)

Spin up MySQL + RabbitMQ + app with a single command — no external services needed:

```bash
docker compose up --build
```

Services started:

| Service | Port | Notes |
|---------|------|-------|
| App (Tomcat) | 8080 | `http://localhost:8080/washer` |
| MySQL 8 | 3306 | data volume persists between restarts |
| RabbitMQ | 5672 | AMQP; management UI at `http://localhost:15672` (guest/guest) |

Flyway runs migrations on startup automatically.

To stop:

```bash
docker compose down
```

> Use `docker compose down -v` to wipe all data volumes.

---

## CI/CD

GitHub Actions runs on every push to `master`/`development` and on pull requests to `master`:

1. Spins up MySQL 8 as a service container
2. Builds with `mvn clean package` (includes all tests)
3. Uploads `washer.war` as a build artifact

See [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

---

## VSCode Tasks

| Task | Shortcut | Description |
|------|----------|-------------|
| Build | `Ctrl+Shift+B` | `mvn clean package` (includes tests) |
| Build (with tests) | Run Task menu | Same as Build |
| Start Tomcat (Debug) | triggered by `F5` | Starts Tomcat with JDWP debug agent on port 8000 |
| Stop Tomcat | Run Task menu | Shuts down the local Tomcat instance |

> The old **Init DB** task is replaced by Flyway — schema migrations run automatically on app startup.

---

## Database Migrations

Schema is managed by [Flyway](https://flywaydb.org/). Migration scripts live in:

```
src/main/resources/db/migration/
  V1__create_schema.sql        ← user, background, item, reservation tables
  V2__add_user_role.sql        ← role column on user (admin / user)
  V3__add_device_id.sql        ← device_id column on item (YoLink sensor pairing)
  V4__add_device_heartbeat.sql ← last_heartbeat column on item (sensor liveness)
```

Flyway runs automatically when the app starts (via `AppContextListener`). To add a schema change, create the next `V{n}__description.sql` — Flyway applies only new versions.

---

## Project Structure

```
WasherDryerBackend/
├── pom.xml                          # Maven build & dependencies
├── .env                             # Local credentials (gitignored — never commit)
├── .env.example                     # Credential template (committed)
├── setup.sh / setup.bat             # One-time setup: download Maven + Tomcat
├── Dockerfile                       # Multi-stage: Maven build → Tomcat runtime
├── docker-compose.yml               # MySQL + app for local full-stack dev
├── tomcat/                          # Bundled Tomcat 10.1 (gitignored)
├── maven/                           # Bundled Maven 3 (gitignored)
├── .github/
│   └── workflows/ci.yml             # GitHub Actions: build + test on push/PR
├── .vscode/
│   ├── launch.json                  # F5 debug config — attaches to Tomcat
│   └── tasks.json                   # Build, Start/Stop Tomcat tasks
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── config/
    │   │   │   └── AppConfig.java            # dotenv-java wrapper
    │   │   ├── db/
    │   │   │   ├── AppContextListener.java   # Startup: Flyway + sensor mode init
    │   │   │   ├── MySQLConnection.java      # All SQL queries
    │   │   │   ├── MySQLDBUtil.java          # DB connection URL / credentials
    │   │   │   └── MySQLTableCreation.java   # Legacy DB reset utility (manual use only)
    │   │   ├── entity/
    │   │   │   └── Machine.java              # Machine value object (record)
    │   │   ├── messaging/
    │   │   │   ├── RabbitMQPublisher.java    # AMQP publisher to "machine-events" queue
    │   │   │   └── MachineStatusConsumer.java # AMQP consumer: updates DB from sensor events
    │   │   ├── notify/
    │   │   │   ├── AutoChangeStatus.java     # Quartz job: auto-transitions machine status
    │   │   │   ├── HeartbeatMonitor.java     # Runnable: alerts manager on silent sensors
    │   │   │   ├── NewStatus.java            # Quartz job: executes the status change
    │   │   │   ├── Reminder.java             # Quartz job: sends email when reservation ends
    │   │   │   └── SendEmail.java            # JavaMail email sender
    │   │   ├── yolink/
    │   │   │   ├── YoLinkSubscriber.java     # MQTT client: subscribes to YoLink sensor events
    │   │   │   └── YoLinkTokenService.java   # OAuth token exchange with YoLink API
    │   │   └── rpc/
    │   │       ├── AddMachine.java           # POST /addMachine (admin only)
    │   │       ├── ChangeMachineStatus.java  # POST /changeMachineStatus
    │   │       ├── GetAllMachine.java        # GET  /getAllMachines
    │   │       ├── GetMachinesByUserId.java  # GET  /getMachinesByUserId
    │   │       ├── Login.java                # POST/GET /login
    │   │       ├── Logout.java               # GET  /logout
    │   │       ├── Register.java             # POST /register
    │   │       ├── RemindUser.java           # POST /remindUser
    │   │       ├── RpcHelper.java            # Shared JSON response helpers + isAdmin()
    │   │       ├── SendingMessageToManager.java # POST /report
    │   │       └── Test.java                 # Smoke-test servlet
    │   ├── resources/
    │   │   ├── db/migration/
    │   │   │   ├── V1__create_schema.sql     # Flyway: user, item, reservation tables
    │   │   │   ├── V2__add_user_role.sql     # Flyway: role column
    │   │   │   ├── V3__add_device_id.sql     # Flyway: sensor device_id column
    │   │   │   └── V4__add_device_heartbeat.sql # Flyway: last_heartbeat column
    │   │   └── logback.xml                   # Logging config (console + rolling file)
    │   └── webapp/
    │       └── WEB-INF/
    │           └── web.xml                   # Servlet mappings + Flyway listener
    └── test/
        └── java/
            ├── db/
            │   └── PasswordHashingTest.java  # BCrypt hash/verify round-trip
            ├── entity/
            │   └── MachineTest.java          # Machine record construction & JSON output
            └── rpc/
                └── RpcHelperTest.java        # buildMachine() from JSON input
```

---

## API Reference

All endpoints are prefixed with `/washer`.

### Authentication

| Method | Endpoint | Request Body | Response |
|--------|----------|--------------|----------|
| POST | `/register` | `{"user_id":"…","phone_number":"…","password":"…"}` | `{"status":"OK"}` or `{"status":"User Already Exists"}` |
| POST | `/login` | `{"user_id":"…","password":"…"}` | `{"user_id":"…","name":"…","status":"OK"}` |
| GET | `/login` | — | `{"status":"Invalid Session"}` (session check) |
| GET | `/logout` | — | `{"status":"logout successfully"}` |

### Machines

| Method | Endpoint | Request Body | Response |
|--------|----------|--------------|----------|
| GET | `/getAllMachines` | — | Array of machine objects |
| GET | `/getMachinesByUserId` | — | Array of machines reserved by session user |
| POST | `/addMachine` | `{"item_id":"…","type":"washer\|dryer","address":"…","item_condition":"available","model":"…","brand":"…"}` | `{"result":"success"}` |
| POST | `/changeMachineStatus` | `{"status":"reserve\|start\|available","item_id":"…"}` | `{"status":"OK"}` |
| POST | `/remindUser` | `{"item_id":"…","user_id":"…"}` | — |

### Reporting

| Method | Endpoint | Request Body | Response |
|--------|----------|--------------|----------|
| POST | `/report` | `{"item_id":"…","issueType":"…","issue":"…"}` | — |

### Machine Object Schema

```json
{
  "item_id": "1",
  "type": "washer",
  "address": "Building A",
  "condition": "available",
  "model": "ModelX",
  "brand": "BrandY",
  "user_id": null,
  "end_time": null
}
```

---

## Key Dependencies

| Library | Purpose |
|---------|---------|
| `com.mysql:mysql-connector-j 9.2.0` | MySQL JDBC driver |
| `org.eclipse.angus:angus-mail 2.0.3` | Email notifications (Jakarta Mail) |
| `quartz 2.3.2` | Scheduled reminder jobs |
| `org.json 20251224` | JSON parsing/serialization |
| `commons-io 2.14.0` | HTTP request body reading |
| `tomcat-catalina 10.1.52` | Servlet API (Jakarta EE 10) |
| `dotenv-java 2.3.2` | `.env` file loading |
| `logback-classic 1.5.12` | Structured logging (rolling file + console) |
| `jbcrypt 0.4` | BCrypt password hashing |
| `flyway-core 10.21.0` | Versioned database migrations |
| `org.eclipse.paho.client.mqttv3 1.2.5` | MQTT client for YoLink sensor events |
| `amqp-client 5.21.0` | RabbitMQ AMQP publisher/consumer |
