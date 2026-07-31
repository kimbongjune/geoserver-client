package io.github.kimbongjune.geoserverclient.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;

/**
 * JSON serializer using Jackson ObjectMapper.
 * <p>
 * Standard configuration — no WRAP/UNWRAP_ROOT_VALUE. Each request and response
 * class must explicitly model the full JSON structure (including root wrapper keys).
 * This keeps deserialization predictable and handles GeoServer's edge cases
 * (e.g., empty list: {@code {"workspaces":""}}) without magic.
 */
public class JsonSerializer implements Serializer {

    private final ObjectMapper mapper;

    /** Constructs a {@code JsonSerializer} with a pre-configured {@code ObjectMapper}. */
    public JsonSerializer() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public String serialize(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SerializationException(
                    "Failed to serialize " + object.getClass().getSimpleName() + " to JSON", e);
        }
    }

    @Override
    public <T> T deserialize(String content, Class<T> type) {
        try {
            return mapper.readValue(content, type);
        } catch (JsonProcessingException e) {
            throw new SerializationException(
                    "Failed to deserialize JSON to " + type.getSimpleName(), e);
        }
    }

    @Override
    public DataFormat getFormat() {
        return DataFormat.JSON;
    }

    /**
     * Returns the underlying ObjectMapper for advanced customization.
     * @return the underlying {@code ObjectMapper}
     */
    public ObjectMapper getObjectMapper() {
        return mapper;
    }
}
