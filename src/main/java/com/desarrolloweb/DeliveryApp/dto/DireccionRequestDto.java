package com.desarrolloweb.DeliveryApp.dto;
public class DireccionRequestDto{
    private Long usuarioId;
    private String calle;
    private String numero;
    private String ciudad;
    private String codigoPostal;
    private String indicacionesExtra;

public DireccionRequestDto(){

}
public Long getUsuarioId(){
    return usuarioId;
}
public void setUsuarioId(Long usuarioId){
    this.usuarioId = usuarioId;
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