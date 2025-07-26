package ewm.user;

import ewm.user.dto.UserCreateDto;
import ewm.user.dto.UserDto;
import ewm.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserCreateDto userDto) {
        return userService.create(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
                                  @RequestParam(value = "from", defaultValue = "0") Integer from,
                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return userService.getUsers(ids, from, size);
    }

}
