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

All bundled sample data under `src/main/resources/` is either genuinely public-domain (Natural
Earth vector data) or a small, permissively-licensed test fixture from GDAL's own autotest suite —
see the comment at the top of each example for which one it uses and why.

## Index

| Example | Demonstrates | Needs a file? |
|---------|--------------|:---:|
| `Ex01_WorkspaceAndNamespace` | Workspace/Namespace CRUD, partial updates, default workspace/namespace, typed exceptions | no |
| `Ex02_DataStoreAndFeatureType` | Real live PostGIS datastore + publish, Shapefile ZIP, GeoPackage upload, `listAvailable`/`reset`/`downloadFile` | bundled |
| `Ex03_CoverageUploadAndLayer` | GeoTIFF, ArcGrid, WorldImage, and GeoPackage raster uploads, auto-created Coverage + Layer, `reset`/`listNative` | bundled |
| `Ex04_StyleAndLayerGroup` | SLD style creation (global + workspace-scoped), `getSld`/`listByLayer`/`addStyleToLayer`, LayerGroup, Layer/LayerGroup workspace-scoped variants | bundled |
| `Ex05_SecurityRolesAndUsers` | Role/User/Group CRUD, assignment, and every `*ByService` twin | no |
| `Ex06_GwcAndMonitoring` | All 10 GWC managers (GridSet, Global, Layer, Bounds, BlobStore, DiskQuota, MassTruncate, Reload, Seed, FilterUpdate) + Monitoring incl. CSV/XML/Excel/ZIP export | bundled |
| `Ex07_SettingsAndLogging` | Global/Contact/workspace Settings, Service settings (WMS/WFS/WCS/WMTS, global + workspace-scoped), Logging | no |
| `Ex08_WmsAndWmtsCascading` | WMS + WMTS cascaded layer publishing incl. `listAvailable` (self-cascade against GeoServer's own capabilities) | bundled |
| `Ex09_AboutAndResource` | Version/manifest/module/system status, full Resource API (create/copy/move/read/metadata/delete), Reset/Reload, fonts/templates, Transform availability | no |
| `Ex10_Importer` | Full Importer task/transform lifecycle — real GeoJSON import into a real PostGIS store | bundled |
| `Ex11_TemplateAndUrlCheck` | Freemarker templates at every scope (global/workspace/datastore/featuretype/coveragestore/coverage), URL Check CRUD | bundled |
| `Ex12_SldBuilder` | `SldBuilder` — all 5 symbolizer types (Point, Line, Polygon, Text, Raster), OGC filters, `SldExpression`, multi-scale rules | no |
| `Ex13_SecurityAdvanced` | Catalog/Service/REST ACL, master + self password, FilterChain (incl. reordering), AuthFilter/AuthProvider/UserGroupService CRUD | no |
| `Ex14_ImageMosaicAndStructuredCoverage` | ImageMosaic upload + `harvest()`, full StructuredCoverage granule index API | bundled |

Every method across all 44 managers is exercised somewhere in this list except
`GwcFilterUpdateManager.updateFilterZip` — it requires a pre-registered `WMSRasterFilter` parameter
filter pointing at a real *external* WMS server's capabilities (self-cascading it against this same
GeoServer risks the Tomcat deadlock documented on that manager), which doesn't fit a self-contained,
disposable-Docker-only example. `updateFilterXml` (the sibling call) is exercised via its documented
error path in Ex06.

Along the way, several genuine GeoServer 2.28.2 bugs/quirks were found and are documented with a
`[GeoServer 2.28.2 bug/quirk]` comment at their call site rather than worked around silently:
directory-backed Shapefile stores 500 on `downloadFile()`; REST ACL rule keys containing `/` can't
be deleted by the single-rule DELETE endpoint; `reloadAcl()`/`postReloadAcl()` can silently revive a
rule deleted immediately beforehand; a REST ACL delete right after an update to the same key can
silently no-op once; `GwcDiskQuotaConfig` round-trips fail through XStream unless `globalQuota` is
omitted; `GwcSeedRequest` NPEs server-side if `threadCount` isn't set explicitly; uploading a
`.geojson`/`.json` file to the Importer's task endpoint with `Content-Type: application/json` 400s
(use `application/octet-stream`); and `CoverageStoreManager.harvest()` 500s for a raw (non-zip) file
when passed `format="imagemosaic"` (pass the file's own format, e.g. `"geotiff"`, instead).
