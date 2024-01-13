package com.sorawee.salary.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "employee_data")
public class EmployeeData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "created_timestamp")
    private String timestamp;

    @Column(name = "employer")
    private String employer;

    @Column(name = "location")
    private String location;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "years_at_employer")
    private String yearsAtEmployer;

    @Column(name = "years_of_experience")
    private String yearsOfExperience;

    @Column(name = "salary")
    private String salary;

    @Column(name = "salary_amount")
    private BigDecimal salaryAmount;

    @Column(name = "signing_bonus")
    private String signingBonus;

    @Column(name = "annual_bonus")
    private String annualBonus;

    @Column(name = "annual_stock_value_bonus")
    private String annualStockValueBonus;

    private String gender;

    @Column(name = "additional_comments")
    private String additionalComments;

}