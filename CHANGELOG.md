# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.1.0] - 2026-08-04

### Added

- **Workspace-level FeatureType endpoints**: `FeatureTypeManager.listByWorkspace/getByWorkspace/
  createByWorkspace/updateByWorkspace/deleteByWorkspace/resetByWorkspace` — store-less variants
  of the existing store-scoped methods. GeoServer resolves the target store automatically on
  create when the workspace has a single/default datastore.
- **`CoverageManager.createByWorkspace`**: `POST /workspaces/{ws}/coverages`. Unlike FeatureTypes,
  GeoServer requires the target store to be named explicitly in the request body.
- **`ResourceManager.headMetadata`**: `HEAD /rest/resource/{path}` — lightweight file/directory
  metadata via response headers, working for both files and directories (unlike `getMetadata()`,
  which only works reliably for directories on GeoServer 2.28.2 due to a server-side format quirk).
  Requires a new `GeoServerHttpClient.head()` method, implemented in `ApacheHttpClient` via
  Apache HttpClient 5's `HttpHead`.
- **`WmsLayerManager.publishByWorkspace` / `WmtsLayerManager.publishByWorkspace`**:
  `POST /workspaces/{ws}/{wmslayers|wmtslayers}` — store-less layer registration; the target
  store is named in the request body.
- **`TransformManager` CRUD**: `list/get/create/update/delete` for WFS output XSLT transforms.
  Implemented per GeoServer's documented endpoint shapes but unverified end-to-end — the XSLT
  plugin is not available on GeoServer 2.28.x (stable or community), matching the pre-existing
  `isAvailable()` method's own documented finding.
- **`GwcIndexManager`** (new, `client.gwcIndex()`): `GET /gwc/rest` — GeoWebCache's REST resource
  index page, parsed into resource links plus the raw HTML body.
- **`ImporterManager` data endpoints**: `getImportData/getTaskData/listImportDataFiles/
  listTaskDataFiles/getImportDataFile/deleteImportDataFile` for browsing and deleting files
  backing an import or import task.
- **`SecurityManager.deleteAllLayerAcl/deleteAllServiceAcl/deleteAllRestAcl`**: bulk ACL rule
  deletion. Documented `@Deprecated` — see Fixed/Known issues below.

### Fixed

- Corrected a REST API coverage audit that had misclassified several endpoints as
  "valid but unimplemented" when they don't actually exist on GeoServer 2.28.2: `GET/POST/PUT
  /rest/security/acl/rest/{rule}` (confirmed via `OPTIONS` → `Allow: DELETE,OPTIONS` only — only
  delete-by-rule is real) and `GET/DELETE /rest/imports/{id}/tasks/{taskId}/data/files/{filename}`
  (route does not exist at all on 2.28.2 — `OPTIONS` itself 404s).

### Known issues (GeoServer server-side, not library defects)

- `SecurityManager.deleteAllLayerAcl/deleteAllServiceAcl/deleteAllRestAcl` always throw
  `GeoServerResponseException(500)` on GeoServer 2.28.2 — a confirmed server-side
  `StringIndexOutOfBoundsException` when building the empty-rule-set response. No rules are
  actually deleted (verified rules are unchanged after the call). Same category as the
  pre-existing `WmsLayerManager.update()` 500 bug.
- `ImporterManager.getImportData(importId)` throws `GeoServerResponseException(500)` (a
  `NullPointerException` server-side) when the import has no import-level data assigned — the
  common case for task-based imports, where data lives on each `ImportTask` instead
  (`getTaskData(importId, taskId)` is unaffected and works reliably).

### Spring Boot example (`spring-example/`)

- Security page: added ACL rule viewing/add/delete for all three categories, plus the new
  bulk-delete-all actions (surfacing the real 500 bug transparently via the existing global
  exception handler instead of hiding it).
- GWC page: added a GWC REST index viewer.
- Importer page: added task-level data display, plus a button demonstrating the import-level
  data 500 bug.
- Fixed a pre-existing latent bug in the Data Directory Browser (`AboutService.isFile`): it used
  `getMetadata()`, which GeoServer answers with an unrequested XML body for file paths regardless
  of the requested JSON format, breaking JSON parsing whenever a file (not a directory) was
  browsed. Replaced with `headMetadata()`, which reads the `Resource-Type` header instead and
  works uniformly for both files and directories.

## [1.0.1] - 2026-07-31

### Added

- **Maven Central Portal `autoPublish` enabled**: releases now go live automatically after
  validation instead of requiring a manual click in the Central Portal UI (1.0.0's release
  needed that manual step).
- **`examples/` expanded**: rewritten in a friendlier walkthrough style, plus 5 new example
  classes covering previously-untested areas — `Ex13_SecurityAdvanced` (auth filters/providers,
  filter chains, user/group services) and `Ex14_ImageMosaicAndStructuredCoverage`
  (ImageMosaic harvest + granule management via `StructuredCoverageManager`) — along with real
  downloadable sample files (Shapefile, GeoPackage, ArcGrid, WorldImage) so every example runs
  against real data instead of only the one GeoTIFF used before.
- `CONTRIBUTING.md` and `SECURITY.md` added.
- SonarCloud static analysis and Codecov coverage upload wired into CI.

### Changed

- **Removed 44 `*CrossCheckTest` classes**: these existed to cross-validate the library's HTTP
  behavior against raw REST calls during initial development; with 552 integration tests already
  covering the same ground and passing consistently, the duplicate cross-check layer was pure
  maintenance overhead with no remaining signal.
- Javadoc cleanup for Central/javadoc.io rendering: fixed a stray `</p>` after `</ul>` in
  `LayerGroupManager`'s Javadoc, set `doclint=none` so pre-existing minor tag issues don't fail
  the build, updated the Javadoc title for 1.0.1.

### Fixed

- CI: Java 8 baseline build was broken by `--release` flag usage the real JDK 8 `javac` doesn't
  support, and by `mockito-core` 5.4.0 requiring JDK 11+ just to load — both fixed so the CI
  matrix's Java 8 leg actually builds again.
- CI: integration tests now bootstrap a default workspace before running, fixing failures on a
  fresh GeoServer instance that has none configured yet.
- Removed a `geotools` subdirectory that had been accidentally staged into git; added to
  `.gitignore` so it can't happen again.

---

## [1.0.0] - 2026-07-30

### Fixed (breaking — pre-release quality pass)

- **`GeoServerClient.Builder.defaultFormat(DataFormat.XML)`가 조용히 무시되던 문제**: 45개 매니저 중
  41개가 `AbstractManager`의 포맷 인지 `doGet/doPost/doPut` 헬퍼를 쓰지 않고 `httpClient.get(path,
  "application/json")`처럼 포맷을 하드코딩하고 있어, 빌더에서 `defaultFormat(DataFormat.XML)`을 설정해도
  대부분의 매니저는 항상 JSON으로만 통신했다 (README/Javadoc이 약속하던 기능이 41개 매니저에서 죽어있었음).
  실제로 XML 목표로 전면 리라이트가 가능한지 `XmlMapper.readTree()`로 실험한 결과, GeoServer의 JSON
  응답은 항목 수와 무관하게 항상 배열이지만 XML 응답은 **항목이 정확히 1개일 때 배열이 아닌 단일
  엘리먼트로 축소**되는 것을 확인했다 — 각 매니저가 공유하는 리스트 파싱 로직(`isArray()` 체크)이 이
  케이스를 방어하지 않아, 41개 파일 전부를 기계적으로 "하드코딩된 문자열 → defaultFormat" 치환만 하면
  XML 모드에서 항목이 1개일 때 조용히 빈 리스트를 반환하는 새 데이터 유실 버그를 만들 수 있는 상황이었다.
  파일별 리스트 파싱 로직 감사 + XML 전용 회귀 테스트 신설은 릴리스 직전에 감당하기엔 리스크가 커서,
  대신 **정직화**로 해결했다: `Builder.build()`가 `defaultFormat`에 `DataFormat.JSON` 외의 값이 오면
  `InvalidParameterException`으로 즉시 실패하도록 변경 (조용한 오동작 대신 명확한 빌드 타임 실패).
  `DataFormat`/`GeoServerClient.Builder#defaultFormat` Javadoc과 README에 현재 지원 범위(클라이언트
  기본값은 JSON만; SLD `StyleContent` 등 명시적으로 XML을 쓰는 API는 영향 없음)를 명시했다.

### Added (사용성 통일)

- **`Xxx.builder(...)...build()`를 모든 Create/Update/Publish 요청 DTO에서 동일하게 사용 가능**: 감사해보니
  `UpdateXxxRequest` 13개는 이미 전부 `builder()...build()`(불변 객체, 별도 Builder 클래스)로 통일돼
  있었는데, `CreateXxxRequest`/`PublishXxxRequest` 10개(`CreateWorkspaceRequest`, `CreateNamespaceRequest`,
  `CreateDataStoreRequest`, `CreateCoverageStoreRequest`, `CreateCoverageRequest`,
  `CreateFeatureTypeRequest`, `CreateWmsStoreRequest`, `PublishWmsLayerRequest`,
  `CreateWmtsStoreRequest`, `PublishWmtsLayerRequest`)만 `of(required...)` + 뮤터블 체이닝(`this` 반환)
  방식이라 매번 "이 DTO는 어떻게 만들지" 다시 찾아봐야 했다. 기존 `of(...)` 경로의 동작(뮤터블 체이닝)을
  그대로 유지한 채 — 이미 629개 테스트와 실사용 코드가 그 동작에 의존하고 있어 시맨틱을 바꾸는 건
  breaking change라 하지 않았다 — 각 클래스에 `builder(...)` 별칭 정적 메서드(내부적으로 `of(...)` 위임)와
  `build()` 항등 종단 메서드(`return this`)를 추가했다. 이제 라이브러리의 모든 Create/Update/Publish DTO가
  `Xxx.builder(...).optionalField(...).build()`라는 하나의 호출 규약으로 예외 없이 통한다.
  `DtoBuilderConsistencyTest`(10개 케이스, `builder()` 결과가 `of()` 결과와 `equals()` 동일함 + `build()`가
  동일 인스턴스를 반환하는 항등 연산임을 검증)로 커버.
- (재검토 후 변경 안 함) DTO 패키지가 `dto.wmsstore`/`dto.wmslayer`처럼 스토어/발행-리소스 별로 나뉜 것은
  `dto.coveragestore`/`dto.coverage`, `dto.datastore`/`dto.featuretype`과 동일한, 라이브러리 전체에 일관된
  기존 컨벤션이었음을 확인 — 최초 검토에서 "일관성 없어 보인다"고 지적했던 건 코드베이스를 처음 접해서
  생긴 착오였고 실제 결함이 아니었다.

### Changed (breaking)

- **100% DTO 원칙 위반 정리**: 초기 구현에서 raw JSON/XML `String`을 파라미터·반환값으로 쓰던 API를
  전부 타입드 DTO로 교체했다.
  - GWC 10개 매니저 전체(`GwcLayerManager`, `GwcSeedManager`, `GwcGlobalManager`,
    `GwcBlobStoreManager`, `GwcGridSetManager`, `GwcDiskQuotaManager`, `GwcMassTruncateManager`,
    `GwcReloadManager`, `GwcFilterUpdateManager`, `GwcBoundsManager`) — 신규 DTO:
    `GwcLayer`, `GwcSeedRequest`, `GwcGlobalSettings`, `GwcFileBlobStore`, `GwcGridSet`,
    `GwcDiskQuotaConfig`/`GwcQuota`, `GwcTruncate*Request`, `GwcReloadResult`, `GwcTileBounds` 등
  - Security 보조 매니저 4개(`AuthFilterManager`, `AuthProviderManager`, `FilterChainManager`,
    `UserGroupServiceManager`) — 신규 DTO: `AuthFilterConfig`, `AuthProviderConfig`,
    `FilterChainEntry`, `UserGroupServiceConfig`
  - `StyleManager`(SLD 본문 → `StyleContent`), `TemplateManager`(FTL 본문 → `TemplateContent`),
    `OutputManager`(raw JSON → `List<String>`/`List<TemplateInfo>`)
  - 알려진 서버 버그(JSON PUT → XStream 500 등)를 라이브러리가 내부적으로 회피하도록 캡슐화
    (해당 엔드포인트는 항상 XML로 직렬화).

### Fixed

- `DataStoreManager.create()`가 POST 성공 후 GET 조회 실패 시 `catch (Exception e)`로
  모든 예외를 삼키던 것을 `DataStoreNotFoundException`(비ASCII 이름 404 케이스)만 좁혀 처리하도록 수정.
  다른 예외(네트워크 오류 등)는 이제 정상적으로 전파된다.
- GWC `GwcLayerManager`/`GwcBlobStoreManager`/`GwcGridSetManager`의 `get()`이 존재하지 않는
  리소스에 대해 GeoServer 2.28.2 / GWC 1.28.2에서 404가 아닌 500을 반환하는 것을 감지해
  `ResourceNotFoundException`으로 정규화 (2026-04-02 검증 당시와 다른 서버 동작 — 재검증 시 발견).
- 로컬 개발 환경 GeoServer 포트를 9090 → 8100으로 정정 (`docker-compose.yml`,
  `BaseIntegrationTest`, README, docs).

### Docs

- README Quick Start 예제가 실제 존재하지 않는 메서드(`workspaceManager()`, `.getAll()`,
  `.getByName()`)와 DTO 클래스(`CreateStyleRequest`)를 참조하던 것을 실제 API에 맞게 수정.
  `DataFormat`의 실제 패키지(`serialization`, 문서엔 `core`로 잘못 기재)도 정정.

### Added (후속 라운드)

- 모든 DTO(106개 파일, 192개 클래스)에 `equals()`/`hashCode()`/`toString()` 구현 추가.
  `javalang` AST 기반 코드 생성 스크립트로 필드 목록을 정확히 추출해 일괄 적용하고,
  기존에 손으로 작성돼 있던 `toString()`은 새 버전으로 교체했다. `Map`/컬렉션 필드를 가진
  DTO를 값 객체로 안전하게 비교·로깅할 수 있게 됨.
- `GeoServerClient.Builder.build()`에 `connectTimeout`/`responseTimeout`/`maxConnections`
  파라미터 유효성 검사 추가 (0 이하 값은 `InvalidParameterException`으로 즉시 실패).
- `AuthFilterManager`/`FilterChainManager`/`UserGroupServiceManager`의 `list()`가 응답에
  래퍼가 비어있는 경우(`wrapper`/`items`가 `null`) `NullPointerException`을 던지던 것을
  `AuthProviderManager.list()`와 동일한 null-safe 패턴으로 통일.
- `ConcurrencyIntegrationTest` 추가: 커넥션 풀 크기 5로 제한한 단일 `GeoServerClient`를
  40개 스레드가 여러 매니저에 걸쳐 동시 호출해도 오류·데드락 없이 완료되는지 검증하는
  회귀 테스트 (기존에는 임시 스크립트로만 검증하던 것을 정식 테스트 스위트에 편입).
- `AuthFilterConfig`/`AuthProviderConfig`/`UserGroupServiceConfig`에 `getExtraString`/
  `getExtraBoolean`/`getExtraLong` 타입 접근자 추가 (동적 `extra` 맵 값을 캐스팅 없이 사용 가능).
- Security 4개 매니저(`AuthFilterManager`, `AuthProviderManager`, `FilterChainManager`,
  `UserGroupServiceManager`)의 `create()`에 실서버 검증된 중복 이름 감지 로직 추가,
  `ResourceAlreadyExistsException`으로 정규화. `get()`도 `isNotFound()` 기반으로 통일해
  `ResourceNotFoundException`을 일관되게 던지도록 수정 (`AuthFilterManager`는 기존에
  `{"null":""}` 버그 응답 시 `null`을 반환하던 것도 예외로 변경).
- `GeoServerClient`에 스레드 안전성 보장 문서화, `Builder.maxConnections(int)` 추가
  (Apache HttpClient 5 `PoolingHttpClientConnectionManager` 풀 크기 조정, 기본값 50).

### Fixed (후속 라운드)

- `WorkspaceManager.java`, `WmsLayerManager.java`의 손상된 한글 Javadoc(mojibake) 복구.
  `WmsLayerManager.java`는 U+FFFD 치환 문자로 완전히 소실된 73줄 분량의 메서드 Javadoc을
  클래스 헤더 Javadoc과 실제 메서드 시그니처를 근거로 재작성.

### Changed (아키텍처/코드 품질 라운드)

- **`GeoServerClient` 파사드를 필드 나열 방식에서 레지스트리 방식으로 재작성**: 매니저 45개를
  각각 `private final XxxManager xxxManager` 필드 + 생성자 대입으로 나열하던 것을
  `Map<Class<? extends AbstractManager>, AbstractManager>` 레지스트리 + 제네릭 `manager(Class)`
  조회로 교체. 공개 API(`workspaces()` 등 accessor 45개, `builder()`, `create()`)는 시그니처가
  전부 동일하게 유지되어 기존 호출부(테스트 548개 포함) 무변경. 새 API 그룹 추가 시 손댈 곳이
  "필드 선언 + 생성자 대입 + accessor" 3곳에서 "`register()` 호출 + accessor" 2곳으로 줄었다.
  트레이드오프: 매니저 등록 누락 같은 실수가 컴파일 에러 대신 런타임 `IllegalStateException`으로
  드러난다 — 이를 상쇄하기 위해 `GeoServerClientWiringTest`를 등록-접근자 양방향 불일치를
  전부 검증하도록 다시 작성했고, 실제로 `register()` 호출 하나를 지워서 테스트가 정확한
  메시지("Manager not registered: ...")로 실패하는 것까지 확인 후 원복.
- **패키지명 오타 수정**: `api.namepsace` → `api.namespace` (`NamespaceManager`,
  `NamespaceManagerIntegrationTest`). 1.0 릴리즈 전 마지막으로 고칠 수 있는 시점이라 지금 정리.
- **예외 계층 일관성 통일**: 이번 DTO 리팩터링 라운드에서 만든 7개 매니저
  (`AuthFilterManager`, `AuthProviderManager`, `FilterChainManager`, `UserGroupServiceManager`,
  `GwcLayerManager`, `GwcBlobStoreManager`, `GwcGridSetManager`)가 제네릭
  `ResourceNotFoundException`만 던지던 것을, 기존 21개 리소스처럼 전용 서브클래스
  (`AuthFilterNotFoundException` 등 7개 신규)로 통일. 이제 모든 리소스를 동일하게
  타입 기반 `catch`로 처리할 수 있다.
- **순수 로직 유닛 테스트 18개 신설**: `GwcTileBoundsTest`(정규식 파싱),
  `GwcReloadManagerTest`(HTML 응답 파싱, `GeoServerHttpClient` Mockito mock 사용),
  `AbstractManagerErrorMappingTest`(상태코드→예외 매핑 전수 검증),
  `GeoServerClientWiringTest`(매니저 필드 45개 각각에 대응하는 public accessor가 정확히
  하나씩 존재하는지, 중복 accessor는 없는지 리플렉션으로 검증 — `GeoServerClient`처럼
  손으로 반복 배선하는 파사드 클래스에 실수(필드만 추가하고 accessor 빠뜨림)가 생겨도
  기존 컴파일/테스트로는 안 잡히는 케이스를 잡아줌). 도커/실서버 없이 1초 내로 실행되며,
  기존에 `mockito-core` 의존성만 있고 실사용은 0건이던 것을 처음으로 활용.

### Changed (0-skip 라운드)

- **`GwcMassTruncateManagerIntegrationTest`의 `@Disabled` 제거**: `truncateParameters`가
  HTTP 500을 반환하는 게 실제로 지금도 재현되는지 라이브 서버에 재확인한 뒤 (테스트 셋업
  실수가 아니라 진짜 GeoWebCache 서버 버그로 확인됨), `WmsLayerManagerIntegrationTest`의
  `update_alwaysThrows500()`와 동일한 패턴으로 "500이 던져지는 것 자체를 검증"하는 능동적
  회귀 테스트로 전환. 이제 전체 스위트에 스킵이 0개다 — 이 버그가 서버 쪽에서 고쳐지면
  `assertThrows`가 실패하면서 알려주므로, 조용히 사라지는 스킵보다 낫다.

### Fixed (아키텍처/코드 품질 라운드)

- **equals/hashCode/toString 코드젠 버그**: `GwcSeedStatus`, `GwcTileBounds`는 `List<long[]>`
  필드를 갖는데, 자동 생성기가 배열 타입을 특별 처리하지 않아 `Objects.equals`/`Objects.hash`가
  배열을 참조 동일성으로 비교하고 `toString()`이 `[J@해시코드` 같은 값을 찍던 문제를
  `Arrays.equals`/`Arrays.hashCode`/`Arrays.toString` 기반 수동 구현으로 교체.
  (`a.equals(b)`가 내용이 같아도 `false`였던 것을 실측으로 확인 후 수정.)
- **중첩 클래스 닫는 중괄호 들여쓰기 유실**: 같은 코드 생성기가 DTO 106개에 메서드를 삽입하면서
  37개 파일, 86곳에서 중첩 클래스의 닫는 `}`가 들여쓰기 0칸으로 잘못 남던 포맷 결함을
  중괄호 짝 추적 스크립트로 일괄 복구 (동작에는 영향 없는 순수 포맷 문제였음).

---

## [0.1.0] - 2025-04-02 (initial development milestone, never published to Maven Central)

### Added

#### Core
- `GeoServerClient` builder pattern entry point (`GeoServerClient.builder()...build()`)
- Apache HttpClient 5 HTTP adapter with connection pooling
- `DataFormat` enum (JSON / XML) with per-request override support
- Typed exception hierarchy: `WorkspaceNotFoundException`, `DataStoreNotFoundException`, `StyleNotFoundException`, `ResourceAlreadyExistsException`, `GeoServerClientException`

#### API Managers (44 total, verified against GeoServer 2.28.2)

**Core**
- `AboutManager` — version info, manifests, system status
- `SystemStatusManager` — system metrics
- `ResetManager` — cache/config reset
- `SettingsManager` — global and workspace-scoped settings
- `LoggingManager` — log level configuration

**Workspaces & Namespaces**
- `WorkspaceManager` — CRUD + default workspace
- `NamespaceManager` — CRUD + default namespace

**Data Stores**
- `DataStoreManager` — CRUD, file upload (shapefile/geotiff/etc.), AppSchema, MongoDB
- `CoverageStoreManager` — CRUD, file upload
- `FeatureTypeManager` — CRUD + recalculate bounding box
- `CoverageManager` — CRUD + reset cache
- `StructuredCoverageManager` — granule management

**Layers & Styles**
- `LayerManager` — CRUD + default style assignment
- `LayerGroupManager` — CRUD (global + workspace-scoped)
- `StyleManager` — CRUD (SLD 1.0/1.1, YSLD, CSS, MapBox), global + workspace-scoped
- `FontManager` — list available fonts
- `TemplateManager` — Freemarker template CRUD

**WMS Cascading**
- `WmsStoreManager` — CRUD for remote WMS stores
- `WmsLayerManager` — CRUD for cascaded WMS layers

**WMTS Cascading**
- `WmtsStoreManager` — CRUD for remote WMTS stores
- `WmtsLayerManager` — CRUD for cascaded WMTS layers

**Service Configuration**
- `ServiceManager` — WMS, WFS, WCS, WMTS global/workspace service settings
- `OutputManager` — WCS output format configuration

**Security**
- `SecurityManager` — master password, ACL, self-management
- `UserGroupManager` — CRUD users and groups per user group service
- `UserGroupServiceManager` — list available user group services
- `RoleManager` — CRUD roles per role service, role-to-user/group assignments
- `AuthProviderManager` — CRUD authentication providers
- `AuthFilterManager` — CRUD authentication filters
- `FilterChainManager` — CRUD security filter chains

**GWC (GeoWebCache)**
- `GwcGlobalManager` — global GWC configuration
- `GwcBlobStoreManager` — blob store CRUD
- `GwcBoundsManager` — layer bound recalculation
- `GwcDiskQuotaManager` — disk quota configuration
- `GwcFilterUpdateManager` — request filter updates
- `GwcGridSetManager` — grid set CRUD
- `GwcLayerManager` — tiled layer CRUD
- `GwcMassTruncateManager` — bulk cache truncation
- `GwcReloadManager` — GWC configuration reload
- `GwcSeedManager` — tile seeding/truncation jobs

**Importer**
- `ImporterManager` — import context creation, file upload, task execution, transformation chains

**Extensions**
- `MonitoringManager` — request/response log query and management
- `TransformManager` — XSLT/Freemarker transformation CRUD
- `UrlCheckManager` — URL validation rule CRUD
- `ResourceManager` — server-side file browser (read, write, delete, metadata)

#### Testing
- 44 `*IntegrationTest` classes, 552 tests (0 failures, 1 skipped)
- `AllManagersIntegrationTest` suite for single-command full test run
- `BaseIntegrationTest` shared setup with `GeoServerClient.builder()`
- Docker Compose configuration for reproducible GeoServer 2.28.2 environment

#### Build
- JaCoCo 0.8.11 coverage reporting (HTML + CSV): Instructions 74.4%, Lines 76.1%, Methods 72.8%
- `maven-source-plugin` — sources JAR
- `maven-javadoc-plugin` — Javadoc JAR (UTF-8, Java 8 doclet)
- `maven-shade-plugin` — fat JAR with all dependencies
- `maven-release-plugin` — release workflow
- `central-publishing-maven-plugin` — Maven Central publishing
- `maven-gpg-plugin` — artifact signing (release profile)

---

## Notes

- **Breaking change vs. geoserver-manager**: Entry point is `GeoServerClient.builder()`, not `new GeoServer(...)`. All model classes are in `io.github.kimbongjune.geoserverclient.*` packages.
- GeoServer 2.x compatibility: tested on 2.28.2; should work on 2.20+.
- The `TransformManager` is a no-op stub — the Importer extension transform API is only available when the Importer plugin is installed and active.
