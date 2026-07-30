package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Objects;

/**
 * Request DTO for truncating orphaned tiles (tiles with no corresponding layer).
 * <b>Warning:</b> with the default BlobStore this may cause a 500 Internal Server Error
 * (see GwcMassTruncateManager for details; behavior verified via curl).
 */
@JacksonXmlRootElement(localName = "truncateOrphans")
public class GwcTruncateOrphansRequest implements GwcTruncateRequest {

    @Override
    public boolean equals(Object o) {
        return this == o || (o != null && getClass() == o.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "GwcTruncateOrphansRequest{}";
    }
}
