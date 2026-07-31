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

    /** Constructs an empty {@code Contact} for deserialization. */
    public Contact() {}

    /**
     * Returns the contact ID.
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the contact ID.
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the address city.
     * @return the address city
     */
    public String getAddressCity() {
        return addressCity;
    }

    /**
     * Sets the address city.
     * @param addressCity the address city
     */
    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    /**
     * Returns the address country.
     * @return the address country
     */
    public String getAddressCountry() {
        return addressCountry;
    }

    /**
     * Sets the address country.
     * @param addressCountry the address country
     */
    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    /**
     * Returns the address delivery point.
     * @return the address delivery point
     */
    public String getAddressDeliveryPoint() {
        return addressDeliveryPoint;
    }

    /**
     * Sets the address delivery point.
     * @param addressDeliveryPoint the address delivery point
     */
    public void setAddressDeliveryPoint(String addressDeliveryPoint) {
        this.addressDeliveryPoint = addressDeliveryPoint;
    }

    /**
     * Returns the address postal code.
     * @return the address postal code
     */
    public Object getAddressPostalCode() {
        return addressPostalCode;
    }

    /**
     * Sets the address postal code.
     * @param addressPostalCode the address postal code
     */
    public void setAddressPostalCode(Object addressPostalCode) {
        this.addressPostalCode = addressPostalCode;
    }

    /**
     * Returns the address state.
     * @return the address state
     */
    public String getAddressState() {
        return addressState;
    }

    /**
     * Sets the address state.
     * @param addressState the address state
     */
    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    /**
     * Returns the address type.
     * @return the address type
     */
    public String getAddressType() {
        return addressType;
    }

    /**
     * Sets the address type.
     * @param addressType the address type
     */
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    /**
     * Returns the contact email.
     * @return the contact email
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Sets the contact email.
     * @param contactEmail the contact email
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /**
     * Returns the contact facsimile number.
     * @return the contact facsimile
     */
    public String getContactFacsimile() {
        return contactFacsimile;
    }

    /**
     * Sets the contact facsimile number.
     * @param contactFacsimile the contact facsimile
     */
    public void setContactFacsimile(String contactFacsimile) {
        this.contactFacsimile = contactFacsimile;
    }

    /**
     * Returns the contact organization name.
     * @return the contact organization
     */
    public String getContactOrganization() {
        return contactOrganization;
    }

    /**
     * Sets the contact organization name.
     * @param contactOrganization the contact organization
     */
    public void setContactOrganization(String contactOrganization) {
        this.contactOrganization = contactOrganization;
    }

    /**
     * Returns the contact person name.
     * @return the contact person
     */
    public String getContactPerson() {
        return contactPerson;
    }

    /**
     * Sets the contact person name.
     * @param contactPerson the contact person
     */
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    /**
     * Returns the contact position.
     * @return the contact position
     */
    public String getContactPosition() {
        return contactPosition;
    }

    /**
     * Sets the contact position.
     * @param contactPosition the contact position
     */
    public void setContactPosition(String contactPosition) {
        this.contactPosition = contactPosition;
    }

    /**
     * Returns the contact voice phone number.
     * @return the contact voice
     */
    public String getContactVoice() {
        return contactVoice;
    }

    /**
     * Sets the contact voice phone number.
     * @param contactVoice the contact voice
     */
    public void setContactVoice(String contactVoice) {
        this.contactVoice = contactVoice;
    }

    /**
     * Returns the online resource URL.
     * @return the online resource
     */
    public String getOnlineResource() {
        return onlineResource;
    }

    /**
     * Sets the online resource URL.
     * @param onlineResource the online resource
     */
    public void setOnlineResource(String onlineResource) {
        this.onlineResource = onlineResource;
    }

    /**
     * Returns the welcome message.
     * @return the welcome message
     */
    public String getWelcome() {
        return welcome;
    }

    /**
     * Sets the welcome message.
     * @param welcome the welcome message
     */
    public void setWelcome(String welcome) {
        this.welcome = welcome;
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
