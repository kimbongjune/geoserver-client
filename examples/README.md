# geoserver-client Examples

Standalone, runnable programs demonstrating real usage of the `geoserver-client` library against
a live GeoServer instance. This is a separate Maven project — it is **not** a module of the root
build and is never packaged into the published library JAR.

Every example prints what it's doing step by step as it runs, explains *why* in its class-level
Javadoc, and cleans up everything it creates — safe to read top-to-bottom as a tutorial, and safe
to re-run over and over.

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

Most examples run standalone with **no arguments needed** — the ones that need a raster file fall
back to a tiny bundled `sample.tif` automatically. Pass your own file path as an argument to use
real imagery instead:

```bash
mvn exec:java -Dexec.mainClass="io.github.kimbongjune.geoserverclient.examples.Ex03_CoverageUploadAndLayer" \
    -Dexec.args="/path/to/your/real.tif"
```

`Ex02_DataStoreAndFeatureType`'s Shapefile-upload step is the one exception that's skipped without
an argument (a real Shapefile ZIP is too large to bundle) — it prints a clear note when this
happens rather than failing.

## Index

| Example | Demonstrates | Needs a file? |
|---------|--------------|:---:|
| `Ex01_WorkspaceAndNamespace` | Workspace/Namespace CRUD, partial updates, typed exceptions | no |
| `Ex02_DataStoreAndFeatureType` | PostGIS datastore config, Shapefile ZIP upload with `configure=first` | optional |
| `Ex03_CoverageUploadAndLayer` | GeoTIFF upload, auto-created Coverage + Layer, Layer partial update | bundled |
| `Ex04_StyleAndLayerGroup` | SLD style creation from raw XML, LayerGroup with a real layer | bundled |
| `Ex05_SecurityRolesAndUsers` | Role/User/Group CRUD and assignment | no |
| `Ex06_GwcAndMonitoring` | Custom GWC GridSet, GWC global config, Monitoring request history | no |
| `Ex07_SettingsAndLogging` | Global/workspace Settings (REPLACE semantics), Logging config | no |
| `Ex08_WmsAndWmtsCascading` | WMS + WMTS cascaded layer publishing (self-cascade against GeoServer's own capabilities) | bundled |
| `Ex09_AboutAndResource` | Version/module status, browsing the data directory (read-only) | no |
| `Ex10_Importer` | Import context lifecycle (create/list/delete) | no |
| `Ex11_TemplateAndUrlCheck` | Freemarker template CRUD, URL Check rule CRUD | no |
| `Ex12_SldBuilder` | `SldBuilder` — all 5 symbolizer types (Point, Line, Polygon, Text, Raster), OGC filters, `SldExpression`, multi-scale rules | no |

Not covered yet by a dedicated example (see the [API Reference wiki page](https://github.com/kimbongjune/geoserver-client/wiki/API-Reference)
for their full method lists): `ServiceManager` (WMS/WFS/WCS/WMTS service settings — the workspace-scoped
variant needs Admin-UI pre-initialization, see its Javadoc), `AuthFilterManager`/`AuthProviderManager`/
`FilterChainManager`/`UserGroupServiceManager` (security config, mostly read-only listing), the
remaining GWC managers beyond GridSet/Global (BlobStore, DiskQuota, Seed, MassTruncate, Reload,
FilterUpdate, Bounds — see `docs/GEOSERVER_BUG_WMS_LAYER_PUT.md`-style Javadoc notes on each for
known quirks), `TransformManager` (plugin-dependent, `isAvailable()` only), and
`StructuredCoverageManager` (ImageMosaic granule management — needs a multi-file mosaic to demo
meaningfully). Contributions adding examples for these are welcome — see `CONTRIBUTING.md`.
