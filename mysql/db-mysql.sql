CREATE DATABASE IF NOT EXISTS diplomas;
USE diplomas;

CREATE TABLE IF NOT EXISTS diploma
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    student_name     VARCHAR(255) NULL,
    completion_date  date NULL,
    course_name      VARCHAR(255) NULL,
    nationality      VARCHAR(255) NULL,
    birthplace       VARCHAR(255) NULL,
    birth_date       date NULL,
    rg_number        VARCHAR(255) NULL,
    issue_date       date NULL,
    diploma_template VARCHAR(255) NULL,
    CONSTRAINT pk_diploma PRIMARY KEY (id)
);