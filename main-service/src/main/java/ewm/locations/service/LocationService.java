package ewm.locations.service;

import ewm.locations.Location;
import ewm.locations.LocationDto;

public interface LocationService {
    Location getOrSave(LocationDto dto);
}
