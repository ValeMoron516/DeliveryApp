package com.desarrolloweb.DeliveryApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "direccion")
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(length = 20, nullable = false)
    private String calle;

    @Column(length = 20, nullable = false)
    private String numero;

    @Column(length = 20, nullable = false)
    private String ciudad;

    @Column(length = 20, nullable = false)
    private String codigoPostal;

    @Column(length = 255)
    private String indicacionesExtra;

    public Direccion(Long id, Usuario usuario, String calle, String numero, String ciudad, String codigoPostal, String indicacionesExtra){
        this.id = id;
        this.usuario = usuario;
        this.calle = calle;
        this.numero = numero;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
        this.indicacionesExtra = indicacionesExtra;
    }
    public Direccion(){
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public Usuario getUsuario(){
        return usuario;
    }
    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }
    public String getCalle(){
        return calle;
    }
    public void setCalle(String calle){
        this.calle = calle;
    }
    public String getNumero(){
        return numero;
    }
    public void setNumero(String numero){
        this.numero = numero;
    }
    public String getCiudad(){
        return ciudad;
    }
    public void setCiudad(String ciudad){
        this.ciudad = ciudad;
    }
    public String getCodigoPostal(){
        return codigoPostal;
    }
    public void setCodigoPostal(String codigoPostal){
        this.codigoPostal = codigoPostal;
    }
    public String getIndicacionesExtra(){
        return indicacionesExtra;
    }
    public void setIndicacionesExtra(String indicacionesExtra){
        this.indicacionesExtra = indicacionesExtra;
    }
}