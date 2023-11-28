#### 개발환경

- Java 17
- Spring Bot 3.2
- Gradle 8.4
- MySQL 8.0.28
- lombok
- Spring Data JDBC 3.2.0
- Spring Data JPA 3.2.0

## Java Web Application에서 DB 접근 방법

1. JDBC driver
2. Spring Data JDBC `org.springframework.jdbc.core.JdbcTemplate`
3. Spring Data JDBC`CRUDRepository`
4. Spring Data JPA`JpaRepository`

### 1. JDBC driver

````bash
dependencies {
    ...
    
    runtimeOnly 'com.mysql:mysql-connector-j'
}
````

<details>
<summary>Java 예제</summary>

```java
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ConnectDB {


    public List<UserDto> getUserList() throws ClassNotFoundException, SQLException {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<UserDto> userDtoList = new ArrayList<>();

        try {
            // JDBC 드라이버 로딩
            Class.forName("com.mysql.cj.jdbc.Driver");
            // DB 연결 (가장 느린 부분, DB-WAS network delay)
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/example_jdbc?serverTimezone=Asia/Seoul", "root", "root");
            // PreparedStatement 생성
            statement = connection.prepareStatement("select id, name from user");
            // 쿼리 실행
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                UserDto userDto = new UserDto(id, name);
                userDtoList.add(userDto);
            }

        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로딩 실패");
            throw e;
        } catch (SQLException e) {
            System.out.println("SQL Error : "e.getMessage());
            throw e;
        } finally {
            // 자원 반납
            try {
                resultSet.close();
            } catch (Exception e) {
            }
            try {
                statement.close();
            } catch (Exception e) {
            }
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
        return userDtoList;
    }


}

````

</details>

#### 2. Spring Data JDBC `org.springframework.jdbc.core.JdbcTemplate`

````bash
dependencies {
    ...
    runtimeOnly 'com.mysql:mysql-connector-j'
    implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
}
````

```yaml
spring:
  datasource: # JDBC DB 접속 정보
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/example_jdbc?serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: root
```

<details>

<summary>Java 예제</summary>

```java
import com.example.jdbc.dto.UserDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // 스프링 빈으로 등록
public class ConnectBySpringDataJdbc {

    private JdbcTemplate jdbcTemplate;

    // Spring이 UserRepository 생성 시 JdbcTemplate을 주입
    public ConnectBySpringDataJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserDto> getUserList() {
        return jdbcTemplate.query("select id, name from user"
                , (rs, rowNum) -> new UserDto(rs.getLong("id"), rs.getString("name")));
    }

}
````

</details>

### 3. Spring Data JDBC`org.springframework.data.repository.CrudRepository`

````bash
dependencies {
    ...
    runtimeOnly 'com.mysql:mysql-connector-j'
    implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
}
````

<details>

<summary>Java 예제</summary>

```java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table("User")
public class UserDto {

    private long id;

    private String name;
}
```

```java
import com.example.jdbc.dto.UserDto;
import org.springframework.data.repository.CrudRepository;

public interface UserDtoRepository extends CrudRepository<UserDto, Long> {

}
```

```java

import com.example.jdbc.bySpringJdbc.UserDtoRepository;
import com.example.jdbc.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
public class JdbcClientTest {

    @Autowired
    private UserDtoRepository userDtoRepository;

    @Test
    @DisplayName("Spring Data JDBCCrudRepository")
    @Transactional
    void db_connenct_with_spring_jdbc_crud_repository() {

        List<UserDto> userDtoList = (List<UserDto>) userDtoRepository.findAll();

        userDtoList.stream().forEach(System.out::println);

        assertThat(userDtoList.size(), is(notNullValue()));
        assertThat(CollectionUtils.isEmpty(userDtoList), is(false));
    }

}

```

</details>

### 4. Spring Data JPA`org.springframework.data.jpa.repository.JpaRepository`

````bash
dependencies {
    ...
    runtimeOnly 'com.mysql:mysql-connector-j'
    implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
    
    annotationProcessor "jakarta.annotation:jakarta.annotation-api"
    annotationProcessor "jakarta.persistence:jakarta.persistence-api"

    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
}
````

<details>

<summary>Java 예제</summary>

```java
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "User")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;
}
```

```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
}
```

```java
import com.example.jdbc.bySpringJdbc.springDataJpa.UserEntity;
import com.example.jdbc.bySpringJdbc.springDataJpa.UserEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
public class JdbcClientTest {

    @Autowired
    private UserEntityRepository userRepository; // Spring Data JPAJpaRepository

    @Test
    @DisplayName("Spring Data JPAJpaRepository")
    void db_connenct_with_spring_jpa_jpa_repository() {

        List<UserEntity> userEntityList = userRepository.findAll();

        userEntityList.stream().forEach(System.out::println);

        assertThat(userEntityList.size(), is(notNullValue()));
        assertThat(CollectionUtils.isEmpty(userEntityList), is(false));
    }

}

```

</details>
