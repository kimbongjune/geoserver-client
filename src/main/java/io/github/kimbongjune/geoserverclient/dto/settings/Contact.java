package io.github.kimbongjune.geoserverclient.dto.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * DTO for the contact information block within GeoServer settings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contact {

    @JsonProperty("id")
    private String id;

    @JsonProperty("addressCity")
    private String addressCity;

    @JsonProperty("addressCountry")
    private String addressCountry;

    @JsonProperty("addressDeliveryPoint")
    private String addressDeliveryPoint;

    @JsonProperty("addressPostalCode")
    private Object addressPostalCode;

    @JsonProperty("addressState")
    private String addressState;

    @JsonProperty("addressType")
    private String addressType;

    @JsonProperty("contactEmail")
    private String contactEmail;

    @JsonProperty("contactFacsimile")
    private String contactFacsimile;

    @JsonProperty("contactOrganization")
    private String contactOrganization;

    @JsonProperty("contactPerson")
    private String contactPerson;

    @JsonProperty("contactPosition")
    private String contactPosition;

    @JsonProperty("contactVoice")
    private String contactVoice;

    @JsonProperty("onlineResource")
    private String onlineResource;

    @JsonProperty("welcome")
    private String welcome;

    public Contact() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAddressCity() { return addressCity; }
    public void setAddressCity(String addressCity) { this.addressCity = addressCity; }

    public String getAddressCountry() { return addressCountry; }
    public void setAddressCountry(String addressCountry) { this.addressCountry = addressCountry; }

    public String getAddressDeliveryPoint() { return addressDeliveryPoint; }
    public void setAddressDeliveryPoint(String addressDeliveryPoint) { this.addressDeliveryPoint = addressDeliveryPoint; }

    public Object getAddressPostalCode() { return addressPostalCode; }
    public void setAddressPostalCode(Object addressPostalCode) { this.addressPostalCode = addressPostalCode; }

    public String getAddressState() { return addressState; }
    public void setAddressState(String addressState) { this.addressState = addressState; }

    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactFacsimile() { return contactFacsimile; }
    public void setContactFacsimile(String contactFacsimile) { this.contactFacsimile = contactFacsimile; }

    public String getContactOrganization() { return contactOrganization; }
    public void setContactOrganization(String contactOrganization) { this.contactOrganization = contactOrganization; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPosition() { return contactPosition; }
    public void setContactPosition(String contactPosition) { this.contactPosition = contactPosition; }

    public String getContactVoice() { return contactVoice; }
    public void setContactVoice(String contactVoice) { this.contactVoice = contactVoice; }

    public String getOnlineResource() { return onlineResource; }
    public void setOnlineResource(String onlineResource) { this.onlineResource = onlineResource; }

    public String getWelcome() { return welcome; }
    public void setWelcome(String welcome) { this.welcome = welcome; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact that = (Contact) o;
        return Objects.equals(id, that.id)
                && Objects.equals(addressCity, that.addressCity)
                && Objects.equals(addressCountry, that.addressCountry)
                && Objects.equals(addressDeliveryPoint, that.addressDeliveryPoint)
                && Objects.equals(addressPostalCode, that.addressPostalCode)
                && Objects.equals(addressState, that.addressState)
                && Objects.equals(addressType, that.addressType)
                && Objects.equals(contactEmail, that.contactEmail)
                && Objects.equals(contactFacsimile, that.contactFacsimile)
                && Objects.equals(contactOrganization, that.contactOrganization)
                && Objects.equals(contactPerson, that.contactPerson)
                && Objects.equals(contactPosition, that.contactPosition)
                && Objects.equals(contactVoice, that.contactVoice)
                && Objects.equals(onlineResource, that.onlineResource)
                && Objects.equals(welcome, that.welcome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addressCity, addressCountry, addressDeliveryPoint, addressPostalCode, addressState, addressType, contactEmail, contactFacsimile, contactOrganization, contactPerson, contactPosition, contactVoice, onlineResource, welcome);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", addressCity=" + addressCity +
                ", addressCountry=" + addressCountry +
                ", addressDeliveryPoint=" + addressDeliveryPoint +
                ", addressPostalCode=" + addressPostalCode +
                ", addressState=" + addressState +
                ", addressType=" + addressType +
                ", contactEmail=" + contactEmail +
                ", contactFacsimile=" + contactFacsimile +
                ", contactOrganization=" + contactOrganization +
                ", contactPerson=" + contactPerson +
                ", contactPosition=" + contactPosition +
                ", contactVoice=" + contactVoice +
                ", onlineResource=" + onlineResource +
                ", welcome=" + welcome +
                '}';
    }
}
