package io.github.kimbongjune.geoserverclient.serialization;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factory for obtaining format-specific serializers.
 * Pre-initializes JSON and XML serializers on construction.
 */
public class SerializerFactory {

    private final Map<DataFormat, Serializer> serializers;

    public SerializerFactory() {
        this.serializers = new EnumMap<>(DataFormat.class);
        this.serializers.put(DataFormat.JSON, new JsonSerializer());
        this.serializers.put(DataFormat.XML, new XmlSerializer());
    }

    /**
     * Returns the serializer for the given format.
     *
     * @param format the target data format
     * @return the corresponding serializer
     * @throws IllegalArgumentException if no serializer exists for the format
     */
    public Serializer getSerializer(DataFormat format) {
        Serializer serializer = serializers.get(format);
        if (serializer == null) {
            throw new IllegalArgumentException("Unsupported data format: " + format);
        }
        return serializer;
    }
}
