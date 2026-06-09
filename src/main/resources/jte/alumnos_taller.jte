@import java.util.List
@import cl.ucn.app.model.Inscripcion

@param String title
@param String usuarioNombre
@param String rol
@param List<Inscripcion> inscripciones

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section style="padding: 20px; font-family: sans-serif;">
            <h1>Lista de Alumnos</h1>
            <p>Usuario: <strong>${usuarioNombre}</strong></p>
            <p>Rol detectado: <strong style="color: blue;">${rol}</strong></p>

            <div style="margin-bottom: 20px;">
                <a href="/talleres" style="text-decoration: none; color: #0066cc; font-weight: bold;">&larr; Volver a Mis Talleres</a>
            </div>

            <hr style="margin: 20px 0; border: 0; border-top: 1px solid #ccc;">

            <div style="background-color: #f0fdf4; padding: 20px; border-left: 5px solid green; border-radius: 4px;">
                <h2>Alumnos Inscritos / En Espera</h2>
                
                @if(inscripciones == null || inscripciones.isEmpty())
                    <p>Aún no hay alumnos inscritos en este taller.</p>
                @else
                    <table style="width: 100%; border-collapse: collapse; margin-top: 15px;">
                        <thead>
                            <tr style="background-color: #e5e7eb; text-align: left;">
                                <th style="padding: 10px; border: 1px solid #ccc;">Nombre</th>
                                <th style="padding: 10px; border: 1px solid #ccc;">Correo</th>
                                <th style="padding: 10px; border: 1px solid #ccc;">Estado</th>
                            </tr>
                        </thead>
                        <tbody>
                            @for(Inscripcion i : inscripciones)
                                <tr>
                                    <td style="padding: 10px; border: 1px solid #ccc;">${i.getUsuario().getNombre()}</td>
                                    <td style="padding: 10px; border: 1px solid #ccc;">${i.getUsuario().getCorreo()}</td>
                                    <td style="padding: 10px; border: 1px solid #ccc;">
                                        @if("INSCRITO".equals(i.getEstado()))
                                            <span style="color: green; font-weight: bold;">INSCRITO</span>
                                        @elseif("EN_ESPERA".equals(i.getEstado()))
                                            <span style="color: orange; font-weight: bold;">EN ESPERA</span>
                                        @elseif("CANCELADO".equals(i.getEstado()))
                                            <span style="color: red; font-weight: bold;">CANCELADO</span>
                                        @else
                                            <span>${i.getEstado()}</span>
                                        @endif
                                    </td>
                                </tr>
                            @endfor
                        </tbody>
                    </table>
                @endif
            </div>
        </section>
    `
)
