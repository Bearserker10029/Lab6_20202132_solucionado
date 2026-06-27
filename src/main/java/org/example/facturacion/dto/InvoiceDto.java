package org.example.facturacion.dto;

import java.time.LocalDate;

public interface InvoiceDto {
    int getId();
    String getTipo();
    String getNombre();
    LocalDate getFecha();
    double getTotal();
}