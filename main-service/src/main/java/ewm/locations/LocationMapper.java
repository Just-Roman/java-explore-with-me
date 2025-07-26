package ewm.locations;

import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location locationDtoToModel(LocationDto dto) {
        return Location.builder()
                .lat(dto.getLat())
                .lon(dto.getLon())
                .build();
    }

    public LocationDto modelToDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

}
