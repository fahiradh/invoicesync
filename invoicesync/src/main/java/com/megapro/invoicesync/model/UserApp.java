package com.megapro.invoicesync.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_app")
public class UserApp {
    @Id
    private long idPenerbit;

    @Column(name = "nama_user", nullable = false)
    private String namaUser;


}
