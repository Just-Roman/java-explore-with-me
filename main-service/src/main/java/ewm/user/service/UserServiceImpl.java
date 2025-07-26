package ewm.user.service;

import ewm.exception.NotFoundException;
import ewm.user.User;
import ewm.user.UserMapper;
import ewm.user.UserRepository;
import ewm.user.dto.UserCreateDto;
import ewm.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto create(UserCreateDto userDto) {
        User user = mapper.createDtoToModel(userDto);
        return mapper.modelToDto(repository.save(user));
    }

    @Override
    public void delete(long userId) {
        if (!repository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        repository.deleteById(userId);
    }

    @Override
    public User getModelById(long userId) {
        return repository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    @Override
    public List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (userIds != null) {
            return repository.findAllByIdIn(userIds, pageable).stream()
                    .map(mapper::modelToDto)
                    .collect(Collectors.toList());
        } else {
            return repository.findAll(pageable).map(mapper::modelToDto).toList();
        }
    }

}
