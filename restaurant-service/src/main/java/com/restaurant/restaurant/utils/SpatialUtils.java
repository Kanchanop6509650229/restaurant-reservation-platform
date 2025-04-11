package com.restaurant.restaurant.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

/**
 * Utility class for handling spatial operations and calculations.
 * This class provides functionality for:
 * - Creating point geometries from coordinates
 * - Calculating distances between geographic points
 * - Supporting spatial queries and location-based operations
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class SpatialUtils {

    /** Factory for creating geometric objects with WGS84 coordinate system (SRID: 4326) */
    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * Creates a point geometry from geographic coordinates.
     * This method is used to create spatial points for database storage and queries.
     *
     * @param longitude The longitude coordinate (x-axis)
     * @param latitude The latitude coordinate (y-axis)
     * @return Point geometry object representing the location
     */
    public Point createPoint(double longitude, double latitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    /**
     * Calculates the great-circle distance between two geographic points.
     * This method uses the Haversine formula to compute the distance
     * between two points on the Earth's surface.
     *
     * @param lat1 Latitude of the first point in degrees
     * @param lon1 Longitude of the first point in degrees
     * @param lat2 Latitude of the second point in degrees
     * @param lon2 Longitude of the second point in degrees
     * @return Distance between the points in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}