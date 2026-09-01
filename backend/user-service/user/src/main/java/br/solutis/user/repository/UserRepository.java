package br.solutis.user.repository;

import br.solutis.user.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findById(@NonNull UUID id);

    List<User> findByActiveTrue();

    boolean existsByEmail(String email);
}
