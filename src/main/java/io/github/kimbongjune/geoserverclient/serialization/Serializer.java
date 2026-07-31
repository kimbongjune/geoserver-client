package io.github.kimbongjune.geoserverclient.serialization;

import io.github.kimbongjune.geoserverclient.exception.SerializationException;

/**
 * Strategy interface for serializing/deserializing DTOs to/from a specific wire format.
 */
public interface Serializer {

    /**
     * Serializes an object to its string representation (JSON or XML).
     *
     * @param object the object to serialize
     * @return serialized string
     * @throws SerializationException if serialization fails
     */
    String serialize(Object object);

    /**
     * Deserializes a string to an object of the specified type.
     *
     * @param content the string to deserialize
     * @param type    the target class
     * @param <T>     the target type
     * @return deserialized object
     * @throws SerializationException if deserialization fails
     */
    <T> T deserialize(String content, Class<T> type);

    /**
     * Returns the data format this serializer handles.
     * @return the data format
     */
    DataFormat getFormat();
}
