# WasherDryerBackend

A Java-based RESTful backend for managing laundry machine reservations. Users can register, log in, view available washers/dryers, reserve machines, and report issues. Email reminders are sent automatically via a scheduled job when a reservation ends.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 8 |
| Apache Maven | 3.6+ |
| Apache Tomcat | 9.0 |
| MySQL | 5.7+ or AWS RDS MySQL |
| VSCode | Latest |

**Recommended VSCode Extensions:**

| Extension | Purpose |
|-----------|---------|
| Extension Pack for Java (Microsoft) | Language support, debugger, Maven |
| Community Server Connectors (Red Hat) | Run/debug Tomcat from VSCode |
| XML (Red Hat) | Syntax support for `web.xml`, `pom.xml` |

---

## Setup

### 1. Configure Database Credentials

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
```

> `.env` is gitignored — never commit it. See [.env.example](.env.example) for the template.

VSCode loads `.env` automatically via [.vscode/launch.json](.vscode/launch.json) when you run the app.

### 2. Initialize the Database

Run `MySQLTableCreation.java` to create all tables and seed a test user:

```bash
mvn compile
mvn exec:java -Dexec.mainClass="db.MySQLTableCreation"
```

This creates four tables: `user`, `background`, `item`, and `reservation`.

### 3. Build

```bash
mvn clean package
```

This produces `target/washer.war`.

### 4. Deploy to Tomcat

Copy `target/washer.war` to your Tomcat `webapps/` directory, then start Tomcat. The context root is `/washer`.

Alternatively, use the **Community Server Connectors** extension in VSCode to start/stop Tomcat directly from the editor.

---

## Project Structure

```
WasherDryerBackend/
├── pom.xml                          # Maven build & dependencies
├── .env                             # Local credentials (gitignored — never commit)
├── .env.example                     # Credential template (committed)
├── .vscode/
│   └── launch.json                  # VSCode launch config — loads .env automatically
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
| `mysql-connector-java 8.0.18` | MySQL JDBC driver |
| `javax.mail 1.6.2` | Email notifications |
| `quartz 2.3.0` | Scheduled reminder jobs |
| `org.json 20190722` | JSON parsing/serialization |
| `commons-io 2.7` | HTTP request body reading |
| `tomcat-catalina 9.0.30` | Servlet API |
