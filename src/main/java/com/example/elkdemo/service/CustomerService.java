package com.example.elkdemo.service;

import com.example.elkdemo.aspect.ElasticsearchLogger;
import com.example.elkdemo.model.Customer;
import com.example.elkdemo.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CustomerService {

    private final ElasticsearchLogger elasticsearchLogger;

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(ElasticsearchLogger elasticsearchLogger, CustomerRepository customerRepository) {
        this.elasticsearchLogger = elasticsearchLogger;
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        log.info("Fetching customer with id: {}", id);
        return customerRepository.findById(id);
    }

    public Customer createCustomer(Customer customer) {
        log.info("Creating new customer: {}", customer);
        saveElkLog(customer);
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        log.info("Updating customer with id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmail(customerDetails.getEmail());
        customer.setDateOfBirth(customerDetails.getDateOfBirth());
        customer.setAddress(customerDetails.getAddress());

        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }

    private void saveElkLog(Customer customer) {
        elasticsearchLogger.logToElasticsearch("save", "success", customer.getFirstName(), customer.getLastName());
    }
}