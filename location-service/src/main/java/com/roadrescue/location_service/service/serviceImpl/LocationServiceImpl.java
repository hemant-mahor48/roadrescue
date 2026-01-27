package com.roadrescue.location_service.service.serviceImpl;

import com.roadrescue.location_service.service.LocationService;

import com.roadrescue.location_service.dto.MechanicLocation;
import com.roadrescue.location_service.dto.NearbyMechanic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private static final String MECHANIC_GEO_KEY = "mechanics:locations";
    private static final String MECHANIC_AVAILABILITY_KEY = "mechanics:availability:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void updateMechanicLocation(UUID mechanicId, BigDecimal lat, BigDecimal lng) {
        Point point = new Point(lng.doubleValue(), lat.doubleValue());

        redisTemplate.opsForGeo().add(
                MECHANIC_GEO_KEY,
                point,
                mechanicId.toString()
        );

        log.info("Updated location for mechanic: {} at ({}, {})",
                mechanicId, lat, lng);
    }

    @Override
    public List<NearbyMechanic> findNearbyMechanics(BigDecimal lat, BigDecimal lng, Double radiusKm) {
        Point center = new Point(lng.doubleValue(), lat.doubleValue());
        Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);

        Circle area = new Circle(center, radius);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands
                .GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<Object>> results =
                redisTemplate.opsForGeo().radius(MECHANIC_GEO_KEY, area, args);

        if (results == null) {
            return List.of();
        }

        return results.getContent().stream()
                .map(result -> {
                    String mechanicIdStr = result.getContent().getName().toString();
                    UUID mechanicId = UUID.fromString(mechanicIdStr);
                    Point location = result.getContent().getPoint();
                    Double distance = result.getDistance().getValue();

                    Boolean isAvailable = (Boolean) redisTemplate
                            .opsForValue()
                            .get(MECHANIC_AVAILABILITY_KEY + mechanicId);

                    return NearbyMechanic.builder()
                            .mechanicId(mechanicId)
                            .latitude(BigDecimal.valueOf(location.getY()))
                            .longitude(BigDecimal.valueOf(location.getX()))
                            .distance(distance)
                            .isAvailable(isAvailable != null ? isAvailable : false)
                            .build();
                })
                .filter(NearbyMechanic::getIsAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public void setMechanicAvailability(UUID mechanicId, Boolean isAvailable) {
        redisTemplate.opsForValue()
                .set(MECHANIC_AVAILABILITY_KEY + mechanicId, isAvailable);

        log.info("Set mechanic {} availability to: {}", mechanicId, isAvailable);
    }

    @Override
    public MechanicLocation getMechanicLocation(UUID mechanicId) {
        List<Point> positions = redisTemplate.opsForGeo()
                .position(MECHANIC_GEO_KEY, mechanicId.toString());

        if (positions == null || positions.isEmpty() || positions.get(0) == null) {
            return null;
        }

        Point point = positions.get(0);
        Boolean isAvailable = (Boolean) redisTemplate.opsForValue()
                .get(MECHANIC_AVAILABILITY_KEY + mechanicId);

        return MechanicLocation.builder()
                .mechanicId(mechanicId)
                .latitude(BigDecimal.valueOf(point.getY()))
                .longitude(BigDecimal.valueOf(point.getX()))
                .isAvailable(isAvailable != null ? isAvailable : false)
                .lastUpdated(System.currentTimeMillis())
                .build();
    }
}
