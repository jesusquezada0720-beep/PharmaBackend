package pe.edu.pe.PharmaBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.pe.PharmaBackend.dto.VentaPorCategoriaDTO;
import pe.edu.pe.PharmaBackend.entity.Venta;
import pe.edu.pe.PharmaBackend.enums.EstadoVenta;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Venta, Long> {

    @Query("""
        SELECT new pe.edu.pe.PharmaBackend.dto.VentaPorCategoriaDTO(
            c.nombre,
            SUM(d.cantidad),
            SUM(d.subtotal)
        )
        FROM DetalleVenta d
        JOIN d.venta v
        JOIN d.producto p
        JOIN p.categoria c
        WHERE v.estado = :estado
          AND (:desde IS NULL OR v.fecha >= :desde)
          AND (:hasta IS NULL OR v.fecha <= :hasta)
        GROUP BY c.id, c.nombre
        ORDER BY c.nombre
        """)
    List<VentaPorCategoriaDTO> ventasPorCategoria(
        @Param("estado") EstadoVenta estado,
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );
}
