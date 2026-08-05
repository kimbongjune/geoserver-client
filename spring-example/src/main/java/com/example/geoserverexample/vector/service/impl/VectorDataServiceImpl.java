package com.example.geoserverexample.vector.service.impl;

import com.example.geoserverexample.vector.service.VectorDataService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.common.ProjectionPolicy;
import io.github.kimbongjune.geoserverclient.dto.datastore.CreateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStore;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.datastore.UpdateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.featuretype.CreateFeatureTypeRequest;
import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureType;
import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureTypeSummary;
import io.github.kimbongjune.geoserverclient.dto.featuretype.UpdateFeatureTypeRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VectorDataServiceImpl implements VectorDataService {

    private final GeoServerClient client;

    @Value("${postgis.host}") private String pgHost;
    @Value("${postgis.port}") private String pgPort;
    @Value("${postgis.database}") private String pgDatabase;
    @Value("${postgis.schema}") private String pgSchema;
    @Value("${postgis.user}") private String pgUser;
    @Value("${postgis.password}") private String pgPassword;
    @Value("${postgis.container-host}") private String pgContainerHost;

    public VectorDataServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<WorkspaceSummary> listWorkspaces() {
        return client.workspaces().list();
    }

    @Override
    public List<DataStoreSummary> listStores(String ws) {
        return client.datastores().list(ws);
    }

    @Override
    public DataStore getStoreDetail(String ws, String store) {
        return client.datastores().get(ws, store);
    }

    @Override
    public List<String> listAvailable(String ws, String store) {
        return client.featureTypes().listAvailable(ws, store);
    }

    @Override
    public List<FtRow> listFeatureTypeRows(String ws, String store) {
        List<FtRow> rows = new ArrayList<>();
        for (FeatureTypeSummary s : client.featureTypes().list(ws, store)) {
            try {
                FeatureType ft = client.featureTypes().get(ws, store, s.getName());
                rows.add(new FtRow(s.getName(), ft.getEnabled(), ft.getSrs()));
            } catch (GeoServerException e) {
                // A feature type can be configured in the catalog but broken at the native-store
                // level (e.g. its underlying table was dropped) — show it instead of 500ing the page.
                rows.add(new FtRow(s.getName(), null, "(unreadable: " + e.getMessage() + ")"));
            }
        }
        return rows;
    }

    @Override
    public boolean isRenaming(String newName) {
        return newName != null && !newName.isBlank();
    }

    @Override
    public void updateStore(String ws, String store, boolean enabled, String description, String newName,
                             boolean defaultStore, boolean disableOnConnFailure) {
        UpdateDataStoreRequest.Builder builder = UpdateDataStoreRequest.builder()
                .enabled(enabled).description(description)
                .defaultStore(defaultStore).disableOnConnFailure(disableOnConnFailure);
        if (isRenaming(newName)) {
            builder.name(newName);
        }
        client.datastores().update(ws, store, builder.build());
    }

    @Override
    public void updateConnectionParams(String ws, String store, Map<String, String> allParams) {
        UpdateDataStoreRequest.Builder builder = UpdateDataStoreRequest.builder();
        for (Map.Entry<String, String> e : allParams.entrySet()) {
            if (e.getKey().startsWith("cp_")) {
                builder.connectionParam(e.getKey().substring(3), e.getValue());
            }
        }
        client.datastores().update(ws, store, builder.build());
    }

    @Override
    public void createPostgisStore(String ws, String storeName, boolean createSampleTable) throws SQLException {
        if (createSampleTable) {
            String jdbcUrl = "jdbc:postgresql://" + pgHost + ":" + pgPort + "/" + pgDatabase;
            try (Connection conn = DriverManager.getConnection(jdbcUrl, pgUser, pgPassword);
                 Statement st = conn.createStatement()) {
                st.execute("DROP TABLE IF EXISTS " + storeName + "_cities");
                st.execute("CREATE TABLE " + storeName + "_cities (id SERIAL PRIMARY KEY, name VARCHAR(64), "
                        + "geom GEOMETRY(Point, 4326))");
                st.execute("INSERT INTO " + storeName + "_cities (name, geom) VALUES "
                        + "('Seoul', ST_SetSRID(ST_MakePoint(126.9780, 37.5665), 4326)), "
                        + "('Tokyo', ST_SetSRID(ST_MakePoint(139.6917, 35.6895), 4326)), "
                        + "('Paris', ST_SetSRID(ST_MakePoint(2.3522, 48.8566), 4326))");
            }
        }
        client.datastores().create(ws, CreateDataStoreRequest.builder(storeName)
                .type("PostGIS")
                .connectionParam("host", pgContainerHost)
                .connectionParam("port", pgPort)
                .connectionParam("database", pgDatabase)
                .connectionParam("schema", pgSchema)
                .connectionParam("user", pgUser)
                .connectionParam("passwd", pgPassword)
                .connectionParam("dbtype", "postgis")
                .build());
    }

    @Override
    public void uploadFile(String ws, String storeName, String format, MultipartFile file) throws Exception {
        File tmp = File.createTempFile("upload-", "-" + file.getOriginalFilename());
        file.transferTo(tmp);
        tmp.deleteOnExit();
        client.datastores().uploadFile(ws, storeName, "file", format, tmp, "first", null, null);
    }

    @Override
    public void publishExisting(String ws, String store, String tableName) {
        client.featureTypes().create(ws, store, CreateFeatureTypeRequest.builder(tableName).srs("EPSG:4326").build());
    }

    @Override
    public void resetStore(String ws, String store) {
        client.datastores().reset(ws, store);
    }

    @Override
    public void deleteStore(String ws, String store) {
        client.datastores().delete(ws, store, true);
    }

    @Override
    public void enableFeatureType(String ws, String store, String ft, boolean enabled) {
        client.featureTypes().update(ws, store, ft, UpdateFeatureTypeRequest.builder()
                .enabled(enabled).recalculate("nativebbox,latlonbbox").build());
    }

    @Override
    public void updateFeatureType(String ws, String store, String ft, String newName, String title, String srs,
                                   String projectionPolicy, Integer maxFeatures) {
        UpdateFeatureTypeRequest.Builder builder = UpdateFeatureTypeRequest.builder()
                .title(title).srs(srs).projectionPolicy(parseProjectionPolicy(projectionPolicy))
                .maxFeatures(maxFeatures).recalculate("nativebbox,latlonbbox");
        if (isRenaming(newName)) {
            builder.name(newName);
        }
        client.featureTypes().update(ws, store, ft, builder.build());
    }

    private static ProjectionPolicy parseProjectionPolicy(String value) {
        return (value == null || value.trim().isEmpty()) ? null : ProjectionPolicy.valueOf(value.trim());
    }

    @Override
    public void deleteFeatureType(String ws, String store, String ft) {
        client.featureTypes().delete(ws, store, ft);
    }
}
