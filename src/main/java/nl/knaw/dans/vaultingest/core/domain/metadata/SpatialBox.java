package nl.knaw.dans.vaultingest.core.domain.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder
public class SpatialBox {
    private final String upper1;
    private final String upper2;
    private final String lower1;
    private final String lower2;
    private final String scheme;

    public String getScheme() {
        return scheme;
    }

    public Box getBox() {
        if ("http://www.opengis.net/def/crs/EPSG/0/28992".equals(scheme)) {
            return new Box(upper2, upper1, lower2, lower1);
        }
        else if ("http://www.opengis.net/def/crs/EPSG/0/4326".equals(scheme)) {
            return new Box(upper1, upper2, lower1, lower2);
        }
        else {
            throw new IllegalArgumentException("Unknown scheme: " + scheme);
        }
    }

    @Value
    @AllArgsConstructor
    public static class Box {
        String north;
        String east;
        String south;
        String west;
    }
}
