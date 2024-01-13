package com.sorawee.salary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorawee.salary.controller.EmployeeDataController;
import com.sorawee.salary.service.EmployeeDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeDataController.class)
class EmployeeDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeDataService employeeDataService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new EmployeeDataController(employeeDataService)).build();
    }

    @Test
    void search_withSpecificParameters_returnsOkAndValidResponse() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("fields", "jobTitle,salary,salaryAmount,gender");
        params.add("sort", "jobTitle,salary");
        params.add("sortDirection", "DESC");
        params.add("jobTitle", "engineer");
        params.add("salaryFrom", "6000");
        params.add("salaryTo", "6500");
        params.add("gender", "Male");

        when(employeeDataService.doSearch(anyMap(), anyList(), anyList(), any(Sort.Direction.class)))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/employees/search")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());

        verify(employeeDataService, times(1))
                .doSearch(anyMap(), anyList(), anyList(), eq(Sort.Direction.DESC));
    }
}