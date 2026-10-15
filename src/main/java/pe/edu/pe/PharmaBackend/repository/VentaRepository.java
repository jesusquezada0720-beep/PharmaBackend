package pe.edu.pe.PharmaBackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.pe.PharmaBackend.entity.Venta;
import pe.edu.pe.PharmaBackend.enums.EstadoVenta;
import java.time.LocalDateTime;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    @Query("SELECT v FROM Venta v WHERE (:clienteId IS NULL OR v.cliente.id = :clienteId) " +
           "AND (:estado IS NULL OR v.estado = :estado) " +
           "AND (:desde IS NULL OR v.fecha >= :desde) " +
           "AND (:hasta IS NULL OR v.fecha <= :hasta)")
    Page<Venta> buscarVentas(@Param("clienteId") Long clienteId,
                             @Param("estado") EstadoVenta estado,
                             @Param("desde") LocalDateTime desde,
                             @Param("hasta") LocalDateTime hasta,
                             Pageable pageable);
}
