# GeoServer REST API 전수조사 마스터 목록

> **대상 서버**: GeoServer 2.28.2 (localhost:8100/geoserver)
> **조사일**: 2026-03-17
> **목적**: geoserver-client 신규 Java 클라이언트 개발을 위한 완전한 API 명세 작성

## 상태 범례
- ✅ 완료: 3-Way Research + 서버 검증 + 문서화 완료
- 🔄 진행중
- ⬜ 미착수

---

## 1. Core REST API (GeoServer 기본)

| # | API 그룹 | 기본 엔드포인트 | Manager 클래스 | 상태 |
|---|---------|---------------|---------------|------|
| 1-1 | Workspace | /rest/workspaces | api/workspace/WorkspaceManager.java | ✅ |
| 1-2 | Namespace | /rest/namespaces | api/namespace/NamespaceManager.java | ✅ |
| 1-3 | DataStore | /rest/workspaces/{ws}/datastores | api/datastore/DataStoreManager.java | ✅ |
| 1-4 | FeatureType | /rest/workspaces/{ws}/datastores/{ds}/featuretypes | api/featuretype/FeatureTypeManager.java | ✅ |
| 1-5 | CoverageStore | /rest/workspaces/{ws}/coveragestores | api/coveragestore/CoverageStoreManager.java | ✅ |
| 1-6 | Coverage | /rest/workspaces/{ws}/coveragestores/{cs}/coverages | api/coverage/CoverageManager.java | ✅ |
| 1-7 | StructuredCoverage | /rest/workspaces/{ws}/coveragestores/{cs}/coverages/{c}/index | api/coverage/StructuredCoverageManager.java | ✅ |
| 1-8 | WmsStore | /rest/workspaces/{ws}/wmsstores | api/wms/WmsStoreManager.java | ✅ |
| 1-9 | WmsLayer | /rest/workspaces/{ws}/wmsstores/{wms}/wmslayers | api/wms/WmsLayerManager.java | ✅ |
| 1-10 | WmtsStore | /rest/workspaces/{ws}/wmtsstores | api/wmts/WmtsStoreManager.java | ✅ |
| 1-11 | WmtsLayer | /rest/workspaces/{ws}/wmtsstores/{wmts}/layers | api/wmts/WmtsLayerManager.java | ✅ |
| 1-12 | Layer | /rest/layers | api/layer/LayerManager.java | ✅ |
| 1-13 | LayerGroup | /rest/layergroups | api/layergroup/LayerGroupManager.java | ✅ |
| 1-14 | Style | /rest/styles | api/style/StyleManager.java | ✅ |
| 1-15 | Font | /rest/fonts | api/font/FontManager.java | ✅ |

## 2. Settings & System API

| # | API 그룹 | 기본 엔드포인트 | Manager 클래스 | 상태 |
|---|---------|---------------|---------------|------|
| 2-1 | Settings (Global/Contact/Local) | /rest/settings | api/settings/SettingsManager.java | ✅ |
| 2-2 | OWS Services (WMS/WFS/WCS/WMTS) | /rest/services/{service}/settings | api/service/ServiceManager.java | ✅ |
| 2-3 | Logging | /rest/logging | api/logging/LoggingManager.java | ✅ |
| 2-4 | Reset & Reload | /rest/reset, /rest/reload | api/reset/ResetManager.java | ✅ |
| 2-5 | About (Version/Manifest/Status/SystemStatus) | /rest/about | api/about/AboutManager.java | ✅ |
| 2-6 | System Status | /rest/about/system-status | api/about/AboutManager.java (2-5에 통합) | ✅ |

## 3. Resource & Template API

| # | API 그룹 | 기본 엔드포인트 | Manager 클래스 | 상태 |
|---|---------|---------------|---------------|------|
| 3-1 | Resource | /rest/resource | api/resource/ResourceManager.java | ✅ |
| 3-2 | Template | /rest/templates, /rest/workspaces/{ws}/templates 등 | api/template/TemplateManager.java | ✅ |

## 4. Security API

| # | API 그룹 | 기본 엔드포인트 | Manager 클래스 | 상태 |
|---|---------|---------------|---------------|------|
| 4-1 | Master Password | /rest/security/masterpw | api/security/SecurityManager.java | ✅ |
| 4-2 | Self Password | /rest/security/self/password | api/security/SecurityManager.java | ✅ |
| 4-3 | ACL (Catalog/Layers/Services/REST) | /rest/security/acl | api/security/SecurityManager.java | ✅ |
| 4-4 | Roles | /rest/security/roles | api/security/RoleManager.java | ✅ |
| 4-5 | User/Group | /rest/security/usergroup | api/security/UserGroupManager.java | ✅ |
| 4-6 | Auth Filters | /rest/security/authfilters | api/security/AuthFilterManager.java | ✅ |
| 4-7 | Auth Providers | /rest/security/authproviders | api/security/AuthProviderManager.java | ✅ |
| 4-8 | Filter Chains | /rest/security/filterchain | api/security/FilterChainManager.java | ✅ |
| 4-9 | User/Group Services | /rest/security/usergroupservices | api/security/UserGroupServiceManager.java | ✅ |

## 5. GeoWebCache REST API

| # | API 그룹 | 기본 엔드포인트 | Manager 클래스 | 상태 |
|---|---------|---------------|---------------|------|
| 5-1 | GWC Layers | /gwc/rest/layers | api/gwc/GwcLayerManager.java | ✅ |
| 5-2 | GWC BlobStores | /gwc/rest/blobstores | api/gwc/GwcBlobStoreManager.java | ✅ |
| 5-3 | GWC Global | /gwc/rest/global | api/gwc/GwcGlobalManager.java | ✅ |
| 5-4 | GWC GridSets | /gwc/rest/gridsets | api/gwc/GwcGridSetManager.java | ✅ |
| 5-5 | GWC DiskQuota | /gwc/rest/diskquota | api/gwc/GwcDiskQuotaManager.java | ✅ |
| 5-6 | GWC Seed | /gwc/rest/seed | api/gwc/GwcSeedManager.java | ✅ |
| 5-7 | GWC MassTruncate | /gwc/rest/masstruncate | api/gwc/GwcMassTruncateManager.java | ✅ |
| 5-8 | GWC Reload | /gwc/rest/reload | api/gwc/GwcReloadManager.java | ✅ |
| 5-9 | GWC FilterUpdate | /gwc/rest/filter | api/gwc/GwcFilterUpdateManager.java | ✅ |
| 5-10 | GWC Bounds | /gwc/rest/bounds | api/gwc/GwcBoundsManager.java | ✅ |

## 6. Extension API (플러그인 설치 필요)

| # | API 그룹 | 기본 엔드포인트 | Manager 클래스 | 상태 |
|---|---------|---------------|---------------|------|
| 6-1 | Importer | /rest/imports | api/importer/ImporterManager.java | ✅ |
| 6-2 | XSLT Transform | /rest/services/wfs/transforms | api/transform/TransformManager.java | N/A (GeoServer 2.28.x stable/community 모두 미지원 — xslt-plugin.zip 404 서버 검증) |
| 6-3 | Monitoring | /rest/monitor/requests | api/monitoring/MonitoringManager.java | ✅ |

## 7. 기타

| # | API 그룹 | 기본 엔드포인트 | Manager 클래스 | 상태 |
|---|---------|---------------|---------------|------|
| 7-1 | URL Checks | /rest/urlchecks | api/urlcheck/UrlCheckManager.java | ✅ |
| 7-2 | Output Formats | /rest/fonts, /rest/templates | api/output/OutputManager.java | ✅ (편의 wrapper — /rest/fonts HTTP 200, /rest/templates HTTP 200 서버 검증 2026-04-02) |

---

## 서버 검증 결과 요약 (2026-03-17, GeoServer 2.28.2)

### GET 200 OK 확인:
- rest/about/version.json, rest/about/system-status.json, rest/about/status.json, rest/about/manifest.json
- rest/workspaces.json, rest/namespaces.json
- rest/styles.json, rest/layers.json, rest/layergroups.json, rest/fonts.json
- rest/settings.json, rest/logging.json
- rest/services/wms/settings.json, rest/services/wfs/settings.json, rest/services/wcs/settings.json, rest/services/wmts/settings.json
- rest/resource/
- rest/security/acl/layers.json, rest/security/acl/services.json, rest/security/acl/rest.json, rest/security/acl/catalog.json
- rest/security/roles.json, rest/security/usergroup/users.json, rest/security/usergroup/groups.json
- rest/security/usergroupservices.json, rest/security/masterpw.json
- rest/security/authfilters.json, rest/security/authproviders.json, rest/security/filterchain.json
- rest/monitor/requests.json
- rest/urlchecks.json
- gwc/rest/layers.json, gwc/rest/blobstores.json, gwc/rest/global.json, gwc/rest/gridsets.json, gwc/rest/diskquota.json
- gwc/rest/seed.json, gwc/rest/masstruncate

### POST/PUT only (GET 405):
- rest/reset, rest/reload
- rest/security/self/password
- gwc/rest/reload

### Extension 미설치 (404):
- rest/imports.json (Importer extension)
- rest/services/wfs/transforms.json (XSLT extension)

### 기타:
- rest/about/manifests.json → 404 (올바른 URL: rest/about/manifest.json)
