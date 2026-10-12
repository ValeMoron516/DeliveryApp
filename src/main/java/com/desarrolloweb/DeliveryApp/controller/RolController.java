package com.desarrolloweb.DeliveryApp.controller;

import com.desarrolloweb.DeliveryApp.exception.BadRequestException;
import com.desarrolloweb.DeliveryApp.exception.ConflictException;
import com.desarrolloweb.DeliveryApp.exception.NotFoundException;
import com.desarrolloweb.DeliveryApp.dtos.rolDTO.RolRequestDTO;
import com.desarrolloweb.DeliveryApp.dtos.rolDTO.RolResponseDTO;
import com.desarrolloweb.DeliveryApp.dtos.rolDTO.RolListResponseDTO;
import com.desarrolloweb.DeliveryApp.service.RolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    // ##################################################

    @GetMapping
    public ResponseEntity<RolListResponseDTO> obtenerTodosRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(rolService.obtenerTodosRoles()); // 200
    }

    // ##################################################

    @GetMapping("/{id}")
    public ResponseEntity<RolResponseDTO> obtenerRolPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(rolService.obtenerRolPorId(id)); // 200
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
    }

    // ##################################################

    @PostMapping
    public ResponseEntity<RolResponseDTO> crearRol(@RequestBody RolRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(rolService.crearRol(dto)); // 201
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }
    }

    // ##################################################

    @PatchMapping("{id}")
    public ResponseEntity<RolResponseDTO> actualizarRolPorId(@PathVariable Integer id, @RequestBody RolRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(rolService.actualizarRolPorId(id, dto)); // 200
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }
    }

    // ##################################################

    @DeleteMapping("{id}")
    public ResponseEntity<Void> borrarRolPorId(@PathVariable Integer id) {
        try {
            rolService.borrarRolPorId(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }
    }
}
