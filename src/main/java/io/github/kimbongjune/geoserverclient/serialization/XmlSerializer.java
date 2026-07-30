package io.github.kimbongjune.geoserverclient.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;

/**
 * XML serializer using Jackson XmlMapper.
 * <p>
 * For XML, the root element is controlled by {@code @JacksonXmlRootElement} on DTOs.
 * Unlike the JSON serializer, no WRAP_ROOT_VALUE is needed because XML naturally
 * uses the root element as the wrapper.
 */
public class XmlSerializer implements Serializer {

    private final XmlMapper mapper;

    public XmlSerializer() {
        this.mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
    }

    @Override
    public String serialize(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SerializationException(
                    "Failed to serialize " + object.getClass().getSimpleName() + " to XML", e);
        }
    }

    @Override
    public <T> T deserialize(String content, Class<T> type) {
        try {
            return mapper.readValue(content, type);
        } catch (JsonProcessingException e) {
            throw new SerializationException(
                    "Failed to deserialize XML to " + type.getSimpleName(), e);
        }
    }

    @Override
    public DataFormat getFormat() {
        return DataFormat.XML;
    }

    /**
     * Returns the underlying XmlMapper for advanced customization.
     */
    public XmlMapper getXmlMapper() {
        return mapper;
    }
}
