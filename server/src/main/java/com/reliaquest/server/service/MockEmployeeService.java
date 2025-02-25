package com.reliaquest.server.service;

import com.reliaquest.server.config.ServerConfiguration;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockEmployeeService {

    private final Faker faker;

    @Getter
    private final List<MockEmployee> mockEmployees;

    public Optional<MockEmployee> findById(@NonNull UUID uuid) {
        log.info("Fetching employee details by id: {}", uuid);
        return mockEmployees.stream()
                .filter(mockEmployee -> Objects.nonNull(mockEmployee.getId())
                        && mockEmployee.getId().equals(uuid))
                .findFirst();
    }

    public MockEmployee create(@NonNull CreateMockEmployeeInput input) {
        final var mockEmployee = MockEmployee.from(
                ServerConfiguration.EMAIL_TEMPLATE.formatted(
                        faker.twitter().userName().toLowerCase()),
                input);
        mockEmployees.add(mockEmployee);
        log.debug("Added employee: {}", mockEmployee);
        return mockEmployee;
    }

    public boolean delete(@NonNull DeleteMockEmployeeInput input) {
        log.info("Deleting employee by name: {}", input.getName());
        final var mockEmployee = mockEmployees.stream()
                .filter(employee -> Objects.nonNull(employee.getName())
                        && employee.getName().equalsIgnoreCase(input.getName()))
                .findFirst();
        if (mockEmployee.isPresent()) {
            mockEmployees.remove(mockEmployee.get());
            log.debug("Removed employee: {}", mockEmployee.get());
            return true;
        }
        return false;
    }

    public List<MockEmployee> getEmployeesByName(String name) {
        String inputName = name + ".*";
        List<String> employeeNames = getMockEmployees().stream().map(MockEmployee::getName).toList();
        Set<String> matchingNames = employeeNames.stream().filter(n -> Pattern.matches(inputName.toLowerCase(), n.toLowerCase())).collect(Collectors.toSet());
        return getMockEmployees().stream().filter(e -> matchingNames.contains(e.getName())).toList();
    }

    public Integer getHighestSalaryOfEmployees() {
        return getMockEmployees().stream().mapToInt(MockEmployee::getSalary).max().orElseThrow(() -> new RuntimeException("No employees found"));
    }

    public List<MockEmployee> getTopTenHighestEarningEmployeeNames() {
        Comparator<MockEmployee> salaryComparator = Comparator.comparingInt(MockEmployee::getSalary).reversed();
        getMockEmployees().sort(salaryComparator);
        return getMockEmployees().stream().limit(10).toList();
    }
}
