package org.example.lab5_20202132.dto;

import java.time.LocalDate;

public interface InvoiceDetailDTO {
    int getId();
    String getTipo();
    String getNombre();
    LocalDate getFecha();
    int getStock();
    String getProducto();
    double getPrecio();
    int getCantidad();
}
