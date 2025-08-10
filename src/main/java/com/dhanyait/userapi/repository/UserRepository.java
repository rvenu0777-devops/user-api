package com.dhanyait.userapi.repository;


import com.dhanyait.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName")
    Optional<User> findByFullName(@Param("firstName") String firstName,
                                  @Param("lastName") String lastName);
}