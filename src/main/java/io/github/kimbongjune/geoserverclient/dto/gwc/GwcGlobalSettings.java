package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kimbongjune.geoserverclient.dto.common.StringMap;
import java.util.Objects;

/**
 * DTO for GWC global settings. Maps {@code GET/PUT /gwc/rest/global}; envelope key is {@code "global"}.
 *
 * <p>Supports partial PUT — null fields are omitted.
 * The {@link #version}, {@link #identifier}, and {@link #location} fields are read-only.</p>
 *
 * <p><b>lockProvider warning:</b> Setting lockProvider via the GeoServer-managed GWC endpoint
 * may cause a 500 ClassCastException (see GwcGlobalManager for workaround details).</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GwcGlobalSettings {

    private Boolean runtimeStatsEnabled;
    private Boolean wmtsCiteCompliant;
    private Integer backendTimeout;
    private Boolean fullWMS;
    private Boolean cacheBypassAllowed;
    private String lockProvider;
    private ServiceInformation serviceInformation;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String version;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String identifier;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String location;

    public Boolean getRuntimeStatsEnabled() { return runtimeStatsEnabled; }
    public void setRuntimeStatsEnabled(Boolean v) { this.runtimeStatsEnabled = v; }

    public Boolean getWmtsCiteCompliant() { return wmtsCiteCompliant; }
    public void setWmtsCiteCompliant(Boolean v) { this.wmtsCiteCompliant = v; }

    public Integer getBackendTimeout() { return backendTimeout; }
    public void setBackendTimeout(Integer v) { this.backendTimeout = v; }

    public Boolean getFullWMS() { return fullWMS; }
    public void setFullWMS(Boolean v) { this.fullWMS = v; }

    public Boolean getCacheBypassAllowed() { return cacheBypassAllowed; }
    public void setCacheBypassAllowed(Boolean v) { this.cacheBypassAllowed = v; }

    /** GeoServer     500  [ ]. */
    public String getLockProvider() { return lockProvider; }
    public void setLockProvider(String v) { this.lockProvider = v; }

    public ServiceInformation getServiceInformation() { return serviceInformation; }
    public void setServiceInformation(ServiceInformation v) { this.serviceInformation = v; }

    public String getVersion() { return version; }
    public String getIdentifier() { return identifier; }
    public String getLocation() { return location; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServiceInformation {
        private String title;
        private String description;
        private StringMap keywords;
        private Contact contact;
        private String fees;
        private String accessConstraints;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public StringMap getKeywords() { return keywords; }
        public void setKeywords(StringMap keywords) { this.keywords = keywords; }
        public Contact getContact() { return contact; }
        public void setContact(Contact contact) { this.contact = contact; }
        public String getFees() { return fees; }
        public void setFees(String fees) { this.fees = fees; }
        public String getAccessConstraints() { return accessConstraints; }
        public void setAccessConstraints(String accessConstraints) { this.accessConstraints = accessConstraints; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ServiceInformation that = (ServiceInformation) o;
            return Objects.equals(title, that.title)
                    && Objects.equals(description, that.description)
                    && Objects.equals(keywords, that.keywords)
                    && Objects.equals(contact, that.contact)
                    && Objects.equals(fees, that.fees)
                    && Objects.equals(accessConstraints, that.accessConstraints);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, description, keywords, contact, fees, accessConstraints);
        }

        @Override
        public String toString() {
            return "ServiceInformation{" +
                    "title=" + title +
                    ", description=" + description +
                    ", keywords=" + keywords +
                    ", contact=" + contact +
                    ", fees=" + fees +
                    ", accessConstraints=" + accessConstraints +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Contact {
        private String addressType;
        private String organization;

        public String getAddressType() { return addressType; }
        public void setAddressType(String addressType) { this.addressType = addressType; }
        public String getOrganization() { return organization; }
        public void setOrganization(String organization) { this.organization = organization; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Contact that = (Contact) o;
            return Objects.equals(addressType, that.addressType)
                    && Objects.equals(organization, that.organization);
        }

        @Override
        public int hashCode() {
            return Objects.hash(addressType, organization);
        }

        @Override
        public String toString() {
            return "Contact{" +
                    "addressType=" + addressType +
                    ", organization=" + organization +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GwcGlobalSettings that = (GwcGlobalSettings) o;
        return Objects.equals(runtimeStatsEnabled, that.runtimeStatsEnabled)
                && Objects.equals(wmtsCiteCompliant, that.wmtsCiteCompliant)
                && Objects.equals(backendTimeout, that.backendTimeout)
                && Objects.equals(fullWMS, that.fullWMS)
                && Objects.equals(cacheBypassAllowed, that.cacheBypassAllowed)
                && Objects.equals(lockProvider, that.lockProvider)
                && Objects.equals(serviceInformation, that.serviceInformation)
                && Objects.equals(version, that.version)
                && Objects.equals(identifier, that.identifier)
                && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runtimeStatsEnabled, wmtsCiteCompliant, backendTimeout, fullWMS, cacheBypassAllowed, lockProvider, serviceInformation, version, identifier, location);
    }

    @Override
    public String toString() {
        return "GwcGlobalSettings{" +
                "runtimeStatsEnabled=" + runtimeStatsEnabled +
                ", wmtsCiteCompliant=" + wmtsCiteCompliant +
                ", backendTimeout=" + backendTimeout +
                ", fullWMS=" + fullWMS +
                ", cacheBypassAllowed=" + cacheBypassAllowed +
                ", lockProvider=" + lockProvider +
                ", serviceInformation=" + serviceInformation +
                ", version=" + version +
                ", identifier=" + identifier +
                ", location=" + location +
                '}';
    }
}
