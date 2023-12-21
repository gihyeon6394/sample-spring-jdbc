package com.example.jdbc.bySpringJdbc.springDataJpa;

import jakarta.persistence.*;
import lombok.Data;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
@Entity
@Table(name = "User")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;
}


