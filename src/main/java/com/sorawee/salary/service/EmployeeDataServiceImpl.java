package com.sorawee.salary.service;

import com.sorawee.salary.model.EmployeeData;
import com.sorawee.salary.repository.EmployeeDataRepository;

import jakarta.persistence.criteria.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class EmployeeDataServiceImpl implements EmployeeDataService {

    private final EmployeeDataRepository repository;

    @Autowired
    public EmployeeDataServiceImpl(EmployeeDataRepository repository) {
        this.repository = repository;
    }

    private static final Set<String> VALID_FIELDS = Set.of(
            "employer", "location", "jobTitle", "yearsAtEmployer",
            "yearsOfExperience", "salary", "salaryAmount", "signingBonus",
            "annualBonus", "annualStockValueBonus", "gender", "additionalComments",
            "createdTimestamp"
    );

    @Override
    public List<Map<String, Object>> doSearch(Map<String, String> conditionParams, List<String> fields, List<String> sortFields, Sort.Direction sortDirection) {

        List<EmployeeData> searchResult = this.findByAttributesAndSort(conditionParams, sortFields, sortDirection);

        return searchResult.stream()
                .map(data -> filterFields(data, fields))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeData> findByAttributes(Map<String, String> conditionParams) {
        return this.findByAttributesAndSort(conditionParams, null, null);
    }

    private List<EmployeeData> findByAttributesAndSort(Map<String, String> conditionParams, List<String> sortFields, Sort.Direction sortDirection) {

        String jobTitle = "";
        BigDecimal salaryFrom = null;
        BigDecimal salaryTo = null;
        String gender = "";

        for (String key : conditionParams.keySet()) {
            String value = conditionParams.get(key).trim();

            if (!isValidAttribute(key)) {
                throw new IllegalArgumentException("Invalid attribute for filtering: " + key);
            }
            switch (key.toLowerCase()) {
                case "jobtitle":
                    jobTitle = value;
                    break;
                case "salaryfrom":
                    try {
                        salaryFrom = BigDecimal.valueOf(Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid salaryFrom value: " + value);
                    }
                    break;
                case "salaryto":
                    try {
                        salaryTo = BigDecimal.valueOf(Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid salaryTo value: " + value);
                    }
                    break;
                case "gender":
                    gender = value;
                    break;
                default:
                    // Ignore
            }
        }

        if (jobTitle.isEmpty() && salaryFrom == null && salaryTo == null && gender.isEmpty()) {
            return getSorted(sortFields, sortDirection);
        }

        return findByCriteria(jobTitle, salaryFrom, salaryTo, gender, sortFields, sortDirection);

    }

    private List<EmployeeData> findByCriteria(String jobTitle, BigDecimal salaryFrom, BigDecimal salaryTo, String gender, List<String> sortFields, Sort.Direction sortDirection) {
        Specification<EmployeeData> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (StringUtils.hasText(jobTitle)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("jobTitle")), "%" + jobTitle.toLowerCase() + "%"));
            }
            if (salaryFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("salaryAmount"), salaryFrom));
            }
            if (salaryTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("salaryAmount"), salaryTo));
            }
            if (StringUtils.hasText(gender)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("gender")), gender.toLowerCase()));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        if (CollectionUtils.isEmpty(sortFields) || sortDirection == null) {
            // If no sorting is required
            return repository.findAll(spec);
        } else {
            Sort sort = Sort.by(sortDirection, sortFields.toArray(new String[0]));
            return repository.findAll(spec, sort);
        }
    }


    @Override
    public List<Map<String, Object>> getFiltered(List<String> fields) {

        if (!areFieldsValid(fields)) {
            throw new IllegalArgumentException("Invalid field(s) provided");
        }

        List<EmployeeData> employeeDataList = repository.findAll();
        return employeeDataList.stream()
                .map(data -> filterFields(data, fields))
                .collect(Collectors.toList());
    }


    public List<EmployeeData> getSorted(List<String> sortFields, Sort.Direction sortDirection) {
        if (!areFieldsValid(sortFields)) {
            throw new IllegalArgumentException("Invalid field(s) provided");
        }

        List<EmployeeData> employeeDataList;

        if (!sortFields.isEmpty()) {
            Sort sort = Sort.by(sortDirection, sortFields.toArray(String[]::new));
            employeeDataList = repository.findAll(sort);
        } else {
            employeeDataList = repository.findAll();
        }

        return employeeDataList;
    }


    private Map<String, Object> filterFields(EmployeeData data, List<String> fields) {

        if (fields == null || fields.isEmpty()) {

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("employer", data.getEmployer());
            resultData.put("location", data.getLocation());
            resultData.put("jobTitle", data.getJobTitle());
            resultData.put("yearsAtEmployer", data.getYearsAtEmployer());
            resultData.put("yearsOfExperience", data.getYearsOfExperience());
            resultData.put("salary", data.getSalary());
            resultData.put("salaryAmount", data.getSalaryAmount());
            resultData.put("signingBonus", data.getSigningBonus());
            resultData.put("annualBonus", data.getAnnualBonus());
            resultData.put("annualStockValueBonus", data.getAnnualStockValueBonus());
            resultData.put("gender", data.getGender());
            resultData.put("additionalComments", data.getAdditionalComments());
            resultData.put("createdTimestamp", data.getTimestamp());
            return resultData;
        } else {
            Map<String, Object> filteredData = new HashMap<>();
            if (fields.contains("employer")) {
                filteredData.put("employer", data.getEmployer());
            }
            if (fields.contains("location")) {
                filteredData.put("location", data.getLocation());
            }
            if (fields.contains("jobTitle")) {
                filteredData.put("jobTitle", data.getJobTitle());
            }
            if (fields.contains("yearsAtEmployer")) {
                filteredData.put("yearsAtEmployer", data.getYearsAtEmployer());
            }
            if (fields.contains("yearsOfExperience")) {
                filteredData.put("yearsOfExperience", data.getYearsOfExperience());
            }
            if (fields.contains("salary")) {
                filteredData.put("salary", data.getSalary());
            }
            if (fields.contains("salaryAmount")) {
                filteredData.put("salaryAmount", data.getSalaryAmount());
            }
            if (fields.contains("signingBonus")) {
                filteredData.put("signingBonus", data.getSigningBonus());
            }
            if (fields.contains("annualBonus")) {
                filteredData.put("annualBonus", data.getAnnualBonus());
            }
            if (fields.contains("annualStockValueBonus")) {
                filteredData.put("annualStockValueBonus", data.getAnnualStockValueBonus());
            }
            if (fields.contains("additionalComments")) {
                filteredData.put("additionalComments", data.getAdditionalComments());
            }
            if (fields.contains("createdTimestamp")) {
                filteredData.put("createdTimestamp", data.getTimestamp());
            }
            if (fields.contains("gender")) {
                filteredData.put("gender", data.getGender());
            }
            return filteredData;
        }
    }


    private boolean isValidAttribute(String attribute) {
        return attribute.equalsIgnoreCase("jobTitle") || attribute.equalsIgnoreCase("salaryFrom") || attribute.equalsIgnoreCase("salaryTo") || attribute.equalsIgnoreCase("gender");
    }

    private boolean areFieldsValid(List<String> fields) {
        return fields.stream().allMatch(VALID_FIELDS::contains);
    }

}
