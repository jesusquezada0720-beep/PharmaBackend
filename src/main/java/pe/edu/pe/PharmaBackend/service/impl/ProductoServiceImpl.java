package pe.edu.pe.PharmaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.pe.PharmaBackend.dto.PaginaResponseDTO;
import pe.edu.pe.PharmaBackend.dto.ProductoRequestDTO;
import pe.edu.pe.PharmaBackend.dto.ProductoResponseDTO;
import pe.edu.pe.PharmaBackend.entity.Categoria;
import pe.edu.pe.PharmaBackend.entity.Producto;
import pe.edu.pe.PharmaBackend.exception.RecursoNoEncontradoException;
import pe.edu.pe.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.pe.PharmaBackend.repository.CategoriaRepository;
import pe.edu.pe.PharmaBackend.repository.ProductoRepository;
import pe.edu.pe.PharmaBackend.service.service.ProductoService;

import java.util.Set;

@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoServiceImpl.class);
    private static final int TAMANIO_MAXIMO = 100;

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public ProductoResponseDTO create(ProductoRequestDTO t) {
        String nombre = t.getNombre().trim();
        if (productoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe un producto con el nombre " + nombre);
        }
        Categoria categoria = buscarCategoriaPorId(t.getCategoriaId());

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(t.getPrecio());
        producto.setStock(t.getStock());
        producto.setEstado(t.getEstado());
        producto.setCategoria(categoria);

        Producto creado = productoRepository.save(producto);
        LOG.info("Producto creado con id={}", creado.getId());
        return convertirResponse(creado);
    }

    @Override
    @Transactional
    public ProductoResponseDTO update(Long id, ProductoRequestDTO t) {
        Producto producto = productoRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Producto no encontrado con id: " + id));

        String nombre = t.getNombre().trim();
        if (productoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otro producto con el nombre " + nombre);
        }
        Categoria categoria = buscarCategoriaPorId(t.getCategoriaId());

        producto.setNombre(nombre);
        producto.setPrecio(t.getPrecio());
        producto.setStock(t.getStock());
        producto.setEstado(t.getEstado());
        producto.setCategoria(categoria);

        Producto actualizado = productoRepository.save(producto);
        LOG.info("Producto id={} actualizado", id);
        return convertirResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO read(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
        return convertirResponse(producto);
    }

    /** Baja lógica: el producto queda con estado = false. */
    @Override
    @Transactional
    public void delete(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Producto no encontrado con id: " + id));

        if (!Boolean.TRUE.equals(producto.getEstado())) {
            throw new ReglaNegocioException("El producto ya se encuentra inactivo");
        }
        producto.setEstado(false);
        productoRepository.save(producto);
        LOG.info("Producto id={} dado de baja", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ProductoResponseDTO> readAll() {
        return productoRepository.findAll().stream().map(this::convertirResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponseDTO<ProductoResponseDTO> listar(
            int pagina, int tamanio, String ordenarPor, String direccion) {

        Set<String> permitidos = Set.of("id", "nombre", "precio", "stock");
        if (!permitidos.contains(ordenarPor)) {
            throw new ReglaNegocioException(
                    "Campo de orden no válido: " + ordenarPor + ". Use: id, nombre, precio o stock");
        }

        Sort orden = "desc".equalsIgnoreCase(direccion)
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Page<Producto> pag = productoRepository.findAll(
                PageRequest.of(Math.max(pagina, 0),
                        Math.min(Math.max(tamanio, 1), TAMANIO_MAXIMO), orden));

        return new PaginaResponseDTO<>(
                pag.getContent().stream().map(this::convertirResponse).toList(),
                pag.getNumber(),
                pag.getSize(),
                pag.getTotalElements(),
                pag.getTotalPages(),
                pag.isLast()
        );
    }

    private Categoria buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Categoria no encontrada con id: " + id));
    }

    private ProductoResponseDTO convertirResponse(Producto p) {
        return new ProductoResponseDTO(
                p.getId(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock(),
                p.getEstado(),
                p.getCategoria() != null ? p.getCategoria().getId() : null,
                p.getCategoria() != null ? p.getCategoria().getNombre() : null,
                p.getFechaCreacion(),
                p.getFechaModificacion()
        );
    }
}