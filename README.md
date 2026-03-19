# WasherDryerBackend

A Java-based RESTful backend for managing laundry machine reservations. Users can register, log in, view available washers/dryers, reserve machines, and report issues. Email reminders are sent automatically via a scheduled job when a reservation ends.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 21+ (tested with JDK 25) |
| MySQL | 5.7+ or AWS RDS MySQL |
| VSCode | Latest |

> Tomcat 10.1 and Maven 3 are bundled in `tomcat/` and `maven/` — no separate installs needed.

**Required environment variables** (set once at user level, VSCode tasks depend on them):

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

## Setup

### 1. Configure Environment Variables

Copy the env template and fill in your values:

```bash
cp .env.example .env
```

Edit `.env`:

```bash
DB_HOST=your-rds-endpoint    # required
DB_USER=your-username        # required
DB_PASS=your-password        # required
DB_PORT=3306                 # optional, defaults to 3306
DB_NAME=washerproject        # optional, defaults to washerproject

SMTP_FROM=your@gmail.com     # required for email notifications
SMTP_PASSWORD=your-app-password # required — use a Gmail App Password, not your account password

MANAGER_EMAIL=manager@example.com  # receives machine issue reports
CORS_ORIGIN=http://localhost:3000  # allowed frontend origin
```

> `.env` is gitignored — never commit it. See [.env.example](.env.example) for the template.

### 2. Initialize the Database

Run via the VSCode task **Terminal → Run Task → Init DB**, or manually:

```bash
./maven/bin/mvn compile
./maven/bin/mvn exec:java -Dexec.mainClass="db.MySQLTableCreation"
```

This creates four tables: `user`, `background`, `item`, and `reservation`.

### 3. Build

Press `Ctrl+Shift+B` in VSCode, or run manually:

```bash
./maven/bin/mvn clean package -DskipTests
```

This produces `target/washer.war`.

### 4. Run & Debug

Press `F5` — VSCode will:
1. Start Tomcat with debug port 8000 open
2. Wait until the server is ready
3. Auto-attach the Java debugger

Set breakpoints anywhere before or after pressing `F5`. To stop Tomcat, run **Terminal → Run Task → Stop Tomcat**.

The app is available at `http://localhost:8080/washer`.

---

## VSCode Tasks

| Task | Shortcut | Description |
|------|----------|-------------|
| Build | `Ctrl+Shift+B` | `mvn clean package -DskipTests` |
| Build (with tests) | Run Task menu | `mvn clean package` |
| Start Tomcat (Debug) | triggered by `F5` | Starts Tomcat with JDWP debug agent on port 8000 |
| Stop Tomcat | Run Task menu | Shuts down the local Tomcat instance |
| Init DB | Run Task menu | Creates DB tables and seeds test data |

---

## Project Structure

```
WasherDryerBackend/
├── pom.xml                          # Maven build & dependencies
├── .env                             # Local credentials (gitignored — never commit)
├── .env.example                     # Credential template (committed)
├── tomcat/                          # Bundled Tomcat 10.1 server (gitignored)
├── maven/                           # Bundled Maven 3 build tool (gitignored)
├── .vscode/
│   ├── launch.json                  # F5 debug config — attaches to Tomcat
│   └── tasks.json                   # Build, Start/Stop Tomcat, Init DB tasks
├── src/
│   └── main/
│       ├── java/
│       │   ├── db/
│       │   │   ├── MySQLConnection.java      # All SQL queries (users, items, reservations)
│       │   │   ├── MySQLDBUtil.java          # DB connection URL / credentials
│       │   │   ├── MySQLTableCreation.java   # One-time DB schema setup
│       │   │   └── test.java                # Ad-hoc DB test
│       │   ├── entity/
│       │   │   └── Item.java                # Machine model (builder pattern)
│       │   ├── notify/
│       │   │   ├── Reminder.java            # Quartz job: sends email when reservation ends
│       │   │   └── SendEmail.java           # JavaMail email sender
│       │   └── rpc/
│       │       ├── AddMachine.java          # POST /addMachine
│       │       ├── ChangeMachineStatus.java # POST /changeMachineStatus
│       │       ├── GetAllMachine.java       # GET  /getAllMachines
│       │       ├── GetMachinesByUserId.java # GET  /getMachinesByUserId
│       │       ├── Login.java               # POST/GET /login
│       │       ├── Logout.java              # GET  /logout
│       │       ├── Register.java            # POST /register
│       │       ├── RpcHelper.java           # Shared JSON request/response helpers
│       │       ├── SendingMessageToManager.java # POST /report
│       │       └── Test.java                # Smoke-test servlet
│       └── webapp/
│           ├── index.jsp
│           └── WEB-INF/
│               └── web.xml                  # Servlet mappings
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
  "end_time": "2024-01-01 12:00:00"
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
