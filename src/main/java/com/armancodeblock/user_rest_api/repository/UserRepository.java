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

Page<User> findByNameStartingWith(String prefix,Pageable pageable);
//JPQL Query
@Query("SELECT u FROM User u WHERE u.name LIKE :prefix%")
List<User> findUserByNamePrefix(@Param("prefix") String prefix);

}


//Pagination and Sorting
//Pagination : It is a way to divide the large data into smaller chunks(pages).
// It improves the performance of the application by reducing the amount of data that needs to be loaded at once.
// also improves the user experience by allowing users to navigate through the data in a more manageable way.
// Pageable Interface: It is used to define the pagination and sorting parameters for a query.(page number, page size, sorting order).
//Page<T> contains a page of data and additional information about the pagination, such as total number of pages, total number of elements, etc.
//SLice<T> is a sublist of the data, it does not contain the pagination information.