
package com.desarrolloweb.DeliveryApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Roles {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(
        name = "nombre",
        nullable = false,
        unique = true,
        length = 50
    )
    private String nombre;

    public Integer getId() {
    return id;
}

public void setId(Integer id) {
    this.id = id;
}

public String getNombre() {
    return nombre;
}

public void setNombre(String nombre) {
    this.nombre = nombre;
}
}
