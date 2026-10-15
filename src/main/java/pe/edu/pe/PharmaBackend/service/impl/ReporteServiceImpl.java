package pe.edu.pe.PharmaBackend.service.impl;

import org.springframework.stereotype.Service;
import pe.edu.pe.PharmaBackend.dto.VentaPorCategoriaDTO;
import pe.edu.pe.PharmaBackend.enums.EstadoVenta;
import pe.edu.pe.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.pe.PharmaBackend.repository.ReporteRepository;
import pe.edu.pe.PharmaBackend.service.service.ReporteService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteServiceImpl(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    @Override
    public List<VentaPorCategoriaDTO> ventasPorCategoria(
            LocalDate desde, LocalDate hasta) {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaNegocioException(
                "La fecha desde no puede ser posterior a la fecha hasta."
            );
        }

        return reporteRepository.ventasPorCategoria(
            EstadoVenta.REGISTRADA,
            desde == null ? null : desde.atStartOfDay(),
            hasta == null ? null : hasta.atTime(LocalTime.MAX)
        );
    }
}