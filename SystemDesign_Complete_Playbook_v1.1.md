# System Design — Complete Interview Playbook (v1.1)

> **What's new in v1.1:** PACELC theorem, Idempotency (keys + patterns), Reverse Proxy vs Forward Proxy, TLS internals, VPC/VPN/Subnets, API pagination, Webhooks, Connection pooling, Vector databases, NewSQL, CDC deep dive, MVCC + write skew, WAL, LSM internals, Gossip Protocol, CRDTs, Fencing tokens, Zero Trust, OWASP API Security, Chaos Engineering, Structured logging, 13 new Classic Design Problems (Autocomplete, Uber, Ticketmaster, Payment, Video Streaming, Web Crawler, Yelp/Proximity, Stock Exchange, Key-Value Store from scratch, Google Drive, Ad Click Aggregation, Job Scheduler, Google Maps), Kubernetes architecture, Auto-scaling, Multi-region failover, Disaster Recovery.
>
> **How to use:** Each topic follows: *What it is → When to use → Nuances/Gotchas → Deep dive → Comparisons.*
> Scan the **Master Cheat Sheet** at the end before any interview.
> Difficulty: 🟢 Fundamental → 🟡 Intermediate → 🔴 Expert.

---

## 📋 Quick-Reference Index

| # | Topic |
|---|-------|
| 1 | [Core Principles](#1-core-principles) |
| 2 | [Networking Fundamentals](#2-networking-fundamentals) |
| 3 | [APIs](#3-apis) |
| 4 | [Load Balancing](#4-load-balancing) |
| 5 | [Databases Deep Dive](#5-databases-deep-dive) |
| 6 | [Database Internals](#6-database-internals) |
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
| 17 | [Classic Design Problems (18 Problems)](#17-classic-design-problems) |
| 18 | [Infrastructure & Cloud](#18-infrastructure--cloud) |
| 19 | [Master Cheat Sheet](#19-master-cheat-sheet) |

---

## 1. Core Principles

### 🟢 Scalability

#### Vertical Scaling (Scale Up)
- Add more CPU, RAM, disk to a single machine.
- **Pros:** Simple, no code changes, no distributed system complexity.
- **Cons:** Hard physical limit, single point of failure, expensive at top end.
- **When to use:** Databases (easier than horizontal), early-stage startups, write-heavy workloads.

#### Horizontal Scaling (Scale Out)
- Add more machines/instances.
- **Pros:** Virtually unlimited scale, fault tolerant, cost-effective.
- **Cons:** Requires stateless design, distributed system complexity.

---

### 🟢 Reliability, Availability, Durability

| Concept | Definition | Formula |
|---------|-----------|---------|
| **Availability** | % of time system is operational | Uptime / (Uptime + Downtime) |
| **Reliability** | System works correctly over time | MTTF / (MTTF + MTTR) |
| **Durability** | Data is not lost | "11 nines" for S3 |

**Availability Nines:**
```
99%       → 3.65 days/year downtime
99.9%     → 8.76 hours/year
99.99%    → 52.6 minutes/year
99.999%   → 5.26 minutes/year (five nines — gold standard)
```

---

### 🟡 Fault Tolerance vs High Availability

These are **distinct** concepts frequently conflated in interviews.

| | High Availability (HA) | Fault Tolerance (FT) |
|--|------------------------|---------------------|
| **Goal** | Minimize downtime | Zero downtime |
| **Approach** | Detect failure, failover quickly | Mask failure completely |
| **User impact** | Brief interruption during failover | No interruption |
| **Cost** | Lower | Higher (redundant active capacity) |
| **Pattern** | Active-Passive | Active-Active |

**Active-Passive Failover:**
- One primary serves all traffic. Standby monitors via heartbeat.
- On failure: standby promotes itself, DNS/LB redirects.
- Failover time: seconds to minutes.
- Example: Traditional database primary-replica with automated failover.

**Active-Active Failover:**
- Multiple nodes serve traffic simultaneously.
- On failure: remaining nodes absorb traffic (needs capacity headroom).
- Zero downtime, but requires careful state synchronization.
- Example: Multi-master databases (CockroachDB), stateless API servers behind LB.

**⚠️ Nuance:** Active-Active is harder because you must prevent split-brain — two nodes both think they're primary. Use fencing tokens, STONITH (Shoot The Other Node In The Head), or consensus protocols.

---

### 🟢 Latency vs Throughput

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

### 🟢 CAP Theorem

> In a distributed system, you can only guarantee **two of three**: Consistency, Availability, Partition Tolerance.

**The real nuance:** Network partitions WILL happen. So you always need P. The real choice is: **CP** (sacrifice availability when partition occurs) vs **AP** (sacrifice consistency when partition occurs).

```
CP Systems:  HBase, ZooKeeper, Etcd, MongoDB (default), Redis Cluster
AP Systems:  Cassandra, DynamoDB, CouchDB, Riak
```

---

### 🟡 PACELC Theorem (CAP Extension)

**CAP's limitation:** Only talks about behavior during partitions. What about when there's no partition?

**PACELC:** Partition → Availability vs Consistency. Else (no partition) → Latency vs Consistency.

```
             During Partition (P):        Else (E):
             Choose A or C               Choose L or C

System       PA/EL      PA/EC      PC/EL      PC/EC
---------    ------     ------     ------     ------
DynamoDB     PA/EL      (default) — fast reads, eventual consistency
Cassandra    PA/EL      — tunable but defaults to low latency
MongoDB      PA/EC      — strong consistency possible
MySQL        PC/EC      — strong consistency, higher latency
CockroachDB  PC/EC      — serializable, accepts latency cost
```

**Why PACELC matters in interviews:** When asked "what tradeoffs does your design make?", go beyond CAP — explain latency tradeoffs in the normal (non-partition) path too.

---

### 🟢 ACID vs BASE

#### ACID Properties — Deep Dive

| Property | Definition | Example |
|----------|-----------|---------|
| **Atomicity** | All or nothing — no partial updates | Bank transfer: debit + credit both succeed or both fail |
| **Consistency** | Transaction brings DB from valid state to valid state | Balance constraint never violated |
| **Isolation** | Concurrent transactions appear serial | Two transfers don't corrupt each other |
| **Durability** | Committed data survives crashes | Written to WAL (Write-Ahead Log) before commit |

**Isolation Levels:**

| Level | Dirty Read | Non-repeatable Read | Phantom Read |
|-------|-----------|---------------------|-------------|
| READ UNCOMMITTED | Possible | Possible | Possible |
| READ COMMITTED | Prevented | Possible | Possible |
| REPEATABLE READ | Prevented | Prevented | Possible |
| SERIALIZABLE | Prevented | Prevented | Prevented |

#### BASE (NoSQL)
- **Basically Available** → AP over CP
- **Soft state** → state changes without input (eventual replication)
- **Eventually Consistent** → all replicas converge given time

---

### 🟢 Idempotency

**Definition:** An operation is idempotent if performing it multiple times produces the same result as performing it once.

**Why it matters:** In distributed systems, networks fail. Retries are necessary. Without idempotency, retries cause duplicate operations (charging a user twice, creating duplicate orders).

**Idempotency by HTTP method:**
```
GET, HEAD, OPTIONS: Always idempotent (read-only)
PUT, DELETE:        Should be idempotent (replace/delete same thing)
POST:               NOT idempotent by default (creates new resources)
PATCH:              May or may not be idempotent
```

**Idempotency Key Pattern:**
```
Client generates unique key: idempotency_key = UUID()

POST /payments
Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
Body: { amount: 100, to: "alice" }

Server behavior:
  First request  → Process payment → Store result with key
  Retry request  → Key already seen → Return cached result (don't reprocess)
  Different key  → Process as new payment
```

**Implementation:**
```
1. Client: generate UUID per unique logical operation, retry with same UUID
2. Server: check Redis/DB for idempotency_key
3. If found: return cached response (same status + body)
4. If not found: process → store {key: response} with TTL (e.g., 24h) → return

Storage schema:
  idempotency_keys:
    key        VARCHAR(64)  PRIMARY KEY
    user_id    BIGINT
    response   JSONB        -- cached response body + status code
    created_at TIMESTAMP
    expires_at TIMESTAMP    -- TTL
```

**⚠️ Nuances:**
- Key must be scoped to (user, operation type) to prevent replay attacks across users.
- Store the result atomically with the operation (use DB transactions or Redis SETNX).
- Different idempotency key ≠ same request — it's a new operation.

---

### 🟡 Back-of-Envelope Estimation Framework

**Always do estimation before designing.** Shows structured thinking.

**Step-by-step framework:**
```
1. Clarify scale: DAU, writes/day, reads/day, data size
2. Convert to QPS: (operations/day) / 86,400 ≈ ÷ 100,000 for rough math
3. Estimate storage: (records/day) × (bytes/record) × (retention days)
4. Estimate bandwidth: QPS × (payload size)
5. Estimate infrastructure: servers needed = QPS / (QPS per server)
```

**Worked Example — Twitter-scale:**
```
Assumptions:
  300M DAU, each user reads feed 10×/day, posts 1 tweet/week average

Writes:
  300M / 7 ≈ 43M tweets/day = 43M / 86,400 ≈ 500 tweet writes/sec

Reads (fan-out on write):
  300M users × 10 reads/day = 3B reads/day = 35,000 reads/sec

Storage:
  Tweet: 140 chars ≈ 300 bytes + metadata ≈ 1KB
  43M tweets/day × 1KB = 43 GB/day ≈ 15 TB/year

Media (10% of tweets have image):
  4.3M images/day × 200KB avg = 860 GB/day

Bandwidth:
  35,000 reads/sec × 1KB per feed entry ≈ 35 MB/s read
  500 writes/sec × 1KB = 0.5 MB/s write
```

**Key numbers to know:**
```
1 day ≈ 100,000 seconds (86,400 for precision)
1 month ≈ 2.5M seconds
1 year ≈ 30M seconds
1 KB = 1,024 bytes ≈ 1,000 bytes (use 1000 for estimation)
1 MB = 10^6 bytes
1 GB = 10^9 bytes
1 TB = 10^12 bytes

Characters in a tweet: 280 → ~300 bytes
A user record: ~1 KB
A photo: ~1 MB compressed
A video (1 min): ~50 MB
```

---

### 🟡 SLI, SLO, SLA and Error Budgets

| Term | Definition | Example |
|------|-----------|---------|
| **SLI** (Service Level Indicator) | Metric that measures service level | p99 latency = 250ms |
| **SLO** (Service Level Objective) | Target for SLI | p99 latency < 500ms, 99.9% of the time |
| **SLA** (Service Level Agreement) | Contract with penalties for breach | 99.9% uptime; 10% credit if breached |

**Error Budget:**
```
SLO = 99.9% → Error budget = 0.1% = 43 min/month of allowed downtime/errors

Error budget burn rate:
  1× burn rate → use all budget in 30 days (sustainable)
  2× burn rate → use all budget in 15 days (warning)
  10× burn rate → use all budget in 3 days (page immediately)

Fast burn alert: if last 1h burn rate will exhaust 2% of monthly budget
Slow burn alert: if last 6h burn rate exceeds 5% of monthly budget
```

**Practical implications:**
- When error budget is healthy → deploy freely, move fast.
- When error budget is low → freeze deployments, focus on reliability.
- Error budget burns from: latency spikes, error spikes, planned downtime.

---

## 2. Networking Fundamentals

### 🟢 OSI Model

```
Layer 7 — Application   → HTTP, DNS, SMTP, gRPC
Layer 4 — Transport     → TCP (reliable), UDP (fast)
Layer 3 — Network       → IP, routing
Layer 2 — Data Link     → MAC, Ethernet
Layer 1 — Physical      → Cables, fiber, radio
```

---

### 🟢 TCP vs UDP

| | TCP | UDP |
|--|-----|-----|
| **Connection** | 3-way handshake | Connectionless |
| **Reliability** | Guaranteed | Best-effort |
| **Speed** | Slower | Faster |
| **Use cases** | HTTP, databases, SSH | DNS, video, gaming |

---

### 🟢 HTTP/1.1 vs HTTP/2 vs HTTP/3

| | HTTP/1.1 | HTTP/2 | HTTP/3 |
|--|----------|--------|--------|
| **Transport** | TCP | TCP | QUIC (UDP) |
| **Multiplexing** | No (HOL blocking) | Yes (streams) | Yes (independent streams) |
| **Header compression** | No | HPACK | QPACK |
| **0-RTT** | No | No | Yes |

---

### 🟡 Reverse Proxy vs Forward Proxy

**Extremely common interview topic — know this cold.**

| | Forward Proxy | Reverse Proxy |
|--|--------------|--------------|
| **Position** | Client side | Server side |
| **Who configures it** | Client | Server admin |
| **Client knows real server?** | Yes | No (proxy hides it) |
| **Server knows real client?** | No (proxy hides it) | No (sees proxy IP) |
| **Use cases** | Bypass geo-restrictions, corporate internet filtering, caching for users, anonymity | Load balancing, SSL termination, caching, WAF, rate limiting |

**Reverse Proxy — what it does:**
```
Client → [Reverse Proxy: nginx/Cloudflare] → Backend Server A
                                           → Backend Server B

Benefits:
  1. Load balancing — distribute across backends
  2. SSL/TLS termination — decrypt once at proxy, plaintext internally
  3. Caching — return cached responses without hitting backend
  4. Compression — gzip responses
  5. Security — hide backend topology, WAF, DDoS protection
  6. Rate limiting — at the edge
  7. A/B testing — route % of traffic to new version
```

**Forward Proxy — what it does:**
```
Client A → [Forward Proxy: Squid] → Internet → Server
Client B → [Forward Proxy: Squid] → Internet → Server

Use cases:
  Corporate: monitor/filter employee traffic
  VPN: tunnel through proxy to bypass geo-restrictions
  Cache: multiple clients share cached responses
```

**NGINX vs HAProxy vs Envoy:**

| | NGINX | HAProxy | Envoy |
|--|-------|---------|-------|
| **Primary purpose** | Web server + reverse proxy | High-performance L4/L7 proxy | L7 proxy / service mesh sidecar |
| **Performance** | High | Very high (best for pure proxying) | High |
| **Protocol support** | HTTP, HTTPS, SMTP, gRPC | TCP, HTTP, gRPC | HTTP, gRPC, TCP, many more |
| **Dynamic config** | Config file reload | Config file reload | xDS API (dynamic, no restart) |
| **Observability** | Basic metrics | Better stats | Excellent (Prometheus, tracing) |
| **Use for** | Web servers, API gateway, static files | High-throughput TCP/HTTP proxying | Service mesh sidecar, modern API gateway |
| **Config hot reload** | Signal (SIGHUP) | Socket-based | Real-time via xDS |

---

### 🟡 TLS Handshake Internals

```
TLS 1.3 Handshake (1-RTT):
  Client → Server: ClientHello (supported ciphers, key share, TLS version)
  Server → Client: ServerHello (chosen cipher, key share)
                   Certificate (server's public cert)
                   CertificateVerify (signature)
                   Finished
  Client → Server: Finished
  [Encrypted application data]

TLS 1.2 (2-RTT) — older, slower
TLS 1.3 (1-RTT) — session resumption: 0-RTT possible (replay attack risk)

Certificate chain:
  Leaf cert (your domain) → signed by Intermediate CA → signed by Root CA
  Browser trusts root CA → chain of trust validated

mTLS (Mutual TLS):
  Both client AND server present certificates
  Server verifies client identity (not just the other way)
  Used in: service mesh (Istio), gRPC between microservices, API-to-API auth
```

---

### 🟡 VPC, Subnets, NAT, VPN

```
VPC (Virtual Private Cloud): Isolated network in cloud
  ├── Public Subnet: Resources with internet access (LB, bastion host)
  │   └── Internet Gateway: Allows inbound/outbound internet
  └── Private Subnet: No direct internet (databases, app servers)
      └── NAT Gateway: Allow outbound internet from private subnet (no inbound)

NAT (Network Address Translation):
  - Private IPs (10.x.x.x, 172.16.x.x, 192.168.x.x) not routable on internet
  - NAT maps private IP:port → public IP:port
  - NAT Gateway (managed AWS) in public subnet handles this

Security Groups (Stateful):
  - Allow/deny inbound and outbound traffic by port/protocol/source
  - Stateful: if inbound allowed, response automatically allowed

Network ACLs (Stateless):
  - Subnet-level firewall, stateless (must explicitly allow return traffic)
  - Evaluated before security groups

VPN (Virtual Private Network):
  - Encrypted tunnel between corporate network and VPC
  - Site-to-Site VPN: office network ↔ VPC
  - Client VPN: individual developer ↔ VPC
```

---

### 🟢 DNS

```
Resolution: Browser → OS Cache → Recursive Resolver → Root → TLD → Authoritative

Record Types:
  A:     domain → IPv4          MX:    mail server
  AAAA:  domain → IPv6          TXT:   SPF, DKIM, verification
  CNAME: alias to another domain NS:    nameserver
```

---

### 🟢 CDN

```
Pull CDN: CDN fetches from origin on first miss → cache → return
Push CDN: You push content to CDN proactively

Use for: static assets, video, software downloads, DDoS protection
```

---

## 3. APIs

### 🟢 REST

**HTTP Methods:**
| Method | Action | Idempotent | Safe |
|--------|--------|-----------|------|
| GET | Read | Yes | Yes |
| POST | Create | No | No |
| PUT | Replace | Yes | No |
| PATCH | Partial update | No | No |
| DELETE | Delete | Yes | No |

---

### 🟡 API Versioning Strategies

| Strategy | Example | Pros | Cons |
|----------|---------|------|------|
| **URI versioning** | `/api/v1/users` | Explicit, cache-friendly | URL pollution |
| **Header versioning** | `Accept: application/vnd.api.v2+json` | Clean URLs | Less visible, harder to test |
| **Query param** | `?version=2` | Easy to test | Often overlooked |
| **Subdomain** | `v2.api.example.com` | Clear separation | DNS overhead |

**Best practice:** URI versioning for public APIs (most common). Header versioning for enterprise APIs. Never break existing versions — deprecate with sunset headers.

**gRPC versioning:** Use package names: `syntax = "proto3"; package users.v2;`. Or use field deprecation with `[deprecated = true]`.

**GraphQL versioning:** Evolve schema without versioning — add fields, deprecate old ones with `@deprecated(reason: "Use newField instead")`. Never remove fields immediately.

---

### 🟡 Idempotency Keys (API Design)

**Pattern for payment/order APIs:**
```
POST /api/v1/payments
Headers:
  Idempotency-Key: client-generated-uuid-v4
  Authorization: Bearer token

Flow:
  1. Receive request → check Redis: SETNX idempotency:{key} "processing" EX 86400
  2. If key exists with "processing" → return 409 (concurrent duplicate)
  3. If key exists with "done:{response}" → return cached response
  4. If key new → process payment → SET idempotency:{key} "done:{response}"
  5. Return result

Client retry logic:
  - Exponential backoff: 1s, 2s, 4s, 8s...
  - Max retries: 5
  - Same idempotency key on all retries
  - Give up after 30s total
```

---

### 🟡 API Pagination Strategies

| Strategy | How | Pros | Cons |
|----------|-----|------|------|
| **Offset pagination** | `?page=2&limit=20` → `OFFSET 40 LIMIT 20` | Simple, random access | Slow for large offsets, inconsistent if data changes |
| **Cursor pagination** | `?cursor=eyJ...&limit=20` | Consistent, fast, works with real-time data | No random access, can't jump to page N |
| **Keyset pagination** | `?after_id=1234&limit=20` → `WHERE id > 1234` | Fast (uses index), stable | Need unique sortable key |
| **Seek pagination** | `?after_created=2024-01-01&after_id=5` | Works with non-unique sort keys | More complex |

**When to use each:**
- **Offset:** Simple admin interfaces, small datasets, when page jumping needed.
- **Cursor/Keyset:** Infinite scroll, feeds, large datasets, real-time data. **Default choice.**

```
Cursor pagination response:
{
  "data": [...20 items...],
  "pagination": {
    "next_cursor": "eyJpZCI6MTIzNH0=",  // Base64 encoded {id: 1234}
    "has_more": true,
    "total": null  // Often omit for performance
  }
}
```

---

### 🟡 Webhook Design

**What:** Instead of client polling, server pushes events to client's registered URL.

```
Flow:
  1. Client registers webhook: POST /webhooks {url: "https://client.com/webhook", events: ["payment.success"]}
  2. Event occurs on server
  3. Server POST to client URL: {event: "payment.success", data: {...}, timestamp: "..."}
  4. Client processes → returns 200 OK within 5s
  5. Server marks delivered

Retry logic (server side):
  - If client returns 5xx or times out: retry with exponential backoff
  - Schedule: immediate, 1min, 5min, 30min, 2h, 1day
  - After max retries: mark as failed, notify client developer

Security:
  - HMAC signature: X-Webhook-Signature: sha256=HMAC(secret, payload)
  - Client verifies signature before processing
  - Timestamp in payload (prevent replay attacks): reject if > 5 min old
  - Use HTTPS only
  - Idempotency: events can be delivered multiple times; client must be idempotent

Reliability issues:
  - At-least-once delivery (may duplicate on retry)
  - Order not guaranteed (use sequence numbers)
  - Client downtime: queue events, retry
  - Backpressure: if client consistently slow, pause + alert
```

---

### 🟡 GraphQL, gRPC, WebSockets

**gRPC:**
```
REST vs GraphQL vs gRPC:
  REST:    External/public APIs, browser-first
  GraphQL: Complex client needs, mobile bandwidth concerns
  gRPC:    Internal microservices, low-latency, polyglot
```

**WebSockets:** Full-duplex, persistent — chat, live dashboards, collaborative editing.

**SSE (Server-Sent Events):** One-way server→client, automatic reconnect — live feeds, notifications.

---

## 4. Load Balancing

### 🟢 Load Balancing Algorithms

| Algorithm | How | Best for |
|-----------|-----|---------|
| Round Robin | Sequential distribution | Equal-capacity, stateless servers |
| Weighted Round Robin | Weight proportional to capacity | Heterogeneous servers |
| Least Connections | Fewest active connections | Long-lived connections |
| IP Hash | Hash(client IP) → server | Sticky sessions |
| Least Response Time | Lowest latency server | Latency-sensitive |

---

### 🟡 Graceful Shutdown / Connection Draining

**Problem:** Removing a server from rotation during deploy drops in-flight requests.

**Solution — Connection Draining:**
```
1. Signal LB to stop sending NEW requests to this instance
2. Wait for in-flight requests to complete (drain timeout: 30s typical)
3. After drain: shut down server process
4. LB removes from pool

Kubernetes: terminationGracePeriodSeconds + preStop hook
  preStop:
    exec:
      command: ["sleep", "5"]  # Allow LB to detect pod as terminating

Health check endpoint:
  /health/readiness: 200 OK when ready to serve
                     503 when draining (stops new traffic)
  /health/liveness:  200 OK while process alive
                     Failure triggers restart
```

---

### 🟡 Blue-Green and Canary Deployments

**Blue-Green Deployment:**
```
Blue (current production) → 100% traffic
Green (new version) → 0% traffic, ready

Deploy:
  1. Deploy new version to Green environment
  2. Run smoke tests on Green
  3. Switch LB to send 100% traffic to Green
  4. Blue becomes standby (instant rollback: flip back)
  5. After confidence: decommission Blue

Pros: Instant rollback, zero downtime
Cons: 2x infrastructure cost, DB migrations must be backward-compatible
```

**Canary Deployment:**
```
Route small % of traffic to new version, gradually increase:
  1% → 5% → 25% → 100%

Monitor error rate, latency at each step.
If metrics worsen → automatic rollback.

Implementation:
  - NGINX: split_clients based on $request_id
  - Kubernetes: two Deployments with different replica counts
  - Feature flags: route by user segment (beta users, internal users)
  - Istio: VirtualService with weight-based routing
```

---

### 🟡 Sticky Sessions

**Problem:** Session stored on one server — next request to different server loses session.
**Solutions:**
1. Sticky sessions (IP hash) — defeats load balancing.
2. **Centralized session store (Redis)** — best practice.
3. **Stateless JWT** — session in signed token, no server storage.

---

## 5. Databases Deep Dive

### 🟢 SQL vs NoSQL

**Choose SQL when:** Complex relationships, ACID transactions, strong consistency, ad-hoc queries.
**Choose NoSQL when:** Massive scale, flexible schema, simple access patterns, high write throughput.

---

### 🟡 SQL Databases Comparison

**PostgreSQL:** Most feature-rich open-source. JSONB, PostGIS, advanced indexing. Best for complex queries, analytics, GIS.

**MySQL:** Faster reads, simpler. LAMP stack, read-heavy web apps.

**CockroachDB / Google Spanner (NewSQL):**
- **What:** Globally distributed SQL databases.
- **Key innovation:** Raft consensus per shard → serializable ACID across shards → horizontal scalability without sacrificing SQL or ACID.
- **Architecture:** Sharded ranges of data. Each range replicated via Raft. Distributed transactions via two-phase commit on top of Raft.
- **Use for:** Global user databases, financial systems that need scale + consistency.
- **vs. Traditional SQL:** Slower for single-node workloads (Raft overhead), but linearly scalable.
- **vs. Cassandra:** CockroachDB gives you SQL and ACID; Cassandra trades consistency for raw throughput.

---

### 🟡 NoSQL Deep Dive

**Document (MongoDB):** Flexible schema, rich queries, ACID in v4+. Best for content, catalogs, hierarchical data.

**Key-Value (Redis):** Sub-millisecond, in-memory. Best for cache, sessions, leaderboards, pub/sub.

**Wide-Column (Cassandra):** Peer-to-peer, tunable consistency, massive write throughput. Best for time-series, IoT, event logging.

**Graph (Neo4j):** Nodes + relationships. Best for social networks, fraud detection, recommendations.

**Time-Series (InfluxDB, TimescaleDB):** Timestamped data with compression. Best for metrics, IoT.

**Search (Elasticsearch):** Inverted index, full-text search, near-realtime. Best for product search, log analytics.

---

### 🔴 Vector Databases

**What:** Store high-dimensional vector embeddings (from ML models) and support similarity search (nearest neighbor search).

**Why:** LLM applications, semantic search, recommendation engines, image similarity.

**How vector search works:**
```
1. Embed items (text, images) using ML model → float[] of 1536 dimensions (e.g., OpenAI embeddings)
2. Store vector in DB
3. Query: embed query → find k-nearest vectors by cosine similarity or L2 distance
4. Return associated data for those vectors

Brute force: O(n × d) — too slow for millions of vectors
ANN algorithms (Approximate Nearest Neighbor):
  HNSW (Hierarchical Navigable Small World): Graph-based, O(log n) query
  IVF (Inverted File Index): Cluster-based, O(1) with tradeoff
  LSH (Locality Sensitive Hashing): Hash-based
```

**Vector Database Comparison:**

| | Pinecone | pgvector (PostgreSQL) | Weaviate | Qdrant | Milvus |
|--|----------|----------------------|---------|--------|--------|
| **Type** | Managed, cloud-native | Extension | Open-source + managed | Open-source | Open-source |
| **Scale** | Very large | Moderate | Large | Large | Very large |
| **Filtering** | Yes | SQL | GraphQL | Rich | Yes |
| **Best for** | Production AI apps, no-ops | Existing PostgreSQL + vectors | Semantic search | Self-hosted AI | Billion-scale vectors |
| **Tradeoff** | Cost, vendor lock-in | Limited index types | Complex setup | Newer ecosystem | Complex ops |

**When to use what:**
```
Prototype / small scale:     pgvector (already have PostgreSQL)
Production AI app, managed:  Pinecone (simplest, most scalable managed)
Self-hosted, open-source:    Qdrant or Milvus
Hybrid search (text + vector): Weaviate or Elasticsearch (dense vector support)
```

**⚠️ Interview nuance:** Vector databases don't replace traditional databases. They're used alongside them — store full data in PostgreSQL, store vectors in Pinecone, join on IDs.

---

### 🟢 Database Normalization

**1NF:** Atomic values, no repeating groups.
**2NF:** 1NF + no partial dependency (non-key depends on entire composite key).
**3NF:** 2NF + no transitive dependency (non-key depends only on key).
**BCNF:** Every determinant is a candidate key.

**Denormalization:** Intentionally violate normalization for read performance. Trade-off: faster reads, slower writes, consistency risk. Used in OLAP, data warehouses, high-read APIs.

---

### 🟡 MVCC Deep Dive (Multi-Version Concurrency Control)

**What:** Readers never block writers; writers never block readers. Each transaction sees a consistent snapshot of the database.

**How it works (PostgreSQL):**
```
Every row has:
  xmin: transaction ID that created this version
  xmax: transaction ID that deleted/updated this version (0 if current)

Transaction snapshot: list of active transactions at start
A row version is VISIBLE if:
  xmin committed before snapshot AND (xmax = 0 OR xmax uncommitted OR xmax > snapshot)

Update = insert new version with new xmin + set xmax on old version
Delete = set xmax on current version

Old versions cleaned up by VACUUM (autovacuum in background)
```

**Write Skew (MVCC edge case):**
```
Scenario: Doctors on-call — at least one must be on call at all times
  Transaction A: reads 2 doctors on call → decides to go off call
  Transaction B: reads 2 doctors on call → decides to go off call
  Both commit → 0 doctors on call (constraint violated!)

This is write skew — each transaction reads consistently but writes to different rows.
Solution: SERIALIZABLE isolation (SSI in PostgreSQL 9.1+)
          or explicit SELECT FOR UPDATE to lock read rows

SSI (Serializable Snapshot Isolation): Detects and aborts transactions that would
create non-serializable behavior — no locking needed, just tracking dependencies.
```

---

## 6. Database Internals

### 🟢 Write-Ahead Log (WAL)

**What:** Before modifying data pages, write the change to a sequential log. On crash, replay WAL to recover.

```
Why sequential log is fast:
  Random writes to data pages: ~150µs (SSD) — expensive
  Sequential writes to WAL:    ~10µs — much faster

WAL flow:
  1. Client sends UPDATE
  2. Write change record to WAL (sequential, fast)
  3. Acknowledge to client (durable now — WAL is on disk)
  4. Background process applies change to actual data pages (random, slower)
  5. CHECKPOINT: flush dirty pages, record WAL position

WAL uses:
  - Crash recovery: replay WAL from last checkpoint
  - Streaming replication: ship WAL records to replicas
  - CDC (Change Data Capture): read WAL to capture changes
  - Point-in-time recovery: replay WAL to any timestamp
```

---

### 🟡 LSM-Tree Internals (Log-Structured Merge Tree)

**Used by:** Cassandra, RocksDB, LevelDB, HBase, InfluxDB.

```
Write path (fast, sequential):
  1. Write to WAL (durability)
  2. Write to MemTable (in-memory sorted tree, e.g., Red-Black tree)
  3. When MemTable full → flush to L0 SSTable (Sorted String Table) on disk
  4. Return success to client (very fast!)

Compaction (background):
  L0 SSTables → merge + sort → L1 SSTables
  L1 SSTables → merge → L2 SSTables
  (Each level N is 10× larger than level N-1)

Read path (slower):
  Check MemTable → Check L0 → Check L1 → Check L2 → ...
  Bloom filters per SSTable to skip levels quickly

Compaction strategies:
  Size-tiered:  Merge similar-sized SSTables — fewer compactions, space amplification
  Leveled:      Maintain strict levels — more compactions, better read performance
  FIFO:         For time-series (retain most recent)

Write amplification: Data written multiple times during compaction
Space amplification: Multiple versions of same key until compaction
Read amplification: May check multiple SSTables

Bloom filters in LSM:
  Each SSTable has a bloom filter
  "Is key K in this SSTable?" → false negatives impossible, false positives possible
  Dramatically reduces disk I/O for missing keys
```

---

### 🟡 Indexing Deep Dive

**B-Tree:** Default, good for equality and range queries. O(log n).
**LSM-Tree:** Write-optimized (Cassandra, RocksDB).
**Hash Index:** O(1) exact match, no range queries.
**Inverted Index:** Text search (Elasticsearch, full-text search).

**PostgreSQL Index Types:**
- **B-tree:** Default, equality, range, sorting.
- **GIN:** Full-text search, JSONB, arrays.
- **GiST:** Geometric types, PostGIS, range.
- **BRIN:** Huge tables with natural ordering (timestamps).
- **Partial:** `WHERE deleted = false` — smaller, faster.

---

### 🟡 Connection Pooling

**Problem:** Database connections are expensive — TCP handshake + authentication + memory allocation = ~50ms. If each request opens/closes a connection, you spend more time on overhead than work. PostgreSQL has a practical limit of ~300-500 connections before performance degrades.

**Solution:** Maintain a pool of persistent connections; reuse them.

```
Connection Pool (HikariCP, PgBouncer):
  - App starts → pre-open N connections to DB
  - Request arrives → borrow connection from pool
  - Query executes → return connection to pool
  - Pool manages idle connections, max size, timeouts

PgBouncer (PostgreSQL-specific proxy):
  Modes:
    Session pooling:      1 server connection per client session (least efficient)
    Transaction pooling:  Reuse server connection after each transaction (most efficient for most apps)
    Statement pooling:    Ultra-aggressive (breaks multi-statement transactions)

  Best practice: Transaction pooling mode + max_client_conn = 10000 + pool_size = 50-100

HikariCP (Java connection pool — fastest):
  hikari.maximumPoolSize=20    // Max connections to DB
  hikari.minimumIdle=5         // Min idle connections
  hikari.connectionTimeout=30000  // Max wait for connection (ms)
  hikari.idleTimeout=600000    // Close idle connections after 10min
  hikari.maxLifetime=1800000   // Recycle connections after 30min

Pool sizing formula: pool_size = (core_count × 2) + effective_spindle_count
  For SSD/NVMe: effective_spindle_count ≈ 1
  8-core server, SSD: pool_size = 8×2 + 1 = 17 → use 20

Connection leak: request borrows connection, throws exception, never returns it
Solution: Spring's @Transactional, HikariCP leak detection timeout
```

---

### 🟡 Sharding Strategies

| Strategy | How | Pros | Cons |
|----------|-----|------|------|
| Range-based | User IDs 1-1M on shard 1 | Range queries efficient | Hotspots |
| Hash-based | hash(key) % N | Even distribution | Range queries hit all shards |
| Directory-based | Lookup table | Flexible | SPOF, bottleneck |
| Geographic | Route by region | Low latency, data residency | Cross-region expensive |

**Hot partition problem:**
- Symptom: One shard overwhelmed (e.g., celebrity's data).
- Solutions: Random suffix on hot key (`userId_1234_{random 0-9}`), read replicas, local app-level caching.

**DynamoDB hot partition:**
- `userId` as partition key → all requests for popular user → same partition.
- Fix: Composite key `(userId, shardSuffix)` where shardSuffix = request mod 10. Scatter writes, gather reads (union of 10 queries).

---

### 🟡 Replication

**Synchronous:** Write waits for replica ACK. Strong consistency, higher latency.
**Asynchronous:** Write doesn't wait. Lower latency, potential data loss.
**Quorum:** W + R > N for strong consistency (Cassandra: N=3, W=2, R=2).

---

### 🟡 Change Data Capture (CDC)

**What:** Capture every insert/update/delete from a database and stream those changes to other systems.

```
Architecture:
  Source DB (PostgreSQL/MySQL) → CDC Tool → Kafka → Consumers
                                               ├── Search index (Elasticsearch)
                                               ├── Cache invalidation (Redis)
                                               └── Data warehouse (Snowflake)

How it works (log-based CDC):
  1. CDC tool reads database WAL / binlog (PostgreSQL logical replication, MySQL binlog)
  2. Translates WAL records into events: {op: INSERT, table: users, data: {...}}
  3. Publishes events to Kafka topics (one topic per table)
  4. Consumers process events in order

Tools:
  Debezium:  Open-source, Kafka Connect plugin, supports PostgreSQL, MySQL, MongoDB, Oracle
  Maxwell's Daemon: MySQL binlog to Kafka
  AWS DMS:   Managed CDC for AWS databases
  Airbyte:   ETL/ELT with CDC support

CDC vs Polling:
  Polling:  App polls DB every N seconds for updated_at > last_check
    Pros: Simple
    Cons: Misses updates between polls, extra DB load, can't detect deletes without soft-delete
  
  CDC (log-based):  Reads WAL directly
    Pros: Captures ALL changes in order, no extra DB load, detects inserts/updates/deletes
    Cons: Requires log access (some managed DBs restrict this), more complex setup

Use cases:
  - Keep search index in sync with DB (write to DB → CDC → Elasticsearch)
  - Microservice data replication (order service → CDC → inventory service)
  - Audit logging
  - Cache invalidation without polling
  - Building event sourcing from existing DB
```

---

### 🟡 Database Migrations (Zero-Downtime)

**Expand-Contract Pattern (3-step migration):**
```
Scenario: Rename column `user_name` to `username`

Step 1 — EXPAND: Add new column `username` (keep both)
  ALTER TABLE users ADD COLUMN username VARCHAR(255);
  Deploy app code that writes to BOTH columns, reads from old column

Step 2 — MIGRATE: Backfill data
  UPDATE users SET username = user_name WHERE username IS NULL;
  -- Run in batches to avoid long lock: UPDATE ... WHERE id BETWEEN x AND y

Step 3 — CONTRACT: Remove old column
  Deploy app code that reads from new column only
  DROP COLUMN user_name;

This avoids locking the table for long periods.
Never: ALTER TABLE ... (locking on large tables can cause outage)
Use: pt-online-schema-change (Percona) or gh-ost (GitHub) for lock-free migrations
```

---

## 7. Caching

### 🟢 Cache Reading/Writing Strategies

**Cache-Aside (Lazy Loading):** Most common. App checks cache → miss → fetch DB → store → return.

**Read-Through:** Cache fetches from DB on miss. App always talks to cache.

**Write-Through:** Write to cache → cache writes to DB synchronously.

**Write-Behind:** Write to cache → async write to DB (low latency, risk of loss).

---

### 🟡 Cache Eviction Policies

| Policy | Algorithm | Best for |
|--------|-----------|---------|
| LRU | Evict least recently accessed | General purpose |
| LFU | Evict least frequently accessed | Varying access frequency |
| TTL | Expire after fixed time | Time-sensitive data |
| ARC | Adaptive combination of LRU + LFU | Better hit rates |

---

### 🟡 Cache Problems & Solutions

**Stampede:** Many miss requests hit DB simultaneously.
- Fix: Probabilistic early expiration, distributed lock, background refresh.

**Penetration:** Requests for non-existent keys bypass cache.
- Fix: Cache null responses with short TTL. Bloom filter to pre-check existence.

**Avalanche:** Many keys expire simultaneously → DB overload.
- Fix: Jitter on TTL (TTL + random 0-300s). Staggered expiration.

**Hot Key:** Single key receives massive traffic → single cache node overloaded.
- Fix: Local in-process L1 cache + Redis L2. Replicate hot key. Read replicas.

---

### 🟡 Cache Warming Strategies

**Problem:** Fresh deployment has empty cache → massive cache miss spike → DB overwhelmed.

```
Strategies:
  1. Pre-warming script: Before deploy, run script to populate common keys
     (top 1000 products, homepage data, popular user profiles)
  
  2. Lazy warming + circuit breaker: Limit DB calls during cold start
     (rate limit cache misses, queue overflow to DB)
  
  3. Shadow traffic: Replay production traffic against new cache
  
  4. Persistent cache: Redis RDB snapshot — reload on restart (data slightly stale but warm)
  
  5. Dual-layer (L1/L2) caching:
     L1: In-process Caffeine/Guava cache (nanosecond, small, JVM-local)
     L2: Redis (millisecond, large, shared across instances)
     
     On L1 miss → check L2 → on L2 miss → DB → populate both
     L1 invalidation: short TTL (30-60s) — small staleness is OK
     
     Used by: Facebook's Memcached + local memcache, LinkedIn's Precomputed feed
```

---

### 🟡 Distributed Cache Consistency

```
Problem: Multiple app instances share Redis, each has local L1 cache
  App A updates user → writes to Redis
  App B has stale local cache → serves stale data

Solutions:
  1. Short TTL on L1: Accept up to 30s staleness
  
  2. Cache invalidation via pub/sub:
     On DB write → publish invalidation event to Redis pub/sub
     All app instances subscribe → clear local cache entry
  
  3. Versioned cache keys: cache key = "user:123:v5"
     Update user → increment version in DB → old cache key becomes orphan (auto-TTL)
  
  4. Read-your-writes consistency:
     After write, same user's next read goes to DB (not cache) within N seconds
     Implement: store "just-updated" flag in user session
```

---

### 🟡 Redis Deep Dive

```
Data Structures:
  String:       Cache, counters, feature flags
  Hash:         Object storage (user profile as hash fields)
  List:         Queue, activity feed (LPUSH/RPOP)
  Set:          Unique tags, friend lists, SADD/SINTER
  Sorted Set:   Leaderboards (ZADD score member, ZRANGE)
  HyperLogLog:  Approximate distinct count (page views, unique visitors)
  Streams:      Append-only log, event sourcing
  Geo:          Geospatial queries (GEOADD, GEODIST, GEORADIUS)

Redis Cluster: 16384 hash slots across multiple masters. Client directed to correct slot.
Redis Sentinel: HA for single shard — monitors, promotes replica on primary failure.
```

---

## 8. Message Queues & Event Streaming

### 🟢 Why Message Queues?

Decouple, buffer, async processing, fan-out, reliability, rate matching.

**Core concepts:**
- Queue: Point-to-point. One consumer per message.
- Topic: Pub-sub. Many consumers per message.
- Push model: Broker pushes to consumers.
- Pull model: Consumers poll.

---

### 🟡 Kafka Deep Dive

**Architecture:**
```
Producers → Broker Cluster → Consumer Groups

Topic → multiple Partitions (ordered within partition)
Offset: position in partition
Consumer Group: each partition consumed by exactly one consumer in group
ISR (In-Sync Replicas): replicas up-to-date with leader
```

**Producer settings:**
```
acks=0:   Fire and forget (fastest, data loss risk)
acks=1:   Leader ACK (balanced)
acks=all: All ISRs ACK (slowest, strongest durability)
```

**Exactly-Once Semantics (EOS) — How Kafka achieves it:**
```
1. Idempotent Producer: Each producer assigned a PID (Producer ID)
   Each message gets sequence number. Broker deduplicates retries.
   Enable: enable.idempotence=true

2. Transactions: Atomic write to multiple partitions
   Producer begins transaction → writes to multiple partitions/topics
   Commits transaction → consumers with isolation.level=read_committed
   only see committed data
   
   transactional.id = unique per producer instance (survives restarts)

3. Exactly-once stream processing (Kafka Streams):
   Read-process-write atomically using transactions
   Each processed record committed with output atomically
```

---

### 🟡 Kafka vs Flink vs Spark Streaming

| | Kafka Streams | Apache Flink | Spark Streaming |
|--|--------------|-------------|----------------|
| **Model** | Library (in-app) | Separate cluster | Separate cluster |
| **Latency** | Low (milliseconds) | Very low (milliseconds) | Medium (seconds, micro-batches) |
| **State management** | Built-in (RocksDB) | Built-in (managed) | External (Redis/S3) |
| **Exactly-once** | Yes (with EOS) | Yes | Yes |
| **Deployment** | Part of your app | Standalone | Standalone |
| **Learning curve** | Low | High | Medium |
| **Best for** | Simple to medium transformations, already on Kafka | Complex stateful streaming, low latency | Batch + streaming on same API (legacy) |
| **Windowing** | Yes | Rich (event time, watermarks) | Yes (limited) |

---

### 🟡 DLQ (Dead Letter Queue) Design

```
Problem: Messages that can't be processed (bad format, downstream failure, logic error)
         If retried indefinitely → blocks queue, wastes resources

DLQ Pattern:
  Consumer fails to process message →
  Retry N times (with exponential backoff: 1s, 2s, 4s...) →
  After max retries → move to DLQ

DLQ contains:
  Original message + metadata (retry count, last error, first attempt time, original topic)

DLQ processing:
  Monitor DLQ size (alert if growing)
  Developer inspects failed messages → fix bug → replay from DLQ
  Replay: consumer reads DLQ → reprocesses → if success, delete from DLQ

Exponential backoff with jitter:
  wait = min(cap, base × 2^attempt) + random(0, jitter)
  cap=32s, base=1s, jitter=1s

Poison pill: Message that always fails (malformed data)
  After max retries → quarantine in DLQ (don't retry automatically)
  Alert + manual inspection required

Kafka DLQ implementation:
  On process failure: produce to {topic}-retry-1
  retry-1 consumer: delay N seconds → try again → on failure: {topic}-retry-2
  retry-2 consumer: delay longer → try again → on failure: {topic}-dlq
  dlq consumer: alert, log, store for investigation
```

---

### 🟡 Schema Registry (Kafka at Production Scale)

```
Problem: Kafka messages are byte arrays. Producers and consumers must agree on schema.
         Without governance: breaking schema changes crash consumers.

Solution: Schema Registry (Confluent) + Avro/Protobuf serialization

How it works:
  1. Producer registers schema with Schema Registry → gets schema ID
  2. Producer serializes message: [magic byte][schema ID 4 bytes][Avro payload]
  3. Consumer reads schema ID → fetches schema from registry → deserializes

Schema evolution rules:
  Backward compatible:  New schema can read old data (add optional fields)
  Forward compatible:   Old schema can read new data (add new fields with defaults)
  Full compatible:      Both (best practice)

Breaking changes (avoid!):
  Rename field, change field type, remove required field without default

Registry modes:
  BACKWARD (default): New version must be backward compatible with latest
  FORWARD:            New version must be forward compatible
  FULL:               Both backward and forward
```

---

### 🟡 MQ Comparison

| Feature | Kafka | RabbitMQ | IBM MQ | ActiveMQ | SQS/SNS | Pulsar |
|---------|-------|----------|--------|----------|---------|--------|
| **Model** | Event log | Queue/Exchange | Queue | Queue | Queue | Stream+Queue |
| **Throughput** | Millions/s | 100k+/s | Tens of k/s | 50k+/s | Managed | Millions/s |
| **Replay** | Yes | No | No | No | No | Yes |
| **Exactly-once** | Yes (EOS) | Manual | Built-in | No | SQS FIFO | Yes |
| **DLQ** | Manual | Built-in | Built-in | Built-in | Built-in | Built-in |
| **Best for** | Event streaming, analytics | Task queues | Banking, enterprise | Java JMS | AWS serverless | Multi-tenant SaaS |

---

### 🟡 Transactional Outbox Pattern Deep Dive

**Problem:** Write to DB and publish event to MQ — can't do atomically without distributed transactions.

```
Naive approach (WRONG):
  1. UPDATE orders SET status='paid'      -- success
  2. Publish 'OrderPaid' event to Kafka   -- fails!
  Result: Order paid but inventory not updated, emails not sent

Outbox Pattern (CORRECT):
  Single DB transaction:
    1. UPDATE orders SET status='paid'
    2. INSERT INTO outbox_events (topic, payload) VALUES ('order-paid', '{...}')
  
  Outbox poller (separate service):
    3. SELECT * FROM outbox_events WHERE published = false ORDER BY id LIMIT 100
    4. Publish each event to Kafka
    5. UPDATE outbox_events SET published = true WHERE id IN (...)

  Better — CDC approach:
    3. Debezium reads outbox table changes from WAL
    4. Automatically publishes to Kafka (no poller needed)
    5. Faster, no polling overhead, exactly-once with Kafka transactions

Tradeoffs:
  Polling:  Simple, portable, works with any DB. Adds latency (polling interval).
  CDC:      Near real-time, no polling load. Requires WAL access, Debezium setup.
```

---

## 9. Storage Systems

### 🟢 Storage Types

| Type | Description | Examples | Best for |
|------|------------|---------|---------|
| Block | Raw volumes | AWS EBS, Azure Disk | Databases, OS |
| File | Hierarchical filesystem | NFS, EFS | Shared files |
| Object | Flat namespace, HTTP API | S3, GCS | Unstructured data, media |

---

### 🟡 Erasure Coding vs Replication

```
Replication (simple):
  Store 3 copies → 200% overhead
  Can lose up to N-1 nodes
  Fast reads/writes (parallel)
  Used by: HDFS default, Cassandra, Kafka

Erasure Coding (S3's approach):
  Split data into k data chunks + m parity chunks
  Can reconstruct from any k chunks (lose up to m chunks)
  Overhead: m/k × 100%

  Example (Reed-Solomon RS(10,4)):
    10 data chunks + 4 parity chunks = 14 total
    Overhead: 40% vs 200% for 3-way replication
    Can lose any 4 of 14 chunks → zero data loss
  
  S3 uses RS(6,3) — can lose 3 of 9 nodes → 11 nines durability
  
Tradeoff:
  Replication: Lower write latency, simpler recovery, but 200%+ storage overhead
  Erasure coding: Lower storage overhead (40-50%), but higher computational cost,
                  longer recovery time (reconstruct from multiple nodes)

RAID levels (for completeness):
  RAID 0: Striping, no redundancy — fast but data loss on any disk failure
  RAID 1: Mirroring — 100% overhead, instant recovery
  RAID 5: Parity distributed — 1 disk overhead, withstand 1 disk failure
  RAID 6: Double parity — 2 disk overhead, withstand 2 disk failures
  RAID 10: RAID 1 + 0 — fast + redundant, 100% overhead
```

---

### 🟡 HDFS Architecture

```
Components:
  NameNode:  Master — stores filesystem metadata (file → blocks mapping, block locations)
             HA: Active + Standby NameNode with shared journal (QJM)
  DataNode:  Stores actual data blocks (default 128MB blocks)
             Sends heartbeat every 3s + block report every 6h to NameNode
  Client:    Reads block locations from NameNode → reads directly from DataNode

Rack awareness:
  3-way replication: place 1st replica on same rack, 2nd on different rack, 3rd on different rack/DC
  Tolerates: single node failure, single rack failure

Read path:
  1. Client → NameNode: "give me locations of blocks for /data/file.txt"
  2. NameNode → Client: [(block1, [DN1, DN4, DN7]), (block2, [DN2, DN5, DN8])]
  3. Client → nearest DataNode: read block1 → read block2 → reconstruct file

Write path:
  1. Client → NameNode: "create /data/newfile.txt"
  2. NameNode → Client: [(block1 → use DN1, DN4, DN7)]
  3. Client → DN1: write block (DN1 → DN4 → DN7 pipeline)
  4. Ack propagates back
```

---

### 🟡 Tiered Storage

```
Hot → Warm → Cold storage tiers

Hot:   SSD-backed, frequently accessed, most expensive
       EBS gp3, Redis, Cassandra SSDs

Warm:  HDD-backed, occasionally accessed, moderate cost
       S3 Standard, EBS st1, HDFS on spinning disk

Cold:  Tape / deep archive, rarely accessed, cheapest
       S3 Glacier, S3 Glacier Deep Archive, Azure Archive

Lifecycle policies:
  S3: Move to Standard-IA after 30 days, Glacier after 90 days, Deep Archive after 365 days
  Kafka/Pulsar: Tiered storage offloads old segments to S3, serving from S3 on demand

Compression algorithms for storage:
  Snappy:     Fast compression/decompression, moderate ratio (1.5-2×) — Kafka default
  LZ4:        Fastest, slightly worse ratio — low-latency systems
  Gzip/Zstd:  Better compression ratio (3-5×), slower — cold storage, backups
  Parquet:    Columnar format, excellent compression for analytics (10-50× vs CSV)
  
Columnar storage (Parquet, ORC, Arrow):
  Row storage: [id, name, age, salary, id, name, age, salary, ...]
  Column storage: [id, id, id, ...][name, name, name, ...][age, age, age, ...]
  
  Why columnar for analytics:
    SELECT SUM(salary) → only reads salary column (skip others)
    Same-type values compress much better (run-length encoding, dictionary encoding)
    Used by: Snowflake, BigQuery, Redshift, Spark, Parquet files in S3 data lakes
```

---

## 10. Consistent Hashing

### 🟡 The Problem and Solution

**Modulo hashing** `server = hash(key) % N`: Adding/removing a server → nearly all keys remap.

**Consistent hashing:** Map both servers and keys to a circular ring. Key → next clockwise server. Adding/removing a server only remaps 1/N keys.

**Virtual Nodes:** Each physical server gets multiple ring positions → even distribution + weighted capacity.

**Used by:** Cassandra, DynamoDB, Memcached (ketama), load balancers.

---

## 11. Microservices & Service Mesh

### 🟡 Monolith vs Microservices

| | Monolith | Microservices |
|--|----------|--------------|
| **Deployment** | Single unit | Independent |
| **Scaling** | All or nothing | Per-service |
| **Failures** | One failure = full outage | Isolated |
| **Data** | Shared DB | Each service owns data |
| **Start with** | Usually yes | When scale demands |

---

### 🟡 Circuit Breaker

```
CLOSED  → Normal. Count failures.
OPEN    → After N failures: fast-fail (don't call downstream). Return error/fallback.
HALF-OPEN → After timeout: try limited requests. If success → CLOSED. If fail → OPEN.

Libraries: Resilience4j (Java), Polly (.NET), Istio (infrastructure level)
```

---

### 🔴 Service Mesh Internals

**Architecture:**
- **Data plane:** Sidecar proxies (Envoy) handle all traffic. Intercept every request/response.
- **Control plane:** (Istio, Linkerd) manages proxy configuration via xDS API.

**xDS API (Envoy):**
```
Control plane pushes config to Envoy via gRPC streaming:
  EDS: Endpoint Discovery (which IPs serve a service)
  CDS: Cluster Discovery (upstream services)
  RDS: Route Discovery (routing rules)
  LDS: Listener Discovery (what to listen on)

Hot reload: Config updates without proxy restart or traffic interruption
```

**What service mesh gives you without code changes:**
```
  mTLS: Every pod-to-pod call encrypted + authenticated
  Distributed tracing: Trace ID propagated automatically
  Retries + timeouts: Configured in mesh, not code
  Circuit breaking: Envoy handles it
  Traffic splitting: Canary at infrastructure level
  Load balancing: P2C (Power of 2 choices) at L7
  Observability: Prometheus metrics from every proxy
```

---

### 🟡 Saga Pattern

**Choreography (events):** Services react to events. No central orchestrator. Hard to track state.

**Orchestration (commands):** Central saga orchestrator directs each step. Easier to track. Orchestrator is another service to maintain.

**Tools:** Temporal (workflow orchestration), Apache Conductor, Axon Framework.

---

## 12. Distributed Systems Concepts

### 🟡 Consistency Models

| Model | Description | Example |
|-------|------------|---------|
| **Strong / Linearizability** | Operations appear instantaneous, globally ordered | ZooKeeper, etcd |
| **Sequential** | All nodes see same order, not necessarily real-time | |
| **Causal** | Causally related ops seen in order | MongoDB sessions |
| **Eventual** | All replicas converge given no new updates | Cassandra default |
| **Read-your-writes** | After write, your reads see it | User session consistency |
| **Monotonic reads** | If you read a value, future reads ≥ that freshness | Don't go backward |
| **Bounded staleness** | Reads within N seconds of latest write | Cosmos DB option |

---

### 🟡 Gossip Protocol

**What:** Nodes periodically share information with random peers. Epidemic-style information dissemination.

**How it works:**
```
1. Node A selects random node B → sends its state
2. Node B merges state with A's state → now has union of knowledge
3. B selects random node C → spreads merged state
4. Repeats until all nodes converge (O(log N) rounds)

What's gossiped:
  - Cluster membership (who's alive, who's dead)
  - Node state (token ranges in Cassandra, load info)
  - Configuration changes

Failure detection via gossip:
  Heartbeat: each node increments its heartbeat counter
  Gossiped to neighbors
  If node X's heartbeat hasn't increased for T seconds → suspect X is down
  If suspected for T2 seconds → mark X as failed

Used by:
  Cassandra: membership, schema, token ranges
  DynamoDB: cluster state
  SWIM protocol: Kubernetes cluster membership
  Redis Cluster: cluster topology

Phi Accrual Failure Detector (Cassandra/Akka):
  Not binary (alive/dead) — phi score = suspicion level
  phi > threshold → suspect failure
  phi adapts to actual network conditions (variable heartbeat intervals)
  More nuanced than simple timeout detection
```

---

### 🟡 CRDTs (Conflict-free Replicated Data Types)

**Problem:** In AP systems, concurrent updates to same data on different replicas conflict. How to merge without coordination?

**Solution:** Design data structures where any merge is always valid (no conflicts).

```
Types:
  G-Counter (Grow only Counter):
    Each node has own counter vector: [node1: 5, node2: 3, node3: 7]
    Merge: element-wise max → [node1: 5, node2: 3, node3: 7]
    Total = sum of max values
    
  PN-Counter (Positive-Negative):
    Two G-Counters: P (increments) + N (decrements)
    Value = sum(P) - sum(N)
    
  LWW-Element-Set (Last-Write-Wins):
    Each element has timestamp. On conflict: higher timestamp wins.
    
  OR-Set (Observed-Remove):
    Add with unique tag; remove by tag
    Solves: "add on one node + remove on other node" → add wins (OR = observed-remove)

Used by:
  Riak: Shopping cart (OR-Set), counters (PN-Counter)
  Redis: CRDT types in Redis Enterprise
  Collaborative editors: Operational Transform vs CRDT (Figma, Google Docs)
  Cassandra: Last-Write-Wins per column (simple LWW)
```

---

### 🟡 Fencing Tokens (Solving Distributed Lock Safety)

**Problem with Redlock:** Even with distributed lock, GC pause or network delay can cause lock to expire while still "holding" it. Then two processes both think they hold the lock.

```
Scenario without fencing:
  Client A acquires lock, pauses due to GC stop-the-world for 30s
  Lock TTL expires (set to 20s)
  Client B acquires same lock
  Client A resumes, still thinks it holds lock
  → Both A and B modify the same resource!

Fencing Token Solution:
  1. Lock service returns monotonically increasing fencing token with each lock grant
     A gets token 33, B gets token 34 (after A's expiry)
  
  2. Client includes token in all storage requests:
     "Write data, but only if your last seen token < 34"
     or "Accept this write only if fencing token is > last accepted"
  
  3. Storage layer rejects writes with stale tokens:
     A (token 33) tries to write after B (token 34) already wrote
     Storage rejects A's write (33 < 34)

Implementation:
  ZooKeeper: zxid (monotonic transaction ID) serves as fencing token
  etcd: revision number
  Custom: Redis INCR on lock key serves as token
  
  Storage side: CHECK and SET with token in same transaction
  Databases: use optimistic locking (version column)
  Object storage: conditional writes (S3: If-Match ETag, GCS: generation)
```

---

### 🟡 Split-Brain and Network Partitions

```
Split-Brain: Network partition causes both sides to believe they're the primary.
Both sides accept writes → divergent state → data corruption when partition heals.

Prevention strategies:
  1. Quorum: Require majority of nodes (n/2 + 1) to accept writes
     → partition minority can't form quorum → refuses writes → stays consistent
  
  2. STONITH (Shoot The Other Node In The Head): Force-terminate suspected other primary
     via out-of-band channel (IPMI, power management) before they can corrupt data
  
  3. Fencing: Use fencing tokens so stale primaries can't write to storage
  
  4. External witness: Third node/region acts as tiebreaker for 2-node clusters
  
  5. Epoch numbers: New leader increments epoch. Followers reject messages with old epoch.
     Old primary's writes to followers are rejected (stale epoch).

Handling partition heal:
  Async replication: compare logs, replay diverged writes (conflict resolution needed)
  CRDTs: merge automatically
  LWW: last write wins (simple but may lose data)
  Manual: alert operators to resolve conflict
```

---

### 🟡 Consensus (Raft)

```
Raft Phases:
  1. Leader Election:
     Follower → Candidate (election timeout) → sends RequestVote
     Gets majority → becomes Leader → sends heartbeats to prevent re-election
  
  2. Log Replication:
     Client → Leader → append to log
     Leader sends AppendEntries to all followers
     Majority ACK → commit
     Committed → notify followers → they commit

Key properties:
  Leader must have all committed entries
  At most one leader per term
  Logs only grow (never rewrite committed entries)
```

---

## 13. Rate Limiting & Throttling

### 🟡 Algorithms

| Algorithm | Burst allowed? | Memory | Best for |
|-----------|---------------|--------|---------|
| Token Bucket | Yes | O(1) per key | Most common, allows short bursts |
| Leaky Bucket | No | O(1) | Smooth output rate |
| Fixed Window | Yes (boundary attack) | O(1) | Simple |
| Sliding Window Log | No | O(requests/window) | Precise |
| Sliding Window Counter | Approximate | O(1) | Best practical balance |

**Distributed rate limiting:**
```
Redis atomic Lua script (sliding window counter):
  local key = KEYS[1]
  local window = tonumber(ARGV[1])
  local limit = tonumber(ARGV[2])
  local now = tonumber(ARGV[3])
  local count = tonumber(redis.call('GET', key) or 0)
  if count >= limit then return 0 end
  redis.call('INCR', key)
  redis.call('EXPIRE', key, window)
  return 1
```

---

## 14. Authentication & Authorization

### 🟢 Session vs Token

**Session-based:** Server stores state (session in DB/Redis). Easy to invalidate. Doesn't scale without shared store.

**JWT (stateless):** Server doesn't store state. Scales easily. Hard to invalidate before expiry.

```
JWT best practices:
  Short expiry (15-30 min) + refresh tokens (7-30 days)
  Store in httpOnly cookies (prevent XSS theft)
  Refresh token rotation (each use issues new RT, old one invalidated)
  Revocation list for immediate invalidation (Redis set of revoked JTIs)
```

---

### 🟡 OAuth 2.0 + OIDC

**Authorization Code + PKCE:** For web and mobile apps. Most secure.
**Client Credentials:** Service-to-service (no user involved).

---

### 🟡 Zero Trust Architecture

**Principle:** "Never trust, always verify." No implicit trust based on network location (inside corporate network ≠ trusted).

```
Traditional perimeter security:
  Inside firewall → trusted → can access everything
  Problem: Insider threats, lateral movement after breach

Zero Trust principles:
  1. Verify explicitly: Authenticate and authorize every request (user, device, service)
  2. Least privilege access: Grant minimum permissions needed, time-limited
  3. Assume breach: Design assuming attacker is already inside; limit blast radius

Implementation:
  1. Identity: Strong MFA for all users. Device certificates for machines.
  2. Network: Micro-segmentation. No implicit trust between services.
             mTLS between all microservices (service mesh enforces this)
  3. Authorization: RBAC/ABAC enforced at every API call.
                    Policy engine (OPA) evaluates every request.
  4. Monitoring: Log all access. Behavioral anomaly detection.
  5. Data: Encrypt at rest and in transit. DLP policies.

Tools:
  BeyondCorp (Google): Original Zero Trust implementation
  Cloudflare Access: Zero Trust network access
  HashiCorp Vault: Secrets management + identity-based access
  OPA (Open Policy Agent): Policy-as-code for authorization
```

---

### 🟡 API Security (OWASP Top 10 for APIs)

```
1. Broken Object Level Authorization (BOLA/IDOR):
   Attack: GET /api/orders/12345 — change 12345 to another user's order
   Fix: Always verify ownership: WHERE id=? AND user_id=current_user

2. Broken Authentication:
   Attack: Weak passwords, no brute-force protection, tokens in URLs
   Fix: Rate limit auth endpoints, strong password policy, HTTPS-only tokens

3. Broken Object Property Level Authorization:
   Attack: Mass assignment — PATCH /users with isAdmin=true
   Fix: Whitelist allowed fields per role (never trust all request fields)

4. Unrestricted Resource Consumption:
   Attack: No rate limits → resource exhaustion
   Fix: Rate limiting per user/IP, request size limits, timeout limits

5. Broken Function Level Authorization:
   Attack: Regular user calls admin-only DELETE /admin/users/123
   Fix: Verify role/permission at every endpoint, not just UI-level hiding

6. SSRF (Server-Side Request Forgery):
   Attack: POST /api/fetch {url: "http://169.254.169.254/metadata"}
           Server fetches AWS metadata → attacker gets cloud credentials
   Fix: Whitelist allowed domains, block private IP ranges (10.x, 172.16.x, 169.254.x)

7. SQL Injection:
   Attack: ?id=1; DROP TABLE users; --
   Fix: Parameterized queries (NEVER string concatenation in SQL)
        SELECT * FROM users WHERE id = ? (not + id + )

8. Security Misconfiguration:
   Debug endpoints enabled in prod, verbose error messages, default creds
   Fix: Security headers (HSTS, CSP, X-Frame-Options), disable debug in prod

9. Improper Inventory Management:
   Old API versions still running, shadow APIs
   Fix: API gateway inventory, sunset headers, decommission old versions

Encryption at rest vs in transit:
  In transit: TLS 1.3 for all connections (HTTPS, mTLS between services)
  At rest:    AES-256 for database files, S3 SSE, disk encryption
  Key management: AWS KMS, GCP Cloud KMS, HashiCorp Vault
                  Never hardcode keys in code or env vars
```

---

### 🟡 Secret Management

```
Never: Hardcode secrets in code, commit to git, store in env vars in plain text

HashiCorp Vault:
  - Dynamic secrets: generate temp DB credentials on demand (auto-expire)
  - Static secrets: API keys, TLS certs with rotation
  - AppRole auth: apps authenticate with role ID + secret ID to get token
  - Audit logging: all secret access logged

AWS Secrets Manager:
  - Stores and rotates secrets automatically (RDS creds, API keys)
  - SDK access: secretsmanager.getSecretValue(Name="prod/db/password")
  - Rotation: Lambda function rotates RDS password automatically

Best practices:
  - Short-lived credentials: prefer dynamic secrets with 1h expiry
  - Secret zero: how does app get initial access? Use IAM roles, not env vars
  - Never log secrets (scrub from logs, use structured logging with field filtering)
  - Separate secrets per environment: prod/db/password, staging/db/password
```

---

## 15. Monitoring, Observability & Alerting

### 🟢 Three Pillars

| Pillar | What | Tools |
|--------|------|-------|
| **Metrics** | Aggregated numeric measurements | Prometheus, Datadog, CloudWatch |
| **Logs** | Event records with context | ELK, Loki, Splunk |
| **Traces** | Request journey across services | Jaeger, Zipkin, X-Ray |

---

### 🟢 Key Metrics (4 Golden Signals)

1. **Latency:** p50, p95, p99. Always use percentiles, never averages.
2. **Traffic:** RPS, QPS, events/sec.
3. **Errors:** Rate of 5xx, timeouts, business logic errors.
4. **Saturation:** CPU%, memory, queue depth, disk I/O.

---

### 🟡 Structured Logging

```
Unstructured (bad for machines):
  "Error processing order 12345 for user 67890 at 2024-01-15T10:30:00Z"

Structured JSON (machine-parseable, searchable):
{
  "timestamp": "2024-01-15T10:30:00Z",
  "level": "ERROR",
  "service": "order-service",
  "trace_id": "abc123def456",
  "user_id": 67890,
  "order_id": 12345,
  "error": "InsufficientFunds",
  "message": "Payment processing failed",
  "latency_ms": 234,
  "environment": "production"
}

Log Levels (use correctly!):
  ERROR:   Something failed, requires attention. PagerDuty-worthy.
  WARN:    Unexpected but handled. High frequency = investigate.
  INFO:    Normal operational events (request completed, job finished).
  DEBUG:   Detailed diagnostic info. Never in production at scale (too noisy).
  TRACE:   Extremely detailed (per-statement SQL). Dev only.

Best practices:
  - Correlation IDs: thread trace_id through all log entries for a request
  - No PII in logs (GDPR): mask emails, phone numbers, credit cards
  - Log at boundaries: service entry/exit, external API calls, DB queries
  - Sampling: log 100% of errors, 1% of successful requests at DEBUG level
  - Centralize: ship to ELK/Loki/Splunk for indexing and search
```

---

### 🟡 SLI, SLO, SLA and Error Budgets

(See Section 1 for detailed explanation — summarized here)
- SLI = metric, SLO = target, SLA = contract.
- Error budget = 1 - SLO. Budget burn rate triggers alerts and feature freeze.

---

### 🔴 Chaos Engineering

**What:** Deliberately introduce failures into production to find weaknesses before customers do.

```
Principles:
  1. Hypothesis: "System remains available when one AZ fails"
  2. Steady state: Define what "normal" looks like (error rate < 0.1%, p99 < 500ms)
  3. Inject failure: Terminate instances, inject latency, corrupt packets
  4. Measure: Does steady state hold?
  5. Learn: Fix weaknesses found

Tools:
  Netflix Chaos Monkey: Randomly terminates EC2 instances in production
  Netflix Chaos Kong:   Simulates entire region failure
  Gremlin:             Commercial platform, more controlled experiments
  Chaos Toolkit:       Open-source, Kubernetes-native experiments
  AWS Fault Injection Simulator (FIS): Managed chaos experiments

Common experiments:
  - Kill random pod: Does Kubernetes reschedule? Does traffic reroute?
  - Add 200ms latency to service X: Do downstream timeouts trigger?
  - Exhaust connection pool: Does circuit breaker open?
  - Network packet loss: Does retry logic work?
  - Fill disk: Does service degrade gracefully?
  - Database failover: Does app reconnect automatically?

Start in staging → game days (planned drills) → automated in production

Incident Response:
  Detection: Alert fires (PagerDuty, OpsGenie)
  On-call rotation: Who is first responder?
  Runbook: Step-by-step diagnosis and resolution
  Incident commander: Coordinates response in Slack war room
  Postmortem: Blameless root cause analysis → prevent recurrence
  5 Whys: Why did it fail → why was that possible → ... → root cause
```

---

## 16. System Design Patterns

### 🟡 CQRS (Command Query Responsibility Segregation)

**Separate read and write models.** Write to normalized DB. Project events to denormalized read models optimized for queries.

---

### 🟡 Event Sourcing

**Store state changes as immutable events.** Derive current state by replaying events. Full audit trail. Replay for new projections.

**Snapshot optimization:** Periodically snapshot current state to avoid replaying from beginning. Store snapshot at event N, then replay only events > N.

```
Without snapshot: replay 10 million events on startup = slow
With snapshot: load snapshot at event 9,999,000 + replay 1,000 events = fast

Store snapshots: every N events (N=100, 1000) or after major state change
```

---

### 🟡 Outbox Pattern

DB transaction: write data + write event to outbox table atomically. Poller or CDC reads outbox → publishes to Kafka.

---

### 🟡 Sidecar Pattern

**What:** Deploy helper functionality (logging, monitoring, config, proxy) as a separate container alongside the main app, sharing network namespace and storage.

```
Main container: your app
Sidecar containers:
  - Envoy proxy: handles mTLS, retries, circuit breaking, observability
  - Log shipper: Fluentd/Filebeat ships logs to centralized logging
  - Config syncer: watches config store, updates shared volume
  - Secrets injector: Vault agent sidecar fetches and refreshes secrets

Benefits:
  - Separation of concerns: app code doesn't need logging/proxy logic
  - Language-agnostic: sidecar works regardless of app language
  - Upgradeable independently: update proxy without touching app

Kubernetes: init containers (run before app) + sidecar containers
```

---

### 🟡 Anti-Corruption Layer (ACL)

**What:** Isolation layer between your bounded context and a legacy/external system. Translates models so your domain isn't corrupted by external domain concepts.

```
Your Order Domain  →  [ACL: Adapter/Translator]  →  Legacy ERP System
                      Translates:
                      Order → PurchaseOrder
                      Customer → ClientRecord
                      Status values → ERP status codes

Without ACL: Your clean domain gets polluted with legacy concepts
With ACL: Clean separation, can swap legacy system without domain changes
```

---

### 🟡 Optimistic Locking / Read-Modify-Write

```
Problem: Read-Modify-Write race condition
  Thread A: reads balance = $100
  Thread B: reads balance = $100
  Thread A: writes balance = $100 - $30 = $70
  Thread B: writes balance = $100 - $50 = $50   ← Lost Thread A's write!
  Actual balance should be $20

Optimistic Locking (version number):
  Schema: account(id, balance, version)
  
  Thread A: SELECT * WHERE id=1 → {balance: 100, version: 5}
  Thread B: SELECT * WHERE id=1 → {balance: 100, version: 5}
  Thread A: UPDATE account SET balance=70, version=6 WHERE id=1 AND version=5 → 1 row updated ✓
  Thread B: UPDATE account SET balance=50, version=6 WHERE id=1 AND version=5 → 0 rows updated ✗
  Thread B: retry → re-read → balance now 70 → set balance=20, version=7 ✓

When to use:
  Low contention: mostly reads, occasional writes → optimistic (no lock overhead)
  High contention: frequent concurrent writes → pessimistic (SELECT FOR UPDATE)

JPA/Hibernate: @Version annotation auto-manages version column + retry
Redis: WATCH/MULTI/EXEC for atomic check-and-set
```

---

## 17. Classic Design Problems

### 🔴 1. Design a URL Shortener (bit.ly)

**Requirements:** Shorten URLs, redirect, analytics. 100M URLs, 10B redirects/day.

**API:**
```
POST /shorten {url: "https://example.com/very-long-path"} → {short_url: "bit.ly/abc123"}
GET  /{code} → 301/302 redirect to long URL
GET  /{code}/stats → click count, geography
```

**ID Generation:**
```
Option: Auto-increment ID → Base62 encode
7 chars Base62 = 62^7 ≈ 3.5 trillion URLs ✓
Example: ID 12345 → "3d7" (Base62)

Distributed ID (Snowflake):
  64-bit: timestamp(41) + datacenter(5) + machine(5) + sequence(12)
  Unique, sortable, no coordination needed
```

**Architecture:**
```
Client → CDN/LB → Redirect Service (stateless, horizontal)
              ↓
          Redis Cache (short_code → long_url, TTL 24h)
              ↓ cache miss
          DB (short_code PK, long_url, created_at, user_id, expires_at)

Analytics: Click → Kafka → Flink → Druid/ClickHouse
           Pre-aggregate: clicks per hour per short_code

Redirect: 301 (cached by browser, fewer server calls) vs 302 (every click hits server for analytics)
          Use 302 for analytics, 301 for CDN caching of popular links
```

---

### 🔴 2. Design a Search Autocomplete / Typeahead

**Requirements:** Suggest top-10 queries as user types. <100ms latency. Globally distributed.

**Key challenge:** Real-time suggestions from billions of historical queries.

```
Data pipeline (offline — updates every hour/day):
  Kafka (search logs) → Spark batch → count top queries per prefix → Redis/Trie

Trie structure (in Redis or custom):
  "ca" → [california, canada, car, cat, ...]  (sorted by frequency)
  "car" → [car, cards, care, career, cartoon, ...]

Online suggestion service:
  Request: "typ" (user typed)
  1. Check local LRU cache (L1): hit → return
  2. Check Redis: "autocomplete:typ" → sorted set of (query, score) → return top 10
  3. Cache miss → query Trie service → cache result in Redis (TTL 1h)

Trie implementation:
  DFS from prefix node → collect all children → sort by frequency → top 10
  Problem: DFS is slow for large subtrees
  Optimization: Store top-10 at every node (pre-computed)
    Update: re-DFS that subtree on frequency change (bounded by Trie depth)

Scale:
  Trie too large for single machine (billions of queries)
  Shard trie by first character: a-f on shard 1, g-m on shard 2, etc.
  Or: use Redis + sorted sets per prefix (prefix:ca → sorted set of completions)

Handling misspellings:
  Levenshtein distance (expensive) → only for zero-result queries
  Edit distance threshold: 1-2 for short queries

Freshness (trending queries):
  Recency-weighted frequency: score = count × decay(time)
  Separate "trending" index updated more frequently (every 5 min) for viral events
```

---

### 🔴 3. Design Google Drive / Dropbox

**Requirements:** Upload/download files, sync across devices, share, versioning.

**File upload (chunked):**
```
Large file → split into 4MB chunks → upload each chunk separately

Benefits:
  Resume interrupted uploads (skip uploaded chunks)
  Parallel upload (multiple chunks simultaneously)
  Deduplication (identical chunks across users/files share same storage)

Upload flow:
  1. Client: compute SHA-256 hash of each chunk
  2. POST /upload/init {filename, size, total_chunks} → session_id
  3. For each chunk: PUT /upload/{session_id}/chunk/{n} {data, hash}
  4. Server verifies hash → stores chunk → returns ACK
  5. POST /upload/{session_id}/complete → server assembles metadata
  6. Response: file_id, version_id

Storage:
  Chunks: Content-addressed storage in S3 (key = SHA-256 hash)
  Deduplication: if SHA-256 already exists in S3, just add reference (no re-upload)
  Metadata DB: file_id, name, owner, chunks[] → chunk_hashes, permissions, versions[]

Sync protocol (delta sync):
  Track block-level diffs (rsync algorithm)
  Only upload changed blocks, not entire file
  Vector clocks to detect conflicts

Conflict resolution:
  Google Drive: create "copy of file (conflict)" + alert user
  Dropbox: "file (conflicted copy)" for human resolution

Sharing:
  ACL: share(file_id, user_id, permission: read/write/admin)
  Public link: signed URL with expiry

Versioning:
  Store all versions (or last N versions)
  Each version = new set of chunk references
  Recovery: restore any previous version
```

---

### 🔴 4. Design Ticketmaster (Ticket Booking)

**Requirements:** Browse events, book seats, prevent double-booking, handle flash sales.

**Key challenges:** Inventory locking, race conditions, surge traffic.

```
Inventory problem:
  Event has 100 seats. 10,000 concurrent users trying to book.
  How to prevent selling seat 42 twice?

Solution — Seat reservation (two-phase commit to user):
  Phase 1: Reserve (hold lock for 10 minutes)
    SELECT seat WHERE status='available' FOR UPDATE  ← pessimistic lock
    UPDATE seat SET status='reserved', user=X, expires=now+10min
    
  Phase 2: Confirm (user pays)
    UPDATE seat SET status='sold', payment_id=Y
    (On timeout: background job → set status='available' again)

Flash sale architecture:
  Pre-sale: queue users (virtual waiting room)
    Users join queue → assigned position → wait for their turn
    Token bucket: release N users/second to booking page
    
  Database: Redis for real-time seat inventory (fast), DB for persistence
    Redis SETNX "seat:42" user_id EX 600 → atomic reservation
    Periodic sync Redis → DB

Queue implementation:
  User enters URL → gets queue token with position
  Server processes queue in order → redirects to booking when it's user's turn
  Protects backend from thundering herd

Caching:
  Event details: CDN + Redis (read-heavy, rarely changes)
  Seat map: Redis (real-time updates needed)
  Don't cache availability (stale = double booking)
```

---

### 🔴 5. Design Uber/Lyft (Ride-Sharing)

**Requirements:** Driver/rider matching, real-time location tracking, ETA, surge pricing.

```
Location tracking:
  Driver app: send location every 4 seconds → WebSocket or HTTP
  Location DB: Redis Geo (GEOADD, GEORADIUS)
    GEOADD drivers:online <lng> <lat> <driver_id>
    GEORADIUS drivers:online <rider_lng> <rider_lat> 5 km ASC COUNT 10
  
  Scale: 1M active drivers × location update every 4s = 250K writes/sec
  Solution: Use Redis cluster, partition by city/region

Matching algorithm:
  1. Rider requests ride with pickup location
  2. Find N nearest available drivers within 5km radius (GEORADIUS)
  3. Filter by: driver rating, car type, ETA
  4. Send offer to top-3 drivers simultaneously
  5. First to accept → assigned
  6. Others: offer to next batch

ETA calculation:
  Simple: Euclidean distance / average speed — too inaccurate
  Better: Road network graph → Dijkstra or A* with traffic weights
  Production: ML model trained on historical trip times + real-time traffic
  Map service: GraphHopper, OSRM, HERE Maps, Google Maps API

Trip management state machine:
  REQUESTED → ACCEPTED → DRIVER_ARRIVING → TRIP_STARTED → COMPLETED
  Each transition → event → Kafka → billing, analytics, notifications

Surge pricing:
  Monitor: demand (ride requests) / supply (available drivers) in geohash cell
  If ratio > threshold → apply surge multiplier
  Geohash: 6-character geohash ≈ 1.2km cell → track per-cell supply/demand
  Update every 1-2 minutes

Geospatial indexing:
  Geohash: encode lat/lng as string. Nearby = shared prefix (not perfect but good)
  S2 (Google): Hilbert curve-based, used in Google Maps
  Quadtree: Recursive 2D partitioning (used in gaming, Uber H3)
  H3 (Uber): Hexagonal grid (uniform coverage, better for circle queries)
```

---

### 🔴 6. Design a Payment System / Digital Wallet

**Requirements:** Transfer money, maintain balances, strong consistency, idempotency.

```
Ledger model (double-entry accounting):
  Every transaction = two entries: debit from one account, credit to another
  Invariant: SUM of all entries = 0 always

  Table: ledger_entries
    id, transaction_id, account_id, amount (+credit / -debit), created_at

  Balance = SUM(amount) WHERE account_id = X
  OR: materialized balance with event sourcing from ledger

Idempotency (critical):
  All payment requests require idempotency key
  Store result with idempotency key for 24h
  Retries return same result (no double charge)

Preventing race conditions (balance going negative):
  Option A: SELECT FOR UPDATE → pessimistic locking
    BEGIN;
    SELECT balance FROM wallets WHERE id=X FOR UPDATE;
    -- Check balance >= amount
    INSERT INTO ledger_entries ...
    UPDATE wallets SET balance = balance - amount WHERE id = X;
    COMMIT;

  Option B: Optimistic locking
    UPDATE wallets SET balance = balance - amount, version = version + 1
    WHERE id = X AND version = known_version AND balance >= amount
    -- If 0 rows updated: retry

  Option C: Saga pattern for cross-service (debit account A, credit account B)
    Use distributed saga with compensating transactions

Distributed transactions (paying across services):
  Don't use 2PC (too slow, too fragile)
  Use: Saga + idempotency + eventual consistency
    1. Debit source account (idempotent)
    2. Credit destination account (idempotent)
    3. If step 2 fails: compensation → credit source account back

Compliance:
  Audit log: immutable record of all transactions
  Write to append-only table (no UPDATE/DELETE allowed on completed txns)
  Or: event sourcing — all state derived from immutable event log
```

---

### 🔴 7. Design Live Video Streaming (YouTube/Netflix)

**Requirements:** Upload video, process, deliver to millions of viewers globally.

```
Upload pipeline:
  User → Upload Service → Raw video stored in S3
  → Transcoding Queue (Kafka) → Transcoding Workers (ffmpeg)
  → Generate multiple resolutions: 240p, 480p, 720p, 1080p, 4K
  → Generate multiple formats: HLS (Apple), DASH (MPEG)
  → Store segments in S3 → Notify CDN to pull
  → Update metadata DB: video_id, status=ready, playback_url

HLS (HTTP Live Streaming):
  Video split into 10-second segments (.ts files)
  Manifest file (.m3u8): lists all segments
  Player downloads manifest → downloads segments sequentially
  Adaptive Bitrate (ABR): player switches quality based on bandwidth

  Master playlist:
    #EXTM3U
    #EXT-X-STREAM-INF:BANDWIDTH=800000,RESOLUTION=640x360
    360p/segment.m3u8
    #EXT-X-STREAM-INF:BANDWIDTH=3000000,RESOLUTION=1280x720
    720p/segment.m3u8

Delivery:
  CDN pull: video segments cached at edge nodes worldwide
  CDN push: pre-populate popular content at edges (live events)
  
  P2P CDN (BitTorrent-like): Viewers share segments with each other
  Saves CDN bandwidth, works well for popular content

Live streaming (real-time):
  Broadcaster → RTMP to ingest server → transcode in real-time → HLS/DASH
  Latency: HLS default ~30s delay (10s segments × 3 buffer)
  Low-latency HLS: 2-6 second delay (1-2s segments + prefetch hints)
  WebRTC: <1s but complex, used for video calls not broadcasts

DRM (Digital Rights Management):
  Widevine (Google), FairPlay (Apple), PlayReady (Microsoft)
  Content encrypted with content key
  License server provides decryption key after auth check
  Key never exposed to user
```

---

### 🔴 8. Design a Web Crawler

**Requirements:** Crawl the web, index pages. Scalable, polite, deduplicate.

```
Core components:
  1. URL Frontier: Queue of URLs to crawl (priority queue)
  2. Fetcher: Downloads web pages
  3. Parser: Extracts links + content
  4. URL Deduplicator: Seen URLs set
  5. Content Store: Stores crawled pages
  6. Indexer: Processes content for search

URL Frontier:
  Priority queue: high-priority (popular, fresh) URLs first
  Per-domain queues: politeness (don't hammer same host)
  Back pressure: if queue too large, pause adding new URLs

Deduplication:
  URL normalization: lowercase, remove tracking params, canonicalize
  URL fingerprint: SHA-256 → store in Redis Set (bloom filter for scale)
  Content fingerprint: SimHash to detect near-duplicate pages

Politeness (robots.txt):
  Fetch robots.txt per domain: honor Disallow rules
  Crawl delay: respect Crawl-delay header, default 1 request/sec per domain
  User-Agent: identify as googlebot/your-crawler

Scale:
  Distributed crawling: partition URL space by domain hash → multiple crawlers
  1B URLs/day = ~11K URLs/sec = ~100 crawlers at 100 URLs/sec each

URL Frontier distributed design:
  Front queues: priority queues (high, medium, low)
  Back queues: one per domain (politeness enforcement)
  Mapping: URL → back queue (by domain hash)
  
  Each crawler: pull from its domain's back queue
  If back queue empty: pull from front queue → add to back queue

Freshness:
  Crawl frequency ∝ change rate (news sites: hourly, static sites: monthly)
  Track last-modified headers, ETag
  Sitemaps: start with sitemap.xml for fast discovery

Scale for content storage:
  Web pages: ~100KB average
  1B pages × 100KB = 100 TB
  Use columnar format (Parquet) in S3 data lake + Elasticsearch for search index
```

---

### 🔴 9. Design an Ad Click Aggregation System

**Requirements:** Count ad clicks in real-time and aggregate by (ad_id, hour). 10B clicks/day.

**Key challenges:** Hot keys (viral ads), exactly-once counting, minute-level aggregates.

```
Scale:
  10B clicks/day = 115K clicks/sec
  Peak: 10× = 1.15M clicks/sec

Raw click ingestion:
  Client → Click API (validates, deduplicates) → Kafka (topic: raw-clicks)
  
  Deduplication (at-most-once clicks):
    Click ID = hash(user_id + ad_id + timestamp rounded to 5s)
    Redis SET with TTL: SETNX click:{click_id} 1 EX 300
    Reject if already seen

Stream processing (Flink/Kafka Streams):
  Read from raw-clicks → window(1 minute) → aggregate by ad_id → output to:
    1. Redis (hot path): INCRBY ad:clicks:{ad_id}:{minute} {count}
    2. ClickHouse/Druid (cold path): bulk insert hourly aggregates

Hot key problem (viral ad):
  Single Kafka partition saturated by one ad_id
  Solution: Write sharding
    Produce to: ad_clicks_{ad_id % 10} (10 partitions per ad)
    Consumer: read from all 10 partitions, sum counts
  
  Or: Local aggregation in producer
    Buffer 100ms of clicks locally, batch-produce aggregated count

Query API:
  GET /ads/{ad_id}/clicks?from=2024-01-01&to=2024-01-02&granularity=hour
  
  Recent (last hour): Redis O(1)
  Historical: ClickHouse/Druid (columnar, fast aggregate queries)

Count-Min Sketch for approximate frequency:
  For top-N ads: use CMS to track frequency without storing all click records
  Size: width=1000, depth=5 → ~4KB per counter, error ≤ 0.1%
  Identify top-100 ads: Count-Min + heap
```

---

### 🔴 10. Design a Proximity Service / Yelp

**Requirements:** Find businesses near a location, search by category, reviews.

```
Geospatial indexing options:
  1. Geohash: Encode lat/lng as base-32 string
     Each character adds precision: 6 chars ≈ 1.2km × 0.6km cell
     Nearby = same prefix (but edge cases at cell boundaries!)
     Simple to implement with string prefix queries
  
  2. Quadtree: Recursively divide 2D space into quadrants
     Leaf node when region has ≤ N points (e.g., N=100)
     In-memory tree for fast queries
  
  3. R-Tree (used by PostGIS): Balanced tree for spatial data
     Efficient range and nearest-neighbor queries
  
  4. S2 Library (Google): Spherical geometry cells on sphere
     Hierarchically indexed cells at multiple levels
     Best accuracy but more complex

Recommended: Geohash for simplicity (interviews), PostGIS/S2 for production

Schema:
  businesses: (id, name, lat, lng, category, rating, geohash_6)
  Index on geohash_6 prefix

Search algorithm:
  1. Compute geohash of user location at level 6 (1.2km cells)
  2. Get 8 neighboring geohash cells (cover edge cases)
  3. SELECT * FROM businesses WHERE geohash_6 LIKE 'u4pruydq%' AND category='restaurant'
  4. Calculate exact distance (Haversine formula) → sort → return top N

For radius queries:
  Calculate which geohash cells are within radius
  Query those cells only
  Filter by exact distance

Scale:
  Read-heavy: businesses don't update frequently
  Solution: Cache business data + geohash-based lookups in Redis
  GEOADD/GEORADIUS in Redis for real-time location data (like Uber)
  
  Search index (Elasticsearch with geo_point type):
    POST /businesses/_search
    {query: {geo_distance: {distance: "5km", location: {lat: 37.7, lon: -122.4}}}}
```

---

### 🔴 11. Design a Stock Exchange

**Requirements:** Order placement, order matching, real-time market data.

**Key challenges:** Ultra-low latency, strict ordering, exactly-once trade execution.

```
Order types:
  Market order: execute immediately at best available price
  Limit order: execute at or better than specified price
  Stop order: trigger when price reaches threshold

Order Book (core data structure):
  Bids (buy orders): sorted by price DESC (highest price first)
  Asks (sell orders): sorted by price ASC (lowest price first)
  
  Best bid: 99.50 → Best ask: 99.55 → Spread: 0.05
  
  Implementation: Sorted map (TreeMap, skip list) per price level
    Price level: (price, [list of orders in FIFO order])
    O(log N) insertion, O(1) best bid/ask access
  
  In-memory order book (microsecond latency):
    100K active orders in memory: ~10MB — fits in L3 cache
    Event sourcing: all orders written to WAL → reconstruct on crash

Matching engine:
  When new buy order at price P arrives:
    Match against asks with price ≤ P
    Execute trades at ask price (price-time priority)
  
  When sell order at price P arrives:
    Match against bids with price ≥ P

Latency requirements:
  Colocation: exchange servers in same DC as market makers
  FPGA: hardware-accelerated matching for sub-microsecond latency
  Kernel bypass networking (DPDK/RDMA): avoid OS network stack
  Lock-free ring buffer: between network thread and matching thread

Distribution:
  Market data (trade feed): Multicast to all subscribers simultaneously
  Order gateway: TCP connection per participant (bidirectional)
  Risk checks: Pre-trade risk (position limits, notional limits) before matching

Sequence numbers:
  Every order/trade gets monotonically increasing sequence number
  Participants can detect missed messages (sequence gap)
  Replay: request missed events from sequence store

Persistence:
  All orders + trades written to WAL before acknowledgment
  PostgreSQL: too slow (row-level locking)
  Custom: append-only log file → replay to reconstruct order book
  TimeScale: for time-series trade data + analytics
```

---

### 🔴 12. Design a Key-Value Store from Scratch

**Requirements:** GET/PUT/DELETE, persistence, high availability.

```
Storage engine (LSM-Tree based, like LevelDB/RocksDB):
  Write path:
    1. Write to WAL (append-only log) for durability
    2. Write to MemTable (in-memory skip list, sorted by key)
    3. Return success to client
    
    When MemTable reaches size limit (e.g., 64MB):
    4. Flush to L0 SSTable on disk (sorted by key, immutable)
    5. Background compaction: merge L0 → L1 → L2 (sorted, deduplicated)

  Read path:
    1. Check MemTable first (most recent writes)
    2. Check each SSTable level: L0 → L1 → L2
    3. Use Bloom filter per SSTable to skip unlikely candidates
    4. Binary search within SSTable for key

  Compaction:
    Merges multiple SSTables, removes deleted keys (tombstone markers)
    Size-tiered: merge SSTables of similar size
    Leveled: L0 → L1 merge triggered at threshold; L1 → L2 etc.

API layer:
  GET /keys/{key} → value or 404
  PUT /keys/{key} {value: "..."}
  DELETE /keys/{key}

Replication:
  Primary-replica replication (leader-based)
  Write to primary → sync to N replicas → ACK client
  Read: from primary (strong) or any replica (eventual)

Partitioning (horizontal scale):
  Consistent hashing → distribute keys across nodes
  Each node responsible for a key range (virtual nodes)

Client library:
  Routes requests to correct partition
  Caches partition topology (refreshed periodically)
  Retries on temporary failures

Failure handling:
  Hinted handoff: if destination node down, store temporarily + retry
  Anti-entropy: Merkle trees to detect and sync diverged replicas
```

---

### 🔴 13. Design a Distributed Job Scheduler

**Requirements:** Schedule cron jobs, execute exactly once, retry on failure. Scale to millions of jobs.

```
Core concepts:
  Job: {id, schedule: "0 9 * * 1" (cron), payload, retries, timeout}
  Execution: One run of a job at a specific time
  Worker: Process that executes job payload

Exactly-once execution challenge:
  Multiple scheduler nodes must not execute same job twice
  Solution 1: DB row lock
    SELECT * FROM due_jobs WHERE status='pending' AND next_run <= now()
    LIMIT 100 FOR UPDATE SKIP LOCKED  ← Claims jobs atomically
    UPDATE SET status='claimed', worker_id=X, claimed_at=now()
    
  Solution 2: Distributed lock (Redis)
    SETNX job:{id}:lock {worker_id} EX 300
    Only one worker claims lock → executes → deletes lock

Architecture:
  Scheduler (N instances): scans for due jobs, claims with FOR UPDATE SKIP LOCKED
  Worker pool: executes job payloads
  DB: jobs table (cron schedule), executions table (each run)
  Queue (Kafka/SQS): scheduler → queue → workers

Cron parsing:
  Parse cron expression → compute next run time
  Quartz Cron / CronExpression libraries
  Support: @yearly, @monthly, @daily, @hourly + standard cron

Retry with backoff:
  On failure: execution.status = 'failed', retry_count++
  If retry_count < max_retries: set next_run = now + backoff(retry_count)
  Exponential backoff: min(2^retry_count * 1s, 1h)

Monitoring:
  Alert: job hasn't run in 2× its schedule interval
  Alert: job failure rate > threshold
  Dashboard: success rate, p99 execution time per job type

Scale:
  1M jobs × 1 run/day = 1M executions/day = ~12/sec (trivial)
  1M jobs × 1 run/minute = 1M/min = ~17K executions/sec (needs partitioning)
  
  Partition by: hash(job_id) → multiple scheduler + worker shards
  Each shard responsible for subset of jobs (consistent hashing on job_id)
```

---

### 🔴 14. Design Google Maps

**Requirements:** Routing (fastest/shortest path), ETA, map tile serving, real-time traffic.

```
Road network:
  Graph: nodes (intersections) + edges (road segments with distance/speed)
  OSM (OpenStreetMap): 7B nodes, 750M ways — too large for single machine
  
  Partition by: geographic region (country, state, city)
  Hierarchical roads: highways (national graph) + local roads (regional graph)

Routing algorithm:
  Dijkstra: O((V+E) log V) — too slow for nationwide (billions of nodes)
  
  Bidirectional Dijkstra: Run from source + target simultaneously → meet in middle
  → ~O(√V) speedup in practice
  
  A* with heuristic: Manhattan/Euclidean distance heuristic → significant speedup
  
  Contraction Hierarchies (CH): Preprocessing step — contract low-importance nodes,
  add shortcut edges. Query time: microseconds even for cross-country.
  Used by: Google Maps, OSRM, Graphhopper
  
  Preprocessing time: hours to days
  Query time: <10ms for cross-country

Traffic:
  Real-time: GPS data from Android devices (crowdsourced)
  Historical: pattern by hour, day of week, holidays
  Combine: ML model predicts travel time given historical + real-time
  
  Update road weights every 5 minutes with live traffic
  Separate graph for each time horizon: now, 15min, 1h

Map tiles:
  Pre-render tiles at each zoom level (z0=world, z15=street level)
  Tile: 256×256 PNG, addressed by (z, x, y)
  Billions of tiles → S3 + CloudFront CDN
  Vector tiles: send raw geometry, render on device (smaller, scalable)
  
  Tile refresh: Re-render when map data changes (incremental diff)

ETA:
  Not just routing time: add traffic, road events, turn delays, traffic lights
  ML model: trained on historical trips for same route, time of day, day of week
  Input features: route segments, historical speed per segment per time, weather
  Output: estimated travel time (with confidence interval)
```

---

## 18. Infrastructure & Cloud

### 🟡 Kubernetes Architecture

```
Kubernetes Cluster:
  Control Plane:
    API Server:     Entry point for all cluster operations. REST API.
    etcd:           Distributed key-value store. All cluster state stored here.
    Scheduler:      Assigns pods to nodes (considers resources, affinity, taints)
    Controller Manager: Controllers that maintain desired state
      - ReplicaSet controller: ensure N pods running
      - Deployment controller: rolling updates
      - Service controller: manage load balancer

  Worker Nodes (Data Plane):
    kubelet:        Agent on each node. Runs pods. Reports health to control plane.
    kube-proxy:     Implements Services (iptables / eBPF rules for routing)
    Container Runtime: containerd (default), Docker (deprecated)

Key Objects:
  Pod:          Smallest unit. One or more containers sharing network + storage.
  Deployment:   Manages replica sets + rolling updates for stateless apps.
  StatefulSet:  Ordered, stable pod identity. For stateful apps (databases, Kafka).
  DaemonSet:    One pod per node. For logging agents, monitoring, storage.
  Service:      Stable network endpoint (ClusterIP, NodePort, LoadBalancer)
  ConfigMap:    Non-secret configuration. Mounted as env vars or files.
  Secret:       Sensitive data (passwords, tokens). Base64 encoded (not encrypted by default!)
  PersistentVolume: Storage abstraction.

Pod lifecycle:
  Pending → Running → Succeeded/Failed
  Init containers → Main containers → Sidecars

Networking:
  Each pod gets unique IP
  All pods can communicate (flat network — CNI: Calico, Flannel, Cilium)
  Service DNS: svc.namespace.svc.cluster.local
```

---

### 🟡 Auto-Scaling

```
Horizontal Pod Autoscaler (HPA):
  Scale pods based on CPU/memory or custom metrics
  Min: 2 pods, Max: 20 pods, Target CPU: 70%
  
  Controller checks every 15s:
    desiredReplicas = ceil(currentReplicas × currentMetric / targetMetric)
    currentCPU=80%, target=70% → ceil(5 × 80/70) = ceil(5.7) = 6 pods

Vertical Pod Autoscaler (VPA):
  Adjusts CPU/memory requests per pod (not replica count)
  Use for stateful workloads (databases) that can't be easily replicated

KEDA (Kubernetes Event-Driven Autoscaling):
  Scale based on Kafka consumer lag, SQS queue depth, Redis list length
  Scale to zero when idle (save cost for batch workloads)

Cluster Autoscaler:
  Adds/removes nodes (EC2 instances) based on pending pods
  Scale up: pod can't be scheduled → add node
  Scale down: node underutilized for 10min → evict pods → remove node

AWS:
  EC2 Auto Scaling: CloudWatch alarm → scale instances
  ECS Auto Scaling: based on CPU/memory of ECS services
```

---

### 🟡 Multi-Region Failover and Disaster Recovery

```
Multi-Region Deployment Patterns:

Active-Passive (Hot Standby):
  Primary region: 100% traffic
  Standby region: mirrors data, no traffic
  Failover: change DNS → route to standby (minutes, RPO = replication lag)
  RTO (Recovery Time Objective): time to restore service after failure
  RPO (Recovery Point Objective): max acceptable data loss

Active-Active:
  Both regions serve traffic (split by user geography or round-robin)
  Data replicated bidirectionally (conflict resolution needed)
  On failure: surviving region absorbs all traffic (needs capacity headroom)
  RTO: seconds to minutes (no failover flip needed)
  RPO: near-zero (synchronous replication) or seconds (async)

RTO vs RPO:
  RTO: "How fast can we recover?" (service downtime)
  RPO: "How much data can we lose?" (data loss window)
  
  Zero RPO: Synchronous replication (higher latency, higher cost)
  Near-zero RPO: Async replication with < 1s lag
  
Disaster Recovery tiers:
  Tier 0: Active-Active (RTO < 1min, RPO = 0) — highest cost
  Tier 1: Hot standby (RTO 1-15min, RPO < 1min)
  Tier 2: Warm standby (RTO 30-60min, RPO < 15min) — data replicated, scaled down
  Tier 3: Cold backup (RTO hours, RPO hours) — backups only, restore from scratch

Database backup strategies:
  Full backup: complete snapshot (weekly)
  Incremental: only changes since last backup (daily)
  WAL archiving: continuous WAL shipping → point-in-time recovery (PITR)
  
  AWS RDS: automated daily snapshots + continuous WAL → restore to any second
  S3: Cross-region replication for backup files

DNS failover:
  Route53 health checks: if primary endpoint unhealthy → failover to secondary
  TTL: low TTL (60s) for fast propagation. High TTL = slow failover.
  
  Failover types:
    Active-passive: primary/secondary alias records
    Geoproximity: route by user location to nearest healthy region
    Weighted: 90%/10% between regions (gradual failover)

Data replication for multi-region:
  PostgreSQL: streaming replication + WAL archiving to S3 cross-region
  DynamoDB: Global Tables (multi-master, multi-region)
  Aurora: Global Database (1 primary region + 5 secondary, <1s replication lag)
  Kafka: MirrorMaker2 replicates topics between clusters
```

---

### 🟡 Infrastructure as Code

```
Terraform:
  Declarative: describe desired state, Terraform figures out how to get there
  State: tracks what's deployed (state file in S3 + DynamoDB for locking)
  Plan: shows what will change before applying
  Modules: reusable infrastructure components

  resource "aws_instance" "web" {
    ami           = "ami-0c55b159cbfafe1f0"
    instance_type = "t3.micro"
    tags = { Name = "WebServer" }
  }

AWS CDK (Cloud Development Kit):
  Define infrastructure in TypeScript/Python/Java
  Compiles to CloudFormation

Ansible: Configuration management (install software, configure servers)
Helm: Kubernetes package manager (charts = parameterized K8s manifests)

GitOps:
  Infrastructure defined in Git → merge = deploy
  ArgoCD/Flux watches Git repo → applies changes to Kubernetes cluster
  Rollback = git revert + merge
```

---

## 19. Master Cheat Sheet

### 🔑 Component Selection Guide

| Need | Solution |
|------|---------|
| Structured data + ACID | PostgreSQL / MySQL |
| Globally distributed SQL | CockroachDB / Spanner |
| Flexible document storage | MongoDB |
| Key-value cache, sub-ms | Redis |
| Time-series metrics | InfluxDB / TimescaleDB |
| Full-text search | Elasticsearch |
| Graph relationships | Neo4j |
| Massive write throughput | Cassandra |
| Vector similarity search | Pinecone / pgvector / Qdrant |
| Event streaming + replay | Kafka |
| Task queue + routing | RabbitMQ |
| Managed serverless queue | SQS / SNS |
| Exactly-once enterprise MQ | IBM MQ |
| Distributed file storage | HDFS / S3 |
| CDN / static assets | CloudFront / Cloudflare |
| Service discovery | Consul / Eureka |
| Configuration management | etcd / Consul |
| Secrets management | HashiCorp Vault / AWS Secrets Manager |
| Container orchestration | Kubernetes |
| Service mesh | Istio / Linkerd |
| Distributed lock | Redis Redlock / ZooKeeper / etcd |
| Workflow orchestration | Temporal / Apache Conductor |
| Policy engine | OPA (Open Policy Agent) |

---

### 🔑 Scaling Playbook

```
Step 1: Vertical scaling — simplest, no code changes
Step 2: Add caching (Redis) — eliminate repeated DB reads
Step 3: Read replicas — distribute read traffic
Step 4: CDN for static assets — remove load from servers
Step 5: Load balancing — multiple stateless app servers
Step 6: Connection pooling — PgBouncer, HikariCP
Step 7: Database sharding — distribute write load
Step 8: Async processing — message queues for spikes
Step 9: Microservices — scale bottleneck components independently
Step 10: Multi-region — low latency + disaster recovery
```

---

### 🔑 Design Interview Framework (RESHADED)

```
R — Requirements:
      Functional: what does the system do?
      Non-functional: scale, latency, availability, consistency
      
E — Estimation:
      DAU, RPS (reads/writes), storage, bandwidth
      "100M DAU, each posts 1/week → ~170 writes/sec"
      
S — Schema / Data Model:
      What entities exist? Key tables/collections?
      
H — High-Level Design:
      Core components, data flow diagram
      
A — API Design:
      Key endpoints, request/response format
      
D — Deep Dive:
      Pick 1-2 bottlenecks: scaling, consistency, fault tolerance
      
E — Evaluation:
      How does design meet non-functional requirements?
      
D — Distinctive Features:
      What makes your design good? Tradeoffs you made?
```

---

### 🔑 Numbers Every Engineer Should Know

```
Throughput:
  Single Redis node:   1M ops/s
  Kafka cluster:       1M msgs/s (with batching)
  MySQL optimized:     100K reads/s
  Cassandra writes:    500K writes/s
  API server (Go):     50K req/s
  API server (Java):   20K req/s

Storage per unit:
  Tweet (280 chars):   ~300 bytes
  User profile:        ~1 KB
  Photo (compressed):  ~1 MB
  Video (1 min 720p):  ~50 MB

Scale of services:
  Twitter:   500M tweets/day = ~6K tweets/sec
  Instagram: 95M photos/day
  Netflix:   ~15% of global internet traffic at peak
  Google:    8.5B searches/day = ~100K searches/sec

Infrastructure:
  EC2 t3.small (2 vCPU, 2GB): $15/month
  RDS db.r5.large (2 vCPU, 16GB): $180/month
  S3: $0.023/GB/month + $0.0004/10K requests
  CDN (CloudFront): $0.0085/GB transfer
```

---

### 🔑 Trade-off Summary

```
Latency       vs Throughput     → Batching trades latency for throughput
Consistency   vs Availability   → CAP + PACELC — pick your position
Space         vs Time           → Caching/indexes: more memory for faster compute
Read speed    vs Write speed    → Indexes speed reads, slow writes
Simplicity    vs Scalability    → Monolith is simpler; microservices scale better
Coupling      vs Performance    → Async messaging decouples but adds latency
Idempotency   vs Performance    → Idempotency keys add latency but prevent duplicates
Strong consistency vs Latency   → Sync replication = safe but slow; async = fast but risky
Optimistic vs Pessimistic locking → Optimistic: better concurrency, retry overhead;
                                    Pessimistic: predictable, but limits throughput
```

---

*System Design Complete Playbook — v1.1. Last updated: May 2026.*
*Covers FAANG / MAANG-level system design interviews: junior through principal/staff engineer.*
