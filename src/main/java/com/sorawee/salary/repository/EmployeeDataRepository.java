package com.sorawee.salary.repository;

import com.sorawee.salary.model.EmployeeData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeeDataRepository extends JpaRepository<EmployeeData, Long> , JpaSpecificationExecutor<EmployeeData> {

}
