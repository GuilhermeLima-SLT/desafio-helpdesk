package br.solutis.user.mapper;

import br.solutis.user.dto.request.UserRequest;
import br.solutis.user.dto.response.UserResponse;
import br.solutis.user.entity.User;
import jakarta.validation.Valid;

public class UserMapper {

    public static User toEntity(@Valid UserRequest dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setRole(dto.role());
        return user;
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt()
        );
    }

}
