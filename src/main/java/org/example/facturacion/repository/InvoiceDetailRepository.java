package org.example.facturacion.repository;

import org.example.facturacion.dto.InvoiceDetailDTO;
import org.example.facturacion.model.InvoiceDetail;
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

    List<InvoiceDetail> findByInvoiceId(Integer invoiceId);
}
