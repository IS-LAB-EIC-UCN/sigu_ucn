package cl.ucn.app.dto;

/**
 * Datos ingresados en el formulario "Registrar producto" (CU01).
 * Los valores llegan tal como los escribió el usuario; el servicio los valida.
 *
 * @param tipo        "EQUIPO" o "INSUMO"
 * @param stockMinimo solo aplica a insumos
 */
public record NuevoProductoDTO(
        String tipo,
        String codigo,
        String nombre,
        String descripcion,
        Long categoriaId,
        Long proveedorId,
        Integer stockMinimo
) {
}
