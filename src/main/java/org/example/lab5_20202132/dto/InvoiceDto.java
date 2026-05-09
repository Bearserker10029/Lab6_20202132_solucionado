package org.example.lab5_20202132.dto;

import java.time.LocalDate;

public interface InvoiceDto {
    int getId();
    String getTipo();
    String getNombre();
    LocalDate getFecha();
    double getTotal();
}