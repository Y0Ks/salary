package com.sorawee.salary.service;

import com.sorawee.salary.model.EmployeeData;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public interface EmployeeDataService {

    List<EmployeeData> findByAttributes(Map<String, String> conditionParams);

    List<Map<String, Object>> getFiltered(List<String> fields);

    List<EmployeeData> getSorted(List<String> sortFields, Sort.Direction sortDirection);

    List<Map<String, Object>> doSearch(Map<String, String> conditionParams, List<String> fieldList, List<String> sortFields, Sort.Direction sortDirection);
}
