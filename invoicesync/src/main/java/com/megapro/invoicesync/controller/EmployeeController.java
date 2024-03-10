package com.megapro.invoicesync.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.megapro.invoicesync.dto.UserMapper;
import com.megapro.invoicesync.dto.request.CreateEmployeeRequestDTO;
import com.megapro.invoicesync.model.Role;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.service.UserService;

import jakarta.validation.Valid;

import com.megapro.invoicesync.service.RoleService;


@Controller
public class EmployeeController {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    UserAppDb userAppDb;
    

    @GetMapping("/create-account")
    public String formCreateEmployee(Model model, @ModelAttribute("successMessage") String successMessage,
    @ModelAttribute("errorMessage") String errorMessage){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        model.addAttribute("email", email);
        model.addAttribute("role", role);

        var employeeDTO = new CreateEmployeeRequestDTO();
        var listRole = roleService.getAllRole();
        model.addAttribute("employeeDTO", employeeDTO);
        model.addAttribute("listRole", listRole);
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);

        return "form-create-account";
    }
    
    @PostMapping("/create-account")
    public String createEmployeeAccount(@Valid CreateEmployeeRequestDTO employeeDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        var employee = userMapper.createEmployeeRequestDTOToEmployee(employeeDTO);

        Long roleId = employeeDTO.getRole().getId();
        Role role = roleService.getRoleByRoleId(roleId);
        employee.setRole(role);

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Please check your input");
            return "form-create-account"; 
        }
        
        employee.setNomorHp(employeeDTO.getNomorHp());
        userService.createEmployee(employee);
        
        model.addAttribute("employeeEmail", employee.getEmail());
        model.addAttribute("employee", employeeDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Employee's account successfully registered.");

        return "redirect:/create-account";
    }
    
    
    @GetMapping("/employees")
    public String viewAllEmployee(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        model.addAttribute("email", email);
        model.addAttribute("role", role);

        var listEmployee = userService.getAllEmployee();
        model.addAttribute("listEmployee", listEmployee);

        return "viewall-employee";
    }
    
}