package com.desarrolloweb.DeliveryApp.entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.INTEGER)
    private Long id;

    @Column(length = 20, nullable = false)
    private String nombre;

    @Column(length = 20, nullable = false)
    private String apellido;

    @Column(length = 20, nullable = false)
    private String telefono;

    @Column (length = 50, nullable = false, unique = true)
    private String email;

    @Column (length = 255, nullable = false)
    private String password;

    @Column (nullable = false)
    private LocalDateTime fechaRegistro;

    // Requiere la migracion SQL documentada. Los roles no se asignan desde el frontend.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Roles> roles = new HashSet<>();

    public Set<Roles> getRoles() { return roles; }
    public void setRoles(Set<Roles> roles) { this.roles = roles; }

    public Usuario(Long id, String nombre, String apellido, String telefono, String email, String password, LocalDateTime fechaRegistro){
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email =email;
        this.password = password;
        this.fechaRegistro = fechaRegistro;
    }
    public Usuario(){

    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id =id;
    }
    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public String getApellido(){
        return apellido;
    }
    public void setApellido(String apellido){
        this.apellido = apellido;
    }
    public String getTelefono(){
        return telefono;
    }
    public void setTelefono(String telefono){
        this.telefono = telefono;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public LocalDateTime getFechaRegistro(){
        return fechaRegistro;
    }
    public void setFechaRegistro(LocalDateTime fechaRegistro){
        this.fechaRegistro = fechaRegistro;
    }
}
