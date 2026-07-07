package com.port.myport.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_USER") // DB 테이블명은 관례에 따라 TB_USER로 지정
@Data
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "userId") // 2. SQL의 userId와 자바의 userId 연결
    private String userId;

    @Column(name = "passwd", nullable = false)
    private String passwd;

    @Column(name = "userName")
    private String userName;

    @Column(name = "role")
    private String role;

    // 이 role 필드가 나중에 우리가 구현할 'HashSet 권한 체크'의 핵심 데이터가 됩니다!
}