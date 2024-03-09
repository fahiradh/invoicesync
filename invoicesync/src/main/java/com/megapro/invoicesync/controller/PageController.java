package com.megapro.invoicesync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.megapro.invoicesync.dto.request.CreateUserAppRequestDTO;
import com.megapro.invoicesync.repository.UserAppDb;

@Controller
public class PageController {
    @Autowired
    UserAppDb userAppDb;

    @GetMapping("/home")
    public String home(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        model.addAttribute("email", email);
        model.addAttribute("role", role);

        if (role.contains("Staf")) {
            if (role.contains("Finance")) {
                return "home/home-staff-finance.html";
            }
            else {
                return "home/home-non-finance.html"; // home staf non finance
            }
        }
        else if (role.contains("Manager") || role.contains("Director")) {
            if (role.contains("Finance")) {
                return "home/home-exc-finance.html"; // home manager finance dan direktur keuangan
            }
            else {
                return "home/home-exc-non-finance.html"; // home manager non finance
            }
        }
        return "home/home-admin.html";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "auth/login.html";
    }

    @GetMapping("/create-user-account")
    public String createUser(Model model){
        var userAppDTO = new CreateUserAppRequestDTO();
        model.addAttribute("userAppDTO", userAppDTO);
        return "auth/create-user-account.html";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403.html"; 
    }
}
