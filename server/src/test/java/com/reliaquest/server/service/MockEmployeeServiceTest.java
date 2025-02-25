package com.reliaquest.server.service;

import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import net.datafaker.Faker;
import net.datafaker.providers.base.Twitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockEmployeeServiceTest {

    @Mock
    private Faker faker;

    @Mock
    private Twitter twitter;

    @InjectMocks
    private MockEmployeeService mockEmployeeService;

    private MockEmployee mockEmployee1;
    private MockEmployee mockEmployee2;
    private List<MockEmployee> mockEmployeeList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockEmployee1 = new MockEmployee(UUID.randomUUID(), "John Doe", 10000,34, "SE", "abc@gmail.com");
        mockEmployee2 = new MockEmployee(UUID.randomUUID(), "Johny Deb", 15000,32, "SSE", "def@gmail.com");

        mockEmployeeList = new ArrayList<>();
        mockEmployeeList.add(mockEmployee1);
        mockEmployeeList.add(mockEmployee2);

        mockEmployeeService = new MockEmployeeService(faker, mockEmployeeList);
    }

    @Test
    void testFindById() {
        UUID uuid = mockEmployee1.getId();
        Optional<MockEmployee> result = mockEmployeeService.findById(uuid);
        assertTrue(result.isPresent());
        assertEquals(mockEmployee1, result.get());
    }

    @Test
    void testCreate() {
        CreateMockEmployeeInput input = new CreateMockEmployeeInput();
        input.setName("Alice");
        input.setSalary(12000);
        when(faker.twitter()).thenReturn(twitter);
        when(twitter.userName()).thenReturn("alice_twitter");
        MockEmployee result = mockEmployeeService.create(input);
        assertNotNull(result);
        assertEquals("alice_twitter@company.com", result.getEmail());
        assertEquals(input.getName(), result.getName());
        assertEquals(input.getSalary(), result.getSalary());
    }

    @Test
    void testDelete() {
        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput();
        input.setName("John Doe");
        boolean result = mockEmployeeService.delete(input);
        assertTrue(result);
        assertEquals(1, mockEmployeeList.size());
        assertFalse(mockEmployeeList.contains(mockEmployee1));
    }

    @Test
    void testDeleteEmployeeNotFound() {
        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput();
        input.setName("Nonexistent Employee");
        boolean result = mockEmployeeService.delete(input);
        assertFalse(result);
        assertEquals(2, mockEmployeeList.size());
    }

    @Test
    void testGetEmployeesByName() {
        List<MockEmployee> result = mockEmployeeService.getEmployeesByName("John");
        assertEquals(2, result.size());
    }

    @Test
    void testGetHighestSalaryOfEmployees() {
        int result = mockEmployeeService.getHighestSalaryOfEmployees();
        assertEquals(15000, result);
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        List<MockEmployee> result = mockEmployeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(2, result.size());
        assertEquals(mockEmployee2, result.get(0));
        assertEquals(mockEmployee1, result.get(1));
    }
}
