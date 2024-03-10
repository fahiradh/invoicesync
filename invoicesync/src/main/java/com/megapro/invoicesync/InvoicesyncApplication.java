package com.megapro.invoicesync;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.megapro.invoicesync.dto.InvoiceMapper;
import com.megapro.invoicesync.dto.UserMapper;
import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.dto.request.CreateEmployeeRequestDTO;
import com.megapro.invoicesync.dto.request.CreateUserAppRequestDTO;
import com.megapro.invoicesync.model.Role;
import com.megapro.invoicesync.service.InvoiceService;
import com.megapro.invoicesync.service.RoleService;
import com.megapro.invoicesync.service.UserService;

@SpringBootApplication
public class InvoicesyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoicesyncApplication.class, args);
	}

	@Bean
	@ConditionalOnProperty(name = "spring.jpa.hibernate.ddl-auto", havingValue = "create")
	public CommandLineRunner initAdministrator(UserMapper userMapper, 
							UserService userService, 
							RoleService roleService, 
							InvoiceService invoiceService,
							InvoiceMapper invoiceMapper) {
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
			userDTO.setRole(roleAdmin);
			
			var userDTO2 = new CreateUserAppRequestDTO();
			userDTO2.setEmail("wh@gmail.com");
			userDTO2.setPassword("wh123");
			userDTO2.setRole(roleStafWarehouse);

			var userDTO3 = new CreateUserAppRequestDTO();
			userDTO3.setEmail("whs@gmail.com");
			userDTO3.setPassword("whs123");
			userDTO3.setRole(roleManagerWarehouse);

			var dummyInvoiceDTO = new CreateInvoiceRequestDTO();
			var dummyInvoice = invoiceMapper.createInvoiceRequestToInvoice(dummyInvoiceDTO);
			invoiceService.createInvoice(dummyInvoice, "dummy");

			
			var userAdmin = userMapper.createUserAppRequestDTOToUserApp(userDTO);
			userService.createUserApp(userAdmin, userDTO);

			var warehouseStaff = new CreateEmployeeRequestDTO();
			warehouseStaff.setEmail("warehouse_staff@gmail.com");
			warehouseStaff.setPassword("warehouse");
			warehouseStaff.setRole(roleStafWarehouse);
			userService.createEmployee(userMapper.createEmployeeRequestDTOToEmployee(warehouseStaff));

			var financeStaff = new CreateEmployeeRequestDTO();
			financeStaff.setEmail("finance_staff@gmail.com");
			financeStaff.setPassword("finance");
			financeStaff.setRole(roleStafFinance);
			userService.createEmployee(userMapper.createEmployeeRequestDTOToEmployee(financeStaff));

			var warehouseManager = new CreateEmployeeRequestDTO();
			warehouseManager.setEmail("warehouse_manager@gmail.com");
			warehouseManager.setPassword("warehouse");
			warehouseManager.setRole(roleManagerWarehouse);
			userService.createEmployee(userMapper.createEmployeeRequestDTOToEmployee(warehouseManager));

			var financeManager = new CreateEmployeeRequestDTO();
			financeManager.setEmail("finance_manager@gmail.com");
			financeManager.setPassword("finance");
			financeManager.setRole(roleManagerFinance);
			userService.createEmployee(userMapper.createEmployeeRequestDTOToEmployee(financeManager));

			var financeDirector = new CreateEmployeeRequestDTO();
			financeDirector.setEmail("finance_director@gmail.com");
			financeDirector.setPassword("finance");
			financeDirector.setRole(roleDirectorFinance);
			userService.createEmployee(userMapper.createEmployeeRequestDTOToEmployee(financeDirector));
		};
	} 

}
