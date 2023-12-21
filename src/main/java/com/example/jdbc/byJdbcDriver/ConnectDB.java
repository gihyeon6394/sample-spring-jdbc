package com.example.jdbc.byJdbcDriver;

import com.example.jdbc.dto.UserDto;
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
            System.out.println("SQL Error : " + e.getMessage());
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
