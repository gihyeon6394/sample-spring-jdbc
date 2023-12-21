package com.example.jdbc.bySpringJdbc;

import com.example.jdbc.dto.UserDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // 스프링 빈으로 등록
public class ConnectBySpringDataJdbc {

    private JdbcTemplate jdbcTemplate; // bean

    // Spring이 ConnectBySpringDataJdbc 생성 시 JdbcTemplate을 주입
    public ConnectBySpringDataJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserDto> getUserList() {
        return jdbcTemplate.query("select id, name from user"
                , (rs, rowNum) -> new UserDto(rs.getLong("id"), rs.getString("name")));
    }

}
