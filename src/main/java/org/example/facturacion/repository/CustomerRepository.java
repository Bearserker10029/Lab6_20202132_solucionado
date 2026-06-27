package org.example.facturacion.repository;

import org.example.facturacion.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    @Query("""
        select distinct e from Customer e where e.id = :id""")
    Optional<Customer> findByIdWithRelations(@Param("id") int id);
}
