package com.global_solution.api.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Entity
@Table(name = "diploma")
public class Diploma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private Date completionDate;
    private String courseName;
    private String nationality;
    private String birthplace;
    private Date birthDate;
    private String rgNumber;
    private Date issueDate;
    private String diplomaTemplate;

}
