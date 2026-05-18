# System Design — Complete Interview Playbook

> **How to use:** Each topic follows: *What it is → When to use → Nuances/Gotchas → Deep dive → Comparisons.*
> Scan the **Master Cheat Sheet** at the end before any interview. Difficulty: 🟢 Fundamental → 🟡 Intermediate → 🔴 Expert.

---

## 📋 Quick-Reference Index

| # | Topic |
|---|-------|
| 1 | [Core Principles: Scalability, Reliability, Availability](#1-core-principles) |
| 2 | [Networking Fundamentals](#2-networking-fundamentals) |
| 3 | [APIs — REST, GraphQL, gRPC, WebSockets](#3-apis) |
| 4 | [Load Balancing](#4-load-balancing) |
| 5 | [Databases Deep Dive](#5-databases-deep-dive) |
| 6 | [Database Internals: Indexes, Sharding, Replication](#6-database-internals) |
| 7 | [Caching](#7-caching) |
| 8 | [Message Queues & Event Streaming](#8-message-queues--event-streaming) |
| 9 | [Storage Systems](#9-storage-systems) |
| 10 | [Consistent Hashing](#10-consistent-hashing) |
| 11 | [Microservices & Service Mesh](#11-microservices--service-mesh) |
| 12 | [Distributed Systems Concepts](#12-distributed-systems-concepts) |
| 13 | [Rate Limiting & Throttling](#13-rate-limiting--throttling) |
| 14 | [Authentication & Authorization](#14-authentication--authorization) |
| 15 | [Monitoring, Observability & Alerting](#15-monitoring-observability--alerting) |
| 16 | [System Design Patterns](#16-system-design-patterns) |
| 17 | [Classic Design Problems](#17-classic-design-problems) |
| 18 | [Master Cheat Sheet](#18-master-cheat-sheet) |

---

## 1. Core Principles

### 🟢 Scalability

**Definition:** The ability of a system to handle increased load by adding resources.

#### Vertical Scaling (Scale Up)
- Add more CPU, RAM, disk to a single machine.
- **Pros:** Simple, no code changes, no distributed system complexity.
- **Cons:** Hard physical limit. Single point of failure. Expensive at the top end. Downtime during upgrade.
- **When to use:** Databases (easier than horizontal), early-stage startups, write-heavy workloads.

#### Horizontal Scaling (Scale Out)
- Add more machines/instances to distribute the load.
- **Pros:** Virtually unlimited scale, fault tolerant, cost-effective (commodity hardware).
- **Cons:** Requires stateless design, introduces distributed system complexity (consistency, coordination).
- **When to use:** Stateless web servers, microservices, read-heavy systems.

#### 🔑 Key Interview Signal
> "How would you handle 10x traffic?" → Talk about horizontal scaling, stateless design, load balancers, read replicas.

---

### 🟢 Reliability vs Availability

| Concept | Definition | Formula | Example |
|---------|-----------|---------|---------|
| **Availability** | % of time system is operational | Uptime / (Uptime + Downtime) | 99.9% = 8.76 hrs downtime/year |
| **Reliability** | System works correctly over time | MTTF / (MTTF + MTTR) | No data loss, correct results |
| **Durability** | Data is not lost | Often 99.9999...% | S3: 11 nines durability |

**Availability Nines:**
```
99%       → 3.65 days/year downtime
99.9%     → 8.76 hours/year
99.99%    → 52.6 minutes/year
99.999%   → 5.26 minutes/year (five nines — gold standard)
99.9999%  → 31.5 seconds/year
```

**⚠️ Nuance:** A system can be highly available but unreliable (returns wrong data). Reliability implies availability. High availability ≠ high reliability.

---

### 🟢 Latency vs Throughput

| | Latency | Throughput |
|--|---------|-----------|
| **Definition** | Time for one request to complete | Requests per second (RPS) handled |
| **Unit** | milliseconds | req/s, MB/s |
| **Optimize by** | Caching, CDN, fewer hops | Parallelism, batching, queues |
| **Bottleneck** | Network round trips, DB queries | CPU, I/O bandwidth |

**Latency Numbers Every Engineer Should Know:**
```
L1 cache access:           ~0.5 ns
L2 cache access:           ~7 ns
RAM access:                ~100 ns
SSD random read:           ~150 µs
HDD random read:           ~10 ms
Network: same datacenter:  ~0.5 ms
Network: cross-region:     ~50 ms
Network: cross-continent:  ~150 ms
```

---

### 🟢 CAP Theorem (Brewer's Theorem)

> In a **distributed system**, you can only guarantee **two of three** properties simultaneously.

```
         Consistency
            /\
           /  \
          /    \
         /  CA  \
        /        \
       /----CP----|----AP
Availability    Partition
               Tolerance
```

| Property | Definition |
|----------|-----------|
| **Consistency (C)** | Every read receives the most recent write or an error. All nodes see the same data at the same time. |
| **Availability (A)** | Every request receives a response (not necessarily the latest data). |
| **Partition Tolerance (P)** | System continues despite network partitions (message loss between nodes). |

**The Real Nuance:**
- Network partitions **will happen** in distributed systems. So you always must choose P.
- The real tradeoff is: **CP** (consistency over availability) or **AP** (availability over consistency).
- **CA** only applies to single-node systems (no network partition possible).

**Real-World Mapping:**
```
CP Systems:  HBase, ZooKeeper, Etcd, MongoDB (default), Redis Cluster
AP Systems:  Cassandra, DynamoDB, CouchDB, Riak
CA Systems:  Traditional RDBMS (single-node PostgreSQL, MySQL)
```

**⚠️ Nuance:** CAP is a binary model. The real world is nuanced — PACELC extends CAP to include latency tradeoffs even when no partition exists.

---

### 🟢 ACID vs BASE

#### ACID (Traditional Databases)
| Property | Definition | Example |
|----------|-----------|---------|
| **Atomicity** | All operations in a transaction succeed or all fail. No partial updates. | Bank transfer: debit + credit both happen or neither |
| **Consistency** | Transaction brings DB from one valid state to another (constraints satisfied). | Balance can't go negative (if constrained) |
| **Isolation** | Concurrent transactions execute as if sequential. | Two transfers don't interfere |
| **Durability** | Committed transactions survive crashes. | Written to disk/WAL |

#### BASE (Distributed NoSQL Databases)
| Property | Definition |
|----------|-----------|
| **Basically Available** | System is available most of the time (AP over CP) |
| **Soft state** | State may change over time even without input (eventual replication) |
| **Eventually Consistent** | Given enough time, all replicas converge to the same value |

**When to use ACID vs BASE:**
- ACID → Financial transactions, inventory, anything where correctness > speed
- BASE → Social media feeds, user profiles, analytics, recommendation engines

---

## 2. Networking Fundamentals

### 🟢 OSI Model (7 Layers)

```
Layer 7 — Application   → HTTP, DNS, SMTP, FTP, gRPC
Layer 6 — Presentation  → TLS/SSL, encoding, compression
Layer 5 — Session       → Session management
Layer 4 — Transport     → TCP (reliable), UDP (fast, unreliable)
Layer 3 — Network       → IP, routing
Layer 2 — Data Link     → MAC, Ethernet, switches
Layer 1 — Physical      → Cables, fiber, radio waves
```

---

### 🟢 TCP vs UDP

| | TCP | UDP |
|--|-----|-----|
| **Connection** | Connection-oriented (3-way handshake) | Connectionless |
| **Reliability** | Guaranteed delivery, ordering, error checking | Best-effort, no guarantee |
| **Speed** | Slower (overhead) | Faster |
| **Use cases** | HTTP, SMTP, FTP, databases | DNS, video streaming, gaming, VoIP |
| **Flow control** | Yes (sliding window) | No |

---

### 🟢 HTTP/1.1 vs HTTP/2 vs HTTP/3

| | HTTP/1.1 | HTTP/2 | HTTP/3 |
|--|----------|--------|--------|
| **Transport** | TCP | TCP | UDP (QUIC) |
| **Multiplexing** | No (head-of-line blocking) | Yes (streams) | Yes (QUIC streams) |
| **Header compression** | No | HPACK | QPACK |
| **Server Push** | No | Yes | Yes |
| **Connection** | Multiple TCP connections | Single TCP connection | Single QUIC connection |
| **Latency** | High | Lower | Lowest (0-RTT) |

---

### 🟢 DNS (Domain Name System)

**How DNS resolution works:**
```
Browser → Local Cache → OS Cache → Recursive Resolver (ISP)
    → Root DNS (.) → TLD DNS (.com) → Authoritative DNS (example.com)
    → Returns IP → Browser caches (TTL) → Connects to IP
```

**DNS Record Types:**
| Record | Purpose | Example |
|--------|---------|---------|
| A | Domain → IPv4 | example.com → 93.184.216.34 |
| AAAA | Domain → IPv6 | |
| CNAME | Alias to another domain | www → example.com |
| MX | Mail server | mail.example.com |
| TXT | Verification, SPF, DKIM | |
| NS | Nameserver for domain | |
| SOA | Start of authority | |

**⚠️ Nuance:** Low TTL = faster propagation but more DNS queries (latency, cost). High TTL = faster lookups but slow updates.

---

### 🟢 CDN (Content Delivery Network)

**What:** Geographically distributed network of servers that cache static content closer to users.

**How it works:**
1. User requests `cdn.example.com/image.jpg`
2. DNS resolves to nearest CDN edge node (GeoDNS/Anycast)
3. Cache HIT → Return cached content
4. Cache MISS → Fetch from origin, cache, return

**Use for:** Static assets (images, JS, CSS), video streaming, software downloads, DDoS protection.

**CDN Strategies:**
- **Pull CDN:** CDN fetches from origin on first request. Simple, but cold-start latency.
- **Push CDN:** You push content to CDN proactively. Good for predictable content (video uploads).

**Popular CDNs:** Cloudflare, AWS CloudFront, Akamai, Fastly, Azure CDN.

---

## 3. APIs

### 🟢 REST (Representational State Transfer)

**Principles:**
1. **Stateless** — Each request contains all info needed. No server-side session.
2. **Client-Server** — Clear separation.
3. **Cacheable** — Responses labeled as cacheable or not.
4. **Uniform Interface** — Resource-based URLs, standard HTTP methods.
5. **Layered System** — Client doesn't know if connected directly to server or through load balancer.

**HTTP Methods:**
| Method | Action | Idempotent | Safe |
|--------|--------|-----------|------|
| GET | Read | Yes | Yes |
| POST | Create | No | No |
| PUT | Replace | Yes | No |
| PATCH | Partial update | No | No |
| DELETE | Delete | Yes | No |

**REST Best Practices:**
```
Resources are nouns, not verbs:
✓ GET    /users/{id}
✓ POST   /users
✓ PUT    /users/{id}
✓ DELETE /users/{id}
✗ GET    /getUser?id=123
✗ POST   /createUser

Versioning:
✓ /api/v1/users  (URI versioning — most common)
✓ Header: Accept: application/vnd.api.v1+json
✓ Query param: ?version=1

Status Codes:
2xx — Success: 200 OK, 201 Created, 204 No Content
3xx — Redirect: 301 Moved, 302 Found, 304 Not Modified
4xx — Client error: 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 429 Too Many Requests
5xx — Server error: 500 Internal, 502 Bad Gateway, 503 Unavailable, 504 Gateway Timeout
```

---

### 🟡 GraphQL

**What:** Query language where clients specify exactly what data they need. Single endpoint.

**When to use:**
- Mobile clients with bandwidth concerns (fetch only needed fields)
- Complex, inter-related data (social graph)
- Rapid API iteration without versioning
- Aggregating data from multiple microservices (BFF pattern)

**Pros vs REST:**
- No over-fetching (get only what you ask for)
- No under-fetching (get nested data in one query)
- Strongly typed schema
- Self-documenting (introspection)

**Cons:**
- N+1 query problem (solved with DataLoader / batching)
- Complex caching (no URL-based caching)
- Higher learning curve
- Not suitable for simple CRUD APIs

```graphql
# Query — fetch specific fields
query {
  user(id: "123") {
    name
    email
    posts(last: 5) { title createdAt }
  }
}

# Mutation — create/update
mutation {
  createPost(title: "Hello", body: "World") { id createdAt }
}

# Subscription — real-time
subscription {
  newMessage(channelId: "chan1") { text sender { name } }
}
```

---

### 🟡 gRPC (Google Remote Procedure Call)

**What:** High-performance RPC framework using Protocol Buffers (binary serialization) over HTTP/2.

**When to use:**
- Internal service-to-service communication (microservices)
- Low-latency, high-throughput requirements
- Polyglot environments (generate clients in any language)
- Streaming (bidirectional)

**Protocol Buffers vs JSON:**
```
JSON:      {"name": "Alice", "age": 30}  → 25 bytes (human-readable)
Protobuf:  binary encoding               → ~8 bytes (3x smaller, 5-10x faster)
```

**gRPC Service Types:**
| Type | Description | Use Case |
|------|------------|---------|
| Unary | Single request, single response | Normal RPC |
| Server streaming | Single request, stream of responses | Logs, notifications |
| Client streaming | Stream of requests, single response | File upload |
| Bidirectional | Both sides stream | Chat, gaming |

**REST vs GraphQL vs gRPC:**
| | REST | GraphQL | gRPC |
|--|------|---------|------|
| **Protocol** | HTTP/1.1 | HTTP/1.1-2 | HTTP/2 |
| **Payload** | JSON/XML | JSON | Protobuf (binary) |
| **Typing** | Weak | Strong | Strong |
| **Streaming** | Limited | Subscriptions | Native |
| **Browser support** | Native | Native | Limited (grpc-web) |
| **Best for** | Public APIs | Complex client needs | Internal microservices |
| **Caching** | Easy (HTTP) | Hard | Hard |

---

### 🟡 WebSockets & Server-Sent Events

**WebSockets:**
- Full-duplex, persistent TCP connection
- Use for: real-time chat, collaborative editing, live dashboards, multiplayer games
- Protocol: `ws://` or `wss://` (TLS)
- Handshake: HTTP upgrade request

**Server-Sent Events (SSE):**
- One-way (server → client only), HTTP-based
- Simpler than WebSockets, automatic reconnect
- Use for: live feeds, notifications, real-time scores

**Long Polling:**
- Client sends request → server holds it open until data available → client immediately re-requests
- Use for: fallback when WebSockets unavailable, simpler implementation
- Inefficient: many connections, high latency

**Comparison:**
| | WebSocket | SSE | Long Polling |
|--|-----------|-----|-------------|
| **Direction** | Bidirectional | Server→Client | Server→Client |
| **Protocol** | WS | HTTP | HTTP |
| **Reconnect** | Manual | Auto | Auto |
| **Firewall friendly** | Sometimes blocked | Yes | Yes |
| **Overhead** | Low | Medium | High |

---

## 4. Load Balancing

### 🟢 What is a Load Balancer?

Distributes incoming requests across multiple backend servers to prevent overload on any single server, improve availability, and enable horizontal scaling.

**Types:**
- **Layer 4 (Transport):** Routes based on IP/TCP. Faster but less intelligent.
- **Layer 7 (Application):** Routes based on content (URL, headers, cookies). Smarter routing.

---

### 🟢 Load Balancing Algorithms

| Algorithm | How it works | Best for |
|-----------|-------------|---------|
| **Round Robin** | Requests distributed sequentially across servers | Servers with equal capacity, stateless |
| **Weighted Round Robin** | Like round robin, but servers with more capacity get more requests | Heterogeneous servers |
| **Least Connections** | New request goes to server with fewest active connections | Long-lived connections |
| **Least Response Time** | Routes to server with lowest latency | Latency-sensitive apps |
| **IP Hash** | Hash client IP → same server always | Sticky sessions needed |
| **Random** | Random server selection | Simple, similar servers |
| **Resource-based** | Route based on current CPU/memory | When health matters |

**⚠️ Nuance:** Round robin breaks if servers have different capacities. IP hash breaks when servers are added/removed (consistent hashing is better).

---

### 🟡 Sticky Sessions

**Problem:** Session data stored on one server — if next request goes to different server, session is lost.

**Solutions:**
1. **Sticky sessions / Session affinity** — Load balancer always routes client to same server (via cookie or IP hash). Problem: defeats the purpose of load balancing.
2. **Centralized session store** — Store sessions in Redis/DB, accessible by all servers. Best practice.
3. **JWT tokens** — Stateless; client carries session data in signed token. No server storage needed.

---

### 🟡 Health Checks

**Active health checks:** Load balancer periodically pings `/health` endpoint.
**Passive health checks:** Load balancer watches for failed responses and marks server unhealthy.

```
Circuit Breaker Pattern:
CLOSED → normal operation
OPEN   → after N failures, stop sending requests (fast-fail)
HALF-OPEN → after timeout, try a few requests to test recovery
```

---

### 🟡 Global Load Balancing (GeoDNS + Anycast)

- **GeoDNS:** Different DNS responses based on client's geographic location. Route users to nearest datacenter.
- **Anycast:** Same IP advertised from multiple locations; BGP routes traffic to nearest node. Used by CDNs and DNS providers (Cloudflare's 1.1.1.1).

---

## 5. Databases Deep Dive

### 🟢 SQL vs NoSQL — The Full Picture

#### SQL (Relational Databases)
- Structured, table-based, rows and columns
- Fixed schema (schema-on-write)
- ACID transactions
- Powerful query language (SQL)
- Joins across tables
- Vertical scaling primarily

#### NoSQL (Non-Relational Databases)
- Flexible/dynamic schema (schema-on-read)
- Horizontally scalable
- BASE consistency (usually)
- No standard query language
- Denormalized data
- Optimized for specific access patterns

**When to choose SQL:**
- Complex relationships (joins needed)
- Strong consistency required
- Complex queries, ad-hoc analytics
- ACID transactions (finance, e-commerce)
- Data integrity and constraints matter
- Team knows SQL well

**When to choose NoSQL:**
- Massive scale (billions of rows)
- Flexible/evolving schema
- Simple access patterns (key-value, document)
- High write throughput
- Geographic distribution
- Specific use cases (graphs, time-series, search)

---

### 🟢 SQL Databases — Detailed Comparison

#### PostgreSQL
- **Type:** Object-relational DBMS
- **Strengths:**
  - Most feature-rich open-source RDBMS
  - JSONB for hybrid SQL/NoSQL queries
  - Full-text search, PostGIS (geospatial)
  - Advanced indexing (partial, GIN, GiST, BRIN)
  - Strong ACID, MVCC for concurrency
  - Window functions, CTEs, recursive queries
  - Supports custom types, functions, extensions
- **Weaknesses:** Slower than MySQL for simple reads; resource-heavy
- **Best for:** Complex queries, analytics, GIS, applications needing JSON + relational
- **Used by:** Instagram, Reddit, Spotify, GitLab

#### MySQL / MariaDB
- **Type:** Relational DBMS (multiple storage engines)
- **Storage Engines:**
  - **InnoDB** (default): ACID, transactions, row-level locking, foreign keys
  - **MyISAM:** Faster reads, no transactions, table-level locking (legacy)
- **Strengths:** Fast reads, wide ecosystem, simple setup, great replication
- **Weaknesses:** Less feature-rich than PostgreSQL, weaker JSON support, no partial indexes
- **Best for:** Web applications, LAMP stack, read-heavy workloads
- **Used by:** WordPress, Facebook (sharded MySQL), Airbnb, Twitter

#### SQLite
- **Type:** Embedded, file-based RDBMS
- **Strengths:** Zero configuration, single file, great for local/mobile storage
- **Best for:** Mobile apps, desktop apps, testing, small embedded apps
- **Not for:** Multi-user concurrent writes, large scale

#### Oracle Database
- **Type:** Commercial enterprise RDBMS
- **Strengths:** Enterprise features, RAC (Real Application Clusters), very mature
- **Best for:** Large enterprises with budget, complex transactions
- **Used by:** Banks, airlines, government systems

#### Microsoft SQL Server
- **Type:** Commercial enterprise RDBMS
- **Strengths:** Deep Windows/Azure integration, excellent BI/reporting tools (SSRS, SSIS)
- **Best for:** Enterprise Microsoft ecosystems

---

### 🟢 SQL vs SQL — Quick Decision Matrix

| Need | Choose |
|------|--------|
| Most feature-rich open-source | PostgreSQL |
| Fastest reads, web apps | MySQL |
| Embedded/mobile | SQLite |
| Enterprise features, Oracle compatibility | Oracle |
| Microsoft ecosystem | SQL Server |
| HTAP (hybrid transaction+analytics) | CockroachDB, TiDB |
| Globally distributed SQL | CockroachDB, Spanner |

---

### 🟡 NoSQL Databases — Types & Deep Dive

#### Type 1: Document Stores

**MongoDB:**
- Documents stored as BSON (Binary JSON)
- Schema-flexible — each document can have different fields
- Rich query language, aggregation pipeline
- Secondary indexes
- Horizontal sharding (built-in)
- ACID transactions (since v4.0, single replica; v4.2 distributed)
- **Strengths:** Flexible schema, developer-friendly, good for hierarchical data
- **Weaknesses:** No joins (use $lookup, but expensive), denormalization leads to data duplication
- **Best for:** Content management, catalogs, user profiles, event data
- **Avoid for:** Highly relational data, complex multi-document transactions

**CouchDB:**
- JSON documents, HTTP REST API
- Multi-master replication (offline-first)
- Eventual consistency
- **Best for:** Offline-first mobile apps, distributed web apps

---

#### Type 2: Key-Value Stores

**Redis:**
- In-memory data structure store
- Data types: String, Hash, List, Set, Sorted Set, Bitmap, HyperLogLog, Streams, Geo
- Sub-millisecond latency
- Persistence: RDB (snapshots) and/or AOF (append-only log)
- Replication: Master-replica
- Cluster mode: horizontal sharding
- Pub/Sub messaging
- Lua scripting, transactions (MULTI/EXEC)
- **Best for:** Caching, sessions, rate limiting, leaderboards (sorted sets), pub/sub, distributed locks, real-time analytics
- **Limits:** Data fits in RAM (expensive), not for primary data store of large datasets

**DynamoDB (AWS):**
- Fully managed, serverless NoSQL
- Key-value + document
- Single-digit millisecond latency at any scale
- Automatic partitioning and replication
- Two consistency models: Eventually consistent (default) or Strongly consistent reads
- Global Tables (multi-region, multi-master)
- DynamoDB Streams for change capture
- **Access patterns must be defined upfront** — no ad-hoc queries
- **Best for:** Serverless apps, high-traffic web apps, IoT, gaming leaderboards
- **Avoid:** Complex queries, ad-hoc analytics, many-to-many relationships

**Memcached:**
- Pure in-memory key-value cache
- Simpler than Redis, multi-threaded
- No persistence, no data structures beyond strings
- **Best for:** Simple distributed caching when Redis features aren't needed

---

#### Type 3: Wide-Column Stores

**Apache Cassandra:**
- Distributed, decentralized (no master node — peer-to-peer)
- Tunable consistency (ONE, QUORUM, ALL)
- Excellent write throughput (append-only writes)
- Linear horizontal scalability
- CQL (Cassandra Query Language) — SQL-like
- Data model: Keyspace → Tables → Partition key → Clustering columns
- **Data modeling rule:** Design tables around queries, not around data
- No joins, no foreign keys, no complex aggregations
- **Best for:** Time-series data, write-heavy workloads, high-availability IoT, event logging, messaging at scale
- **Avoid:** Complex queries, transactions, rapidly changing schema
- **Used by:** Netflix, Apple, Discord, Uber

**HBase (Apache):**
- Built on HDFS (Hadoop)
- Column-family oriented
- Strong consistency (CP in CAP)
- Good for sparse, large tables (billions of rows)
- **Best for:** Big data analytics, sparse data, MapReduce integration
- **Avoid:** OLTP, low-latency requirements

---

#### Type 4: Graph Databases

**Neo4j:**
- Native graph storage and processing
- Nodes, relationships, properties
- Cypher query language
- ACID transactions
- **Best for:** Social networks, recommendation engines, fraud detection, knowledge graphs, network topology
- **Avoid:** Non-graph data, high write throughput, large-scale analytics

**Amazon Neptune:**
- Fully managed graph database (AWS)
- Supports both Property Graph (Gremlin) and RDF (SPARQL)
- **Best for:** Knowledge graphs, fraud detection, compliance

**Why graphs for relationships?**
```
SQL (finding friends of friends):
SELECT u.name FROM users u
JOIN friendships f1 ON u.id = f1.user_id
JOIN friendships f2 ON f1.friend_id = f2.user_id
WHERE f2.friend_id = 1;
-- Gets very slow at depth 4-5 (N hops)

Neo4j Cypher:
MATCH (me:User {id:1})-[:FRIEND*1..5]->(them:User)
RETURN them.name
-- Same performance at any depth via pointer chasing
```

---

#### Type 5: Time-Series Databases

**InfluxDB:**
- Optimized for timestamped data
- Time-series data compression (10-50x smaller than RDBMS)
- InfluxQL / Flux query language
- Retention policies, continuous queries
- **Best for:** Metrics, monitoring, IoT sensor data, financial tick data

**TimescaleDB:**
- PostgreSQL extension for time-series
- Full SQL, PostGIS compatibility
- Hypertables (automatic time-based partitioning)
- **Best for:** When you want time-series performance but need SQL and joins

**Apache Druid:**
- Real-time analytics database
- Sub-second OLAP queries at petabyte scale
- Columnar storage
- **Best for:** Event analytics, clickstream, ad-tech

---

#### Type 6: Search Engines

**Elasticsearch:**
- Distributed full-text search and analytics
- Built on Apache Lucene
- Inverted index for text search
- Aggregations for analytics
- Near real-time indexing (1 second)
- JSON documents, REST API
- **Best for:** Full-text search, log analytics (ELK stack), e-commerce product search, autocomplete
- **Avoid:** Primary data store, ACID transactions, simple key-value

**Apache Solr:**
- Also Lucene-based
- Mature, enterprise features
- SolrCloud for distributed deployment
- **Best for:** Enterprise search, document indexing

---

### 🟢 Database Normalization

**Purpose:** Reduce data redundancy, improve data integrity, eliminate anomalies (insert, update, delete).

#### Normal Forms

**1NF (First Normal Form):**
- Each column has atomic (single, indivisible) values
- No repeating groups or arrays
```
VIOLATION:
| student | courses        |
|---------|----------------|
| Alice   | Math, Physics  |   ← Not atomic

1NF:
| student | course  |
|---------|---------|
| Alice   | Math    |
| Alice   | Physics |
```

**2NF (Second Normal Form):**
- Must be in 1NF
- No partial dependency (non-key column depends on only part of composite key)
```
VIOLATION (composite key: student + course):
| student | course  | student_age | grade |
|---------|---------|-------------|-------|
| Alice   | Math    | 20          | A     |
  ↑ student_age depends only on student, not the full composite key

2NF (separate table):
students: (student, student_age)
grades:   (student, course, grade)
```

**3NF (Third Normal Form):**
- Must be in 2NF
- No transitive dependency (non-key column depends on another non-key column)
```
VIOLATION:
| student | zip_code | city       |
  ↑ city depends on zip_code, not on student (transitive)

3NF:
students: (student, zip_code)
locations: (zip_code, city)
```

**BCNF (Boyce-Codd Normal Form):**
- Stricter version of 3NF
- Every determinant must be a candidate key
- Handles anomalies 3NF misses with overlapping candidate keys

**4NF:**
- No multi-valued dependencies
- Rarely needed in practice

**Denormalization:**
- Intentionally violate normalization to improve **read performance**
- Add redundant data, pre-computed aggregates
- Trade-off: faster reads, slower writes, data inconsistency risk
- **Use when:** Read-heavy workloads, performance requirements override purity, OLAP/data warehouses

---

### 🟡 ACID Properties — Deep Dive

#### Isolation Levels (SQL Standard)

| Isolation Level | Dirty Read | Non-repeatable Read | Phantom Read | Performance |
|-----------------|-----------|---------------------|-------------|------------|
| **READ UNCOMMITTED** | Possible | Possible | Possible | Highest |
| **READ COMMITTED** | Prevented | Possible | Possible | High |
| **REPEATABLE READ** | Prevented | Prevented | Possible | Medium |
| **SERIALIZABLE** | Prevented | Prevented | Prevented | Lowest |

**Phenomena Explained:**
- **Dirty Read:** Transaction reads data written by another uncommitted transaction.
- **Non-repeatable Read:** Same row read twice in a transaction returns different values (another transaction updated it).
- **Phantom Read:** Same query returns different rows (another transaction inserted/deleted rows).

**Practical defaults:**
- PostgreSQL: READ COMMITTED (default), REPEATABLE READ uses MVCC (no locking)
- MySQL InnoDB: REPEATABLE READ (default)
- Most apps: READ COMMITTED is sufficient

**MVCC (Multi-Version Concurrency Control):**
- Used by PostgreSQL, Oracle, MySQL InnoDB
- Writers don't block readers; each transaction sees a snapshot of the DB
- Old row versions kept until no transaction needs them (VACUUM in PostgreSQL)
- Alternative to lock-based concurrency

---

## 6. Database Internals

### 🟡 Indexing — Deep Dive

**What:** Data structure that speeds up queries. Trade-off: faster reads, slower writes, more storage.

#### B-Tree Index (Default)
- Balanced tree, O(log n) lookups
- Good for: equality (`=`), range (`>`, `<`, `BETWEEN`), sorting (`ORDER BY`)
- Used by: PostgreSQL, MySQL, Oracle (default)

```
B-Tree:              Range query [30, 60]:
        [40]          Walk tree to 30, scan right
       /    \
    [20]    [60]
   /    \  /    \
 [10] [30][50] [70]
```

#### Hash Index
- O(1) exact lookups
- Does **not** support range queries
- PostgreSQL: supports hash indexes (rarely used)
- Memory-only in some systems

#### LSM-Tree (Log-Structured Merge-Tree)
- Write-optimized: all writes go to in-memory MemTable, periodically flushed to disk as SSTables
- Reads check multiple levels (slower than B-Tree for reads)
- Compaction merges SSTables over time
- Used by: Cassandra, RocksDB, LevelDB, HBase

```
Write path: MemTable → L0 SSTables → L1 → L2 (compaction)
Read path:  Check MemTable → L0 → L1 → L2 (slower but OK with bloom filters)
```

#### Index Types in PostgreSQL
- **B-tree:** Default, good for most cases
- **GIN (Generalized Inverted Index):** Full-text search, JSONB, arrays
- **GiST (Generalized Search Tree):** Geometric types, full-text, range types
- **BRIN (Block Range Index):** Very large tables with natural ordering (timestamps, sequential IDs)
- **Partial index:** Index only a subset of rows (`WHERE deleted = false`)
- **Composite index:** Index on multiple columns (order matters!)

**Composite Index Column Order Rule:**
```
Index on (a, b, c) speeds up:
  WHERE a = ?
  WHERE a = ? AND b = ?
  WHERE a = ? AND b = ? AND c = ?

Does NOT speed up:
  WHERE b = ?          (skips leading column)
  WHERE c = ? AND a = ? AND b = ? → reordered, but leading columns needed
```

---

### 🟡 Sharding (Horizontal Partitioning)

**What:** Split a large database into smaller pieces (shards) across multiple nodes.

**Sharding Strategies:**

| Strategy | How | Pros | Cons |
|----------|-----|------|------|
| **Range-based** | Shard by value range (e.g., user IDs 1-1M on shard 1) | Simple, range queries efficient | Hotspots if data not uniform |
| **Hash-based** | Hash the key, assign to shard | Even distribution | Range queries hit all shards |
| **Directory-based** | Lookup table maps key → shard | Flexible, can rebalance | Lookup table is bottleneck/SPOF |
| **Geographic** | Route by user location | Low latency, data residency | Cross-geo queries expensive |

**Problems with Sharding:**
- **Cross-shard joins:** Expensive, often impossible. Denormalize or application-side join.
- **Cross-shard transactions:** Distributed transactions (2PC) — complex, slow.
- **Resharding:** Adding/removing shards requires data migration. Use consistent hashing to minimize movement.
- **Hotspots:** Some shards receive more load (celebrities, trending topics). Solution: sub-sharding, random prefix for hot keys.

---

### 🟡 Replication

**Purpose:** Copy data to multiple nodes for fault tolerance, read scaling, disaster recovery.

#### Replication Modes

**Synchronous Replication:**
- Primary waits for replica acknowledgment before confirming write
- Strong consistency, no data loss
- Higher write latency
- Use when: Data loss is unacceptable (financial transactions)

**Asynchronous Replication:**
- Primary confirms write without waiting for replica
- Lower write latency
- Risk of data loss if primary fails before replica catches up (replication lag)
- Use when: Read scaling, analytics, backups

**Semi-synchronous:**
- At least one replica must acknowledge (middle ground)
- MySQL: `rpl_semi_sync_master_enabled`

#### Replication Topologies

| Topology | Description | Use Case |
|----------|------------|---------|
| **Single Leader (Master-Replica)** | All writes to primary, replicas serve reads | Most common, read scaling |
| **Multi-Leader** | Multiple primaries, each accepts writes | Multi-datacenter, offline clients (CouchDB) |
| **Leaderless (Dynamo-style)** | Any node can accept writes, quorum-based | Cassandra, DynamoDB, Riak |

**Leaderless / Quorum:**
```
With N replicas, W write quorum, R read quorum:
Strong consistency: W + R > N
  Example: N=3, W=2, R=2 → 2+2>3 ✓

Cassandra consistency levels:
  ONE    → fastest, least consistent
  QUORUM → balanced
  ALL    → slowest, most consistent
  LOCAL_QUORUM → quorum within local datacenter only
```

---

### 🟡 Partitioning (Vertical)

**What:** Split a table by columns (different from sharding which splits by rows).

```
Users table → Split into:
  users_core:     (id, name, email, created_at)  — frequently queried
  users_profile:  (id, bio, avatar, preferences) — rarely queried
  users_auth:     (id, password_hash, mfa_secret) — security-sensitive
```

**Benefits:** Smaller, faster tables for hot columns. Security isolation. Easier caching.

---

## 7. Caching

### 🟢 Why Cache?

- Reduce latency (memory: ns vs disk: ms)
- Reduce database load
- Reduce cost (less DB compute)
- Improve throughput

**What to cache:** Expensive computations, frequent reads, slow external API responses, session data.

**What NOT to cache:** Rapidly changing data (stock prices), user-specific secure data, data that must always be fresh.

---

### 🟢 Cache Placement Strategies

| Type | Location | Example | Latency |
|------|----------|---------|---------|
| **Client-side** | Browser/app | Browser cache, Service Worker | ~0ms |
| **DNS cache** | OS / resolver | TTL-based | ~0ms |
| **CDN cache** | Edge nodes | CloudFront, Cloudflare | ~1-5ms |
| **Application cache** | In-process | Guava Cache, Caffeine | ~0ms |
| **Distributed cache** | Separate service | Redis, Memcached | ~0.5ms |
| **Database cache** | DB query cache | MySQL Query Cache (deprecated) | ~1ms |

---

### 🟢 Cache Reading Strategies

**Cache-Aside (Lazy Loading):**
```
App → Check cache → HIT: return data
App → Check cache → MISS: fetch from DB → store in cache → return
```
- Most common pattern
- Cache only contains what's actually requested (no unused data)
- Resilient to cache failure (app falls back to DB)
- **Con:** Cache miss penalty (3 round trips). Cache stampede on cold start.

**Read-Through:**
```
App → Cache → MISS: Cache fetches from DB → stores → returns to app
```
- Cache manages DB interaction transparently
- Consistent interface for app
- **Con:** First request always slow. Cache must know DB schema.

---

### 🟢 Cache Writing Strategies

**Write-Through:**
```
App → Write to Cache → Cache writes to DB synchronously
```
- Always consistent (cache and DB in sync)
- **Con:** Higher write latency. Cache may hold data that's never read.

**Write-Behind (Write-Back):**
```
App → Write to Cache → Async write to DB (batched)
```
- Low write latency (returns after writing to cache)
- **Con:** Data loss risk if cache fails before DB write. Complex error handling.

**Write-Around:**
```
App → Write directly to DB (bypass cache)
Cache is populated on next read miss
```
- Good for data written once and rarely read
- **Con:** First read after write is always a miss.

---

### 🟡 Cache Eviction Policies

| Policy | Algorithm | Best for |
|--------|-----------|---------|
| **LRU** (Least Recently Used) | Evict item not accessed longest | General purpose, temporal locality |
| **LFU** (Least Frequently Used) | Evict least accessed item | Items with varying access frequency |
| **FIFO** | Evict oldest added item | Simple queue-like access |
| **TTL** (Time-to-Live) | Evict items after fixed duration | Time-sensitive data |
| **Random** | Evict random item | Simple, low overhead |
| **ARC** (Adaptive Replacement) | Combines LRU + LFU dynamically | Better than either alone |

**Redis default:** `allkeys-lru` or `volatile-lru` (only evict TTL-set keys)

---

### 🟡 Cache Problems & Solutions

**Cache Stampede (Thundering Herd):**
- Many requests hit a cold/expired cache simultaneously, all going to DB.
- **Solutions:** Probabilistic early expiration, mutex locking (only one request populates cache), background refresh.

**Cache Penetration:**
- Repeated queries for non-existent keys bypass cache and hit DB.
- **Solutions:** Cache negative results (store "null" with short TTL), Bloom filter to check key existence.

**Cache Avalanche:**
- Many cache keys expire simultaneously → DB overloaded.
- **Solutions:** Jitter (randomize TTL), staggered expiration, circuit breaker.

**Hot Key Problem:**
- One key receives massive traffic → single cache node overloaded.
- **Solutions:** Replicate hot key to multiple nodes, local in-process cache + Redis, read replicas.

---

### 🟡 Redis Deep Dive

**Data Structures and Use Cases:**
```
String:       Counters, simple cache, feature flags
              SET user:123:name "Alice"

Hash:         Object storage (user profile)
              HSET user:123 name "Alice" age 30

List:         Feed/queue, recent activity
              LPUSH notifications:user1 "message"
              RPOP notifications:user1

Set:          Unique members, tags, friends list
              SADD user:1:friends 2 3 4
              SINTER user:1:friends user:2:friends   ← mutual friends

Sorted Set:   Leaderboards, priority queues
              ZADD leaderboard 100 "Alice" 200 "Bob"
              ZRANGE leaderboard 0 -1 WITHSCORES REV

HyperLogLog:  Approximate count distinct (UV counting)
              PFADD visitors "user1" "user2"
              PFCOUNT visitors → ~2

Streams:      Append-only log, event sourcing
              XADD events * action "click" page "/home"

Geo:          Location-based queries
              GEOADD locations 13.361389 38.115556 "Palermo"
              GEODIST locations "Palermo" "Rome" km
```

**Redis Cluster:**
- 16384 hash slots distributed across nodes
- Each master handles a range of slots
- At least 3 masters + 3 replicas for HA
- Client redirected to correct node (MOVED error)

**Redis Sentinel vs Cluster:**
| | Sentinel | Cluster |
|--|----------|---------|
| **Purpose** | HA for single shard | Sharding + HA |
| **Scale** | Single master | Multiple masters |
| **Use when** | Data fits on one node | Data exceeds single node |

---

## 8. Message Queues & Event Streaming

### 🟢 Why Message Queues?

**Problems they solve:**
1. **Decoupling:** Producer and consumer don't need to know about each other
2. **Buffering:** Handle traffic spikes; smooth out burst loads
3. **Async processing:** Don't block user-facing requests on slow operations (email, notifications)
4. **Fan-out:** Send one message to many consumers
5. **Reliability:** Messages persisted until acknowledged
6. **Rate matching:** Slow consumers don't block fast producers

**Core Concepts:**
```
Producer → [ Queue / Topic ] → Consumer

Queue:     Point-to-point. One message consumed by one consumer.
Topic:     Publish-subscribe. One message consumed by many consumers.

Push model: Broker pushes messages to consumers
Pull model: Consumers poll broker for messages
```

---

### 🟡 Message Queue vs Event Stream

| | Message Queue | Event Stream |
|--|---------------|-------------|
| **Purpose** | Task distribution | Event log / audit trail |
| **Retention** | Until consumed | Configured retention (time/size) |
| **Replay** | No (usually) | Yes — re-read from any offset |
| **Consumer** | One consumer per message | Many independent consumers |
| **Model** | Destructive read | Non-destructive read |
| **Examples** | RabbitMQ, SQS, IBM MQ, ActiveMQ | Kafka, Kinesis, Pulsar |

---

### 🔴 Apache Kafka — Deep Dive

**What:** Distributed event streaming platform. Not just a queue — a persistent, replayable event log.

**Architecture:**
```
Producers → Broker Cluster → Consumers

Topic: logical stream of records
Partition: ordered, immutable sequence within a topic
Offset: position of a message within a partition
Consumer Group: group of consumers sharing a topic; each partition → one consumer
```

**Key Concepts:**
```
Topic partitions: enable parallel consumption (more partitions = more parallelism)
Replication factor: how many brokers hold a copy (usually 3)
Leader: handles reads/writes for a partition
Follower: replicates from leader
ISR (In-Sync Replicas): replicas up-to-date with leader

Retention: by time (default 7 days) or by size
Log compaction: keep only latest value per key (useful for state snapshots)
```

**Producer Configuration (Key Settings):**
```
acks=0:   No acknowledgment (fire and forget) — fastest, possible data loss
acks=1:   Leader acknowledgment — balanced
acks=all: All ISRs acknowledge — slowest, strongest durability

batch.size:       Batch messages before sending (throughput)
linger.ms:        Wait this long before sending batch even if not full
compression.type: gzip/snappy/lz4 — reduce network and storage
```

**Consumer Configuration:**
```
auto.offset.reset=earliest: Start from beginning if no offset stored
auto.offset.reset=latest:   Start from newest messages
enable.auto.commit=false:   Manual offset commit (exactly-once processing)
```

**Delivery Semantics:**
| Guarantee | How | Risk |
|-----------|-----|------|
| At-most-once | Commit offset before processing | Data loss |
| At-least-once | Commit after processing | Duplicates |
| Exactly-once | Transactions + idempotent producers | Complex, overhead |

**Kafka Strengths:**
- Extremely high throughput (millions of events/sec)
- Persistent storage with replay
- Multiple independent consumer groups
- Strong ordering within partition
- Stream processing with Kafka Streams / ksqlDB

**Kafka Weaknesses:**
- Not designed for traditional message queuing (no individual message deletion)
- Complex to operate (ZooKeeper historically, now KRaft)
- High latency for single message delivery
- Not suited for long-polling, dead letter queues out of box

**Best for:**
- Event sourcing, CQRS
- Real-time analytics pipelines
- Log aggregation (replace syslog)
- CDC (Change Data Capture) from databases
- Microservice event bus at high scale
- Stream processing (Kafka Streams, Flink + Kafka)

---

### 🟡 RabbitMQ — Deep Dive

**What:** Traditional message broker implementing AMQP (Advanced Message Queuing Protocol).

**Architecture:**
```
Producer → Exchange → Queue(s) → Consumer

Exchange Types:
  Direct:  Route by exact routing key match
  Topic:   Route by pattern (user.* , *.error)
  Fanout:  Broadcast to all bound queues
  Headers: Route by message header attributes
```

**Key Features:**
- **Acknowledgments:** Consumer explicitly ACKs or NACKs (negative ack → requeue or DLQ)
- **Dead Letter Queue (DLQ):** Unprocessable messages routed here for inspection
- **Message TTL:** Messages expire after set time
- **Priority Queues:** Higher priority messages consumed first
- **Request-Reply pattern:** Correlation ID + reply-to queue
- **Durability:** Mark queues and messages as durable (survives restart)
- **Prefetch count:** Limit how many unacked messages consumer receives (back pressure)

**RabbitMQ Strengths:**
- Flexible routing logic (exchanges)
- Per-message acknowledgment and reliability
- Rich plugin ecosystem
- Good for complex routing requirements
- Low latency for traditional task queues
- DLQ, message TTL built-in
- Multiple protocols: AMQP, MQTT, STOMP

**RabbitMQ Weaknesses:**
- Messages deleted after consumption (no replay)
- Not designed for extreme throughput like Kafka
- In-memory queue — large backlogs can cause memory issues
- Clustering is more complex at scale

**Best for:**
- Task queues (background jobs)
- Request-reply patterns
- Complex routing (different rules for different message types)
- IoT messaging (MQTT plugin)
- When you need DLQ, message TTL, priority

---

### 🟡 IBM MQ (formerly WebSphere MQ / MQSeries)

**What:** Enterprise-grade message broker. Industry standard for decades in banking and large enterprises.

**Key Features:**
- **Persistent messaging:** Messages stored on disk, survive crashes
- **Exactly-once delivery:** Guaranteed (unlike most other MQs)
- **Transaction support:** JTA/XA distributed transactions
- **Channel encryption:** TLS, mutual authentication
- **Multi-protocol:** JMS, AMQP, MQTT, HTTP, REST
- **Queue Manager:** Central administration unit
- **Dead Letter Queue:** Built-in, highly configurable

**IBM MQ Strengths:**
- **Strongest delivery guarantees** (exactly-once)
- Mature, battle-tested in financial systems
- Excellent transaction support (integrates with DB2, Oracle)
- Strict security and audit capabilities
- Reliable across heterogeneous systems

**IBM MQ Weaknesses:**
- Expensive (commercial license)
- Heavyweight, complex administration
- Not designed for high-throughput event streaming
- Legacy architecture

**Best for:**
- Banking, financial transactions, insurance
- Legacy enterprise system integration
- Regulatory compliance requiring audit trails
- Critical mission systems where data loss is unacceptable

---

### 🟡 Apache ActiveMQ / ActiveMQ Artemis

**What:** Open-source, Java-based message broker implementing JMS (Java Message Service).

**Two versions:**
- **ActiveMQ Classic:** Mature, widely used, supports many protocols
- **ActiveMQ Artemis:** Next-gen, higher performance, better for modern workloads

**Key Features:**
- JMS 1.1 and 2.0 compliant
- Supports: OpenWire, AMQP, STOMP, MQTT, WebSocket
- Message groups (messages with same group key go to same consumer)
- Virtual destinations, composite destinations
- Network of brokers (cluster topology)

**Best for:**
- Java/Spring enterprise applications (Spring JMS/AMQP integration)
- When JMS compliance required
- Traditional enterprise messaging

---

### 🟡 Amazon SQS & SNS

**SQS (Simple Queue Service):**
- Fully managed message queue, serverless
- Two types:
  - **Standard Queue:** At-least-once, out-of-order possible, near-unlimited throughput
  - **FIFO Queue:** Exactly-once, strict ordering, 300 TPS (or 3000 with batching)
- Visibility timeout: Message hidden from others while being processed
- DLQ: Built-in
- Long polling: Reduce empty responses (up to 20s wait)
- **Best for:** Serverless task queues on AWS, decoupling Lambda functions

**SNS (Simple Notification Service):**
- Publish-subscribe messaging, push-based
- Fan-out to multiple SQS queues, Lambda functions, HTTP endpoints, SMS, email
- **Pattern:** SNS → multiple SQS queues (fan-out)
- **Best for:** Event notifications, fan-out to multiple consumers

---

### 🟡 Apache Pulsar

**What:** Cloud-native distributed messaging and streaming. Combines Kafka-style streaming with RabbitMQ-style queuing.

**Architecture:**
- Separates compute (Brokers) from storage (BookKeeper/Bookie nodes)
- Multi-tenancy built-in (namespaces, tenants)
- Geo-replication natively

**Strengths:**
- True multi-tenancy
- Tiered storage (offload old data to S3/GCS cheaply)
- Both streaming and traditional queuing in one system
- Better than Kafka for multi-tenant SaaS platforms

---

### 🔴 MQ Comparison Table

| Feature | Kafka | RabbitMQ | IBM MQ | ActiveMQ | SQS/SNS | Pulsar |
|---------|-------|----------|--------|----------|---------|--------|
| **Model** | Event log | Queue/Exchange | Queue | Queue/Topic | Queue | Stream+Queue |
| **Throughput** | Millions/s | 100k+/s | Tens of k/s | 50k+/s | 3k-unlimited | Millions/s |
| **Delivery** | At-least-once (configurable) | At-least-once | Exactly-once | At-least-once | At-least-once | At-least-once |
| **Message Replay** | Yes (by offset) | No | No | No | No | Yes |
| **Ordering** | Per-partition | Per-queue | Per-queue | Per-queue | FIFO queue | Per-partition |
| **Routing** | Topic/partition | Complex exchanges | Queue/topic | Complex | Simple | Topic |
| **DLQ** | Manual | Built-in | Built-in | Built-in | Built-in | Built-in |
| **Persistence** | Days (configurable) | Configurable | Persistent | Configurable | 4 days default | Configurable |
| **Multi-consumer** | Yes (groups) | Yes (bindings) | Yes | Yes | No (SQS) / Yes (SNS) | Yes |
| **Cost** | Open-source | Open-source | Commercial | Open-source | AWS pay-per-use | Open-source |
| **Managed** | Confluent Cloud | CloudAMQP | IBM Cloud | No managed | AWS native | StreamNative |
| **Best for** | Event streaming, analytics | Task queues, routing | Banking, enterprise | Java enterprise | AWS serverless | Multi-tenant SaaS |

---

### 🔴 When to Use Which MQ?

```
Use KAFKA when:
  ✓ You need replay/reprocessing of events
  ✓ Multiple independent consumers of same events
  ✓ Event sourcing or CQRS
  ✓ High-throughput event pipeline (millions/sec)
  ✓ Stream processing (Flink, Spark, Kafka Streams)
  ✓ CDC (Debezium capturing DB changes)
  ✓ Audit log / event history needed

Use RABBITMQ when:
  ✓ Complex routing rules (exchanges, bindings)
  ✓ Traditional task queue (background jobs)
  ✓ Per-message acknowledgment with retry logic
  ✓ DLQ with inspection/reprocessing
  ✓ IoT/MQTT devices
  ✓ Request-Reply pattern

Use IBM MQ when:
  ✓ Financial industry standard required
  ✓ Exactly-once delivery is mandatory
  ✓ Integrating with legacy IBM/mainframe systems
  ✓ Regulatory compliance with strict audit

Use SQS when:
  ✓ AWS-native serverless architecture
  ✓ Lambda-driven processing
  ✓ Simple task queue without complex routing
  ✓ FIFO required (SQS FIFO)
  ✓ Managed, zero-ops required

Use PULSAR when:
  ✓ Multi-tenant SaaS platform
  ✓ Need both streaming and queuing
  ✓ Tiered storage for cost optimization
  ✓ Geo-replication is critical
```

---

## 9. Storage Systems

### 🟢 Storage Types

| Type | Description | Examples | Best for |
|------|------------|---------|---------|
| **Block Storage** | Raw storage volumes, low-level | AWS EBS, Azure Disk | Databases, OS volumes |
| **File Storage** | Hierarchical filesystem | NFS, EFS, CIFS | Shared file systems |
| **Object Storage** | Flat namespace, metadata-rich, HTTP API | S3, GCS, Azure Blob | Unstructured data, backups, media |

#### Object Storage (S3 and equivalents)
- Store files as objects with unique key (path)
- Virtually unlimited capacity
- 11 nines (99.999999999%) durability (via erasure coding + replication)
- Versioning, lifecycle policies, access control
- Cheap (cents per GB/month)
- High latency vs block storage (not suitable for databases)
- **Use for:** Images, videos, backups, logs, static website, ML datasets

#### Block Storage
- Appears as a disk to the OS
- Low latency, high IOPS
- SSD-backed for databases
- Snapshots for backups
- **Use for:** Database primary storage, OS volumes

---

### 🟡 Data Warehouse vs Data Lake vs Lakehouse

| | Data Warehouse | Data Lake | Data Lakehouse |
|--|---------------|-----------|----------------|
| **Storage** | Structured, columnar | Raw, any format | Raw + table format |
| **Schema** | Schema-on-write | Schema-on-read | Schema-on-write/read |
| **Users** | BI analysts, SQL users | Data scientists | Both |
| **Cost** | Expensive | Cheap (object storage) | Cheap |
| **Performance** | Fast for SQL | Slow without tuning | Fast with Delta/Iceberg |
| **Examples** | Snowflake, Redshift, BigQuery | S3 + Glue, HDFS | Databricks, Apache Iceberg |

---

## 10. Consistent Hashing

### 🟡 The Problem with Simple Hashing

**Modulo hashing:** `server = hash(key) % N`
- Adding/removing a server changes N → almost all keys remap → massive cache invalidation

**Example:**
```
N=3 servers: key "user123" → hash(user123) % 3 = 2 → Server 2
N=4 servers: key "user123" → hash(user123) % 4 = 1 → Server 1 (different!)
Result: ~75% of keys remap when adding 1 server
```

---

### 🟡 Consistent Hashing Solution

**Concept:** Map both servers and keys onto a circular ring (0 to 2³²). A key is served by the next clockwise server.

```
Ring (0 to 2³²):
    0
   / \
  S1  S2
   \  /
    S3

Key K maps to ring → walk clockwise → find server
Adding S4: only keys between S3 and S4 remap to S4 (minimal movement)
Removing S2: only S2's keys remap to S3
```

**Virtual Nodes:**
- Problem: Uneven distribution if few servers
- Solution: Each physical server gets multiple positions (virtual nodes)
- More virtual nodes = more even distribution
- Also allows weighted distribution (powerful server gets more virtual nodes)

**Used by:** Cassandra, DynamoDB, Memcached (ketama), Chord DHT, load balancers

---

## 11. Microservices & Service Mesh

### 🟢 Monolith vs Microservices

| | Monolith | Microservices |
|--|----------|--------------|
| **Deployment** | Single deployable unit | Independent deployments |
| **Scaling** | Scale entire app | Scale individual services |
| **Development** | Simpler initially | Complex (distributed systems) |
| **Technology** | Single stack | Polyglot possible |
| **Failure** | One failure = full outage | Isolated failures |
| **Data** | Shared database | Each service owns its data |
| **Communication** | In-process | Network (REST, gRPC, MQ) |
| **Start with** | Usually yes | Only when needed |

**Microservices Principles:**
1. **Single Responsibility:** Each service does one thing well
2. **Bounded Context (DDD):** Clear ownership of data and domain
3. **Independent Deployability:** Deploy without coordinating with other teams
4. **Decentralized Data:** No shared databases between services
5. **Design for Failure:** Expect and handle network failures

---

### 🟡 Service Communication Patterns

**Synchronous (Request-Response):**
- REST API, gRPC
- Simple, easy to debug
- Tight coupling in time (both must be up)
- Risk of cascading failures

**Asynchronous (Event-driven):**
- Message queue / event bus
- Decoupled, resilient
- Harder to debug, eventual consistency
- Use for: notifications, async processing, fan-out

**API Gateway:**
- Single entry point for clients
- Handles: routing, auth, rate limiting, SSL termination, request aggregation
- Examples: Kong, AWS API Gateway, NGINX, Traefik

---

### 🟡 Service Discovery

**Client-side discovery:**
- Client queries service registry (Eureka, Consul) → gets list of instances → picks one
- Load balancing in client (Ribbon)
- Client needs discovery library

**Server-side discovery:**
- Client → Load Balancer → LB queries registry → routes request
- Client is simpler (no discovery logic)
- Examples: AWS ALB + ECS, Kubernetes Service

**Service Registries:** Consul, Eureka, etcd, ZooKeeper

---

### 🟡 Circuit Breaker Pattern

**States:**
```
CLOSED  → Requests pass through normally. Count failures.
OPEN    → After N failures in time window, stop all requests (fast-fail).
          Return error immediately without calling downstream.
HALF-OPEN → After timeout, allow limited requests through to test recovery.
            If success → CLOSED. If fail → OPEN again.
```

**Benefits:** Prevent cascading failures, fast-fail vs slow timeout, automatic recovery.

**Implementation:** Hystrix (deprecated), Resilience4j, Polly (.NET).

---

### 🔴 Service Mesh

**What:** Infrastructure layer for service-to-service communication. Handles: encryption, observability, traffic management, retries, circuit breaking — without changing application code.

**Architecture:**
- **Sidecar proxy** (Envoy) injected alongside each service
- **Control plane** (Istio, Linkerd) manages proxy configuration

**Features:**
- Mutual TLS (mTLS) between all services
- Distributed tracing (automatically propagate trace IDs)
- Traffic splitting (canary deployments, A/B testing)
- Retries, timeouts, circuit breaking
- Load balancing policies

**When to use:** Large microservices deployments (20+ services), when security and observability are critical, multi-team environments.

---

### 🟡 Saga Pattern (Distributed Transactions)

**Problem:** Distributed transactions across microservices are hard (no 2PC at scale).

**Saga:** Sequence of local transactions. If one fails, execute compensating transactions to undo previous steps.

**Choreography (Event-driven):**
```
OrderService → (OrderCreated event) → PaymentService → (PaymentDone event) → InventoryService
If InventoryService fails → emit InventoryFailed → PaymentService reverses payment → OrderService cancels
```

**Orchestration (Centralized):**
```
SagaOrchestrator tells each service what to do step by step.
Easier to track state, but orchestrator is another service to maintain.
```

---

## 12. Distributed Systems Concepts

### 🟡 Consistency Models

| Model | Description | Example |
|-------|------------|---------|
| **Strong Consistency** | Read always returns latest write | Single-node DB |
| **Linearizability** | Operations appear instantaneous and ordered | Zookeeper, etcd |
| **Sequential Consistency** | All processes see operations in same order (not necessarily real-time) | |
| **Causal Consistency** | Causally related operations seen in order; unrelated can be in any order | MongoDB sessions |
| **Eventual Consistency** | Given no new updates, all replicas converge | Cassandra, DynamoDB default |
| **Read-your-writes** | After write, subsequent reads see that write | Session consistency |

---

### 🟡 Consensus Algorithms

**Paxos:**
- Classic consensus algorithm
- Hard to understand and implement
- Basis for many distributed databases

**Raft:**
- Understandable alternative to Paxos
- Leader election + log replication
- Used by: etcd, CockroachDB, TiKV, Consul

**Raft Phases:**
```
1. Leader Election:
   - All nodes start as FOLLOWER
   - If no heartbeat → become CANDIDATE → request votes
   - Majority votes → become LEADER
   - Leader sends heartbeats to prevent re-elections

2. Log Replication:
   - Client → Leader → Leader appends to log
   - Leader sends entry to all followers
   - Majority acknowledge → commit
   - Leader notifies followers → they commit
```

---

### 🟡 Distributed Clocks

**Problem:** No global clock in distributed systems. Each node has its own clock (clock skew, clock drift).

**Logical Clocks (Lamport Timestamps):**
- Counter incremented with each event
- Establish causal ordering (if A → B, then timestamp(A) < timestamp(B))
- But: timestamp(A) < timestamp(B) doesn't mean A → B

**Vector Clocks:**
- Each node has a vector of counters (one per node)
- Can determine causality or concurrency
- Example: `[A:3, B:2, C:1]`

**Hybrid Logical Clocks (HLC):**
- Combine physical time with logical counters
- Used by CockroachDB for multi-version concurrency

---

### 🟡 Two-Phase Commit (2PC)

**Phase 1 (Prepare):** Coordinator asks all participants: "Can you commit?"
- Each participant writes to WAL and replies Yes/No

**Phase 2 (Commit/Abort):** If all said Yes → send Commit. If any said No → send Abort.

**Problems:**
- Blocking: If coordinator dies after Phase 1, participants are stuck
- Single point of failure (coordinator)
- High latency (2 round trips + disk writes)

**Three-Phase Commit (3PC):** Adds pre-commit phase to reduce blocking. Still has issues.

**Better alternatives:** Saga pattern, Paxos/Raft-based distributed transactions (CockroachDB, Spanner).

---

### 🟡 Distributed Locking

**Use case:** Ensure only one instance does something at a time (e.g., cron job, cache population).

**Redis-based lock (Redlock):**
```
1. Get current time T1
2. Try to acquire lock on N/2+1 Redis nodes with TTL
3. If acquired on majority AND (T2-T1) < TTL → lock held
4. On release: delete from all nodes
5. If failed → release all partial locks
```

**⚠️ Nuance:** Even Redlock has edge cases (GC pauses, network delays cause lock expiry during hold). For strong guarantees use ZooKeeper or etcd (Raft-based).

---

## 13. Rate Limiting & Throttling

### 🟢 Why Rate Limit?
- Prevent abuse / DoS attacks
- Fair resource allocation
- Protect downstream services
- Enforce business rules (API tiers)

---

### 🟡 Rate Limiting Algorithms

**Token Bucket:**
- Bucket holds `N` tokens. Tokens added at rate `r`.
- Each request consumes a token. If empty → reject.
- Allows bursts (up to bucket size).
- **Use:** Most common. Allows short bursts, enforces average rate.

**Leaky Bucket:**
- Requests enter a queue (bucket). Processed at constant rate.
- If queue full → reject.
- Smooths out traffic (no bursts allowed).
- **Use:** Traffic shaping, ensuring downstream receives steady flow.

**Fixed Window Counter:**
- Count requests in fixed time window (1 min). If count > limit → reject.
- **Problem:** Boundary attack: 100 req at 11:59 + 100 req at 12:00 = 200 req in 2 seconds.

**Sliding Window Log:**
- Store timestamp of each request. Count timestamps in last N seconds.
- Precise but memory-intensive.

**Sliding Window Counter:**
- Hybrid: fixed window count weighted by overlap with previous window.
- `current_count = prev_count × (1 - overlap%) + curr_count`
- Memory efficient, approximate but good enough.

---

### 🟡 Distributed Rate Limiting

**Problem:** Multiple API server instances — how to share rate limit state?

**Solutions:**
1. **Redis counter:** Atomic increment in Redis. `INCR user:123:req` with TTL.
2. **Redis sliding window:** Store timestamps in sorted set.
3. **API Gateway:** Centralized rate limiting at gateway level (Kong, AWS API GW).
4. **Token bucket in Redis:** Use Lua script for atomic check-and-decrement.

---

## 14. Authentication & Authorization

### 🟢 Authentication vs Authorization

| | Authentication (AuthN) | Authorization (AuthZ) |
|--|----------------------|----------------------|
| **Question** | Who are you? | What can you do? |
| **Mechanism** | Passwords, OAuth, MFA | RBAC, ABAC, ACL |
| **Example** | Login with Google | Can this user delete posts? |

---

### 🟢 Session-based vs Token-based

**Session-based:**
```
1. User logs in → Server creates session in DB/Redis → Returns session ID cookie
2. Subsequent requests: Cookie sent → Server looks up session → Validates
```
- State stored on server (stateful)
- Easy to invalidate (delete session)
- Doesn't scale well horizontally without shared session store

**Token-based (JWT):**
```
1. User logs in → Server creates JWT (signed with secret) → Returns token
2. Client stores token (localStorage / httpOnly cookie)
3. Subsequent requests: Token sent in Authorization header
4. Server validates signature → No DB lookup needed
```
- Stateless (server doesn't store session)
- Scales easily
- Hard to invalidate (must wait for expiry or maintain blocklist)

**JWT Structure:**
```
Header.Payload.Signature

Header:  {"alg": "HS256", "typ": "JWT"}
Payload: {"sub": "user123", "roles": ["admin"], "exp": 1234567890}
Signature: HMACSHA256(base64(header) + "." + base64(payload), secret)
```

**⚠️ JWT Nuances:**
- Never store sensitive data in payload (it's base64 encoded, not encrypted)
- Short expiry (15-30 min) + refresh token for long sessions
- Use `httpOnly` cookies to prevent XSS token theft
- Rotation of refresh tokens prevents theft

---

### 🟡 OAuth 2.0 & OpenID Connect

**OAuth 2.0:** Authorization framework — delegates access without sharing credentials.

**Flows:**
| Flow | Use Case |
|------|---------|
| **Authorization Code** | Web apps with server-side (most secure) |
| **Authorization Code + PKCE** | Single-page apps, mobile apps |
| **Client Credentials** | Service-to-service (no user involved) |
| **Device Flow** | Smart TVs, CLI tools |
| **Implicit** (deprecated) | Old SPAs (avoid) |

**Authorization Code Flow:**
```
1. Client → Authorization Server: redirect with client_id, scope, code_challenge
2. User logs in, grants permission
3. Auth Server → Client: authorization code (short-lived)
4. Client → Auth Server: exchange code + code_verifier for access_token + refresh_token
5. Client → Resource Server: access_token in Authorization header
```

**OpenID Connect (OIDC):** Layer on top of OAuth 2.0 for authentication (adds ID token, UserInfo endpoint, standardized claims).

---

### 🟡 RBAC vs ABAC

**RBAC (Role-Based Access Control):**
- Permissions assigned to roles; users assigned to roles
- Simple, scalable, easy to audit
- Example: `Admin can delete users`, `Editor can edit posts`

**ABAC (Attribute-Based Access Control):**
- Rules based on attributes (user, resource, environment)
- More flexible, fine-grained
- Example: `User can edit post IF user.id == post.authorId AND time.isBusinessHours`
- More complex to manage

**PBAC (Policy-Based):** ABAC with explicit policy language (OPA / Rego, Cedar by AWS).

---

## 15. Monitoring, Observability & Alerting

### 🟢 The Three Pillars of Observability

| Pillar | What it tells you | Tools |
|--------|------------------|-------|
| **Metrics** | Aggregated numeric measurements over time | Prometheus, Datadog, CloudWatch |
| **Logs** | Event records with context | ELK (Elasticsearch+Logstash+Kibana), Loki, Splunk |
| **Traces** | Request journey across services | Jaeger, Zipkin, AWS X-Ray, Datadog APM |

---

### 🟢 Key Metrics to Monitor

**The Four Golden Signals (Google SRE):**
1. **Latency:** Time to serve a request (p50, p95, p99 percentiles)
2. **Traffic:** How much demand (RPS, QPS)
3. **Errors:** Rate of failed requests (5xx, timeouts)
4. **Saturation:** How "full" the service is (CPU %, memory, queue depth)

**RED Method (for services):**
- **R**ate — Requests per second
- **E**rrors — Error rate
- **D**uration — Latency distribution

**USE Method (for resources):**
- **U**tilization — % time resource is busy
- **S**aturation — Amount of work queued
- **E**rrors — Error count

---

### 🟡 Distributed Tracing

**Problem:** A request touches 10 microservices. Which one is slow?

**Solution:** Trace ID propagated through all services. Each service records a "span".

```
Trace: [trace_id: abc123]
  Span: API Gateway  (0ms - 120ms)
    Span: User Service  (2ms - 30ms)
    Span: Order Service  (31ms - 115ms)
      Span: DB Query  (32ms - 110ms)  ← Bottleneck!
```

**Standards:** OpenTelemetry (OpenTracing + OpenCensus merged), W3C Trace Context

---

### 🟡 Alerting Best Practices

**Alert on symptoms, not causes:**
- Good: "Error rate > 1% for 5 minutes"
- Bad: "CPU > 80%"

**SLI, SLO, SLA:**
| Term | Definition | Example |
|------|-----------|---------|
| **SLI** (Service Level Indicator) | Metric that measures service level | 99th percentile latency = 250ms |
| **SLO** (Service Level Objective) | Target for SLI | p99 latency < 500ms 99.9% of time |
| **SLA** (Service Level Agreement) | Contract with consequences | 99.9% availability; credits if breached |

**Error Budget:** `1 - SLO`. If SLO = 99.9%, error budget = 0.1% (43 min/month). Used to balance reliability vs feature velocity.

---

## 16. System Design Patterns

### 🟡 CQRS (Command Query Responsibility Segregation)

**What:** Separate read model (Query) from write model (Command).

```
Client → Command API → Write DB → Event → Read Projection → Read DB → Query API → Client
```

**Why:** Read and write requirements often differ:
- Write: normalized, transactional
- Read: denormalized, optimized for specific views

**Use when:** Complex domain, high read/write imbalance, different scaling needs for reads vs writes.

---

### 🟡 Event Sourcing

**What:** Store state changes as immutable sequence of events. Derive current state by replaying events.

```
Traditional DB: store current balance = $100
Event Sourcing: store events:
  Deposited $200
  Withdrew $50
  Deposited $100
  Withdrew $150
  → Replay = $200 - $50 + $100 - $150 = $100
```

**Benefits:** Full audit log, time-travel queries, replay for new projections, natural fit with CQRS.

**Challenges:** Event schema evolution, eventual consistency, complex queries, storage growth.

---

### 🟡 Outbox Pattern (Reliable Event Publishing)

**Problem:** Write to DB and publish event to MQ atomically (you can't have a distributed transaction between DB and MQ).

**Solution:**
```
1. Write to DB AND write event to outbox table in same transaction
2. Outbox poller reads unpublished events → publishes to MQ → marks as published
```

**Better:** Debezium reads DB write-ahead log (CDC) and publishes to Kafka — no polling needed.

---

### 🟡 Strangler Fig Pattern

**What:** Gradually migrate monolith to microservices by routing traffic at API gateway.

```
All traffic → Monolith
Gradually:
  /users traffic → User Microservice
  /orders traffic → Order Microservice
  remaining → Monolith (shrinking)
```

---

### 🟡 Bulkhead Pattern

**What:** Isolate different consumers of a service into separate thread pools / resource buckets.

```
Without bulkhead: slow /export endpoint exhausts all threads → /login also fails
With bulkhead:    /export gets 20 threads, /login gets 50 threads — isolated failure
```

---

### 🔴 Backpressure

**Problem:** Fast producer, slow consumer → consumer overwhelmed.

**Strategies:**
1. **Block producer:** Producer waits until consumer ready (TCP flow control)
2. **Drop messages:** Shed load, sample data (metrics use this)
3. **Buffer:** Queue messages (limited — queue fills up)
4. **Scale consumer:** Add more consumer instances
5. **Signal back to user:** HTTP 429, slow down user requests

**Reactive Streams:** Standard for async stream processing with backpressure (RxJava, Project Reactor).

---

## 17. Classic Design Problems

### 🔴 Design a URL Shortener (bit.ly)

**Requirements:**
- Shorten URL: `bit.ly/xkcd` → `https://xkcd.com/`
- Redirect: visit short URL → redirect to long URL
- Analytics: click counts, geography
- Scale: 100M URLs, 10B redirects/day

**Key Decisions:**

**ID Generation:**
```
Option 1: MD5(long_url) → take first 7 chars
  Problem: collisions, same URL → same short code

Option 2: Auto-increment ID → Base62 encode
  IDs: 1 → "1", 61 → "z", 62 → "10" etc.
  7 chars Base62 = 62^7 ≈ 3.5 trillion URLs ✓
  Problem: sequential, predictable

Option 3: Random 7-char Base62 (UUID-based)
  Problem: must check for collisions

Option 4: Distributed ID generator (Snowflake)
  Unique, roughly sequential, no central coordination ✓
```

**Data Model:**
```sql
urls: (short_code VARCHAR(7) PK, long_url TEXT, user_id, created_at, expiry)
clicks: (short_code, timestamp, country, device) → time-series DB or Kafka + batch
```

**Caching:** Cache short_code → long_url in Redis (most URLs are read-heavy).

**Architecture:**
```
Client → CDN/Load Balancer → Redirect Service (stateless) → Redis Cache → DB
Analytics: Click event → Kafka → Stream Processor → Analytics DB (Druid/ClickHouse)
```

---

### 🔴 Design Twitter/X Feed

**Core Problems:**
1. **Fan-out on write (push model):** When you tweet, pre-compute feed for all followers.
   - Fast reads, slow writes
   - Problem: celebrities with 100M followers → 100M writes per tweet

2. **Fan-out on read (pull model):** When you load feed, fetch from all followed accounts.
   - Fast writes, slow reads
   - Problem: follows 5000 accounts → 5000 DB reads per load

3. **Hybrid model (Twitter's actual approach):**
   - Regular users: fan-out on write (push to followers' feed cache)
   - Celebrities: fan-out on read (fetch from their timeline on load)
   - Combine: user feed = cached + celebrity tweets fetched on read

**Feed Storage:**
```
Redis sorted set: key=user_id, members=tweet_ids, scores=timestamps
ZADD feed:user123 1703000000 tweet456
ZRANGE feed:user123 0 19 WITHSCORES REV  → Latest 20 tweets
```

---

### 🔴 Design a Distributed Cache

**Requirements:** Key-value store, sub-millisecond reads, horizontal scale.

**Consistent Hashing:** Distribute keys across cache nodes. On node add/remove, minimal key migration.

**Replication:** Each key on 2-3 nodes. Read from any; write to all. Gossip protocol for cluster membership.

**Eviction:** LRU per node.

**Client Library:** Knows cluster topology, routes requests directly to correct node (no proxy hop).

---

### 🔴 Design a Rate Limiter

```
Architecture:
  Client → API Gateway → Rate Limiter (Redis) → Backend

Redis Sliding Window Implementation:
  Key: ratelimit:{user_id}:{minute}
  Command: INCR key; EXPIRE key 60
  If value > limit → reject with 429
  
  For sliding window:
  Script (Lua for atomicity):
    ZADD timestamps:{user_id} now now
    ZREMRANGEBYSCORE timestamps:{user_id} 0 (now - window)
    count = ZCARD timestamps:{user_id}
    if count > limit → reject
    EXPIRE timestamps:{user_id} window
```

---

### 🔴 Design a Notification System

**Requirements:** Push notifications, email, SMS. At-scale (1B users).

**Architecture:**
```
Trigger Service → Message Queue (Kafka by type)
  → Email Workers    → SendGrid/SES
  → Push Workers     → APNs (iOS) / FCM (Android)
  → SMS Workers      → Twilio/SNS

Key considerations:
  - Priority queues (OTP > marketing)
  - Deduplication (same notification sent twice)
  - User preferences (unsubscribe, quiet hours)
  - Delivery tracking and retry
  - Template rendering service
```

---

## 18. Master Cheat Sheet

### 🔑 Component Selection Guide

| Need | Solution |
|------|---------|
| Store structured data with relations | PostgreSQL / MySQL |
| Flexible document storage | MongoDB |
| Key-value cache | Redis |
| Time-series metrics | InfluxDB / TimescaleDB |
| Full-text search | Elasticsearch |
| Graph relationships | Neo4j |
| Massive write throughput | Cassandra |
| Globally distributed SQL | CockroachDB / Spanner |
| Event streaming + replay | Kafka |
| Task queue + routing | RabbitMQ |
| Managed serverless queue (AWS) | SQS |
| Distributed file storage | HDFS / S3 |
| CDN / static assets | CloudFront / Cloudflare |
| Service discovery | Consul / Eureka |
| Configuration management | etcd / Consul |
| Distributed coordination | ZooKeeper |
| Container orchestration | Kubernetes |
| Service mesh | Istio / Linkerd |

---

### 🔑 Scaling Playbook

```
Step 1: Vertical scaling (buy bigger server) — simplest
Step 2: Add caching (Redis) — eliminate repeated DB reads
Step 3: Add read replicas — distribute read traffic
Step 4: CDN for static assets — reduce server load
Step 5: Load balancing across multiple stateless app servers
Step 6: Database sharding — distribute write load
Step 7: Async processing with message queues — handle traffic spikes
Step 8: Microservices — scale individual bottleneck components
Step 9: Geographical distribution — reduce latency for global users
```

---

### 🔑 Numbers to Know

```
Throughput targets:
  Typical web server:     10k req/s
  Redis:                  1M ops/s
  Kafka:                  1M+ msgs/s
  MySQL (optimized):      100k reads/s
  Cassandra (write):      500k writes/s

Storage estimates:
  1 tweet (140 chars):    ~300 bytes
  1 photo (compressed):   ~1 MB
  1 video (1 min, 720p):  ~50 MB

Traffic estimates:
  Twitter: ~500M tweets/day = ~6k tweets/sec
  Facebook: 2B DAU, ~100B+ interactions/day
  Netflix: ~15% of global internet traffic at peak
```

---

### 🔑 Interview Framework (RESHADED)

```
R — Requirements: Functional + Non-functional
E — Estimation: Traffic, storage, bandwidth
S — Storage Schema: What tables/documents
H — High-Level Design: Core components, APIs
A — API Design: Endpoints and contracts
D — Deep Dive: Bottlenecks, scale each component
E — Evaluation: How does design meet requirements?
D — Distinctive Features: What makes this design good?
```

---

### 🔑 CAP + Database Quick Reference

```
CP (Consistent + Partition Tolerant):
  → MongoDB, Redis Cluster, HBase, ZooKeeper, etcd
  → Use for: Financial data, distributed locks, config management

AP (Available + Partition Tolerant):
  → Cassandra, DynamoDB, CouchDB
  → Use for: Social media, recommendations, shopping carts

CA (Single-node only, not truly distributed):
  → Single-node PostgreSQL, MySQL
  → Use for: Traditional apps, ACID transactions
```

---

### 🔑 Trade-off Summary

```
Latency     vs  Throughput    → Batching increases throughput at cost of latency
Consistency vs  Availability  → CAP theorem: pick 2 of 3 in distributed systems
Space       vs  Time          → Caching/indexing: more memory for faster compute
Read speed  vs  Write speed   → Indexing/normalization: indexes speed reads, slow writes
Simplicity  vs  Scalability   → Monolith is simpler; microservices scale better
Coupling    vs  Performance   → Async messaging decouples but adds latency
```

---

*Last updated: May 2026. Covers FAANG-level system design interviews, from junior through principal engineer.*
