package io.github.kimbongjune.geoserverclient;

import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.api.coverage.CoverageManager;
import io.github.kimbongjune.geoserverclient.api.coverage.StructuredCoverageManager;
import io.github.kimbongjune.geoserverclient.api.layer.LayerManager;
import io.github.kimbongjune.geoserverclient.api.layergroup.LayerGroupManager;
import io.github.kimbongjune.geoserverclient.api.style.StyleManager;
import io.github.kimbongjune.geoserverclient.api.font.FontManager;
import io.github.kimbongjune.geoserverclient.api.settings.SettingsManager;
import io.github.kimbongjune.geoserverclient.api.service.ServiceManager;
import io.github.kimbongjune.geoserverclient.api.logging.LoggingManager;
import io.github.kimbongjune.geoserverclient.api.reset.ResetManager;
import io.github.kimbongjune.geoserverclient.api.about.AboutManager;
import io.github.kimbongjune.geoserverclient.api.resource.ResourceManager;
import io.github.kimbongjune.geoserverclient.api.template.TemplateManager;
import io.github.kimbongjune.geoserverclient.api.security.SecurityManager;
import io.github.kimbongjune.geoserverclient.api.security.RoleManager;
import io.github.kimbongjune.geoserverclient.api.security.UserGroupManager;
import io.github.kimbongjune.geoserverclient.api.security.AuthFilterManager;
import io.github.kimbongjune.geoserverclient.api.security.AuthProviderManager;
import io.github.kimbongjune.geoserverclient.api.security.FilterChainManager;
import io.github.kimbongjune.geoserverclient.api.security.UserGroupServiceManager;
import io.github.kimbongjune.geoserverclient.api.gwc.GwcLayerManager;
import io.github.kimbongjune.geoserverclient.api.gwc.GwcBlobStoreManager;
import io.github.kimbongjune.geoserverclient.api.gwc.GwcGlobalManager;
import io.github.kimbongjune.geoserverclient.api.gwc.GwcGridSetManager;
import io.github.kimbongjune.geoserverclient.api.gwc.GwcDiskQuotaManager;
import io.github.kimbongjune.geoserverclient.api.gwc.GwcSeedManager;
import io.github.kimbongjune.geoserverclient.api.gwc.GwcMassTruncateManager;
import io.github.kimbongjune.geoserverclient.api.gwc.GwcReloadManager;
import io.github.kimbongjune.geoserverclient.api.gwc.GwcFilterUpdateManager;
import io.github.kimbongjune.geoserverclient.api.gwc.GwcBoundsManager;
import io.github.kimbongjune.geoserverclient.api.coveragestore.CoverageStoreManager;
import io.github.kimbongjune.geoserverclient.api.urlcheck.UrlCheckManager;
import io.github.kimbongjune.geoserverclient.api.monitoring.MonitoringManager;
import io.github.kimbongjune.geoserverclient.api.importer.ImporterManager;
import io.github.kimbongjune.geoserverclient.api.transform.TransformManager;
import io.github.kimbongjune.geoserverclient.api.output.OutputManager;
import io.github.kimbongjune.geoserverclient.api.wms.WmsLayerManager;
import io.github.kimbongjune.geoserverclient.api.wms.WmsStoreManager;
import io.github.kimbongjune.geoserverclient.api.wmts.WmtsLayerManager;
import io.github.kimbongjune.geoserverclient.api.wmts.WmtsStoreManager;
import io.github.kimbongjune.geoserverclient.api.datastore.DataStoreManager;
import io.github.kimbongjune.geoserverclient.api.featuretype.FeatureTypeManager;
import io.github.kimbongjune.geoserverclient.api.namespace.NamespaceManager;
import io.github.kimbongjune.geoserverclient.api.workspace.WorkspaceManager;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.http.ApacheHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Main entry point for the GeoServer REST API client library.
 *
 * <p>Provides access to every GeoServer REST API group through typed manager accessors.
 * Construct a single instance per GeoServer endpoint using {@link Builder} and reuse it
 * across the lifetime of the application — each manager is stateless and thread-safe.
 *
 * <p>Implements {@link Closeable}: call {@link #close()} (or use try-with-resources) when
 * the application shuts down to release underlying HTTP connections.
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * // 1. Build the client once per JVM lifecycle
 * try (GeoServerClient client = GeoServerClient.builder()
 *         .url("http://localhost:8080/geoserver")
 *         .credentials("admin", "geoserver")
 *         .defaultFormat(DataFormat.JSON)
 *         .connectTimeout(30_000)
 *         .responseTimeout(60_000)
 *         .maxConnections(20)
 *         .build()) {
 *
 *     // 2. Workspace / namespace operations
 *     client.workspaces().list();
 *     client.namespaces().get("topp");
 *
 *     // 3. Data store and feature type operations
 *     client.datastores().list("topp");
 *     client.featureTypes().list("topp", "taz_shapes");
 *
 *     // 4. Layer and style operations
 *     client.layers().get("topp:tasmania_roads");
 *     StyleContent sld = SldBuilder.create("topp:tasmania_roads")
 *         .styleName("roads")
 *         .rule("all").line("#CC0000", 2).build();
 *     client.styles().create("roads", sld);
 *
 *     // 5. Coverage (raster) operations
 *     client.coverageStores().list("nurc");
 *     client.coverages().list("nurc", "mosaic");
 *
 *     // 6. GWC tile-cache operations
 *     client.gwcLayers().list();
 *     client.gwcSeed().seed("topp:tasmania_roads", seedRequest);
 *
 *     // 7. Security management
 *     client.roles().list();
 *     client.userGroups().listUsers("ADMIN");
 *
 *     // 8. Server administration
 *     client.about().version();
 *     client.logging().get();
 *     client.reset().reset();
 *
 * } catch (AuthenticationException e) {
 *     // 401 / 403 from GeoServer
 * } catch (ResourceNotFoundException e) {
 *     // 404 — resource does not exist
 * } catch (GeoServerResponseException e) {
 *     // other non-2xx response
 * } catch (IOException e) {
 *     // close() failure
 * }
 * }</pre>
 *
 * <h2>Thread safety</h2>
 * <p>A single {@code GeoServerClient} (and every manager it exposes) is safe to share and call
 * concurrently from multiple threads. Managers hold only immutable, constructor-injected
 * references and keep no per-call mutable state. The underlying
 * {@link io.github.kimbongjune.geoserverclient.http.ApacheHttpClient} wraps a pooled,
 * thread-safe Apache HttpClient 5 connection manager; use {@link Builder#maxConnections(int)}
 * to size the pool for concurrent use.
 *
 * <h2>Internals</h2>
 * <p>Every manager is constructed once at build time and stored in an internal
 * {@code Class → instance} registry. Each public accessor (e.g. {@link #workspaces()}) is a
 * one-line type-safe lookup into that registry via {@link #manager(Class)}. Adding a new API
 * group requires only one {@code register} call in the constructor and one accessor method.
 *
 * @see Builder
 * @see AbstractManager
 * @since 1.0.0
 */
public class GeoServerClient implements Closeable {

    private final GeoServerHttpClient httpClient;
    private final SerializerFactory serializerFactory;
    private final DataFormat defaultFormat;
    private final Map<Class<? extends AbstractManager>, AbstractManager> managers;

    /**
     * Private constructor — use {@link Builder} or {@link #create(GeoServerHttpClient, DataFormat)}.
     *
     * @param httpClient    the configured HTTP client
     * @param defaultFormat the default serialization format for API calls
     */
    private GeoServerClient(GeoServerHttpClient httpClient, DataFormat defaultFormat) {
        this.httpClient = httpClient;
        this.serializerFactory = new SerializerFactory();
        this.defaultFormat = defaultFormat;

        Map<Class<? extends AbstractManager>, AbstractManager> m = new HashMap<>();
        register(m, new WorkspaceManager(httpClient, serializerFactory, defaultFormat));
        register(m, new NamespaceManager(httpClient, serializerFactory, defaultFormat));
        register(m, new DataStoreManager(httpClient, serializerFactory, defaultFormat));
        register(m, new FeatureTypeManager(httpClient, serializerFactory, defaultFormat));
        register(m, new CoverageStoreManager(httpClient, serializerFactory, defaultFormat));
        register(m, new CoverageManager(httpClient, serializerFactory, defaultFormat));
        register(m, new WmsStoreManager(httpClient, serializerFactory, defaultFormat));
        register(m, new WmsLayerManager(httpClient, serializerFactory, defaultFormat));
        register(m, new WmtsStoreManager(httpClient, serializerFactory, defaultFormat));
        register(m, new WmtsLayerManager(httpClient, serializerFactory, defaultFormat));
        register(m, new StructuredCoverageManager(httpClient, serializerFactory, defaultFormat));
        register(m, new LayerManager(httpClient, serializerFactory, defaultFormat));
        register(m, new LayerGroupManager(httpClient, serializerFactory, defaultFormat));
        register(m, new StyleManager(httpClient, serializerFactory, defaultFormat));
        register(m, new FontManager(httpClient, serializerFactory, defaultFormat));
        register(m, new SettingsManager(httpClient, serializerFactory, defaultFormat));
        register(m, new ServiceManager(httpClient, serializerFactory, defaultFormat));
        register(m, new LoggingManager(httpClient, serializerFactory, defaultFormat));
        register(m, new ResetManager(httpClient, serializerFactory, defaultFormat));
        register(m, new AboutManager(httpClient, serializerFactory, defaultFormat));
        register(m, new ResourceManager(httpClient, serializerFactory, defaultFormat));
        register(m, new TemplateManager(httpClient, serializerFactory, defaultFormat));
        register(m, new SecurityManager(httpClient, serializerFactory, defaultFormat));
        register(m, new RoleManager(httpClient, serializerFactory, defaultFormat));
        register(m, new UserGroupManager(httpClient, serializerFactory, defaultFormat));
        register(m, new AuthFilterManager(httpClient, serializerFactory, defaultFormat));
        register(m, new AuthProviderManager(httpClient, serializerFactory, defaultFormat));
        register(m, new FilterChainManager(httpClient, serializerFactory, defaultFormat));
        register(m, new UserGroupServiceManager(httpClient, serializerFactory, defaultFormat));
        register(m, new GwcLayerManager(httpClient, serializerFactory, defaultFormat));
        register(m, new GwcBlobStoreManager(httpClient, serializerFactory, defaultFormat));
        register(m, new GwcGlobalManager(httpClient, serializerFactory, defaultFormat));
        register(m, new GwcGridSetManager(httpClient, serializerFactory, defaultFormat));
        register(m, new GwcDiskQuotaManager(httpClient, serializerFactory, defaultFormat));
        register(m, new GwcSeedManager(httpClient, serializerFactory, defaultFormat));
        register(m, new GwcMassTruncateManager(httpClient, serializerFactory, defaultFormat));
        register(m, new GwcReloadManager(httpClient, serializerFactory, defaultFormat));
        register(m, new GwcFilterUpdateManager(httpClient, serializerFactory, defaultFormat));
        register(m, new GwcBoundsManager(httpClient, serializerFactory, defaultFormat));
        register(m, new UrlCheckManager(httpClient, serializerFactory, defaultFormat));
        register(m, new MonitoringManager(httpClient, serializerFactory, defaultFormat));
        register(m, new ImporterManager(httpClient, serializerFactory, defaultFormat));
        register(m, new TransformManager(httpClient, serializerFactory, defaultFormat));
        register(m, new OutputManager(httpClient, serializerFactory, defaultFormat));
        this.managers = Collections.unmodifiableMap(m);
    }

    /**
     * Registers a manager instance in the class-keyed registry.
     *
     * @param map     the mutable registry being built during construction
     * @param manager the manager instance to register under its own concrete class
     */
    private static void register(Map<Class<? extends AbstractManager>, AbstractManager> map,
                                 AbstractManager manager) {
        map.put(manager.getClass(), manager);
    }

    /**
     * Type-safe lookup into the manager registry.
     *
     * <p>Every public accessor (e.g. {@link #workspaces()}) delegates to this single method,
     * so adding a new API group requires only one {@code register} call in the constructor
     * and one accessor — rather than a field declaration, a constructor assignment, and an
     * accessor.
     *
     * @param <T>  the concrete manager type
     * @param type the class token of the desired manager
     * @return the registered manager instance, cast to {@code T}
     * @throws IllegalStateException if {@code type} was never registered — indicates a
     *                               programming error in this class, not a user-facing condition
     */
    private <T extends AbstractManager> T manager(Class<T> type) {
        AbstractManager instance = managers.get(type);
        if (instance == null) {
            throw new IllegalStateException("Manager not registered: " + type.getName());
        }
        return type.cast(instance);
    }

    /**
     * Factory method accepting a custom {@link GeoServerHttpClient} implementation.
     *
     * <p>Intended for testing with mock HTTP clients or for integration with a custom
     * HTTP layer. For normal production use, prefer {@link #builder()}.
     *
     * @param httpClient    a fully configured (and already authenticated) HTTP client
     * @param defaultFormat the default serialization format for API calls; must be
     *                      {@link DataFormat#JSON} in this release
     * @return a new {@code GeoServerClient} backed by the supplied HTTP client
     */
    public static GeoServerClient create(GeoServerHttpClient httpClient, DataFormat defaultFormat) {
        return new GeoServerClient(httpClient, defaultFormat);
    }

    //  Manager Accessors

    /**
     * Returns the Workspace API manager.
     *
     * <p>Covers the {@code /rest/workspaces} API group: list, get, create, update, and delete
     * workspaces. Workspaces are the top-level namespace containers in GeoServer.
     *
     * @return the {@link WorkspaceManager} instance shared for the lifetime of this client
     */
    public WorkspaceManager workspaces() {
        return manager(WorkspaceManager.class);
    }

    /**
     * Returns the Namespace API manager.
     *
     * <p>Covers the {@code /rest/namespaces} API group: list, get, create, update, and delete
     * XML namespaces. Each namespace is associated with a workspace and provides the URI
     * prefix used in WFS responses.
     *
     * @return the {@link NamespaceManager} instance shared for the lifetime of this client
     */
    public NamespaceManager namespaces() {
        return manager(NamespaceManager.class);
    }

    /**
     * Returns the DataStore API manager.
     *
     * <p>Covers the {@code /rest/workspaces/{ws}/datastores} API group: list, get, create,
     * update, and delete vector data stores (PostGIS, Shapefile, etc.) within a workspace.
     *
     * @return the {@link DataStoreManager} instance shared for the lifetime of this client
     */
    public DataStoreManager datastores() {
        return manager(DataStoreManager.class);
    }

    /**
     * Returns the FeatureType API manager.
     *
     * <p>Covers the {@code /rest/workspaces/{ws}/datastores/{ds}/featuretypes} API group:
     * list, get, create, update, and delete vector feature type definitions (table or layer
     * mappings) within a data store.
     *
     * @return the {@link FeatureTypeManager} instance shared for the lifetime of this client
     */
    public FeatureTypeManager featureTypes() {
        return manager(FeatureTypeManager.class);
    }

    /**
     * Returns the CoverageStore API manager.
     *
     * <p>Covers the {@code /rest/workspaces/{ws}/coveragestores} API group: list, get, create,
     * update, and delete raster coverage stores (GeoTIFF, ImageMosaic, etc.) within a workspace.
     *
     * @return the {@link CoverageStoreManager} instance shared for the lifetime of this client
     */
    public CoverageStoreManager coverageStores() {
        return manager(CoverageStoreManager.class);
    }

    /**
     * Returns the Coverage API manager.
     *
     * <p>Covers the {@code /rest/workspaces/{ws}/coveragestores/{cs}/coverages} API group:
     * list, get, create, update, and delete raster coverages within a coverage store.
     *
     * @return the {@link CoverageManager} instance shared for the lifetime of this client
     */
    public CoverageManager coverages() {
        return manager(CoverageManager.class);
    }

    /**
     * Returns the WMS Store API manager.
     *
     * <p>Covers the {@code /rest/workspaces/{ws}/wmsstores} API group: list, get, create,
     * update, and delete cascaded WMS stores (remote WMS endpoints configured as data sources).
     *
     * @return the {@link WmsStoreManager} instance shared for the lifetime of this client
     */
    public WmsStoreManager wmsStores() {
        return manager(WmsStoreManager.class);
    }

    /**
     * Returns the WMS Layer API manager.
     *
     * <p>Covers the {@code /rest/workspaces/{ws}/wmsstores/{wms}/wmslayers} API group:
     * list, get, create, update, and delete cascaded WMS layer definitions within a WMS store.
     *
     * @return the {@link WmsLayerManager} instance shared for the lifetime of this client
     */
    public WmsLayerManager wmsLayers() {
        return manager(WmsLayerManager.class);
    }

    /**
     * Returns the WMTS Store API manager.
     *
     * <p>Covers the {@code /rest/workspaces/{ws}/wmtsstores} API group: list, get, create,
     * update, and delete cascaded WMTS stores (remote tile server endpoints).
     *
     * @return the {@link WmtsStoreManager} instance shared for the lifetime of this client
     */
    public WmtsStoreManager wmtsStores() {
        return manager(WmtsStoreManager.class);
    }

    /**
     * Returns the WMTS Layer API manager.
     *
     * <p>Covers the {@code /rest/workspaces/{ws}/wmtsstores/{wmts}/layers} API group:
     * list, get, create, update, and delete cascaded WMTS layer definitions within a WMTS store.
     *
     * @return the {@link WmtsLayerManager} instance shared for the lifetime of this client
     */
    public WmtsLayerManager wmtsLayers() {
        return manager(WmtsLayerManager.class);
    }

    /**
     * Returns the Structured Coverage API manager.
     *
     * <p>Covers the {@code /rest/workspaces/{ws}/coveragestores/{cs}/coverages/{c}/index}
     * API group for ImageMosaic granule management: list, get, add, and delete granules
     * within a structured coverage (image mosaic).
     *
     * @return the {@link StructuredCoverageManager} instance shared for the lifetime of this client
     */
    public StructuredCoverageManager structuredCoverages() {
        return manager(StructuredCoverageManager.class);
    }

    /**
     * Returns the Layer API manager.
     *
     * <p>Covers the {@code /rest/layers} API group: list, get, update, and delete published
     * layers. A layer is the published view of a resource (feature type or coverage) with
     * associated default and extra styles.
     *
     * @return the {@link LayerManager} instance shared for the lifetime of this client
     */
    public LayerManager layers() {
        return manager(LayerManager.class);
    }

    /**
     * Returns the Layer Group API manager.
     *
     * <p>Covers the {@code /rest/layergroups} API group: list, get, create, update, and delete
     * layer groups. Layer groups aggregate multiple layers into a single publishable unit
     * (WMS GetMap layer name).
     *
     * @return the {@link LayerGroupManager} instance shared for the lifetime of this client
     */
    public LayerGroupManager layerGroups() {
        return manager(LayerGroupManager.class);
    }

    /**
     * Returns the Style API manager.
     *
     * <p>Covers the {@code /rest/styles} and {@code /rest/workspaces/{ws}/styles} API groups:
     * list, get, create (from SLD/CSS body), update, and delete named styles. Use
     * {@link io.github.kimbongjune.geoserverclient.dto.style.SldBuilder} to generate SLD 1.0 bodies.
     *
     * @return the {@link StyleManager} instance shared for the lifetime of this client
     */
    public StyleManager styles() {
        return manager(StyleManager.class);
    }

    /**
     * Returns the Font API manager.
     *
     * <p>Covers the {@code /rest/fonts} API group: list fonts available on the GeoServer
     * instance for use in SLD text symbolizers.
     *
     * @return the {@link FontManager} instance shared for the lifetime of this client
     */
    public FontManager fonts() {
        return manager(FontManager.class);
    }

    /**
     * Returns the Settings API manager.
     *
     * <p>Covers the {@code /rest/settings} API group: get and update global GeoServer settings
     * (contact info, character set, verbose mode, etc.) as well as per-workspace local settings.
     *
     * @return the {@link SettingsManager} instance shared for the lifetime of this client
     */
    public SettingsManager settings() {
        return manager(SettingsManager.class);
    }

    /**
     * Returns the OWS Services API manager.
     *
     * <p>Covers the {@code /rest/services/wms}, {@code /rest/services/wfs},
     * {@code /rest/services/wcs}, and {@code /rest/services/wmts} API groups:
     * get and update WMS, WFS, WCS, and WMTS service configuration (capabilities metadata,
     * SRS list, etc.) globally and per workspace.
     *
     * @return the {@link ServiceManager} instance shared for the lifetime of this client
     */
    public ServiceManager services() {
        return manager(ServiceManager.class);
    }

    /**
     * Returns the Logging API manager.
     *
     * <p>Covers the {@code /rest/logging} API group: get and update the GeoServer logging
     * configuration (log4j profile name and log location).
     *
     * @return the {@link LoggingManager} instance shared for the lifetime of this client
     */
    public LoggingManager logging() {
        return manager(LoggingManager.class);
    }

    /**
     * Returns the Reset / Reload API manager.
     *
     * <p>Covers the {@code /rest/reset} and {@code /rest/reload} endpoints: clears the
     * internal GeoServer caches (image, connection pools, etc.) or reloads the catalog
     * from disk without a full server restart.
     *
     * @return the {@link ResetManager} instance shared for the lifetime of this client
     */
    public ResetManager reset() {
        return manager(ResetManager.class);
    }

    /**
     * Returns the About API manager.
     *
     * <p>Covers the {@code /rest/about/version} and {@code /rest/about/manifest} endpoints:
     * retrieve the GeoServer version string and the full JAR manifest for diagnostics.
     *
     * @return the {@link AboutManager} instance shared for the lifetime of this client
     */
    public AboutManager about() {
        return manager(AboutManager.class);
    }

    /**
     * Returns the Resource API manager.
     *
     * <p>Covers the {@code /rest/resource} API group: browse, read, write, and delete files
     * in the GeoServer data directory as a virtual file system. Useful for uploading
     * configuration snippets or custom JNDI property files.
     *
     * @return the {@link ResourceManager} instance shared for the lifetime of this client
     */
    public ResourceManager resources() {
        return manager(ResourceManager.class);
    }

    /**
     * Returns the Template API manager.
     *
     * <p>Covers the {@code /rest/templates} API group: list, get, upload, and delete
     * Freemarker templates used for WMS GetFeatureInfo and WFS HTML output customization.
     *
     * @return the {@link TemplateManager} instance shared for the lifetime of this client
     */
    public TemplateManager templates() {
        return manager(TemplateManager.class);
    }

    /**
     * Returns the Security (master password / ACL rules) API manager.
     *
     * <p>Covers the {@code /rest/security/masterpw} and {@code /rest/security/acl} API groups:
     * change the master password and manage service / layer / REST access control rules.
     *
     * @return the {@link SecurityManager} instance shared for the lifetime of this client
     */
    public SecurityManager security() {
        return manager(SecurityManager.class);
    }

    /**
     * Returns the Security Roles API manager.
     *
     * <p>Covers the {@code /rest/security/roles} API group: list, create, and delete roles,
     * and manage role membership for users and groups.
     *
     * @return the {@link RoleManager} instance shared for the lifetime of this client
     */
    public RoleManager roles() {
        return manager(RoleManager.class);
    }

    /**
     * Returns the Security User/Group API manager.
     *
     * <p>Covers the {@code /rest/security/usergroup} API group: list, create, update, and
     * delete users and groups within user/group services.
     *
     * @return the {@link UserGroupManager} instance shared for the lifetime of this client
     */
    public UserGroupManager userGroups() {
        return manager(UserGroupManager.class);
    }

    /**
     * Returns the Authentication Filter API manager.
     *
     * <p>Covers the {@code /rest/security/authFilters} API group: list, get, create, update,
     * and delete GeoServer authentication filters (Basic, Form, Certificate, etc.).
     *
     * @return the {@link AuthFilterManager} instance shared for the lifetime of this client
     */
    public AuthFilterManager authFilters() {
        return manager(AuthFilterManager.class);
    }

    /**
     * Returns the Authentication Provider API manager.
     *
     * <p>Covers the {@code /rest/security/authProviders} API group: list, get, create, update,
     * and delete authentication providers (UsernamePassword, LDAP, JDBC, etc.).
     *
     * @return the {@link AuthProviderManager} instance shared for the lifetime of this client
     */
    public AuthProviderManager authProviders() {
        return manager(AuthProviderManager.class);
    }

    /**
     * Returns the Security Filter Chain API manager.
     *
     * <p>Covers the {@code /rest/security/filterChain} API group: get and update the ordered
     * chain of security filters applied to incoming HTTP requests. Modifying the chain
     * controls which authentication mechanisms are active for which URL patterns.
     *
     * @return the {@link FilterChainManager} instance shared for the lifetime of this client
     */
    public FilterChainManager filterChains() {
        return manager(FilterChainManager.class);
    }

    /**
     * Returns the User/Group Service API manager.
     *
     * <p>Covers the {@code /rest/security/userGroupServices} API group: list, get, create,
     * update, and delete the user/group service configurations that back role lookup.
     *
     * @return the {@link UserGroupServiceManager} instance shared for the lifetime of this client
     */
    public UserGroupServiceManager userGroupServices() {
        return manager(UserGroupServiceManager.class);
    }

    /**
     * Returns the GeoWebCache (GWC) Tile Layer API manager.
     *
     * <p>Covers the {@code /gwc/rest/layers} API group: list, get, create, update, and delete
     * GWC tile layer configurations (grid sets, formats, cache parameters).
     *
     * @return the {@link GwcLayerManager} instance shared for the lifetime of this client
     */
    public GwcLayerManager gwcLayers() {
        return manager(GwcLayerManager.class);
    }

    /**
     * Returns the GWC BlobStore API manager.
     *
     * <p>Covers the {@code /gwc/rest/blobstores} API group: list, get, create, update, and
     * delete tile storage back ends (file system, S3, Azure, etc.).
     *
     * @return the {@link GwcBlobStoreManager} instance shared for the lifetime of this client
     */
    public GwcBlobStoreManager gwcBlobStores() {
        return manager(GwcBlobStoreManager.class);
    }

    /**
     * Returns the GWC Global Configuration API manager.
     *
     * <p>Covers the {@code /gwc/rest/global} endpoint: get and update the global GeoWebCache
     * configuration (cache base directory, lock provider, service enablement flags).
     *
     * @return the {@link GwcGlobalManager} instance shared for the lifetime of this client
     */
    public GwcGlobalManager gwcGlobal() {
        return manager(GwcGlobalManager.class);
    }

    /**
     * Returns the GWC GridSet API manager.
     *
     * <p>Covers the {@code /gwc/rest/gridsets} API group: list, get, create, update, and delete
     * tile grid set definitions (tile sizes, SRS, zoom levels, scale denominators).
     *
     * @return the {@link GwcGridSetManager} instance shared for the lifetime of this client
     */
    public GwcGridSetManager gwcGridSets() {
        return manager(GwcGridSetManager.class);
    }

    /**
     * Returns the GWC Disk Quota API manager.
     *
     * <p>Covers the {@code /gwc/rest/diskquota} endpoint: get and update the global disk quota
     * policy (maximum cache size, eviction policy — LFU or LRU).
     *
     * @return the {@link GwcDiskQuotaManager} instance shared for the lifetime of this client
     */
    public GwcDiskQuotaManager gwcDiskQuota() {
        return manager(GwcDiskQuotaManager.class);
    }

    /**
     * Returns the GWC Seed API manager.
     *
     * <p>Covers the {@code /gwc/rest/seed/{layer}} API group: start, query, and terminate
     * cache seeding and truncation tasks for a specific tile layer.
     *
     * @return the {@link GwcSeedManager} instance shared for the lifetime of this client
     */
    public GwcSeedManager gwcSeed() {
        return manager(GwcSeedManager.class);
    }

    /**
     * Returns the GWC Mass Truncate API manager.
     *
     * <p>Covers the {@code /gwc/rest/masstruncate} endpoint: submit a mass-truncate request
     * to clear tiles for multiple layers, grid sets, or the entire cache in one operation.
     *
     * @return the {@link GwcMassTruncateManager} instance shared for the lifetime of this client
     */
    public GwcMassTruncateManager gwcMassTruncate() {
        return manager(GwcMassTruncateManager.class);
    }

    /**
     * Returns the GWC Reload API manager.
     *
     * <p>Covers the {@code /gwc/rest/reload} endpoint: trigger a full reload of the GeoWebCache
     * configuration from disk without restarting GeoServer.
     *
     * @return the {@link GwcReloadManager} instance shared for the lifetime of this client
     */
    public GwcReloadManager gwcReload() {
        return manager(GwcReloadManager.class);
    }

    /**
     * Returns the GWC Filter Update API manager.
     *
     * <p>Covers the {@code /gwc/rest/filterupdate} endpoint: notify GeoWebCache that an
     * OGC filter used by a tile layer has been updated, triggering selective cache invalidation.
     *
     * @return the {@link GwcFilterUpdateManager} instance shared for the lifetime of this client
     */
    public GwcFilterUpdateManager gwcFilterUpdates() {
        return manager(GwcFilterUpdateManager.class);
    }

    /**
     * Returns the GWC Bounds API manager.
     *
     * <p>Covers the {@code /gwc/rest/bounds} endpoint: compute the bounding box for a tile
     * layer in a given grid set, useful for seeding requests and extent calculations.
     *
     * @return the {@link GwcBoundsManager} instance shared for the lifetime of this client
     */
    public GwcBoundsManager gwcBounds() {
        return manager(GwcBoundsManager.class);
    }

    /**
     * Returns the URL Checks API manager.
     *
     * <p>Covers the {@code /rest/urlchecks} API group: list, get, create, update, and delete
     * URL validation rules that GeoServer uses when resolving remote OWS and data source URLs
     * (security allowlist for SSRF prevention).
     *
     * @return the {@link UrlCheckManager} instance shared for the lifetime of this client
     */
    public UrlCheckManager urlChecks() {
        return manager(UrlCheckManager.class);
    }

    /**
     * Returns the Monitoring (request history) API manager.
     *
     * <p>Covers the {@code /rest/monitor/requests} API group (GeoServer Monitor plugin):
     * query the request audit log — recent OGC service calls, execution times, and error counts.
     * Only available when the GeoServer Monitor extension is installed.
     *
     * @return the {@link MonitoringManager} instance shared for the lifetime of this client
     */
    public MonitoringManager monitoring() {
        return manager(MonitoringManager.class);
    }

    /**
     * Returns the Importer API manager.
     *
     * <p>Covers the {@code /rest/imports} API group (GeoServer Importer extension): create and
     * manage import sessions that bulk-load data files (Shapefiles, GeoTIFFs, etc.) into
     * GeoServer stores and publish them as layers in one workflow.
     *
     * @return the {@link ImporterManager} instance shared for the lifetime of this client
     */
    public ImporterManager importer() {
        return manager(ImporterManager.class);
    }

    /**
     * Returns the XSLT Transform API manager.
     *
     * <p>Covers the {@code /rest/services/wfs/transforms} API group (GeoServer XSLT WFS Output
     * extension): list, get, create, update, and delete XSLT transformation definitions for
     * custom WFS output formats. Only available when the XSLT WFS Output plugin is installed.
     *
     * @return the {@link TransformManager} instance shared for the lifetime of this client
     */
    public TransformManager transforms() {
        return manager(TransformManager.class);
    }

    /**
     * Returns the Output Formats convenience manager.
     *
     * <p>A thin convenience wrapper that aggregates {@code /rest/fonts} and
     * {@code /rest/templates} into a single accessor for output-rendering concerns
     * (available fonts and Freemarker response templates).
     *
     * @return the {@link OutputManager} instance shared for the lifetime of this client
     */
    public OutputManager output() {
        return manager(OutputManager.class);
    }

    //  Infrastructure Accessors

    /**
     * Returns the underlying HTTP client.
     *
     * <p>Exposed for advanced use cases (custom request signing, metrics hooks, etc.).
     * Direct use is not needed for normal API calls.
     *
     * @return the {@link GeoServerHttpClient} used by all managers in this instance
     */
    public GeoServerHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Returns the serializer factory shared by all managers.
     *
     * <p>Exposed for advanced use cases (custom serialization, testing overrides, etc.).
     *
     * @return the {@link SerializerFactory} used to obtain JSON and XML serializers
     */
    public SerializerFactory getSerializerFactory() {
        return serializerFactory;
    }

    /**
     * Returns the default data format used by all managers when no format is specified per-call.
     *
     * @return the {@link DataFormat} configured at build time (always {@link DataFormat#JSON}
     *         in this release)
     */
    public DataFormat getDefaultFormat() {
        return defaultFormat;
    }

    /**
     * Releases the underlying HTTP connection pool.
     *
     * <p>After this call the client and all managers it exposes are unusable. Use
     * try-with-resources or call this in a shutdown hook to avoid connection leaks.
     *
     * @throws IOException if the HTTP client pool cannot be cleanly shut down
     */
    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    //  Builder

    /**
     * Creates a new {@link Builder} for constructing a {@code GeoServerClient}.
     *
     * <pre>{@code
     * GeoServerClient client = GeoServerClient.builder()
     *     .url("http://localhost:8080/geoserver")
     *     .credentials("admin", "geoserver")
     *     .build();
     * }</pre>
     *
     * @return a new, unconfigured {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for {@link GeoServerClient}.
     *
     * <p>Obtain an instance via {@link GeoServerClient#builder()}. All fields except
     * {@link #url(String)} and {@link #credentials(String, String)} have sensible defaults
     * and may be omitted for simple use cases.
     *
     * <pre>{@code
     * GeoServerClient client = GeoServerClient.builder()
     *     .url("http://localhost:8080/geoserver")
     *     .credentials("admin", "geoserver")
     *     .connectTimeout(30_000)
     *     .responseTimeout(90_000)
     *     .maxConnections(10)
     *     .build();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static class Builder {
        private String url;
        private String username;
        private String password;
        private DataFormat defaultFormat = DataFormat.JSON;
        private int connectTimeoutMs = 60_000;
        private int responseTimeoutMs = 120_000;
        private int maxConnections = ApacheHttpClient.DEFAULT_MAX_CONNECTIONS;

        /** Package-private — obtain via {@link GeoServerClient#builder()}. */
        private Builder() {}

        /**
         * Sets the GeoServer base URL.
         *
         * <p>Must include the protocol, host, optional port, and the GeoServer context path,
         * for example {@code "http://localhost:8080/geoserver"} or
         * {@code "https://geoserver.example.com/geoserver"}. Do not include a trailing slash
         * or the {@code /rest} segment — the managers append those automatically.
         *
         * @param url the GeoServer base URL; must not be {@code null} or blank
         * @return this builder for chaining
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * Sets the HTTP Basic authentication credentials used for every REST request.
         *
         * <p>GeoServer ships with {@code admin} / {@code geoserver} as defaults. In production
         * environments supply a dedicated service account with the minimum required roles.
         *
         * @param username the GeoServer user name; must not be {@code null}
         * @param password the GeoServer password; must not be {@code null}
         * @return this builder for chaining
         */
        public Builder credentials(String username, String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        /**
         * Sets the default data format for API calls that support a configurable format
         * (default and only supported value: {@link DataFormat#JSON}).
         * <p>
         * <b>Current limitation:</b> only {@link DataFormat#JSON} is accepted here. Internally,
         * most managers communicate with GeoServer over a fixed wire format chosen per-endpoint
         * (usually JSON; a few GWC/security writes are forced to XML to work around GeoServer
         * XStream persister bugs — see {@code CHANGELOG.md}), independent of this setting. A
         * client-wide {@code DataFormat.XML} default is not implemented: GeoServer's XML REST
         * responses collapse a single-item list to a bare element instead of a one-item array
         * (unlike its JSON responses, which are consistently arrays), so the generic
         * list-parsing logic shared by ~40 managers cannot safely assume XML today without a
         * per-manager audit. Passing {@link DataFormat#XML} here fails fast at {@link #build()}
         * rather than silently falling back to JSON.
         * <p>
         * XML is still fully usable where the API is explicitly XML-shaped regardless of this
         * setting — e.g. {@code StyleContent}/SLD bodies, or the explicit per-call
         * {@code DataFormat} overloads on individual manager methods, where you take on the
         * parsing risk directly.
         *
         * @param format the default data format (must be {@link DataFormat#JSON})
         * @return this builder
         */
        public Builder defaultFormat(DataFormat format) {
            this.defaultFormat = format;
            return this;
        }

        /**
         * Sets the TCP connection establishment timeout in milliseconds.
         *
         * <p>Default: {@code 60 000} ms (60 s). Applies to the time allowed for the TCP
         * handshake plus TLS negotiation. Connections that are not established within this
         * window are aborted with a timeout exception. Set lower for fast LANs or higher for
         * remote or slow-starting GeoServer instances.
         *
         * @param milliseconds the connection timeout; must be greater than {@code 0}
         * @return this builder for chaining
         */
        public Builder connectTimeout(int milliseconds) {
            this.connectTimeoutMs = milliseconds;
            return this;
        }

        /**
         * Sets the maximum time in milliseconds to wait for the full HTTP response.
         *
         * <p>Default: {@code 120 000} ms (120 s). Applies from the moment the request is fully
         * sent until the last byte of the response body is received. Increase for large payloads
         * (e.g. bulk importer responses) or slow GeoServer operations.
         *
         * @param milliseconds the response timeout; must be greater than {@code 0}
         * @return this builder for chaining
         */
        public Builder responseTimeout(int milliseconds) {
            this.responseTimeoutMs = milliseconds;
            return this;
        }

        /**
         * Sets the maximum number of concurrent HTTP connections in the connection pool.
         *
         * <p>Default: {@value ApacheHttpClient#DEFAULT_MAX_CONNECTIONS}. All connections in the
         * pool target the same GeoServer host. Raise this value when managers are called
         * concurrently from multiple threads against the same {@code GeoServerClient} instance
         * and HTTP connections are observed queuing or blocking under load. Setting this value
         * higher than the GeoServer thread pool size provides no benefit.
         *
         * @param maxConnections the maximum pool size; must be greater than {@code 0}
         * @return this builder for chaining
         */
        public Builder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        /**
         * Builds the GeoServerClient.
         *
         * @return configured GeoServerClient instance
         * @throws InvalidParameterException if required parameters are missing or invalid, or if
         *                                    {@code defaultFormat} is {@link DataFormat#XML}
         *                                    (not supported as a client-wide default — see
         *                                    {@link #defaultFormat(DataFormat)})
         */
        public GeoServerClient build() {
            if (url == null || url.trim().isEmpty()) {
                throw new InvalidParameterException("url", "GeoServer URL is required");
            }
            if (username == null) {
                throw new InvalidParameterException("username", "Username is required");
            }
            if (password == null) {
                throw new InvalidParameterException("password", "Password is required");
            }
            if (defaultFormat != DataFormat.JSON) {
                throw new InvalidParameterException("defaultFormat",
                        "only DataFormat.JSON is supported as a client-wide default in this release — "
                                + "most managers communicate in JSON internally regardless of this setting, "
                                + "and GeoServer's XML REST responses collapse single-item lists in a way "
                                + "the shared list-parsing logic does not yet handle safely; "
                                + "see GeoServerClient.Builder#defaultFormat javadoc");
            }
            if (connectTimeoutMs <= 0) {
                throw new InvalidParameterException("connectTimeout", "must be greater than 0");
            }
            if (responseTimeoutMs <= 0) {
                throw new InvalidParameterException("responseTimeout", "must be greater than 0");
            }
            if (maxConnections <= 0) {
                throw new InvalidParameterException("maxConnections", "must be greater than 0");
            }

            GeoServerHttpClient httpClient = new ApacheHttpClient(
                    url, username, password, connectTimeoutMs, responseTimeoutMs, maxConnections);

            return new GeoServerClient(httpClient, defaultFormat);
        }
    }
}
