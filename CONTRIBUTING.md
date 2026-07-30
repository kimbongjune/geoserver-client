# Contributing

## Setup

1. Java 8+ and Maven 3.6+ installed.
2. Start a local GeoServer: `docker-compose up -d` (GeoServer 2.28.2 + PostGIS, `http://localhost:8100/geoserver`, `admin`/`geoserver`). Integration tests are skipped (not failed) if it's unreachable.
3. `mvn clean verify` — should pass with 0 failures before you start and before you open a PR.

## Coding conventions

- **100% DTO**: no manager method may accept or return a raw JSON/XML `String` for structured data. Wrap it in a DTO instead.
- **Construction style**: `CreateXxxRequest`/`UpdateXxxRequest`/`PublishXxxRequest` DTOs support `Xxx.builder(...).field(...).build()` — add it to any new DTO alongside `of(...)` if you use that as the primary factory.
- **Typed exceptions**: never let a raw `RuntimeException` or a bare HTTP status code leak to the caller. Add a resource-specific `*NotFoundException` (extending `ResourceNotFoundException`) for any new resource type; reuse the existing ones (`AuthenticationException`, `ResourceAlreadyExistsException`, etc.) for cross-cutting cases.
- **One manager at a time**: when adding a new API group, implement it fully (manager + DTOs + exceptions + integration test) before starting the next one, per this repo's established workflow — see `docs/REST_API_MASTER_LIST.md` for what's covered.
- **Document server quirks where you find them**: GeoServer's REST API has real inconsistencies (status codes that don't match its own docs, fields that appear/disappear depending on state). When you find one, document it in the Javadoc of the method that hits it — don't just work around it silently.

## Testing

- Every manager needs an `*IntegrationTest` (runs against the live GeoServer from `docker-compose.yml`) covering create → read → update → delete where applicable, not just "call it once."
- Pure-logic unit tests (no live server needed) go in a plain `*Test` class alongside the integration test, for parsing/validation logic that doesn't need a real HTTP round-trip.
- If you're cross-checking library behavior against the raw REST API directly (not JUnit), that's a throwaway verification program — delete it once it passes, don't leave it in `src/test`.

## Pull requests

1. Add a `CHANGELOG.md` entry under `[Unreleased]` describing *why* the change was made, not just what changed.
2. `mvn clean verify` passes locally.
3. CI (GitHub Actions, same `mvn clean verify` against a fresh `docker-compose` GeoServer) is green on the PR.
4. For a behavior change to an existing method, re-run the full suite, not just the tests you touched — a shared `AbstractManager` helper or DTO change can have side effects elsewhere.
