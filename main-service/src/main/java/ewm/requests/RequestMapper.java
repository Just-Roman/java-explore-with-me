package ewm.requests;

import ewm.requests.dto.RequestDto;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public RequestDto modelToDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }

}