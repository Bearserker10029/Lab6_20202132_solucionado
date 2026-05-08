package org.example.lab5_20202132.controller;

import jakarta.validation.Valid;
import org.example.lab5_20202132.model.Customer;
import org.example.lab5_20202132.repository.CustomerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class CustomerController {
    final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping(value = "")
    public String displayCustomers(Model model) {
        return "title";
    }

    @GetMapping(value = {"/cliente"})
    public String listarEmpleados(Model model) {

        model.addAttribute("employeeList", customerRepository.findAll());

        return "customer/list";
    }

    @GetMapping("/new")
    public String nuevoEmpleadoFrm(Model model) {
        model.addAttribute("employee", new Customer());
        return "customer/form";
    }

    @PostMapping("/save")
    public String guardarNuevoEmpleado(@ModelAttribute("employee") @Valid Customer customer,
                                       BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("employee", customer);
            return "customer/form";
        }
        if (customer.getDocumentType().equals("DNI")){
            if(customer.getDocument().length()!=8){
                bindingResult.rejectValue("documentType", "documentType.documentType", "El documento debe ser igual a 8 caracteres");
            }
        } else if(customer.getDocumentType().equals("RUC")){
            if(customer.getDocument().length()!=11){
                bindingResult.rejectValue("documentType", "documentType.documentType", "El documento debe ser igual a 11 caracteres");
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("employee", customer);
            return "customer/form";
        }
        Customer customerexiste = null;
        if(customer.getId() != null && customer.getId() > 0){
            customerexiste = customerRepository.findById(customer.getId()).orElse(null);
        }
        try {
            if (customerexiste != null) {
                customerexiste.setDocument(customer.getDocument());
                customerexiste.setName(customer.getName());
                customerexiste.setDocumentType(customer.getDocumentType());
                customerRepository.save(customerexiste);
            } else {
                customerRepository.save(customer);
            }
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("document", "document.duplicate", "Este número de documento ya está registrado");
            model.addAttribute("employee", customer);
            return "customer/form";
        }
        redirectAttributes.addFlashAttribute("message", "Se ha registrado correctamente");


    return "redirect:/cliente";
    }

    @GetMapping("/edit/{id}")
    public String editarEmpleado(Model model, @PathVariable int id, RedirectAttributes redirectAttributes) {

        Optional<Customer> optEmployee = customerRepository.findByIdWithRelations(id);

        if (optEmployee.isPresent()) {
            Customer employee = optEmployee.get();
            model.addAttribute("employee", employee);
            redirectAttributes.addFlashAttribute("msg","Empleado actualizado correctamente");
            return "customer/form";
        } else {
            return "redirect:/list";
        }
    }
}

