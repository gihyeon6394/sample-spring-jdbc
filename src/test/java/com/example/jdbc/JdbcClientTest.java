package com.example.jdbc;

import com.example.jdbc.dto.UserDto;
import com.example.jdbc.onlyJdbcDriver.ConnectDB;
import com.example.jdbc.withSpringDataJpa.UserEntity;
import com.example.jdbc.withSpringDataJpa.UserEntityRepository;
import com.example.jdbc.withSpringJdbc.ConnectBySpringDataJdbc;
import com.example.jdbc.withSpringJdbc.UserDtoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
public class JdbcClientTest {

    @Autowired
    private ConnectDB ConnectDB; // JDBC Driver

    @Autowired
    private ConnectBySpringDataJdbc connectBySpringDataJdbc; // Spring Data JDBC

    @Autowired
    private UserDtoRepository userDtoRepository; // Spring Data JDBC + CrudRepository

    @Autowired
    private UserEntityRepository userRepository; // Spring Data JPA + JpaRepository


    @Test
    @DisplayName("JDBC Driver")
    void db_connenct_only_jdbc_driver() throws SQLException, ClassNotFoundException {

        List<UserDto> userDtoList = ConnectDB.getUserList();

        userDtoList.stream().forEach(System.out::println);

        assertThat(userDtoList.size(), is(notNullValue()));
        assertThat(CollectionUtils.isEmpty(userDtoList), is(false));
    }

    @Test
    @DisplayName("Spring Data JDBC")
    void db_connenct_with_spring_jdbc() {

        List<UserDto> userDtoList = connectBySpringDataJdbc.getUserList();

        userDtoList.stream().forEach(System.out::println);

        assertThat(userDtoList.size(), is(notNullValue()));
        assertThat(CollectionUtils.isEmpty(userDtoList), is(false));
    }


    @Test
    @DisplayName("Spring Data JDBC + CrudRepository")
    @Transactional
    void db_connenct_with_spring_jdbc_crud_repository() {

        List<UserDto> userDtoList = (List<UserDto>) userDtoRepository.findAll();

        userDtoList.stream().forEach(System.out::println);

        assertThat(userDtoList.size(), is(notNullValue()));
        assertThat(CollectionUtils.isEmpty(userDtoList), is(false));
    }

    @Test
    @DisplayName("Spring Data JPA + JpaRepository")
    void db_connenct_with_spring_jpa_jpa_repository() {

        List<UserEntity> userEntityList = userRepository.findAll();

        userEntityList.stream().forEach(System.out::println);

        assertThat(userEntityList.size(), is(notNullValue()));
        assertThat(CollectionUtils.isEmpty(userEntityList), is(false));
    }

}
