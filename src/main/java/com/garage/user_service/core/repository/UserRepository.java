package com.garage.user_service.core.repository;

import com.garage.user_service.core.model.User;
import com.garage.user_service.core.type.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	List<User> findAllByRole(Role role);

	List<User> findAllByRoleIn(List<Role> roles);

}
