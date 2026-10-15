package pe.edu.pe.PharmaBackend.dto;

import java.math.BigDecimal;

public class VentaPorCategoriaDTO {
    private String categoria;
    private Long unidades;
    private BigDecimal monto;

    public VentaPorCategoriaDTO(String categoria, Long unidades, BigDecimal monto) {
        this.categoria = categoria;
        this.unidades = unidades;
        this.monto = monto;
    }

    public String getCategoria() {
        return categoria;
    }

    public Long getUnidades() {
        return unidades;
    }

    public BigDecimal getMonto() {
        return monto;
    }
}
