package com.example.jdbc.withSpringDataJpa;


import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository  extends JpaRepository<UserEntity, Long> {
}
