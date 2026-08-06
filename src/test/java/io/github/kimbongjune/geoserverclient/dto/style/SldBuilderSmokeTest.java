package io.github.kimbongjune.geoserverclient.dto.style;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SldBuilderSmokeTest {

    @Test
    void redSquarePoint() {
        String xml = SldBuilder.create("simple_point")
                .title("Red Square Point")
                .rule()
                .mark(WellKnownName.SQUARE)
                    .fill("#FF0000")
                    .stroke("#000000", 2)
                    .size(12)
                .end()
                .build()
                .getSldBody();

        System.out.println(xml);

        assertTrue(xml.contains("<WellKnownName>square</WellKnownName>"));
        assertTrue(xml.contains("<CssParameter name=\"fill\">#FF0000</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"stroke\">#000000</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"stroke-width\">2</CssParameter>"));
        assertTrue(xml.contains("<Size>12</Size>"));
        assertTrue(xml.contains("<Title>Red Square Point</Title>"));
    }
}
