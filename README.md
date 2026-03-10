# NexusDB

A lightweight async database library for **PowerNukkitX** plugins. Runs database operations on a separate thread — keeping your main server thread clean and lag-free.

---

## Features

- ✅ **H2** (embedded, zero-config) support
- ✅ **MySQL** (remote) support
- ⚡ Async query execution via external thread
- 🔜 MainThread `Consumer` support *(coming soon)*

---

## How It Works

NexusDB offloads all SQL operations to a dedicated worker thread. You submit a `Runnable` task and NexusDB handles the execution asynchronously, so your plugin never blocks the server's main tick loop.

> ⚠️ **Note:** Currently only supports running tasks inside an **external thread**. MainThread `Consumer` support is planned for an upcoming update.

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
NexusDB db = new NexusDB(DatabaseType.H2, "path/to/database");
```

### MySQL Connection

```java
NexusDB db = new NexusDB(DatabaseType.MYSQL, "host", port, "database", "user", "password");
```

---

## Roadmap

- [x] H2 support
- [x] MySQL support
- [x] Async execution (external thread)
- [ ] MainThread Consumer support
- [ ] Query result callbacks

---

## Requirements

- Java 17+
- PowerNukkitX

---

## License

MIT