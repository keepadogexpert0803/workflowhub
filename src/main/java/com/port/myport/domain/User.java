package com.port.myport.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_USER")
@Data
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "userId")
    private String userId;

    @Column(name = "passwd", nullable = false)
    private String passwd;

    @Column(name = "userName")
    private String userName;

    @Column(name = "role")
    private String role;
}
