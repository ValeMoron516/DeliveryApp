package com.desarrolloweb.DeliveryApp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.desarrolloweb.DeliveryApp.entity.Vehiculo;
import com.desarrolloweb.DeliveryApp.repository.VehiculoRepository;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public Page<Vehiculo> listar(int page, int limit) {
        if (page < 1 || limit < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La página y el límite deben ser mayores que cero"
            );
        }
        return vehiculoRepository.findAll(PageRequest.of(page - 1, limit));
    }

    public Vehiculo obtenerPorId(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe el vehículo solicitado"
                ));
    }

    public Vehiculo crear(String tipo, String descripcion) {
        if (vehiculoRepository.existsByTipo(tipo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un vehículo con ese tipo"
            );
        }
        return vehiculoRepository.save(new Vehiculo(tipo, descripcion));
    }

    public Vehiculo actualizar(Long id, String tipo, String descripcion) {
        Vehiculo vehiculo = obtenerPorId(id);
        if (tipo != null) {
            vehiculoRepository.findByTipo(tipo)
                    .filter(otro -> !otro.getIdVehiculo().equals(id))
                    .ifPresent(otro -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ya existe un vehículo con ese tipo"
                        );
                    });
            vehiculo.setTipo(tipo);
        }
        if (descripcion != null) {
            vehiculo.setDescripcion(descripcion);
        }
        if (tipo == null && descripcion == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe especificarse al menos un campo para actualizar"
            );
        }
        return vehiculoRepository.save(vehiculo);
    }

    public void eliminar(Long id) {
        vehiculoRepository.delete(obtenerPorId(id));
    }
}
