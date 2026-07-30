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
 * <p>
 * Use the {@link Builder} to create an instance:
 * <pre>{@code
 * GeoServerClient client = GeoServerClient.builder()
 *     .url("http://localhost:8080/geoserver")
 *     .credentials("admin", "geoserver")
 *     .defaultFormat(DataFormat.JSON)
 *     .build();
 *
 * // Access API managers
 * List<Workspace> workspaces = client.workspaces().list();
 * }</pre>
 * <p>
 * Implements {@link Closeable} — call {@link #close()} when done to release HTTP connections.
 * <p>
 * <b>Thread safety:</b> a single {@code GeoServerClient} (and every manager it exposes) is safe
 * to share and call concurrently from multiple threads. Managers hold only immutable,
 * constructor-injected references and keep no per-call mutable state; the underlying
 * {@link io.github.kimbongjune.geoserverclient.http.ApacheHttpClient} wraps a pooled, thread-safe Apache
 * HttpClient 5 connection manager (see {@link Builder#maxConnections(int)} to size the pool for
 * concurrent use). Build one client per GeoServer endpoint and reuse it — do not construct a new
 * one per request or per thread.
 * <p>
 * <b>Internals:</b> every manager is constructed once and kept in an internal
 * {@code Class -> instance} registry; each public accessor below (e.g. {@link #workspaces()})
 * is a type-safe lookup into that registry. Adding a new API group means adding one
 * {@link #register(Map, AbstractManager)} call and one accessor method — see {@link #manager(Class)}.
 */
public class GeoServerClient implements Closeable {

    private final GeoServerHttpClient httpClient;
    private final SerializerFactory serializerFactory;
    private final DataFormat defaultFormat;
    private final Map<Class<? extends AbstractManager>, AbstractManager> managers;

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

    private static void register(Map<Class<? extends AbstractManager>, AbstractManager> map,
                                 AbstractManager manager) {
        map.put(manager.getClass(), manager);
    }

    /**
     * Type-safe lookup into the manager registry. Every public accessor (e.g.
     * {@link #workspaces()}) is a one-line call to this method — it exists so adding a 45th (or
     * 46th) API group touches exactly two places (a {@link #register(AbstractManager)} call and
     * an accessor), instead of a field declaration, a constructor assignment, and an accessor.
     *
     * @throws IllegalStateException if {@code type} was never {@link #register(AbstractManager) registered}
     *                                — a programming error inside this class, never a user-facing condition
     */
    private <T extends AbstractManager> T manager(Class<T> type) {
        AbstractManager instance = managers.get(type);
        if (instance == null) {
            throw new IllegalStateException("Manager not registered: " + type.getName());
        }
        return type.cast(instance);
    }

    /**
     * Alternative constructor accepting a custom {@link GeoServerHttpClient} implementation.
     * Useful for testing with mocks or custom HTTP implementations.
     *
     * @param httpClient    custom HTTP client
     * @param defaultFormat default data format
     * @return new GeoServerClient instance
     */
    public static GeoServerClient create(GeoServerHttpClient httpClient, DataFormat defaultFormat) {
        return new GeoServerClient(httpClient, defaultFormat);
    }

    //  Manager Accessors 

    /** Returns the Workspace API manager. */
    public WorkspaceManager workspaces() {
        return manager(WorkspaceManager.class);
    }

    /** Returns the Namespace API manager. */
    public NamespaceManager namespaces() {
        return manager(NamespaceManager.class);
    }

    /** Returns the DataStore API manager. */
    public DataStoreManager datastores() {
        return manager(DataStoreManager.class);
    }

    /** Returns the FeatureType API manager. */
    public FeatureTypeManager featureTypes() {
        return manager(FeatureTypeManager.class);
    }

    /** Returns the CoverageStore API manager. */
    public CoverageStoreManager coverageStores() {
        return manager(CoverageStoreManager.class);
    }

    /** Returns the Coverage API manager. */
    public CoverageManager coverages() {
        return manager(CoverageManager.class);
    }

    /** Returns the WmsStore API manager. */
    public WmsStoreManager wmsStores() {
        return manager(WmsStoreManager.class);
    }

    /** Returns the WmsLayer API manager. */
    public WmsLayerManager wmsLayers() {
        return manager(WmsLayerManager.class);
    }

    /** Returns the WmtsStore API manager. */
    public WmtsStoreManager wmtsStores() {
        return manager(WmtsStoreManager.class);
    }

    /** Returns the WmtsLayer API manager. */
    public WmtsLayerManager wmtsLayers() {
        return manager(WmtsLayerManager.class);
    }

    /** Returns the StructuredCoverage API manager. */
    public StructuredCoverageManager structuredCoverages() {
        return manager(StructuredCoverageManager.class);
    }

    /** Returns the Layer API manager. */
    public LayerManager layers() {
        return manager(LayerManager.class);
    }

    /** Returns the LayerGroup API manager. */
    public LayerGroupManager layerGroups() {
        return manager(LayerGroupManager.class);
    }

    /** Returns the Style API manager. */
    public StyleManager styles() {
        return manager(StyleManager.class);
    }

    /** Returns the Font API manager. */
    public FontManager fonts() {
        return manager(FontManager.class);
    }

    /** Returns the Settings API manager. */
    public SettingsManager settings() {
        return manager(SettingsManager.class);
    }

    /** Returns the OWS Services API manager. */
    public ServiceManager services() {
        return manager(ServiceManager.class);
    }

    /** Returns the Logging API manager. */
    public LoggingManager logging() {
        return manager(LoggingManager.class);
    }

    /** Returns the Reset/Reload API manager. */
    public ResetManager reset() {
        return manager(ResetManager.class);
    }

    /** Returns the About API manager. */
    public AboutManager about() {
        return manager(AboutManager.class);
    }

    /** Returns the Resource API manager. */
    public ResourceManager resources() {
        return manager(ResourceManager.class);
    }

    /** Returns the Template API manager. */
    public TemplateManager templates() {
        return manager(TemplateManager.class);
    }

    /** Returns the Security (master password / ACL) API manager. */
    public SecurityManager security() {
        return manager(SecurityManager.class);
    }

    /** Returns the Security Roles API manager. */
    public RoleManager roles() {
        return manager(RoleManager.class);
    }

    /** Returns the Security User/Group API manager. */
    public UserGroupManager userGroups() {
        return manager(UserGroupManager.class);
    }

    /** Returns the Authentication Filter API manager. */
    public AuthFilterManager authFilters() {
        return manager(AuthFilterManager.class);
    }

    /** Returns the Authentication Provider API manager. */
    public AuthProviderManager authProviders() {
        return manager(AuthProviderManager.class);
    }

    /** Returns the Security Filter Chain API manager. */
    public FilterChainManager filterChains() {
        return manager(FilterChainManager.class);
    }

    /** Returns the User/Group Service API manager. */
    public UserGroupServiceManager userGroupServices() {
        return manager(UserGroupServiceManager.class);
    }

    /** Returns the GWC Tile Layer API manager. */
    public GwcLayerManager gwcLayers() {
        return manager(GwcLayerManager.class);
    }

    /** Returns the GWC BlobStore API manager. */
    public GwcBlobStoreManager gwcBlobStores() {
        return manager(GwcBlobStoreManager.class);
    }

    /** Returns the GWC Global Configuration API manager. */
    public GwcGlobalManager gwcGlobal() {
        return manager(GwcGlobalManager.class);
    }

    /** Returns the GWC GridSet API manager. */
    public GwcGridSetManager gwcGridSets() {
        return manager(GwcGridSetManager.class);
    }

    /** Returns the GWC DiskQuota API manager. */
    public GwcDiskQuotaManager gwcDiskQuota() {
        return manager(GwcDiskQuotaManager.class);
    }

    /** Returns the GWC Seed API manager. */
    public GwcSeedManager gwcSeed() {
        return manager(GwcSeedManager.class);
    }

    /** Returns the GWC MassTruncate API manager. */
    public GwcMassTruncateManager gwcMassTruncate() {
        return manager(GwcMassTruncateManager.class);
    }

    /** Returns the GWC Reload API manager. */
    public GwcReloadManager gwcReload() {
        return manager(GwcReloadManager.class);
    }

    /** Returns the GWC Filter Update API manager. */
    public GwcFilterUpdateManager gwcFilterUpdates() {
        return manager(GwcFilterUpdateManager.class);
    }

    /** Returns the GWC Bounds API manager. */
    public GwcBoundsManager gwcBounds() {
        return manager(GwcBoundsManager.class);
    }

    /** Returns the URL Checks API manager. */
    public UrlCheckManager urlChecks() {
        return manager(UrlCheckManager.class);
    }

    /** Returns the Monitoring (request history) API manager. */
    public MonitoringManager monitoring() {
        return manager(MonitoringManager.class);
    }

    /** Returns the Importer API manager. */
    public ImporterManager importer() {
        return manager(ImporterManager.class);
    }

    /** Returns the XSLT Transform API manager (plugin-dependent; returns isAvailable()=false on GeoServer 2.28.x). */
    public TransformManager transforms() {
        return manager(TransformManager.class);
    }

    /** Returns the Output Formats convenience manager (wraps /rest/fonts and /rest/templates). */
    public OutputManager output() {
        return manager(OutputManager.class);
    }

    //  Infrastructure Accessors 

    public GeoServerHttpClient getHttpClient() {
        return httpClient;
    }

    public SerializerFactory getSerializerFactory() {
        return serializerFactory;
    }

    public DataFormat getDefaultFormat() {
        return defaultFormat;
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    //  Builder 

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private String username;
        private String password;
        private DataFormat defaultFormat = DataFormat.JSON;
        private int connectTimeoutMs = 60_000;
        private int responseTimeoutMs = 120_000;
        private int maxConnections = ApacheHttpClient.DEFAULT_MAX_CONNECTIONS;

        private Builder() {}

        /**
         * Sets the GeoServer base URL (e.g., "http://localhost:8080/geoserver").
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * Sets the authentication credentials.
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
         */
        public Builder defaultFormat(DataFormat format) {
            this.defaultFormat = format;
            return this;
        }

        /**
         * Sets the connection timeout in milliseconds (default: 60000).
         */
        public Builder connectTimeout(int milliseconds) {
            this.connectTimeoutMs = milliseconds;
            return this;
        }

        /**
         * Sets the response timeout in milliseconds (default: 120000).
         */
        public Builder responseTimeout(int milliseconds) {
            this.responseTimeoutMs = milliseconds;
            return this;
        }

        /**
         * Sets the maximum number of concurrent HTTP connections to the GeoServer host
         * (default: {@value ApacheHttpClient#DEFAULT_MAX_CONNECTIONS}). Raise this if you call
         * managers concurrently from multiple threads against the same {@code GeoServerClient}
         * and see connections queuing/blocking under load.
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
