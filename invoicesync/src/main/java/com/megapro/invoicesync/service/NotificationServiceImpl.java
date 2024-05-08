package com.megapro.invoicesync.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.dto.NotificationMapper;
import com.megapro.invoicesync.dto.response.NotificationResponseDTO;
import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.Notification;
import com.megapro.invoicesync.repository.NotificationDb;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    InvoiceServiceImpl invoiceService;

    @Autowired
    NotificationDb notificationDb;

    @Autowired
    UserService userService;

    @Autowired
    NotificationMapper notificationMapper;

    @Override
    public void generateInvoiceMakerNotification(String employeeEmail, UUID invoiceId) {
        var invoice = invoiceService.getInvoiceById(invoiceId);
        var invoiceNumber = invoice.getInvoiceNumber();
        var invoiceStatus = invoice.getStatus();

        var employee = userService.findByEmail(employeeEmail);

        var content = "";
        if(invoiceStatus.equals("Approved")){
            content = invoiceNumber + " has been approved.";
        } else if (invoiceStatus.equals("Rejected")){
            content = invoiceNumber + " has been rejected.";
        } else if (invoiceStatus.equals("Need Revision")){
            content = invoiceNumber + " needs to be revised.";        }
        Notification notification = new Notification(content, employee, invoiceId, invoiceNumber);

        notificationDb.save(notification);
    }

    @Override
    public void generateInvoiceApproverNotification(String employeeEmail, UUID invoiceId) {
        var invoice = invoiceService.getInvoiceById(invoiceId);
        var invoiceNumber = invoice.getInvoiceNumber();

        var employee = userService.findByEmail(employeeEmail);

        var content = invoiceNumber + " needs your approval";
        Notification notification = new Notification(content, employee, invoiceId, invoiceNumber);

        notificationDb.save(notification);
    }

    @Override
    public List<NotificationResponseDTO> getEmployeeNotification(Employee employee) {
        var notifications = employee.getListNotifications();
        var notificationsDTO = notificationMapper.listNotificationToListNotificationResponseDTO(notifications);
        return notificationsDTO;
    }
}
