package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kimbongjune.geoserverclient.dto.common.StringMap;
import java.util.Objects;

/**
 * DTO for GWC global settings. Maps {@code GET/PUT /gwc/rest/global}; envelope key is {@code "global"}.
 *
 * <p>Supports partial PUT — null fields are omitted.
 * The {@link #version}, {@link #identifier}, and {@link #location} fields are read-only.
 *
 * <p><b>lockProvider warning:</b> Setting lockProvider via the GeoServer-managed GWC endpoint
 * may cause a 500 ClassCastException (see GwcGlobalManager for workaround details).
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

    /** @return {@code true} if runtime statistics are enabled */
    public Boolean getRuntimeStatsEnabled() {
        return runtimeStatsEnabled;
    }
    /**
     * Sets whether runtime statistics are enabled.
     * @param v {@code true} to enable
     */
    public void setRuntimeStatsEnabled(Boolean v) {
        this.runtimeStatsEnabled = v;
    }

    /** @return {@code true} if WMTS CITE compliance is enabled */
    public Boolean getWmtsCiteCompliant() {
        return wmtsCiteCompliant;
    }
    /**
     * Sets whether WMTS CITE compliance is enabled.
     * @param v {@code true} to enable
     */
    public void setWmtsCiteCompliant(Boolean v) {
        this.wmtsCiteCompliant = v;
    }

    /** @return the backend timeout in seconds */
    public Integer getBackendTimeout() {
        return backendTimeout;
    }
    /**
     * Sets the backend timeout in seconds.
     * @param v the timeout
     */
    public void setBackendTimeout(Integer v) {
        this.backendTimeout = v;
    }

    /** @return {@code true} if full WMS passthrough is enabled */
    public Boolean getFullWMS() {
        return fullWMS;
    }
    /**
     * Sets whether full WMS passthrough is enabled.
     * @param v {@code true} to enable
     */
    public void setFullWMS(Boolean v) {
        this.fullWMS = v;
    }

    /** @return {@code true} if cache bypass is allowed */
    public Boolean getCacheBypassAllowed() {
        return cacheBypassAllowed;
    }
    /**
     * Sets whether cache bypass is allowed.
     * @param v {@code true} to allow
     */
    public void setCacheBypassAllowed(Boolean v) {
        this.cacheBypassAllowed = v;
    }

    /**
     * Returns the lock provider bean name.
     * Setting this via the GeoServer-managed GWC endpoint may cause a 500 ClassCastException.
     * @return the lock provider bean name
     */
    public String getLockProvider() {
        return lockProvider;
    }
    /**
     * Sets the lock provider bean name.
     * @param v the lock provider bean name
     */
    public void setLockProvider(String v) {
        this.lockProvider = v;
    }

    /** @return the service information block */
    public ServiceInformation getServiceInformation() {
        return serviceInformation;
    }
    /**
     * Sets the service information block.
     * @param v the service information
     */
    public void setServiceInformation(ServiceInformation v) {
        this.serviceInformation = v;
    }

    /** @return the GWC version (read-only) */
    public String getVersion() {
        return version;
    }
    /** @return the GWC identifier (read-only) */
    public String getIdentifier() {
        return identifier;
    }
    /** @return the GWC service location (read-only) */
    public String getLocation() {
        return location;
    }

    /** GWC service information (title, description, keywords, contact, fees, access constraints). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServiceInformation {
        private String title;
        private String description;
        private StringMap keywords;
        private Contact contact;
        private String fees;
        private String accessConstraints;

        /** @return the service title */
        public String getTitle() {
            return title;
        }
        /** @param title the service title */
        public void setTitle(String title) {
            this.title = title;
        }
        /** @return the service description */
        public String getDescription() {
            return description;
        }
        /** @param description the service description */
        public void setDescription(String description) {
            this.description = description;
        }
        /** @return the keywords map */
        public StringMap getKeywords() {
            return keywords;
        }
        /** @param keywords the keywords map */
        public void setKeywords(StringMap keywords) {
            this.keywords = keywords;
        }
        /** @return the contact information */
        public Contact getContact() {
            return contact;
        }
        /** @param contact the contact information */
        public void setContact(Contact contact) {
            this.contact = contact;
        }
        /** @return the fees description */
        public String getFees() {
            return fees;
        }
        /** @param fees the fees description */
        public void setFees(String fees) {
            this.fees = fees;
        }
        /** @return the access constraints description */
        public String getAccessConstraints() {
            return accessConstraints;
        }
        /** @param accessConstraints the access constraints */
        public void setAccessConstraints(String accessConstraints) {
            this.accessConstraints = accessConstraints;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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

    /** GWC contact information (address type and organization). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Contact {
        private String addressType;
        private String organization;

        /** @return the address type */
        public String getAddressType() {
            return addressType;
        }
        /** @param addressType the address type */
        public void setAddressType(String addressType) {
            this.addressType = addressType;
        }
        /** @return the organization name */
        public String getOrganization() {
            return organization;
        }
        /** @param organization the organization name */
        public void setOrganization(String organization) {
            this.organization = organization;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
