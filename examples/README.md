# geoserver-client Examples

Standalone, runnable programs demonstrating real usage of the `geoserver-client` library against
a live GeoServer instance. This is a separate Maven project — it is **not** a module of the root
build and is never packaged into the published library JAR.

## Prerequisites

1. Install the library to your local Maven repository first (the examples depend on it by version):
   ```bash
   cd ..
   mvn clean install -DskipTests
   ```
2. Start a local GeoServer:
   ```bash
   docker-compose up -d
   ```
   All examples connect to `http://localhost:8100/geoserver` with `admin`/`geoserver`.

## Running an example

```bash
cd examples
mvn compile
mvn exec:java -Dexec.mainClass="io.github.kimbongjune.geoserverclient.examples.Ex01_WorkspaceAndNamespace"
```

Examples that need a real file (GeoTIFF, Shapefile ZIP) take its path as a program argument:

```bash
mvn exec:java -Dexec.mainClass="io.github.kimbongjune.geoserverclient.examples.Ex03_CoverageUploadAndLayer" \
    -Dexec.args="/path/to/sample.tif"
```

## Index

| Example | Demonstrates |
|---------|--------------|
| `Ex01_WorkspaceAndNamespace` | Workspace/Namespace CRUD, partial updates, typed exceptions |
| `Ex02_DataStoreAndFeatureType` | PostGIS datastore config, Shapefile ZIP upload with `configure=first` |
| `Ex03_CoverageUploadAndLayer` | GeoTIFF upload, auto-created Coverage + Layer, Layer partial update |
| `Ex04_StyleAndLayerGroup` | SLD style creation from raw XML, LayerGroup with a real layer |
| `Ex05_SecurityRolesAndUsers` | Role/User/Group CRUD and assignment |
| `Ex06_GwcAndMonitoring` | Custom GWC GridSet, GWC global config, Monitoring request history |

Every example cleans up the resources it creates, so they're safe to re-run repeatedly.
