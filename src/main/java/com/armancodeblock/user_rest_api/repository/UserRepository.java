package com.armancodeblock.user_rest_api.repository;

import com.armancodeblock.user_rest_api.enity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
//Derived Methods

//find mean get or retrieve
//By , separate the field name
public interface UserRepository extends JpaRepository<User,Long> {
List<User> findByNameStartingWith(String prefix);
//JPQL Query
@Query("SELECT u FROM User u WHERE u.name LIKE :prefix%")
List<User> findUserByNamePrefix(@Param("prefix") String prefix);

}
