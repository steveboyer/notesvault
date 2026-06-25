# Design Notes

## Production Questions

### How would you deploy this to production?

I would build the container image in CI and run multiple stateless instances behind a load balancer. Since
authentication is stateless (JWT), instances need no shared session state and scale horizontally. I'd also use a managed
PostgreSQL instance rather than a containerized database, and supply secrets (DB credentials, JWT key) from a secrets
manager rather than environment files.

### What would you monitor or alert on?

I'd monitor request latency and error rates, paying particular attention to 4xx/5xx ratios. A spike in authentication
failures (401s) can indicate an attack or a broken client. I'd monitor database connection pool saturation and query
latency as well.

### How would you handle database migrations over time?

I'd use a versioned migration tool such as Flyway. With this method, migration scripts live in source control and run as
part of the deployment pipeline, so schema changes are reviewable, repeatable, and ordered. ddl-auto is convenient for
this exercise but unsafe for production as it infers schema changes rather than applying reviewed ones.

### What changes to support 10,000 concurrent users?

For simple note CRUD app, 10k concurrent users isn't much load on the app tier. Since auth is stateless, the app is easy
to scale horizontally. The real question is the database. I'd start there by raising the connection pool ceiling
carefully (more app instances multiply connections fast, so I'd put PgBouncer in front before bumping Postgres's own
limit), then add read replicas, since note reads dominate writes here.

I'd reach for caching last, not first. Note reads are gated by the share table, so a cache has to key on the viewer, not
just the note, and invalidate correctly when a note is un-shared. This is easy to get subtly wrong, so I'd only add it
once a replica read is provably the bottleneck.

Mostly I'd want a load test pointing at the real schema before committing to any of this; the indexes on owner_id and
the share (note_id, user_id) lookups matter more than I can guess at without numbers.
