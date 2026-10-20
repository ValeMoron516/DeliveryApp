package com.example.demo.entity;

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

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;
}
