# Maven 명령어 가이드

> 프로젝트: `geoserver-client`  
> Java 8, Maven 3.6+, JUnit 5, JaCoCo

---

## 사전 조건

| 항목 | 값 |
|------|-----|
| GeoServer URL | `http://localhost:8100/geoserver` |
| 인증 | `admin / geoserver` |
| 설정 파일 | `src/test/resources/test.properties` |

통합 테스트는 **실행 중인 GeoServer 인스턴스**가 필요합니다.

```bash
# Docker로 GeoServer 실행
docker-compose up -d
```

---

## 기본 빌드 명령어

### 컴파일

```bash
# 소스 코드만 컴파일
mvn compile

# 테스트 코드 포함 컴파일
mvn test-compile
```

### 전체 빌드 (테스트 제외)

```bash
mvn clean package -DskipTests
```

### 전체 빌드 (테스트 포함)

```bash
mvn clean package
```

---

## 테스트 명령어

### 전체 통합 테스트 실행

```bash
mvn clean test
```

**예상 결과:** `Tests run: 552, Failures: 0, Errors: 0, Skipped: 1`

### 특정 테스트 클래스만 실행

```bash
# WorkspaceManager 테스트만
mvn test -Dtest=WorkspaceManagerIntegrationTest

# DataStore + FeatureType 테스트
mvn test -Dtest=DataStoreManagerIntegrationTest,FeatureTypeManagerIntegrationTest

# 패턴으로 매칭 (모든 Security  테스트)
mvn test -Dtest="*SecurityManager*"
```

### 특정 테스트 메서드만 실행

```bash
mvn test -Dtest=WorkspaceManagerIntegrationTest#testListWorkspaces
```

### 테스트 결과 리포트 생성

```bash
# surefire-report-plugin으로 HTML 리포트 생성
mvn surefire-report:report-only

# 리포트: target/site/surefire-report.html
```

---

## 커버리지 명령어

### JaCoCo 커버리지 리포트 생성

```bash
# 테스트 실행 + 커버리지 리포트 생성 (javadoc 스킵 권장)
mvn clean test jacoco:report

# 또는 mvn verify (javadoc 오류 시 -Dmaven.javadoc.skip=true 추가)
mvn clean verify -Dmaven.javadoc.skip=true

# 리포트 위치: target/site/jacoco/index.html
```

### 커버리지 조회 (PowerShell)

```powershell
$csv = Import-Csv "target\site\jacoco\jacoco.csv"
$totalMissed = ($csv | Measure-Object -Property INSTRUCTION_MISSED -Sum).Sum
$totalCovered = ($csv | Measure-Object -Property INSTRUCTION_COVERED -Sum).Sum
$pct = [math]::Round(($totalCovered / ($totalMissed + $totalCovered)) * 100, 1)
Write-Host "Instruction Coverage: $pct%"
```

**현재 커버리지 (2026-04-03 기준):**

| 항목 | 커버된 수 | 전체 | 비율 |
|------|-----------|------|------|
| Instructions | 20,344 | 27,297 | **74.5%** |
| Lines | 4,372 | 5,710 | **76.6%** |
| Branches | 714 | 1,331 | **53.6%** |
| Methods | 1,664 | 2,277 | **73.1%** |

---

## QA / 검증 명령어

### 전체 QA 파이프라인 (테스트 + 커버리지)

```bash
# 가장 권장하는 QA 실행 방법
mvn clean test jacoco:report
```

### Javadoc 검증

```bash
# Javadoc 오류 확인 (빌드는 실패해도 오류 목록 확인 가능)
mvn javadoc:javadoc

# 오류 상세 확인
mvn javadoc:javadoc -e
```

> **알려진 이슈**: Javadoc 주석 내 `<FQCN>` 태그가 HTML5 strict 모드에서 unknown tag 오류 발생.  
> 기능 동작에는 영향 없음. `mvn test`는 정상 동작.

### 의존성 확인

```bash
# 의존성 트리 출력
mvn dependency:tree

# Classpath 출력
mvn dependency:build-classpath

# 의존성 업데이트 가능 여부 확인
mvn versions:display-dependency-updates
```

---

## 아티팩트 생성 명령어

### JAR 빌드 (일반 + Shaded)

```bash
mvn clean package -DskipTests

# 생성 파일:
# target/geoserver-client-1.0.0-SNAPSHOT.jar         (일반 JAR)
# target/geoserver-client-1.0.0-SNAPSHOT-all.jar     (모든 의존성 포함 Shaded JAR)
```

### Sources JAR 생성

```bash
mvn source:jar-no-fork
# target/geoserver-client-1.0.0-SNAPSHOT-sources.jar
```

### Javadoc JAR 생성

```bash
mvn javadoc:jar
# target/geoserver-client-1.0.0-SNAPSHOT-javadoc.jar
```

---

## 릴리즈 명령어

### 로컬 Maven 저장소에 설치

```bash
mvn clean install -DskipTests
```

### SNAPSHOT 배포

```bash
mvn clean deploy -DskipTests
# distributionManagement 설정 필요 (pom.xml)
```

### 정식 릴리즈 (maven-release-plugin)

```bash
# 1. 릴리즈 준비 (SNAPSHOT → 1.0.0, 태그 생성)
mvn release:prepare

# 2. 릴리즈 수행 (빌드 + 배포)
mvn release:perform

# 3. 실패 시 원상복구
mvn release:rollback

# 4. 준비 파일 정리
mvn release:clean
```

> `release:prepare` 실행 전 `pom.xml`에 `maven-release-plugin` 및 `distributionManagement` 설정이 필요합니다.  
> 자세한 내용은 [RELEASE_PREPARATION.md](./RELEASE_PREPARATION.md) 참조.

---

## 유틸리티 명령어

### 캐시 정리 및 강제 재다운로드

```bash
mvn clean -U
```

### 프로젝트 정보 확인

```bash
# 현재 활성 프로파일
mvn help:active-profiles

# 유효한 POM (inheritance, interpolation 적용 후)
mvn help:effective-pom

# 플러그인 정보
mvn help:describe -Dplugin=jacoco -Ddetail
```

### 소스 정리

```bash
# 빌드 결과물 삭제
mvn clean
```

---

## 빠른 참조

```bash
# 개발 중 가장 자주 쓰는 명령어
mvn clean test                          # 전체 테스트
mvn test -Dtest=<TestClass>             # 단일 클래스 테스트
mvn clean test jacoco:report            # 테스트 + 커버리지
mvn clean package -DskipTests          # JAR 빌드 (테스트 skip)
mvn clean install -DskipTests           # 로컬 저장소 배포
```
