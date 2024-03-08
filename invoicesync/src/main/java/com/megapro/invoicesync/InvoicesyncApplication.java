package com.megapro.invoicesync;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.megapro.invoicesync.dto.UserMapper;
import com.megapro.invoicesync.dto.request.CreateUserAppRequestDTO;
import com.megapro.invoicesync.model.Role;
import com.megapro.invoicesync.service.RoleService;
import com.megapro.invoicesync.service.UserService;

@SpringBootApplication
public class InvoicesyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoicesyncApplication.class, args);
	}

	@Bean
	@ConditionalOnProperty(name = "spring.jpa.hibernate.ddl-auto", havingValue = "create")
	public CommandLineRunner initAdministrator(UserMapper userMapper, UserService userService, RoleService roleService) {
		return args -> {

			var roleAdmin = new Role();
			roleAdmin.setId(1L);
			roleAdmin.setRole("Admin");
			roleService.createRole(roleAdmin);

			var roleStafWarehouse = new Role();
			roleStafWarehouse.setId(2L);
			roleStafWarehouse.setRole("Warehouse Staff");
			roleService.createRole(roleStafWarehouse);

			var roleStafFinance = new Role();
			roleStafFinance.setId(3L);
			roleStafFinance.setRole("Finance Staff");
			roleService.createRole(roleStafFinance);

			var roleManagerWarehouse = new Role();
			roleManagerWarehouse.setId(4L);
			roleManagerWarehouse.setRole("Warehouse Manager");
			roleService.createRole(roleManagerWarehouse);

			var roleManagerFinance = new Role();
			roleManagerFinance.setId(5L);
			roleManagerFinance.setRole("Finance Manager");
			roleService.createRole(roleManagerFinance);

			var roleDirectorFinance = new Role();
			roleDirectorFinance.setId(6L);
			roleDirectorFinance.setRole("Finance Director");
			roleService.createRole(roleDirectorFinance);

			var userDTO = new CreateUserAppRequestDTO();
			userDTO.setEmail("admin@gmail.com");
			userDTO.setPassword("admin123");
			userDTO.setRole("Admin");

			var userAdmin = userMapper.createUserAppRequestDTOToUserApp(userDTO);
			userService.createUserApp(userAdmin, userDTO);
		};
	} 

}
