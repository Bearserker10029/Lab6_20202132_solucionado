package org.example.facturacion.repository;

import org.example.facturacion.dto.InvoiceDto;
import org.example.facturacion.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    @Query("""
        select distinct i from Invoice i where i.id = :id""")
    Optional<Invoice> findByIdWithRelations(@Param("id") int id);

    @Query("""
        select i.id as id,
               i.type as tipo,
               c.name as nombre,
               i.date as fecha,
               coalesce(sum(d.subtotal), 0) as total
        from Invoice i
        join i.customer c
        left join InvoiceDetail d on d.invoice = i
        group by i.id, i.type, c.name, i.date
        order by i.id
        """)
    List<InvoiceDto> findInvoiceList();
}
