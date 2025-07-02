package com.armancodeblock.user_rest_api.repository;

import com.armancodeblock.user_rest_api.enity.AuthUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthUserRepository extends CrudRepository<AuthUser,Long> {
     Optional<AuthUser> findByUsername(String username);
}
