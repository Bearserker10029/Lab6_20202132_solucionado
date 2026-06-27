package org.example.facturacion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product", schema = "lab6")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "El nombre no puede estar en blanco")
    private String name;

    @NotNull
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(name = "price", nullable = false)
    private Double price;

    @NotNull
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private Integer stock;

}