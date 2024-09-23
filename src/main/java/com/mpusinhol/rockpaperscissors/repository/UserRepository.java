package com.mpusinhol.rockpaperscissors.repository;

import com.mpusinhol.rockpaperscissors.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
