package com.example.geoserverexample.settings.service.impl;

import com.example.geoserverexample.settings.service.SettingsService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.logging.LoggingInfo;
import io.github.kimbongjune.geoserverclient.dto.service.ServiceSettings;
import io.github.kimbongjune.geoserverclient.dto.settings.Contact;
import io.github.kimbongjune.geoserverclient.dto.settings.GlobalSettings;
import io.github.kimbongjune.geoserverclient.dto.settings.WorkspaceSettings;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final GeoServerClient client;

    public SettingsServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public GlobalSettings.GeoServerSettings getGlobalSettings() {
        return client.settings().getGlobal().getSettings();
    }

    @Override
    public Contact getContact() {
        return client.settings().getContact();
    }

    @Override
    public LoggingInfo getLogging() {
        return client.logging().getLogging();
    }

    @Override
    public List<WorkspaceSummary> listWorkspaces() {
        return client.workspaces().list();
    }

    @Override
    public ServiceSettings getWms() {
        return client.services().getWms();
    }

    @Override
    public ServiceSettings getWfs() {
        return client.services().getWfs();
    }

    @Override
    public ServiceSettings getWcs() {
        return client.services().getWcs();
    }

    @Override
    public ServiceSettings getWmts() {
        return client.services().getWmts();
    }

    @Override
    public void updateService(String svc, boolean enabled, String title, boolean verbose) {
        ServiceSettings settings = switch (svc) {
            case "wms" -> client.services().getWms();
            case "wfs" -> client.services().getWfs();
            case "wcs" -> client.services().getWcs();
            case "wmts" -> client.services().getWmts();
            default -> throw new IllegalArgumentException("Unknown service: " + svc);
        };
        settings.setEnabled(enabled);
        settings.setTitle(title);
        settings.setVerbose(verbose);
        switch (svc) {
            case "wms" -> client.services().updateWms(settings);
            case "wfs" -> client.services().updateWfs(settings);
            case "wcs" -> client.services().updateWcs(settings);
            case "wmts" -> client.services().updateWmts(settings);
        }
    }

    @Override
    public ServiceSettings getWorkspaceServiceSettingsBestEffort(String svc, String ws) {
        try {
            return client.services().getWorkspaceSettings(svc, ws);
        } catch (GeoServerException e) {
            // No workspace-scoped override configured yet — falls back to the global service settings.
            return null;
        }
    }

    @Override
    public void saveWorkspaceServiceSettings(String svc, String ws, boolean enabled, String title, boolean verbose) {
        ServiceSettings settings = getWorkspaceServiceSettingsBestEffort(svc, ws);
        if (settings == null) {
            settings = new ServiceSettings();
        }
        settings.setEnabled(enabled);
        settings.setTitle(title);
        settings.setVerbose(verbose);
        client.services().updateWorkspaceSettings(svc, ws, settings);
    }

    @Override
    public void deleteWorkspaceServiceSettings(String svc, String ws) {
        client.services().deleteWorkspaceSettings(svc, ws);
    }

    @Override
    public WorkspaceSettings getWorkspaceSettingsBestEffort(String ws) {
        try {
            return client.settings().getWorkspaceSettings(ws);
        } catch (GeoServerException e) {
            return null;
        }
    }

    @Override
    public void createWorkspaceSettings(String ws) {
        client.settings().createWorkspaceSettings(ws, WorkspaceSettings.of(ws));
    }

    @Override
    public void updateWorkspaceSettings(String ws, int numDecimals, boolean verbose) {
        WorkspaceSettings settings = getWorkspaceSettingsBestEffort(ws);
        if (settings == null) {
            settings = WorkspaceSettings.of(ws);
        }
        settings.setNumDecimals(numDecimals);
        settings.setVerbose(verbose);
        client.settings().updateWorkspaceSettings(ws, settings);
    }

    @Override
    public void deleteWorkspaceSettings(String ws) {
        client.settings().deleteWorkspaceSettings(ws);
    }

    @Override
    public void updateGlobal(int numDecimals, boolean verbose) {
        GlobalSettings global = client.settings().getGlobal();
        global.getSettings().setNumDecimals(numDecimals);
        global.getSettings().setVerbose(verbose);
        client.settings().updateGlobal(global);
    }

    @Override
    public void updateContact(String organization) {
        Contact contact = new Contact();
        contact.setContactOrganization(organization);
        client.settings().updateContact(contact);
    }

    @Override
    public void updateLogging(String level, boolean stdOutLogging) {
        LoggingInfo logging = client.logging().getLogging();
        logging.setLevel(level);
        logging.setStdOutLogging(stdOutLogging);
        client.logging().updateLogging(logging);
    }
}
