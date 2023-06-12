/*
 * Copyright (C) 2023 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
