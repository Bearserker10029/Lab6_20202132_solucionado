package org.example.lab5_20202132.repository;

import org.example.lab5_20202132.dto.InvoiceDetailDTO;
import org.example.lab5_20202132.model.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Integer> {
    @Query("""
        select distinct i from InvoiceDetail i where i.id = :id""")
    Optional<InvoiceDetail> findByIdWithRelations(@Param("id") int id);

    @Query("""
        select p.id as id,
               p.name as producto,
               p.price as precio,
               p.stock as stock
        from Product p
        order by p.name
        """)
    List<InvoiceDetailDTO> findAvailableProducts();
}
