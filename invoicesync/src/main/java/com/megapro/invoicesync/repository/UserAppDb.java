package com.megapro.invoicesync.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megapro.invoicesync.model.UserApp;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface UserAppDb extends JpaRepository<UserApp, UUID>{
    UserApp findByEmail(String email);
}
