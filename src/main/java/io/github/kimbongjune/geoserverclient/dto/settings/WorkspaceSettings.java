package io.github.kimbongjune.geoserverclient.dto.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * DTO for workspace-level settings. Maps {@code GET /rest/workspaces/{ws}/settings}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkspaceSettings {

    @JsonProperty("id")
    private String id;

    @JsonProperty("workspace")
    private WorkspaceRef workspace;

    @JsonProperty("contact")
    private Contact contact;

    @JsonProperty("charset")
    private String charset;

    @JsonProperty("numDecimals")
    private Integer numDecimals;

    @JsonProperty("verbose")
    private Boolean verbose;

    @JsonProperty("verboseExceptions")
    private Boolean verboseExceptions;

    @JsonProperty("localWorkspaceIncludesPrefix")
    private Boolean localWorkspaceIncludesPrefix;

    @JsonProperty("showCreatedTimeColumnsInAdminList")
    private Boolean showCreatedTimeColumnsInAdminList;

    @JsonProperty("showModifiedTimeColumnsInAdminList")
    private Boolean showModifiedTimeColumnsInAdminList;

    @JsonProperty("showModifiedUserAdminList")
    private Boolean showModifiedUserAdminList;

    public WorkspaceSettings() {}

    public static WorkspaceSettings of(String workspaceName) {
        WorkspaceSettings ws = new WorkspaceSettings();
        WorkspaceRef ref = new WorkspaceRef();
        ref.setName(workspaceName);
        ws.setWorkspace(ref);
        ws.setCharset("UTF-8");
        ws.setNumDecimals(8);
        ws.setVerbose(false);
        ws.setVerboseExceptions(false);
        ws.setLocalWorkspaceIncludesPrefix(false);
        ws.setShowCreatedTimeColumnsInAdminList(false);
        ws.setShowModifiedTimeColumnsInAdminList(false);
        ws.setShowModifiedUserAdminList(false);
        return ws;
    }

    public boolean hasLocalSettings() {
        return workspace != null && workspace.getName() != null;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public WorkspaceRef getWorkspace() {
        return workspace;
    }
    public void setWorkspace(WorkspaceRef workspace) {
        this.workspace = workspace;
    }

    public Contact getContact() {
        return contact;
    }
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getCharset() {
        return charset;
    }
    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getNumDecimals() {
        return numDecimals;
    }
    public void setNumDecimals(Integer numDecimals) {
        this.numDecimals = numDecimals;
    }

    public Boolean getVerbose() {
        return verbose;
    }
    public void setVerbose(Boolean verbose) {
        this.verbose = verbose;
    }

    public Boolean getVerboseExceptions() {
        return verboseExceptions;
    }
    public void setVerboseExceptions(Boolean verboseExceptions) {
        this.verboseExceptions = verboseExceptions;
    }

    public Boolean getLocalWorkspaceIncludesPrefix() {
        return localWorkspaceIncludesPrefix;
    }
    public void setLocalWorkspaceIncludesPrefix(Boolean localWorkspaceIncludesPrefix) {
        this.localWorkspaceIncludesPrefix = localWorkspaceIncludesPrefix;
    }

    public Boolean getShowCreatedTimeColumnsInAdminList() {
        return showCreatedTimeColumnsInAdminList;
    }
    public void setShowCreatedTimeColumnsInAdminList(Boolean v) {
        this.showCreatedTimeColumnsInAdminList = v;
    }

    public Boolean getShowModifiedTimeColumnsInAdminList() {
        return showModifiedTimeColumnsInAdminList;
    }
    public void setShowModifiedTimeColumnsInAdminList(Boolean v) {
        this.showModifiedTimeColumnsInAdminList = v;
    }

    public Boolean getShowModifiedUserAdminList() {
        return showModifiedUserAdminList;
    }
    public void setShowModifiedUserAdminList(Boolean v) {
        this.showModifiedUserAdminList = v;
    }

    //  Nested: WorkspaceRef 

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkspaceRef {

        @JsonProperty("name")
        private String name;

        @JsonProperty("isolated")
        private Boolean isolated;

        @JsonProperty("dateCreated")
        private String dateCreated;

        public WorkspaceRef() {}

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public Boolean getIsolated() {
            return isolated;
        }
        public void setIsolated(Boolean isolated) {
            this.isolated = isolated;
        }

        public String getDateCreated() {
            return dateCreated;
        }
        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            WorkspaceRef that = (WorkspaceRef) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(isolated, that.isolated)
                    && Objects.equals(dateCreated, that.dateCreated);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, isolated, dateCreated);
        }

        @Override
        public String toString() {
            return "WorkspaceRef{" +
                    "name=" + name +
                    ", isolated=" + isolated +
                    ", dateCreated=" + dateCreated +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WorkspaceSettings that = (WorkspaceSettings) o;
        return Objects.equals(id, that.id)
                && Objects.equals(workspace, that.workspace)
                && Objects.equals(contact, that.contact)
                && Objects.equals(charset, that.charset)
                && Objects.equals(numDecimals, that.numDecimals)
                && Objects.equals(verbose, that.verbose)
                && Objects.equals(verboseExceptions, that.verboseExceptions)
                && Objects.equals(localWorkspaceIncludesPrefix, that.localWorkspaceIncludesPrefix)
                && Objects.equals(showCreatedTimeColumnsInAdminList, that.showCreatedTimeColumnsInAdminList)
                && Objects.equals(showModifiedTimeColumnsInAdminList, that.showModifiedTimeColumnsInAdminList)
                && Objects.equals(showModifiedUserAdminList, that.showModifiedUserAdminList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workspace, contact, charset, numDecimals, verbose, verboseExceptions, localWorkspaceIncludesPrefix, showCreatedTimeColumnsInAdminList, showModifiedTimeColumnsInAdminList, showModifiedUserAdminList);
    }

    @Override
    public String toString() {
        return "WorkspaceSettings{" +
                "id=" + id +
                ", workspace=" + workspace +
                ", contact=" + contact +
                ", charset=" + charset +
                ", numDecimals=" + numDecimals +
                ", verbose=" + verbose +
                ", verboseExceptions=" + verboseExceptions +
                ", localWorkspaceIncludesPrefix=" + localWorkspaceIncludesPrefix +
                ", showCreatedTimeColumnsInAdminList=" + showCreatedTimeColumnsInAdminList +
                ", showModifiedTimeColumnsInAdminList=" + showModifiedTimeColumnsInAdminList +
                ", showModifiedUserAdminList=" + showModifiedUserAdminList +
                '}';
    }
}
