package com.desarrolloweb.DeliveryApp.controllerTest;

import com.desarrolloweb.DeliveryApp.controller.PaquetesCompartidosController;
import com.desarrolloweb.DeliveryApp.controller.SolicitudesPendientesController;
import com.desarrolloweb.DeliveryApp.dto.SolicitudPendienteDTO;
import com.desarrolloweb.DeliveryApp.exception.BadRequestException;
import com.desarrolloweb.DeliveryApp.exception.ConflictException;
import com.desarrolloweb.DeliveryApp.exception.GlobalExceptionHandler;
import com.desarrolloweb.DeliveryApp.exception.NotFoundException;
import com.desarrolloweb.DeliveryApp.service.SolicitudesPendientesService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Contratos HTTP sin conexiones externas: MockMvc standalone + servicio simulado.
 * No prueba filtros JWT ni JPA/Aiven; esos requieren otra prueba de integracion.
 */
class SolicitudesPendientesHttpContractTest {
    private SolicitudesPendientesService service;
    private MockMvc mvc;

    private SolicitudPendienteDTO.Resumen resumen() {
        return new SolicitudPendienteDTO.Resumen("15", "RECIBIR", "ESPERANDO_DATOS",
                null, 9L, "Libros", null, null, null, null,
                LocalDateTime.of(2026, 11, 14, 12, 0), null);
    }

    @BeforeEach
    void setUp() {
        service = mock(SolicitudesPendientesService.class);
        mvc = MockMvcBuilders.standaloneSetup(
                    new SolicitudesPendientesController(service),
                    new PaquetesCompartidosController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void crearBorradorRecibirDevuelve201YEnlace() throws Exception {
        when(service.crear(any())).thenReturn(new SolicitudPendienteDTO.Creacion(
                resumen(), "token-de-prueba", LocalDateTime.of(2026, 11, 16, 12, 0)));
        mvc.perform(post("/api/v1/solicitudes-pendientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"RECIBIR\",\"direccionDestinoId\":9}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.solicitud.estado").value("ESPERANDO_DATOS"))
                .andExpect(jsonPath("$.tokenCompartir").value("token-de-prueba"));
        verify(service).crear(argThat(d -> "RECIBIR".equals(d.tipo()) && d.direccionDestinoId()==9L));
    }

    @Test
    void listarBorradoresNoExponeToken() throws Exception {
        when(service.mias()).thenReturn(List.of(resumen()));
        mvc.perform(get("/api/v1/solicitudes-pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("15"))
                .andExpect(jsonPath("$[0].tokenCompartir").doesNotExist());
    }

    @Test
    void consultarBorradorPorId() throws Exception {
        when(service.ver("15")).thenReturn(resumen());
        mvc.perform(get("/api/v1/solicitudes-pendientes/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("RECIBIR"));
    }

    @Test
    void completarMedidasDelCliente() throws Exception {
        when(service.completarPropia(eq("15"), any())).thenReturn(resumen());
        mvc.perform(put("/api/v1/solicitudes-pendientes/15/paquete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descripcionPaquete\":\"Libros\",\"pesoKg\":2,\"largoCm\":20,\"anchoCm\":10,\"altoCm\":5}"))
                .andExpect(status().isOk());
        verify(service).completarPropia(eq("15"), argThat(m -> m.pesoKg().compareTo(new BigDecimal("2"))==0));
    }

    @Test
    void fijarOrigenUsaPatch() throws Exception {
        when(service.fijarOrigen("15", 3L)).thenReturn(resumen());
        mvc.perform(patch("/api/v1/solicitudes-pendientes/15/origen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"direccionOrigenId\":3}"))
                .andExpect(status().isOk());
        verify(service).fijarOrigen("15", 3L);
    }

    @Test
    void regenerarEnlaceUsaPost() throws Exception {
        when(service.regenerarEnlace("15")).thenReturn(new SolicitudPendienteDTO.Creacion(
                resumen(), "nuevo-token", LocalDateTime.of(2026,11,16,12,0)));
        mvc.perform(post("/api/v1/solicitudes-pendientes/15/enlace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenCompartir").value("nuevo-token"));
    }

    @Test
    void cotizarInvocaElServicio() throws Exception {
        mvc.perform(post("/api/v1/solicitudes-pendientes/15/cotizar"))
                .andExpect(status().isOk());
        verify(service).cotizar("15");
    }

    @Test
    void confirmarDevuelve201YPrecio() throws Exception {
        mvc.perform(post("/api/v1/solicitudes-pendientes/15/confirmar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tarifaId\":1,\"precioAceptado\":1608.00}"))
                .andExpect(status().isCreated());
        verify(service).confirmar(eq("15"), argThat(c ->
                c.tarifaId()==1L && c.precioAceptado().compareTo(new BigDecimal("1608"))==0));
    }

    @Test
    void consultaPublicaSoloExponeDatosMinimos() throws Exception {
        when(service.verInvitado("token-test")).thenReturn(
                new SolicitudPendienteDTO.Invitado("ESPERANDO_DATOS", "Libros"));
        mvc.perform(get("/api/v1/paquetes-compartidos/token-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcionPaquete").value("Libros"))
                .andExpect(jsonPath("$.direccionDestinoId").doesNotExist())
                .andExpect(jsonPath("$.clienteId").doesNotExist());
    }

    @Test
    void completarMedidasConEnlaceDevuelve204() throws Exception {
        mvc.perform(put("/api/v1/paquetes-compartidos/token-test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descripcionPaquete\":\"Caja\",\"pesoKg\":1,\"largoCm\":10,\"anchoCm\":10,\"altoCm\":10}"))
                .andExpect(status().isNoContent());
        verify(service).completarInvitado(eq("token-test"), any());
    }

    @Test
    void solicitudAjenaOcultaCon404() throws Exception {
        when(service.ver("99")).thenThrow(new NotFoundException("Solicitud no encontrada"));
        mvc.perform(get("/api/v1/solicitudes-pendientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void solicitudInvalidaDevuelve400() throws Exception {
        when(service.crear(any())).thenThrow(new BadRequestException("Datos invalidos"));
        mvc.perform(post("/api/v1/solicitudes-pendientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"OTRO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void enlaceVencidoDevuelve409() throws Exception {
        when(service.verInvitado("token-vencido"))
                .thenThrow(new ConflictException("Enlace vencido o utilizado"));
        mvc.perform(get("/api/v1/paquetes-compartidos/token-vencido"))
                .andExpect(status().isConflict());
    }
}
