package org.example.facturacion.repository;

import org.example.facturacion.model.Product;
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
