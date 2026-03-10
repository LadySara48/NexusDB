# NexusDB

A lightweight async database library for **PowerNukkitX** plugins. Runs database operations on a separate thread — keeping your main server thread clean and lag-free.

---

## Features

- ✅ **H2** (embedded, zero-config) support
- ✅ **MySQL** (remote) support
- ⚡ Async query execution via external thread
- 🔜 MainThread `Consumer` support

---

## How It Works

NexusDB offloads all SQL operations to a dedicated worker thread. You submit a `Consumer` task and NexusDB handles the execution asynchronously, so your plugin never blocks the server's main tick loop.

---

## Supported Databases

| Database | Type     | Notes                        |
|----------|----------|------------------------------|
| H2       | Embedded | No external server needed    |
| MySQL    | Remote   | Standard JDBC connection     |

---

## Usage

### H2 Connection

```java
//If {@code true}, starts H2 in server mode — allowing multiple threads and external applications to access the same database simultaneously
//first param "queryname"
NexusDB db = new NexusDB("DataBase", "path/to/database", false);
```

### MySQL Connection

```java
//first param "queryname"
NexusDB db = new NexusDB("SQLDatabase", "host", port, "database", "user", "password");
```

### Query Adding

```java
import io.hearlov.nexus.db.cache.NexusDB;

NexusDB db = new NexusDB("moneydb", "main/money", false);
String playername = <Player>.getName();
db.addTask("SELECT * FROM money WHERE playername = ?", new Object[]{playername},
        (List<Map<String, Object>> rs) -> {}, //If you're writing ThreadSafe-compliant code, running your code here won't interrupt the progress of the MainThread
        (List<Map<String, Object>> rs) -> {}); //If ThreadSafe isn't suitable code, and it needs to run on MainThread, you can use this part
```

---

### Dont Forget!

Don't forget to add the dependencies to your plugin.<br>
plugin.yml

```
depends:
- NexusDB
```

---

## Roadmap

- [x] H2 support
- [x] MySQL support
- [x] Async execution (external thread)
- [x] MainThread Consumer support
- [x] Query result callbacks

---

## Requirements

- Java 21+
- PowerNukkitX

---

## License

MIT