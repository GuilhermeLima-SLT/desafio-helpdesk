package br.solutis.user.repository;

import br.solutis.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findById(UUID id);
}
