package com.rookies6.myspringboot4project.repository;

import com.rookies6.myspringboot4project.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    @Test
    void testCreate() {
        //Given(준비단계)
        Customer customer = new Customer();
        customer.setCustomerId("A004");
        customer.setCustomerName("둘리");
        //When(실행단계)
        Customer addCustomer = customerRepository.save(customer);
        //Then(검증단계)
        assertThat(addCustomer).isNotNull();
        assertThat(addCustomer.getCustomerName()).isEqualTo("둘리");
    }
}