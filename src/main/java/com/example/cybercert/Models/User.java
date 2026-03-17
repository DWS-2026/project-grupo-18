package com.example.cybercert.Models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.cybercert.Role;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
        name = "user_certifications",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "certification_id")
    )
    private Set<Certification> purchasedCertifications = new HashSet<>();

    public User() {}

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {     
        return email;
    }

    public void setEmail(String email) {  
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Set<Certification> getPurchasedCertifications() {
        return purchasedCertifications;
    }

    public void setPurchasedCertifications(Set<Certification> purchasedCertifications) {
        this.purchasedCertifications = purchasedCertifications;
    }

    public boolean hasCertification(Long certificationId) {
        if (certificationId == null) {
            return false;
        }

        return purchasedCertifications.stream()
                .anyMatch(certification -> certification != null && certificationId.equals(certification.getId()));
    }

    public void addCertification(Certification certification) {
        if (certification != null) {
            purchasedCertifications.add(certification);
        }
    }
}