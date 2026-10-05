package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.dto.DisponibilidadDTO;
import com.desarrolloweb.DeliveryApp.dto.RepartidorCreateDTO;
import com.desarrolloweb.DeliveryApp.dto.RepartidorResponseDTO;
import com.desarrolloweb.DeliveryApp.dto.RepartidorUpdateDTO;
import com.desarrolloweb.DeliveryApp.dto.VehiculoActivoDTO;
import com.desarrolloweb.DeliveryApp.entity.Repartidor;
import com.desarrolloweb.DeliveryApp.repository.RepartidorRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service

public class RepartidorService {

    private final RepartidorRepository repartidorRepository;



    public RepartidorService (RepartidorRepository repartidorRepository){
        this.repartidorRepository = repartidorRepository;
    }

    // Registrar nuevo repartidor (Metodo POST)
    public RepartidorResponseDTO registrarRepartidor(RepartidorCreateDTO createDTO) {
        // Validar si el usuario ya es repartidor
        if (repartidorRepository.existsByIdRepartidor(createDTO.getIdRepartidor())) {
            throw new RuntimeException("El usuario con ID " + createDTO.getIdRepartidor() + " ya tiene un perfil de repartidor activo.");
        }

        // Crear objeto entidad
        Repartidor repartidor = new Repartidor();
        repartidor.setIdRepartidor(createDTO.getIdRepartidor());
        repartidor.setFotoUrl(createDTO.getFotoUrl());
        repartidor.setVehiculoActId(createDTO.getVehiculoActId());

        // Asignar valores por defecto según el requerimiento
        repartidor.setDisponible(0);
        repartidor.setSaldoAcumulado(BigDecimal.ZERO);
        repartidor.setCalificacionPromedio(BigDecimal.ZERO);
        repartidor.setTasaRechazoInterna(0);

        // Guardar en la base de datos
        Repartidor repartidorGuardado = repartidorRepository.save(repartidor);

        // Devolver la respuesta mapeada
        return new RepartidorResponseDTO(
                repartidorGuardado.getIdRepartidor(),
                repartidorGuardado.getFotoUrl(),
                repartidorGuardado.getDisponible(),
                repartidorGuardado.getSaldoAcumulado(),
                repartidorGuardado.getCalificacionPromedio(),
                repartidorGuardado.getTasaRechazoInterna(),
                repartidorGuardado.getVehiculoActId()
        );
    }

    // Obtener perfil por ID (Método GET)
    public RepartidorResponseDTO obtenerPorId(Long idRepartidor) {
        // Buscar por ID o lanzar excepción si no existe
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró un perfil de repartidor asociado al ID: " + idRepartidor));

        // Devolver la respuesta mapeada a DTO
        return new RepartidorResponseDTO(
                repartidor.getIdRepartidor(),
                repartidor.getFotoUrl(),
                repartidor.getDisponible(),
                repartidor.getSaldoAcumulado(),
                repartidor.getCalificacionPromedio(),
                repartidor.getTasaRechazoInterna(),
                repartidor.getVehiculoActId()
        );
    }

    // Actualizar datos de un repartidor (Método PUT)
    public RepartidorResponseDTO actualizarRepartidor(Long idRepartidor, RepartidorUpdateDTO updateDTO) {
        // 1. Buscar si el repartidor existe
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar. El repartidor con ID: " + idRepartidor + " no existe."));

        // 2. Modificar los campos editables
        repartidor.setFotoUrl(updateDTO.getFotoUrl());
        repartidor.setDisponible(updateDTO.getDisponible());
        repartidor.setVehiculoActId(updateDTO.getVehiculoActId());

        // 3. Guardar cambios en la base de datos
        Repartidor repartidorActualizado = repartidorRepository.save(repartidor);

        // 4. Retornar el perfil completo actualizado
        return new RepartidorResponseDTO(
                repartidorActualizado.getIdRepartidor(),
                repartidorActualizado.getFotoUrl(),
                repartidorActualizado.getDisponible(),
                repartidorActualizado.getSaldoAcumulado(),
                repartidorActualizado.getCalificacionPromedio(),
                repartidorActualizado.getTasaRechazoInterna(),
                repartidorActualizado.getVehiculoActId()
        );
    }

    // Eliminar repartidor (Método DELETE)
    public void eliminarRepartidor(Long idRepartidor) {
        // Validar si existe antes de eliminar
        if (!repartidorRepository.existsByIdRepartidor(idRepartidor)) {
            throw new RuntimeException("No se puede eliminar. El repartidor con ID: " + idRepartidor + " no existe.");
        }
        repartidorRepository.deleteById(idRepartidor);
    }
    
    // Cambiar disponibilidad (Método PATCH)
    public RepartidorResponseDTO actualizarDisponibilidad(Long idRepartidor, DisponibilidadDTO disponibilidadDTO) {
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor));

        repartidor.setDisponible(disponibilidadDTO.getDisponible());
        Repartidor actualizado = repartidorRepository.save(repartidor);

        return new RepartidorResponseDTO(
                actualizado.getIdRepartidor(),
                actualizado.getFotoUrl(),
                actualizado.getDisponible(),
                actualizado.getSaldoAcumulado(),
                actualizado.getCalificacionPromedio(),
                actualizado.getTasaRechazoInterna(),
                actualizado.getVehiculoActId()
        );
    }

    // Cambiar vehículo activo (Método PATCH)
    public RepartidorResponseDTO actualizarVehiculoActivo(Long idRepartidor, VehiculoActivoDTO vehiculoActivoDTO) {
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor));

        repartidor.setVehiculoActId(vehiculoActivoDTO.getVehiculoActId());
        Repartidor actualizado = repartidorRepository.save(repartidor);

        return new RepartidorResponseDTO(
                actualizado.getIdRepartidor(),
                actualizado.getFotoUrl(),
                actualizado.getDisponible(),
                actualizado.getSaldoAcumulado(),
                actualizado.getCalificacionPromedio(),
                actualizado.getTasaRechazoInterna(),
                actualizado.getVehiculoActId()
        );
    }

    // Obtener historial de pedidos (Método GET)
    public Object obtenerHistorialPedidos(Long idRepartidor) {
        // Validar que el repartidor exista
        if (!repartidorRepository.existsByIdRepartidor(idRepartidor)) {
            throw new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor);
        }
        // Retorna la información o lista de pedidos
        return "Historial de pedidos del repartidor ID: " + idRepartidor;
    }

    //Obtener balance del repartidor (Método GET)
    public BigDecimal obtenerBalance(Long idRepartidor) {
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("No se encontró el repartidor con ID: " + idRepartidor));

        return repartidor.getSaldoAcumulado();
    }


}
