package gg.jte.generated.ondemand;
@SuppressWarnings("unchecked")
public final class JtehomeGenerated {
	public static final String JTE_NAME = "home.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,3,3,7,7,37,37,37,37,37,0,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, String title, String usuarioNombre) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.layout.JtemainGenerated.render(jteOutput, jteHtmlInterceptor, title, usuarioNombre, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <section class=\"dashboard\">\r\n        <h1>Panel principal</h1>\r\n        <p>Bienvenido al sistema integrado de gestión universitaria.</p>\r\n\r\n        <div class=\"card-grid\">\r\n            <div class=\"info-card\">\r\n                <h3>Instrucciones para los equipos</h3>\r\n                <p>\r\n                    Cada grupo deberá desarrollar su módulo y reemplazar la opción de menú\r\n                    correspondiente por la ruta real de acceso a su funcionalidad.\r\n                </p>\r\n            </div>\r\n\r\n            <div class=\"info-card\">\r\n                <h3>Integración</h3>\r\n                <p>\r\n                    Las nuevas rutas, vistas y controladores deben respetar la estructura\r\n                    común del sistema base.\r\n                </p>\r\n            </div>\r\n\r\n            <div class=\"info-card\">\r\n                <h3>Tecnologías base</h3>\r\n                <p>\r\n                    Java, Javalin, JPA/Hibernate, PostgreSQL, JTE y jQuery.\r\n                </p>\r\n            </div>\r\n        </div>\r\n    </section>\r\n");
			}
		});
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		String title = (String)params.get("title");
		String usuarioNombre = (String)params.get("usuarioNombre");
		render(jteOutput, jteHtmlInterceptor, title, usuarioNombre);
	}
}
