package com.sorawee.salary.controller;

import com.sorawee.salary.model.EmployeeData;
import com.sorawee.salary.dto.ErrorResponse;
import com.sorawee.salary.service.EmployeeDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/employees")
public class EmployeeDataController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeDataController.class);
    private final EmployeeDataService employeeDataService;

    @Autowired // Dependency Injection
    public EmployeeDataController(EmployeeDataService employeeDataService) {
        this.employeeDataService = employeeDataService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam Map<String, String> allParams) {
        try {
            String fields = "";
            List<String> sort = new ArrayList<>();
            Sort.Direction sortDirection = Sort.Direction.ASC; // Default value
            Map<String, String> conditionParams = new HashMap<>();

            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                switch (entry.getKey()) {
                    case "fields":
                        fields = entry.getValue();
                        break;
                    case "sort":
                        sort = Arrays.asList(entry.getValue().split(","));
                        break;
                    case "sortDirection":
                        sortDirection = Sort.Direction.fromString(entry.getValue());
                        break;
                    default:
                        conditionParams.put(entry.getKey(), entry.getValue());
                }
            }
            List<String> fieldList = null;
            if (!fields.isEmpty()) {
                fieldList = Arrays.asList(fields.split(","));
            }

            if (sort.isEmpty()) {
                sort = Collections.emptyList();
            }

            return ResponseEntity.ok(employeeDataService.doSearch(conditionParams, fieldList, sort, sortDirection));

        } catch (Exception exception) {
            logger.error(exception.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
        }

    }


    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestParam Map<String, String> conditionParams) {
        try {
            return ResponseEntity.ok(employeeDataService.findByAttributes(conditionParams));
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
        }
    }

    @GetMapping("/fields")
    public ResponseEntity<?> fields(@RequestParam String fields) {
        try {
            List<String> fieldList = Arrays.asList(fields.split(","));
            return ResponseEntity.ok(employeeDataService.getFiltered(fieldList));
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
        }
    }


    @GetMapping("/sorted")
    public ResponseEntity<?> getSortedEmployeeDatasorted(
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortDirection) {
        try {
            if (sort == null) {
                sort = Collections.emptyList();
            }
            List<EmployeeData> result = employeeDataService.getSorted(sort, sortDirection);
            return ResponseEntity.ok(result);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
        }
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<ErrorResponse> methodNotAllowed() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("Method not allowed"));
    }

}
