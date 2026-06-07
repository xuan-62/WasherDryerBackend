# Project Structure

## Directory Tree

```
WasherDryerBackend/
├── pom.xml                                      # Maven build, dependencies, packaging (WAR)
├── README.md                                    # Setup, API reference, prerequisites
├── PROJECT_STRUCTURE.md                         # This file
│
├── src/main/
│   ├── java/
│   │   ├── db/
│   │   │   ├── MySQLDBUtil.java                 # DB connection URL — reads from environment variables
│   │   │   ├── MySQLConnection.java             # All SQL operations (users, items, reservations)
│   │   │   ├── MySQLTableCreation.java          # One-time schema creation + seed data (run as Java app)
│   │   │   └── test.java                        # Ad-hoc DB connectivity test
│   │   │
│   │   ├── entity/
│   │   │   └── Item.java                        # Machine data model (builder pattern)
│   │   │
│   │   ├── notify/
│   │   │   ├── Reminder.java                    # Quartz scheduled job — fires when reservation ends
│   │   │   ├── SendEmail.java                   # JavaMail email sender
│   │   │   ├── AutoChangeStatus.java            # Auto-updates machine status after reservation expires
│   │   │   └── NewStatus.java                   # Status change event model
│   │   │
│   │   └── rpc/
│   │       ├── RpcHelper.java                   # Shared helpers: parse JSON request body, write response
│   │       ├── Register.java                    # POST /register
│   │       ├── Login.java                       # POST /login (authenticate), GET /login (session check)
│   │       ├── Logout.java                      # GET  /logout
│   │       ├── AddMachine.java                  # POST /addMachine
│   │       ├── GetAllMachine.java               # GET  /getAllMachines
│   │       ├── GetMachinesByUserId.java          # GET  /getMachinesByUserId
│   │       ├── ChangeMachineStatus.java         # POST /changeMachineStatus
│   │       ├── RemindUser.java                  # POST /remindUser
│   │       ├── SendingMessageToManager.java     # POST /report
│   │       └── Test.java                        # Smoke-test servlet  GET /test
│   │
│   └── webapp/
│       ├── index.jsp                            # Default landing page
│       └── WEB-INF/
│           └── web.xml                          # Servlet declarations and URL mappings
│
└── target/                                      # Maven build output (git-ignored)
    ├── washer.war                               # Deployable WAR artifact
    └── washer/WEB-INF/
        ├── classes/                             # Compiled .class files
        └── lib/                                 # All runtime JARs
```

---

## Config Files

| File | Purpose | Edit for |
|------|---------|----------|
| [src/main/java/db/MySQLDBUtil.java](src/main/java/db/MySQLDBUtil.java) | Builds the JDBC connection URL from environment variables | DB credentials — set via env vars, not in this file |
| [src/main/webapp/WEB-INF/web.xml](src/main/webapp/WEB-INF/web.xml) | Maps servlet classes to URL patterns | Adding/removing API endpoints |
| [pom.xml](pom.xml) | Maven dependencies, Java version, build output name | Adding libraries, changing Java version |

---

## Environment Variables (DB Credentials)

Credentials are **not stored in any file**. Set these before starting Tomcat:

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DB_HOST` | Yes | — | MySQL hostname or RDS endpoint |
| `DB_USER` | Yes | — | MySQL username |
| `DB_PASS` | Yes | — | MySQL password |
| `DB_PORT` | No | `3306` | MySQL port |
| `DB_NAME` | No | `washerproject` | Database name |

---

## Database Schema

Managed by [src/main/java/db/MySQLTableCreation.java](src/main/java/db/MySQLTableCreation.java). Run once to initialize.

| Table | Key Columns |
|-------|-------------|
| `user` | `user_id`, `email`, `password` |
| `background` | `user_id`, `first_name`, `last_name`, `about_me`, `email` |
| `item` | `item_id`, `type`, `address`, `user_id`, `item_condition`, `model`, `brand` |
| `reservation` | `user_id`, `item_id`, `start_time`, `end_time` |
