package com.megapro.invoicesync.dto.response;

import java.util.UUID;

import lombok.Data;

@Data
public class NotificationResponseDTO {
    private int notificationId;
    private String content;
    private UUID invoiceId;
    private boolean isRead;
}
