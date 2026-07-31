package com.example.geoserverexample.vector.service;

import io.github.kimbongjune.geoserverclient.dto.datastore.DataStore;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface VectorDataService {

    List<WorkspaceSummary> listWorkspaces();

    List<DataStoreSummary> listStores(String ws);

    DataStore getStoreDetail(String ws, String store);

    List<String> listAvailable(String ws, String store);

    List<FtRow> listFeatureTypeRows(String ws, String store);

    boolean isRenaming(String newName);

    void updateStore(String ws, String store, boolean enabled, String description, String newName,
                      boolean defaultStore, boolean disableOnConnFailure);

    void updateConnectionParams(String ws, String store, Map<String, String> allParams);

    void createPostgisStore(String ws, String storeName, boolean createSampleTable) throws SQLException;

    void uploadFile(String ws, String storeName, String format, MultipartFile file) throws Exception;

    void publishExisting(String ws, String store, String tableName);

    void resetStore(String ws, String store);

    void deleteStore(String ws, String store);

    void enableFeatureType(String ws, String store, String ft, boolean enabled);

    void updateFeatureType(String ws, String store, String ft, String newName, String title, String srs,
                            String projectionPolicy, Integer maxFeatures);

    void deleteFeatureType(String ws, String store, String ft);

    class FtRow {
        public final String name;
        public final boolean enabled;
        public final String srs;
        public FtRow(String name, Boolean enabled, String srs) {
            this.name = name;
            this.enabled = Boolean.TRUE.equals(enabled);
            this.srs = srs;
        }
        public String getName() { return name; }
        public boolean isEnabled() { return enabled; }
        public String getSrs() { return srs; }
    }
}
