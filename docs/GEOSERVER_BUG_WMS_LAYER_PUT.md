# GeoServer Bug: WMS Layer PUT 500 Internal Server Error

> **작성일**: 2026-03-18  
> **영향 버전**: GeoServer 2.16.x ~ 2.28.2 이상 (main 브랜치 미수정)  
> **심각도**: Major — WMS Cascading Layer PUT이 실제 환경에서 100% 실패  
> **발견 경위**: geoserver-client Java 라이브러리 개발 중 서버 검증 시 발견  

---

## 1. 증상

WMS Cascading Layer를 PUT으로 수정하면 항상 500 Internal Server Error가 반환됨.

```http
PUT /geoserver/rest/workspaces/{ws}/wmsstores/{store}/wmslayers/{layer}
Content-Type: application/json
{"wmsLayer": {"title": "any value"}}

→ HTTP 500 Internal Server Error
```

어떤 요청 바디를 보내도 동일하게 실패함. 바디 파싱 이전 단계에서 터지기 때문.

---

## 2. 서버 로그 (GeoServer 2.28.2)

WMS 서버가 연결 가능한 경우:
```
Caused by: java.lang.UnsupportedOperationException
    at java.util.AbstractCollection.add(AbstractCollection.java:251)
    at java.util.AbstractCollection.addAll(AbstractCollection.java:336)
    at org.geoserver.ows.util.OwsUtils.updateCollectionProperty(OwsUtils.java:378)
    at org.geoserver.ows.util.OwsUtils.copy(OwsUtils.java:303)
```

WMS 서버가 연결 불가한 경우:
```
Caused by: java.lang.reflect.UndeclaredThrowableException
    at jdk.proxy3.$Proxy135.getRemoteStyleInfos(Unknown Source)
Caused by: java.lang.IllegalAccessException: 
    class org.geoserver.catalog.impl.ModificationProxyCloner cannot access 
    class java.util.Collections$EmptySet (in module java.base)
```

---

## 3. 근본 원인 분석

### 버그 위치
`src/main/src/main/java/org/geoserver/catalog/impl/WMSLayerInfoImpl.java`

```java
@Override
public Set<StyleInfo> getRemoteStyleInfos() {
    try {
        return getWMSLayer(null).getStyles().stream()
                .map(WMSLayerInfoImpl::getStyleInfo)
                .collect(Collectors.toSet());  // ✅ 정상: 가변 HashSet
    } catch (Exception e) {
        return Collections.emptySet();         // ❌ 버그: 불변 Set
    }
}
```

### 실패 흐름

REST PUT 처리 시 `OwsUtils.copy(catalogObject, emptyObject)` 호출됨:

1. `newValue = catalogObject.getRemoteStyleInfos()`
   - WMS 연결 가능: 실제 스타일 → 가변 `HashSet` (정상)
   - WMS 연결 불가: catch → `Collections.emptySet()` (불변)

2. `oldValue = emptyObject.getRemoteStyleInfos()`  
   - emptyObject의 store=null → getWMSLayer(null) 실패 → catch → `Collections.emptySet()` (불변)

3. `OwsUtils.updateCollectionProperty`:
   - `oldValue.addAll(newValue)` 호출
   - **oldValue가 `Collections.emptySet()` (불변)** → `UnsupportedOperationException`
   - 또는 newValue도 `Collections.emptySet()`이면 → `ModificationProxyCloner`가 `Collections$EmptySet` 내부 접근 → `IllegalAccessException`

### 수정 방법 (1줄)

```java
// 변경 전
return Collections.emptySet();

// 변경 후  
return new HashSet<>();
```

또는 더 명확하게:
```java
@Override
public Set<StyleInfo> getRemoteStyleInfos() {
    try {
        return getWMSLayer(null).getStyles().stream()
                .map(WMSLayerInfoImpl::getStyleInfo)
                .collect(Collectors.toSet());
    } catch (Exception e) {
        return new HashSet<>();  // 가변 Set으로 변경
    }
}
```

---

## 4. 왜 6년간 미발견됐나

### 단위 테스트 분석
`src/restconfig/src/test/java/org/geoserver/rest/catalog/WMSLayerTest.java`의 `testPut()`:

- **Mock WMS 사용**: `MockHttpClient` + `caps130.xml`
- **caps130.xml의 `topp:states` 레이어**: `<Style>` 태그 없음 (스타일 0개)
- 결과: `newValue = getRemoteStyleInfos()` → `Collections.emptySet()` (스타일 없으므로 catch 미진입, 빈 Set)
- `oldValue.addAll(emptySet)` → no-op (추가할 게 없으므로 실패 안 함)
- ✅ 테스트 통과 → **버그 미검출**

실제 WMS 서버는 항상 스타일이 1개 이상 있으므로 `addAll(non-empty)` → 즉시 실패.

### 버그 도입 시점
- **커밋**: `60776167` (2019-10-07)
- **PR**: #3729 - [GEOS-9312] Cascaded WMS - Options to select and force Image Format and Styles
- **도입 GeoServer 버전**: 2.16.x
- **현재까지**: main 브랜치 포함 2026-03-18 기준 미수정

---

## 5. PR 작성 방법

### 5-1. 사전 준비

```bash
# GeoServer 포크
# https://github.com/geoserver/geoserver → Fork

git clone https://github.com/[YOUR_USERNAME]/geoserver.git
cd geoserver
git remote add upstream https://github.com/geoserver/geoserver.git
git fetch upstream
git checkout -b fix/GEOS-XXXXX-wms-layer-put-500-immutable-set upstream/main
```

### 5-2. 코드 수정

파일: `src/main/src/main/java/org/geoserver/catalog/impl/WMSLayerInfoImpl.java`

```java
// 수정 전 (약 L180 근처, catch 블록)
    } catch (Exception e) {
        return Collections.emptySet();
    }

// 수정 후
    } catch (Exception e) {
        return new HashSet<>();
    }
```

`import java.util.HashSet;` 가 이미 있는지 확인. 없으면 추가.

### 5-3. 테스트 수정

`src/restconfig/src/test/java/org/geoserver/rest/catalog/WMSLayerTest.java`

`testPut()` 테스트가 실제 환경을 반영하도록 `caps130.xml`에 `<Style>` 태그 추가하거나,
별도 `testPutWithStyles()` 추가:

```xml
<!-- src/restconfig/src/test/resources/caps130.xml 의 topp:states 레이어에 추가 -->
<Style>
  <Name>polygon</Name>
  <Title>A boring default style</Title>
</Style>
```

이렇게 하면 기존 `testPut()`이 수정 후에도 통과하는지 확인됨.

### 5-4. JIRA 이슈 먼저 생성

URL: https://osgeo-org.atlassian.net/projects/GEOS/issues

제목: `WMS Layer REST PUT always returns 500 due to Collections.emptySet() in getRemoteStyleInfos()`

설명 내용:
```
WMS Cascading Layer PUT /rest/workspaces/{ws}/wmsstores/{store}/wmslayers/{layer} always returns HTTP 500.

Root cause: WMSLayerInfoImpl.getRemoteStyleInfos() returns Collections.emptySet() (immutable)
in the catch block. OwsUtils.copy() then calls oldValue.addAll(newValue) where
oldValue is this immutable Set, causing UnsupportedOperationException.

Fix: Change Collections.emptySet() to new HashSet<>() in the catch block.

Affects: 2.16.x through current (introduced in commit 60776167, PR #3729, GEOS-9312)
Note: Unit test testPut() passes because mock WMS caps130.xml has no <Style> elements
for the topp:states layer, making the addAll() call a no-op.
```

### 5-5. PR 제목/본문

```
PR 제목: [GEOS-XXXXX] Fix WMS Layer PUT 500 - use HashSet instead of Collections.emptySet()

PR 본문:
## Issue
Fixes GEOS-XXXXX

## Problem
WMSLayerInfoImpl.getRemoteStyleInfos() returns Collections.emptySet() (immutable) in 
the catch block. When OwsUtils.copy() processes a PUT request, it calls:
  oldValue.addAll(newValue)  -- where oldValue is this immutable Set
This throws UnsupportedOperationException → HTTP 500.

## Root Cause
Introduced in commit 60776167 (PR #3729, GEOS-9312, Oct 2019).
The unit test testPut() uses a mock WMS (caps130.xml) with no <Style> elements,
making addAll() a no-op (no-op on empty set is fine), so the bug was never detected.

## Fix
Return new HashSet<>() instead of Collections.emptySet() in the catch block.

## Testing
- Modified caps130.xml to include a <Style> element in the topp:states layer
- Existing testPut() now exercises the real code path and passes
- Added testPutWithStyles() to explicitly verify PUT works when remote styles exist
```

---

## 6. 관련 파일 & 링크

| 항목 | 경로/URL |
|------|---------|
| 버그 파일 | `src/main/src/main/java/org/geoserver/catalog/impl/WMSLayerInfoImpl.java` |
| 컨트롤러 | `src/restconfig/src/main/java/org/geoserver/rest/catalog/WMSLayerController.java` |
| 단위테스트 | `src/restconfig/src/test/java/org/geoserver/rest/catalog/WMSLayerTest.java` |
| Mock WMS caps | `src/restconfig/src/test/resources/caps130.xml` |
| OwsUtils | `src/main/src/main/java/org/geoserver/ows/util/OwsUtils.java` (L303, L378) |
| 버그 도입 커밋 | https://github.com/geoserver/geoserver/commit/60776167a8d0e53ac68201f491e82c056c2993e1 |
| GeoServer JIRA | https://osgeo-org.atlassian.net/projects/GEOS/issues |
| GeoServer Contributing | https://docs.geoserver.org/latest/en/developer/policies/contrib.html |
