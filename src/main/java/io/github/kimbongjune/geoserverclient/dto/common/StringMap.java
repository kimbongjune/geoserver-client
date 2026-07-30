package io.github.kimbongjune.geoserverclient.dto.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Objects;

/**
 * General-purpose DTO for dynamic string key-value pairs.
 *
 * <p>Used wherever a JSON object has dynamic keys, for example:
 * <ul>
 *   <li>WMS layer {@code vendorParameters}</li>
 *   <li>Layer group {@code internationalTitle} / {@code internationalAbstract}</li>
 *   <li>Layer group {@code metadata}</li>
 * </ul>
 *
 * <p><b>Usage:</b>
 * <pre>{@code
 * // Single entry
 * StringMap params = StringMap.of("FORMAT", "image/png");
 *
 * // Multiple entries (builder style)
 * StringMap i18n = new StringMap()
 *         .put("en", "English Title")
 *         .put("ko", "Korean Title");
 * }</pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StringMap {

    private final LinkedHashMap<String, String> entries = new LinkedHashMap<>();

    public StringMap() {}

    /** Factory method that creates a StringMap with a single entry. */
    public static StringMap of(String key, String value) {
        return new StringMap().put(key, value);
    }

    /** Adds an entry and returns {@code this} for chaining (builder style). */
    public StringMap put(String key, String value) {
        entries.put(key, value);
        return this;
    }

    /** Returns the value for the given key, or {@code null} if absent. */
    public String get(String key) {
        return entries.get(key);
    }

    /** Returns {@code true} if the given key is present. */
    public boolean containsKey(String key) {
        return entries.containsKey(key);
    }

    /** Returns an unmodifiable view of all keys. */
    public Set<String> keySet() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    /** Returns the number of entries. */
    public int size() {
        return entries.size();
    }

    /** Returns {@code true} if there are no entries. */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    // Jackson serialization

    @JsonAnySetter
    public void setEntry(String key, Object value) {
        entries.put(key, value != null ? value.toString() : null);
    }

    @JsonAnyGetter
    public Map<String, String> getEntries() {
        return Collections.unmodifiableMap(entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringMap that = (StringMap) o;
        return Objects.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }

    @Override
    public String toString() {
        return "StringMap{" +
                "entries=" + entries +
                '}';
    }
}
