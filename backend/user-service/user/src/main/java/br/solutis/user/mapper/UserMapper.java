package br.solutis.user.mapper;

import br.solutis.user.dto.request.UpdateUserRequest;
import br.solutis.user.dto.request.UserRequest;
import br.solutis.user.entity.User;

public class UserMapper {

    public static User toEntity(UserRequest dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setRole(dto.role());
        return user;
    }

    public static UserRequest toResponse(User user) {
        return new UserRequest(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

}
