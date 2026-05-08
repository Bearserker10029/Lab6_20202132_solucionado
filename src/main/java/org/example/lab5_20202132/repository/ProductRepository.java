package org.example.lab5_20202132.repository;

import org.example.lab5_20202132.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    @Query("""
        select distinct e from Product e where e.id = :id""")
    Optional<Product> findByIdWithRelations(@Param("id") int id);
}
