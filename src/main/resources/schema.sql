-- 게시판 테이블 생성
CREATE TABLE IF NOT EXISTS TB_BOARD (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 자바의 @Id, @GeneratedValue
                          title VARCHAR(255) NOT NULL,           -- 제목
                          content VARCHAR(2000),                 -- 내용
                          author VARCHAR(100)                    -- 작성자
);

CREATE TABLE IF NOT EXISTS TB_USER (
                          userId NVARCHAR(100) PRIMARY KEY, -- 자바의 @Id, @GeneratedValue
                          passwd NVARCHAR(255) NOT NULL,           -- 비밀번호
                          userName VARCHAR(2000),                 -- 이름
                          role NVARCHAR(1)                    -- 권한
);