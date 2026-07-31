package com.example.geoserverexample.settings.service;

import io.github.kimbongjune.geoserverclient.dto.logging.LoggingInfo;
import io.github.kimbongjune.geoserverclient.dto.service.ServiceSettings;
import io.github.kimbongjune.geoserverclient.dto.settings.Contact;
import io.github.kimbongjune.geoserverclient.dto.settings.GlobalSettings;
import io.github.kimbongjune.geoserverclient.dto.settings.WorkspaceSettings;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;

import java.util.List;

public interface SettingsService {

    GlobalSettings.GeoServerSettings getGlobalSettings();

    Contact getContact();

    LoggingInfo getLogging();

    List<WorkspaceSummary> listWorkspaces();

    ServiceSettings getWms();

    ServiceSettings getWfs();

    ServiceSettings getWcs();

    ServiceSettings getWmts();

    /**
     * Updates one of the four global services (svc must be "wms", "wfs", "wcs", or "wmts").
     */
    void updateService(String svc, boolean enabled, String title, boolean verbose);

    /**
     * Returns the workspace-scoped override for a service, or {@code null} if the workspace
     * has no override yet (GeoServer 404s — it just falls back to the global settings).
     */
    ServiceSettings getWorkspaceServiceSettingsBestEffort(String svc, String ws);

    void saveWorkspaceServiceSettings(String svc, String ws, boolean enabled, String title, boolean verbose);

    void deleteWorkspaceServiceSettings(String svc, String ws);

    /**
     * Returns the workspace's general settings override, or {@code null} if none exists yet.
     */
    WorkspaceSettings getWorkspaceSettingsBestEffort(String ws);

    void createWorkspaceSettings(String ws);

    void updateWorkspaceSettings(String ws, int numDecimals, boolean verbose);

    void deleteWorkspaceSettings(String ws);

    void updateGlobal(int numDecimals, boolean verbose);

    void updateContact(String organization);

    void updateLogging(String level, boolean stdOutLogging);
}
