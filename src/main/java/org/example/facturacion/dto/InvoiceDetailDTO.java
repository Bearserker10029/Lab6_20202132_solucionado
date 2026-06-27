package org.example.facturacion.dto;

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
