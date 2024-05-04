package com.megapro.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megapro.invoicesync.model.Notification;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface NotificationDb extends JpaRepository<Notification,Integer> {
    
}
