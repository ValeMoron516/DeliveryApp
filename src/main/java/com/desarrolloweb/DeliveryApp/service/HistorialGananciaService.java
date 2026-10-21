package com.desarrolloweb.DeliveryApp.service;

import com.desarrolloweb.DeliveryApp.entity.HistorialGanancia;
import com.desarrolloweb.DeliveryApp.entity.Repartidor;
import com.desarrolloweb.DeliveryApp.entity.Envio;
import com.desarrolloweb.DeliveryApp.dtos.historialGananciaDTO.HistorialGananciaRequestDTO;
import com.desarrolloweb.DeliveryApp.dtos.historialGananciaDTO.HistorialGananciaResponseDTO;
import com.desarrolloweb.DeliveryApp.dtos.paginacionDTO.MetaDTO;
import com.desarrolloweb.DeliveryApp.dtos.paginacionDTO.PaginacionDTO;
import com.desarrolloweb.DeliveryApp.exception.BadRequestException;
import com.desarrolloweb.DeliveryApp.exception.ConflictException;
import com.desarrolloweb.DeliveryApp.exception.NotFoundException;

import com.desarrolloweb.DeliveryApp.repository.HistorialGananciaRepository;
import com.desarrolloweb.DeliveryApp.repository.RepartidorRepository;
import com.desarrolloweb.DeliveryApp.repository.EnvioRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class HistorialGananciaService {

    @Autowired
    private HistorialGananciaRepository historialGananciaRepository;

    @Autowired
    private RepartidorRepository repartidorRepository;

    @Autowired
    private EnvioRepository envioRepository;

    // ##################################################

    // Obtener lista paginada de historiales de ganancia (con filtro opcional por repartidor)
    public PaginacionDTO<HistorialGananciaResponseDTO> obtenerTodosHistorialesGanancias(Integer page, Integer limit, Long idRepartidor) { 
        
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<HistorialGanancia> paginaHistoriales;

        if (idRepartidor != null) {
            paginaHistoriales = historialGananciaRepository.findByRepartidor_Id(idRepartidor, pageable);
        } else {
            paginaHistoriales = historialGananciaRepository.findAll(pageable);
        }

        List<HistorialGanancia> historiales = paginaHistoriales.getContent();
        
        List<HistorialGananciaResponseDTO> dtos = new ArrayList<>();
        for (HistorialGanancia h : historiales) {
            dtos.add(new HistorialGananciaResponseDTO(
                h.getIdGanancia(),
                h.getRepartidor() != null ? h.getRepartidor().getId() : null,
                h.getEnvio() != null ? h.getEnvio().getId() : null,
                h.getMontoLiquidado(),
                h.getFechaLiquidacion()
            ));
        }
        
        MetaDTO meta = new MetaDTO(
            paginaHistoriales.getTotalElements(),
            paginaHistoriales.getNumberOfElements(),
            paginaHistoriales.getSize(),
            paginaHistoriales.getTotalPages(),
            paginaHistoriales.getNumber() + 1
        );
    
        return new PaginacionDTO<>(dtos, meta);
    }

    // ##################################################

    // Obtener historial de ganancia por ID
    public HistorialGananciaResponseDTO obtenerHistorialGananciaPorId(Long id) { 
        Optional<HistorialGanancia> optH = historialGananciaRepository.findById(id);
        if (!optH.isPresent()) { 
            throw new NotFoundException("El historial de ganancia solicitado no existe"); 
        }
        
        HistorialGanancia h = optH.get();
        return new HistorialGananciaResponseDTO(
            h.getIdGanancia(),
            h.getRepartidor() != null ? h.getRepartidor().getId() : null,
            h.getEnvio() != null ? h.getEnvio().getId() : null,
            h.getMontoLiquidado(),
            h.getFechaLiquidacion()
        );
    }

    // ##################################################

    // Crear nuevo historial de ganancia
    public HistorialGananciaResponseDTO crearHistorialGanancia(HistorialGananciaRequestDTO dto) {

        if (dto.getIdRepartidor() == null || dto.getEnvioId() == null || dto.getMontoLiquidado() == null) {
            throw new BadRequestException("Datos incompletos o inválidos");
        }

        if (historialGananciaRepository.existsByEnvio_Id(dto.getEnvioId())) {
            throw new ConflictException("El envío ya tiene una ganancia liquidada asociada");
        }

        Optional<Repartidor> optRepartidor = repartidorRepository.findById(dto.getIdRepartidor());
        if (!optRepartidor.isPresent()) {
            throw new NotFoundException("El repartidor con el ID provisto no existe");
        }

        Optional<Envio> optEnvio = envioRepository.findById(dto.getEnvioId());
        if (!optEnvio.isPresent()) {
            throw new NotFoundException("El envío con el ID provisto no existe");
        }

        HistorialGanancia nuevoHistorial = new HistorialGanancia();
        nuevoHistorial.setRepartidor(optRepartidor.get());
        nuevoHistorial.setEnvio(optEnvio.get());
        nuevoHistorial.setMontoLiquidado(dto.getMontoLiquidado());

        HistorialGanancia h = historialGananciaRepository.save(nuevoHistorial);

        return new HistorialGananciaResponseDTO(
            h.getIdGanancia(),
            h.getRepartidor().getId(),
            h.getEnvio().getId(),
            h.getMontoLiquidado(),
            h.getFechaLiquidacion()
        );
    }

    // ##################################################

    // Actualizar historial de ganancia por ID
    public HistorialGananciaResponseDTO actualizarHistorialGananciaPorId(Long id, HistorialGananciaRequestDTO dto) {

        Optional<HistorialGanancia> optH = historialGananciaRepository.findById(id);
        if (!optH.isPresent()) {
            throw new NotFoundException("El historial de ganancia con el ID provisto no existe");
        }

        HistorialGanancia historial = optH.get();

        if (dto.getMontoLiquidado() != null) {
            historial.setMontoLiquidado(dto.getMontoLiquidado());
        }

        if (dto.getIdRepartidor() != null) {
            Optional<Repartidor> optRepartidor = repartidorRepository.findById(dto.getIdRepartidor());
            if (!optRepartidor.isPresent()) {
                throw new NotFoundException("El repartidor con el ID provisto no existe");
            }
            historial.setRepartidor(optRepartidor.get());
        }

        if (dto.getEnvioId() != null) {
            if (!historial.getEnvio().getId().equals(dto.getEnvioId())) {
                if (historialGananciaRepository.existsByEnvio_Id(dto.getEnvioId())) {
                    throw new ConflictException("El envío asociado ya está vinculado a otra ganancia");
                }
                Optional<Envio> optEnvio = envioRepository.findById(dto.getEnvioId());
                if (!optEnvio.isPresent()) {
                    throw new NotFoundException("El envío con el ID provisto no existe");
                }
                historial.setEnvio(optEnvio.get());
            }
        }

        HistorialGanancia h = historialGananciaRepository.save(historial);

        return new HistorialGananciaResponseDTO(
            h.getIdGanancia(),
            h.getRepartidor().getId(),
            h.getEnvio().getId(),
            h.getMontoLiquidado(),
            h.getFechaLiquidacion()
        );
    }

    // ##################################################

    // Borrar historial de ganancia por ID
    public void borrarHistorialGananciaPorId(Long id) {
        if (!historialGananciaRepository.existsById(id)) {
            throw new NotFoundException("El historial de ganancia con el ID provisto no existe");
        }
        
        try {
            historialGananciaRepository.deleteById(id);
        } catch (Exception e) {
            throw new ConflictException("No se puede eliminar debido a restricciones de integridad con la base de datos");
        }
    }
}
