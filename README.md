# GeoServer Client

[![CI](https://github.com/kimbongjune/geoserver-client/actions/workflows/ci.yml/badge.svg)](https://github.com/kimbongjune/geoserver-client/actions/workflows/ci.yml)
[![Security](https://github.com/kimbongjune/geoserver-client/actions/workflows/security.yml/badge.svg)](https://github.com/kimbongjune/geoserver-client/actions/workflows/security.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=kimbongjune_geoserver-client&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=kimbongjune_geoserver-client)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=kimbongjune_geoserver-client&metric=coverage)](https://sonarcloud.io/summary/new_code?id=kimbongjune_geoserver-client)
[![Codecov](https://codecov.io/gh/kimbongjune/geoserver-client/branch/main/graph/badge.svg)](https://codecov.io/gh/kimbongjune/geoserver-client)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kimbongjune/geoserver-client.svg)](https://central.sonatype.com/artifact/io.github.kimbongjune/geoserver-client)
[![Java](https://img.shields.io/badge/Java-8%2B-blue)](#requirements)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)
[![Javadoc](https://img.shields.io/badge/javadoc-1.1.2-blue)](https://kimbongjune.github.io/geoserver-client/apidocs/)

A modern Java 8+ client library for the GeoServer REST API — a complete, actively-tested replacement for the legacy `geoserver-manager` library.

Covers **44 API groups** across Core, Data, Security, GWC (GeoWebCache), Importer, and Monitoring, verified against a live GeoServer 2.28.2 instance with 951 automated tests (0 failures, 0 skipped).

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [API Coverage](#api-coverage)
- [Documentation](#documentation)
- [Examples](#examples)
- [Building from Source](#building-from-source)
- [Requirements](#requirements)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Builder pattern entry point**: `GeoServerClient.builder().url(...).credentials(...).build()`
- **100% DTO-based**: every request and response is a strongly-typed Java object — no raw JSON/XML strings in the public API
- **Consistent construction**: every Create/Update/Publish request DTO supports `Xxx.builder(...).field(...).build()`
- **Typed exception hierarchy**: `WorkspaceNotFoundException`, `ResourceAlreadyExistsException`, `AuthenticationException`, and 25+ other resource-specific exceptions — never a bare `RuntimeException`
- **Thread-safe by design**: a single `GeoServerClient` (and every manager it exposes) can be shared and called concurrently — build one per GeoServer endpoint and reuse it
- **Apache HttpClient 5**: connection-pooled HTTP backend, pool size tunable via `.maxConnections(int)` (default 50)
- **Jackson serialization**: JSON is the supported client-wide wire format; `DataFormat.XML` remains available for XML-shaped APIs that don't depend on the client default (e.g. SLD `StyleContent` bodies)
- **Java 8 compatible**: works on Java 8, 11, 17, 21
- **Known upstream GeoServer/GeoWebCache bugs** (e.g. WMS Layer PUT, GWC `truncateParameters`) are documented and covered by active regression tests that assert the failure itself, so a server-side fix surfaces as a test failure rather than silently disappearing

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.kimbongjune</groupId>
    <artifactId>geoserver-client</artifactId>
    <version>1.1.2</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.kimbongjune:geoserver-client:1.1.2'
```

## Quick Start

### 1. Create the client

```java
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

GeoServerClient client = GeoServerClient.builder()
        .url("http://localhost:8080/geoserver")
        .credentials("admin", "geoserver")
        .defaultFormat(DataFormat.JSON)
        .build();
```

### 2. Use managers

Every API group is accessed through a dedicated manager off `GeoServerClient`, discoverable via IDE autocomplete:

```java
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.datastore.CreateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.style.StyleContent;

// Workspaces
client.workspaces().list();
client.workspaces().get("my-workspace");
client.workspaces().create(CreateWorkspaceRequest.builder("my-workspace").isolated(false).build());
client.workspaces().delete("my-workspace", true);  // true = recursive

// Data stores
client.datastores().list("my-workspace");
client.datastores().create("my-workspace",
        CreateDataStoreRequest.builder("my-postgis")
                .type("PostGIS")
                .connectionParam("host", "localhost")
                .connectionParam("port", "5432")
                .connectionParam("database", "mydb")
                .connectionParam("dbtype", "postgis")
                .build());

// Styles (SLD)
client.styles().create(StyleContent.of(sldBody), "my-style");
client.styles().list();
```

### 3. Handle exceptions

```java
import io.github.kimbongjune.geoserverclient.exception.WorkspaceNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;

try {
    client.workspaces().get("nonexistent");
} catch (WorkspaceNotFoundException e) {
    System.out.println("Not found: " + e.getMessage());
}
```

See [Examples](#examples) for full runnable programs covering every major manager group.

## SLD Style Builder

`SldBuilder` generates SLD 1.0 (OGC 02-070) XML via a fluent builder API — no raw XML string concatenation.

### Point symbolizer with OGC filter
```java
StyleContent sld = SldBuilder.create("roads")
    .styleName("my-point-style")
    .rule("important-cities")
        .filter(OgcFilters.and(
            OgcFilters.greaterThan("population", SldExpression.literal(1_000_000)),
            OgcFilters.equalTo("type", SldExpression.literal("city"))
        ))
        .minScale(50_000).maxScale(500_000)
        .point()
            .wellKnownName(WellKnownName.CIRCLE)
            .size(SldExpression.literal(10))
            .fillColor("#FF6B35").fillOpacity(0.9)
            .strokeColor("#FFFFFF").strokeWidth(2.0)
        .end()
    .end()
    .build();

client.styles().createByWorkspace("my-workspace", sld, "my-point-style");
```

### Polygon symbolizer
```java
StyleContent sld = SldBuilder.create("polygons")
    .styleName("land-use")
    .rule("residential")
        .filter(OgcFilters.equalTo("land_use", SldExpression.literal("residential")))
        .polygon()
            .fillColor("#E8F5E9").fillOpacity(0.8)
            .strokeColor("#4CAF50").strokeWidth(1.5)
        .end()
    .end()
    .build();
```

### Raster symbolizer with color map
```java
StyleContent sld = SldBuilder.create("elevation")
    .styleName("dem-colors")
    .rule("dem")
        .raster()
            .opacity(1.0)
            .colorMap(SldColorMapType.RAMP,
                SldColorMapEntry.of("#313695", 0, 1.0, "sea level"),
                SldColorMapEntry.of("#74ADD1", 500, 1.0, "low"),
                SldColorMapEntry.of("#FEE090", 2000, 1.0, "mid"),
                SldColorMapEntry.of("#FFFFFF", 4000, 1.0, "high"))
        .end()
    .end()
    .build();
```

All OGC Filter predicates (`equalTo`, `greaterThan`, `bbox`, `intersects`, etc.), SLD geometry types, and expression functions (`SldExpression.property()`, `SldExpression.function()`) are available via `OgcFilters` and `SldExpression` static factories.

## API Coverage

| Group | Managers |
|-------|----------|
| **Core** | About, System, Settings, Logging, Namespace, Workspace |
| **Data** | DataStore, CoverageStore, FeatureType, Coverage, StructuredCoverage, Resource |
| **Layers** | Layer, LayerGroup, Style, Font, WmsLayer, WmtsLayer |
| **Services** | WMS, WFS, WCS, WMTS, Output |
| **Security** | SecurityManager, UserGroup, Role, AuthProvider, AuthFilter, FilterChain, UserGroupService |
| **GWC** | Global, BlobStore, Bounds, DiskQuota, GridSet, GwcLayer, Seed, MassTruncate, Reload, FilterUpdate |
| **Importer** | Importer |
| **Extensions** | Monitoring, Transform, URLCheck, Reset, Template |

**44 managers total**, verified against GeoServer 2.28.2 (should work on 2.20+).

## Documentation

Full API reference, usage guides, architecture diagrams, and known-quirks per manager are on the [project Wiki](https://github.com/kimbongjune/geoserver-client/wiki). Every public class and method also carries Javadoc — browse it locally with:

```bash
mvn javadoc:javadoc
# open target/site/apidocs/index.html
```

Javadoc is automatically published to **[GitHub Pages](https://kimbongjune.github.io/geoserver-client/apidocs/)** on every push to main.

## Examples

The [`examples/`](examples/) module contains standalone, runnable programs demonstrating real usage against a live GeoServer instance (not shipped inside the published JAR). See [examples/README.md](examples/README.md).

The [`spring-example/`](spring-example/) module is a full Spring Boot admin console (Gradle project) built on top of this library — one page per API domain (workspaces, vector/raster data, styles, layers, security, GWC, importer, WMS/WMTS cascading, settings, an OpenLayers map viewer with WMS/WFS-editable modes) with real forms wired to the actual managers, not a generic REST console. Useful as a reference for wiring the library into a real web app.

## Building from Source

```bash
# Compile only (no tests)
mvn clean compile

# Build JAR without tests
mvn clean package -DskipTests

# Full build with tests + coverage report
mvn clean verify
```

### Running Tests

> **Requires** a running GeoServer at `http://localhost:8100/geoserver` (admin/geoserver).
> Start with Docker: `docker-compose up -d` (see [docker-compose.yml](docker-compose.yml))

```bash
# Run all integration tests
mvn clean test

# Run a single test class
mvn test -Dtest="WorkspaceManagerIntegrationTest"

# Generate JaCoCo HTML coverage report
mvn clean verify
# Report: target/site/jacoco/index.html
```

## Project Structure

```
src/
  main/java/io/github/kimbongjune/geoserverclient/
    GeoServerClient.java           # Entry point (builder pattern)
    api/                           # 44 Manager classes, one package per API group
    dto/                           # Request/Response DTOs, one package per API group
    exception/                     # Typed exception hierarchy
    http/                          # Apache HttpClient 5 adapter
    serialization/                 # DataFormat enum, JSON/XML (de)serializers

  test/java/io/github/kimbongjune/geoserverclient/
    BaseIntegrationTest.java       # Shared client setup
    api/                           # Per-manager integration tests (44 classes)

examples/                          # Standalone runnable usage examples (not shipped in the JAR)
spring-example/                    # Spring Boot admin console demo app (Gradle, separate project)
docs/                              # Release/API-research reference docs
```

## Requirements

- **Java** 8 or above
- **Maven** 3.6+
- **GeoServer** 2.20+ (tested on 2.28.2)

## Contributing

Issues and pull requests are welcome — see [CONTRIBUTING.md](CONTRIBUTING.md) for setup, coding conventions, and the PR checklist.

## License

MIT License — see [LICENSE](LICENSE).
