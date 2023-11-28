package com.example.jdbc.bySpringJdbc;

import com.example.jdbc.dto.UserDto;
import org.springframework.data.repository.CrudRepository;

public interface UserDtoRepository extends CrudRepository<UserDto, Long> {

}
