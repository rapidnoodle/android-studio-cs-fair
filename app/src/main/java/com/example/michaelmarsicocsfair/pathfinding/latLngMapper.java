package com.example.michaelmarsicocsfair.pathfinding;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class latLngMapper {

    private static float width;
    private static float height;
    private static LatLngBounds bounds;

    public latLngMapper(float width, float height, LatLngBounds bounds) {
        latLngMapper.width = width;
        latLngMapper.height = height;
        latLngMapper.bounds = bounds;
    }

    /**
     * Returns and LatLng object with the map coordinates
     * at the overlay's image's pixes coordinates
     * @param x
     * @param y
     * @return LatLng
     */
    public static LatLng calculateLatLng(int x, int y) {
        return new LatLng((bounds.northeast.latitude + ((y / (height / 100)) / 100) * (bounds.southwest.latitude - bounds.northeast.latitude)),
                (bounds.southwest.longitude + ((x / (width / 100)) / 100) * (bounds.northeast.longitude - bounds.southwest.longitude)));
    }
}