
package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.entity.Roles;
import com.desarrolloweb.DeliveryApp.dtos.rolDTO.RolRequestDTO;
import com.desarrolloweb.DeliveryApp.dtos.rolDTO.RolResponseDTO;
import com.desarrolloweb.DeliveryApp.exception.BadRequestException;
import com.desarrolloweb.DeliveryApp.exception.ConflictException;
import com.desarrolloweb.DeliveryApp.exception.NotFoundException;
import com.desarrolloweb.DeliveryApp.repository.RolRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    // Obtener lista de roles
    public List<RolResponseDTO> obtenerTodosRoles() {
        List<Roles> roles = rolRepository.findAll();
        List<RolResponseDTO> rDtos = new ArrayList<>();

        for (Roles r : roles) {
            rDtos.add(new RolResponseDTO(
                r.getId(),
                r.getNombre()
            ));
        }

        return rDtos;
    }

    // Obtener rol por ID
    public RolResponseDTO obtenerRolPorId(Integer id) {
        Optional<Roles> optR = rolRepository.findById(id);

        if (!optR.isPresent()) {
            throw new NotFoundException(
                "El rol con el ID provisto no existe"
            );
        }

        Roles r = optR.get();

        return new RolResponseDTO(
            r.getId(),
            r.getNombre()
        );
    }

    // Crear nuevo rol
    public RolResponseDTO crearRol(RolRequestDTO dto) {

        if (dto.getId() == null ||
            dto.getNombre() == null ||
            dto.getNombre().isBlank()) {

            throw new BadRequestException(
                "Datos de rol inválidos"
            );
        }

        if (rolRepository.existsById(dto.getId())) {
            throw new ConflictException(
                "Ya existe un rol con ese ID"
            );
        }

        if (rolRepository.existsByNombre(dto.getNombre())) {
            throw new ConflictException(
                "El nombre ya está siendo utilizado por otro rol"
            );
        }

        Roles nuevoRol = new Roles();

        nuevoRol.setId(dto.getId());
        nuevoRol.setNombre(dto.getNombre());

        Roles r = rolRepository.save(nuevoRol);

        return new RolResponseDTO(
            r.getId(),
            r.getNombre()
        );
    }

    // Actualizar rol por ID
    public RolResponseDTO actualizarRolPorId(
            Integer id, RolRequestDTO dto) {

        Optional<Roles> optR = rolRepository.findById(id);

        if (!optR.isPresent()) {
            throw new NotFoundException(
                "El rol con el ID provisto no existe"
            );
        }

        Roles rol = optR.get();

        if (dto.getNombre() != null &&
            !dto.getNombre().isBlank()) {

            if (rolRepository.existsByNombre(dto.getNombre())
                && !rol.getNombre().equals(dto.getNombre())) {

                throw new ConflictException(
                    "El nombre ya está siendo utilizado por otro rol"
                );
            }

            rol.setNombre(dto.getNombre());
        }

        Roles r = rolRepository.save(rol);

        return new RolResponseDTO(
            r.getId(),
            r.getNombre()
        );
    }

    // Borrar rol por ID
    public void borrarRolPorId(Integer id) {

        if (!rolRepository.existsById(id)) {
            throw new NotFoundException(
                "El rol con el ID provisto no existe"
            );
        }

        try {
            rolRepository.deleteById(id);
        } catch (Exception e) {
            throw new ConflictException(
                "No se pudo eliminar el rol"
            );
        }
    }
}
