# geoserver-client Spring Boot Demo

A minimal Spring Boot web app showing `geoserver-client` used from real application code (as
opposed to the standalone programs in [`../examples/`](../examples)): a single CRUD screen for
GeoServer Workspaces.

Standalone Maven project — not a module of the root build, not packaged into the library JAR.

## What it demonstrates

- A single `GeoServerClient` as a Spring singleton bean (`GeoServerClientConfig`), matching the
  library's intended usage: one client per GeoServer endpoint, shared and reused, not constructed
  per-request.
- A plain `@Controller` (`WorkspaceController`) doing `list()` / `create()` / `delete()` — no raw
  REST/HTTP/JSON code anywhere in the app; that's all encapsulated inside the library.
- Typed exception handling: `GeoServerException` (the library's base exception) caught and shown
  as a flash message instead of a 500 page.

## Running it

1. Install the library locally first if you haven't already (or wait for it to appear on Maven
   Central — either resolves fine):
   ```bash
   cd .. && mvn clean install -DskipTests
   ```
2. Start a local GeoServer: `docker-compose up -d` from the repo root.
3. Run the app:
   ```bash
   cd webapp
   mvn spring-boot:run
   ```
4. Open http://localhost:8090 — create/delete workspaces from the browser.

Connection settings are in `src/main/resources/application.properties`, defaulting to this repo's
`docker-compose.yml` credentials. Override without editing the file via env vars:
```bash
GEOSERVER_URL=http://your-geoserver/geoserver \
GEOSERVER_USERNAME=admin \
GEOSERVER_PASSWORD=your-real-password \
mvn spring-boot:run
```
