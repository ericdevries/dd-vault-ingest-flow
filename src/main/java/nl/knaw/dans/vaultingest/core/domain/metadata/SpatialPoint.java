package nl.knaw.dans.vaultingest.core.domain.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

@Builder
public class SpatialPoint {
    private final String point1;
    private final String point2;
    private final String scheme;

    public Point getPoint() {
        // TS002
        if (Objects.equals(scheme, "http://www.opengis.net/def/crs/EPSG/0/28992")) {
            return new Point(point1, point2);
        }
        else if (scheme == null || scheme.equals("http://www.opengis.net/def/crs/EPSG/0/4326")) {
            return new Point(point2, point1);
        }
        else {
            throw new IllegalArgumentException("Unknown scheme: " + scheme);
        }
    }

    public String getScheme() {
        return scheme;
    }

    @Value
    @AllArgsConstructor
    public static class Point {
        String x;
        String y;
    }
}
