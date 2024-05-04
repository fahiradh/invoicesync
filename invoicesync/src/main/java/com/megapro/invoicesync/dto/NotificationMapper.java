package com.megapro.invoicesync.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.megapro.invoicesync.dto.response.NotificationResponseDTO;
import com.megapro.invoicesync.model.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "employee", ignore = true)
    List<NotificationResponseDTO> listNotificationToListNotificationResponseDTO(List<Notification> listNotification);
}
