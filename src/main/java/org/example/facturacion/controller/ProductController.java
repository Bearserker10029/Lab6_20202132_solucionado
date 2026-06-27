package org.example.facturacion.controller;

import jakarta.validation.Valid;
import org.example.facturacion.model.Product;
import org.example.facturacion.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class ProductController {
    final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping(value = {"/producto"})
    public String listarProductos(Model model) {
        model.addAttribute("productList", productRepository.findAll());
        return "product/list";
    }

    @GetMapping("/producto/new")
    public String nuevoProductoFrm(Model model) {
        model.addAttribute("product", new Product());
        return "product/form";
    }

    @PostMapping("/producto/save")
    public String guardarProducto(@ModelAttribute("product") @Valid Product product,
                                  BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            return "product/form";
        }
        Product productoExiste = null;
        if (product.getId() != null && product.getId() > 0) {
            productoExiste = productRepository.findById(product.getId()).orElse(null);
        }
        try {
            if (productoExiste != null) {
                productoExiste.setName(product.getName());
                productoExiste.setPrice(product.getPrice());
                productoExiste.setStock(product.getStock());
                productRepository.save(productoExiste);
            } else {
                productRepository.save(product);
            }
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("name", "name.duplicate", "Este nombre de product ya está registrado");
            model.addAttribute("product", product);
            return "product/form";
        }
        redirectAttributes.addFlashAttribute("message", "Se ha registrado correctamente");

        return "redirect:/producto";
    }

    @GetMapping("/producto/edit/{id}")
    public String editarProducto(Model model, @PathVariable int id) {
        Optional<Product> optProduct = productRepository.findByIdWithRelations(id);

        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            model.addAttribute("product", product);
            return "product/form";
        } else {
            return "redirect:/producto";
        }
    }

    @GetMapping("/producto/delete")
    public String borrarProducto(@RequestParam("id") int id, RedirectAttributes attr) {
        Optional<Product> optProduct = productRepository.findById(id);

        if (optProduct.isPresent()) {
            try {
                productRepository.deleteById(id);
                attr.addFlashAttribute("msg", "Producto borrado exitosamente");
            } catch (DataIntegrityViolationException e) {
                attr.addFlashAttribute("msg",
                        "No se puede borrar el producto porque está usado en comprobantes");
            }
        }
        return "redirect:/producto";
    }
}