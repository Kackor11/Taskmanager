package com.example.taskmanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String zitadelId;

    @Column(nullable = false)
    private String username;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getZitadelId() { return zitadelId; }
    public void setZitadelId(String zitadelId) { this.zitadelId = zitadelId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}