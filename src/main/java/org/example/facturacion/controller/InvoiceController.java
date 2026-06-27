package org.example.facturacion.controller;

import jakarta.validation.Valid;
import org.example.facturacion.model.Customer;
import org.example.facturacion.model.Invoice;
import org.example.facturacion.model.InvoiceDetail;
import org.example.facturacion.model.Product;
import org.example.facturacion.repository.CustomerRepository;
import org.example.facturacion.repository.InvoiceDetailRepository;
import org.example.facturacion.repository.InvoiceRepository;
import org.example.facturacion.repository.ProductRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/comprobante")
public class InvoiceController {
    final InvoiceRepository invoiceRepository;
    final InvoiceDetailRepository invoiceDetailRepository;
    final CustomerRepository customerRepository;
    final ProductRepository productRepository;
    final MessageSource messageSource;

    public InvoiceController(InvoiceRepository invoiceRepository,
                             InvoiceDetailRepository invoiceDetailRepository,
                             CustomerRepository customerRepository,
                             ProductRepository productRepository,
                             MessageSource messageSource) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String listarComprobantes(Model model) {
        model.addAttribute("invoiceList", invoiceRepository.findInvoiceList());
        return "invoice/list";
    }

    @GetMapping("/new")
    public String nuevoComprobanteFrm(Model model) {
        model.addAttribute("invoice", new Invoice());
        model.addAttribute("customerList", customerRepository.findAll());
        model.addAttribute("invoiceDetailList", invoiceDetailRepository.findAvailableProducts());
        return "invoice/form";
    }

    @PostMapping("/save")
    public String guardarComprobante(@ModelAttribute("invoice") @Valid Invoice invoice,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     Model model,
                                     @RequestParam(value = "productIds", required = false)
                                     List<Integer> productIds,
                                     @RequestParam(value = "quantities", required = false)
                                     List<Integer> quantities) {
        Locale locale = Locale.getDefault();
        List<InvoiceDetail> detalles = new ArrayList<>();

        if (invoice.getType() == null || invoice.getType().isBlank()) {
            bindingResult.rejectValue("type", "invoice.type.required","Debe seleccionar el tipo de comprobante");
        }

        if (invoice.getCustomer() == null || invoice.getCustomer().getId() == null) {
            bindingResult.rejectValue("customer", "invoice.customer.required","Debe seleccionar un cliente");
        }

        if (invoice.getDate() != null && invoice.getDate().isAfter(LocalDate.now())) {
            bindingResult.rejectValue("date", "invoice.date.future","La fecha no puede ser futura");
        }

        if (productIds == null || quantities == null || productIds.isEmpty()) {
            bindingResult.reject("invoice.product.required","Debe seleccionar al menos un producto");
        }

        boolean tieneCantidad = false;
        boolean tieneRepetidos = false;
        List<Integer> productosVistos = new ArrayList<>();

        if (productIds != null && quantities != null) {
            for (int i = 0; i < productIds.size(); i++) {
                Integer productId = productIds.get(i);
                Integer cantidad = quantities.size() > i ? quantities.get(i) : null;
                if (productId == null) {
                    continue;
                }
                if (productosVistos.contains(productId)) {
                    tieneRepetidos = true;
                } else {
                    productosVistos.add(productId);
                }
                if (cantidad != null && cantidad > 0) {
                    tieneCantidad = true;
                }
            }
        }

        if (!tieneCantidad) {
            bindingResult.reject("invoice.product.quantity","Debe ingresar cantidad por lo menos para un producto");
        }

        if (tieneRepetidos) {
            bindingResult.reject("invoice.product.duplicate","No se puede repetir el mismo producto en un comprobante");
        }

        if (productIds != null && quantities != null) {
            for (int i = 0; i < productIds.size(); i++) {
                Integer productId = productIds.get(i);
                Integer cantidad = quantities.size() > i ? quantities.get(i) : null;
                if (productId == null || cantidad == null || cantidad <= 0) {
                    continue;
                }

                Optional<Product> optProduct = productRepository.findById(productId);
                if (optProduct.isEmpty()) {
                    bindingResult.reject("invoice.product.invalid","Producto inválido");
                    continue;
                }

                Product product = optProduct.get();
                if (product.getStock() < cantidad) {
                    bindingResult.reject("invoice.product.stock", "No se puede exceder el stock disponible");
                    continue;
                }

                InvoiceDetail detail = new InvoiceDetail();
                detail.setInvoice(invoice);
                detail.setProduct(product);
                detail.setQuantity(cantidad);
                detail.setPrice(product.getPrice());
                detail.setSubtotal(product.getPrice() * cantidad);
                detalles.add(detail);
            }
        }

        if (invoice.getCustomer() != null && invoice.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(invoice.getCustomer().getId()).orElse(null);
            if (customer == null) {
                bindingResult.rejectValue("customer", "invoice.customer.invalid", "Cliente inválido");
            } else if ("FACTURA".equalsIgnoreCase(invoice.getType()) &&
                !"RUC".equalsIgnoreCase(customer.getDocumentType())) {
                bindingResult.rejectValue("type", "invoice.type.factura", "Factura solo permite clientes con RUC");
            } else if ("BOLETA".equalsIgnoreCase(invoice.getType()) &&
                !"DNI".equalsIgnoreCase(customer.getDocumentType())) {
                bindingResult.rejectValue("type", "invoice.type.boleta", "Boleta solo permite clientes con DNI");
            }
            invoice.setCustomer(customer);
        }

        if (detalles.isEmpty()) {
            bindingResult.reject("invoice.product.required", "Debe seleccionar al menos un producto");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("customerList", customerRepository.findAll());
            model.addAttribute("invoiceDetailList", invoiceDetailRepository.findAvailableProducts());
            return "invoice/form";
        }

        invoiceRepository.save(invoice);
        for (InvoiceDetail detail : detalles) {
            invoiceDetailRepository.save(detail);
            Product product = detail.getProduct();
            product.setStock(product.getStock() - detail.getQuantity());
            productRepository.save(product);
        }

        redirectAttributes.addFlashAttribute("message", "Se ha registrado correctamente");
        return "redirect:/comprobante";
    }

    @GetMapping("/delete")
    public String borrarComprobante(@RequestParam("id") int id, RedirectAttributes attr) {
        Optional<Invoice> optInvoice = invoiceRepository.findById(id);

        if (optInvoice.isPresent()) {
            // Borrar primero los detalles del comprobante
            List<InvoiceDetail> detalles = invoiceDetailRepository.findByInvoiceId(id);
            invoiceDetailRepository.deleteAll(detalles);
            invoiceRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Comprobante borrado exitosamente");
        }
        return "redirect:/comprobante";
    }
}
