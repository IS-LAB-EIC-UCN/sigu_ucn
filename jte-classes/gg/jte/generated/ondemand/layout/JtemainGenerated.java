package gg.jte.generated.ondemand.layout;
import gg.jte.Content;
@SuppressWarnings("unchecked")
public final class JtemainGenerated {
	public static final String JTE_NAME = "layout/main.jte";
	public static final int[] JTE_LINE_INFO = {0,0,2,2,2,2,11,11,11,11,20,20,21,21,21,23,23,28,28,44,44,47,47,47,51,51,51,2,3,4,4,4,4};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, String title, String usuarioNombre, Content content) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"es\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n    <title>");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(title);
		jteOutput.writeContent("</title>\r\n    <link rel=\"stylesheet\" href=\"/css/styles.css\">\r\n    <script src=\"https://code.jquery.com/jquery-3.7.1.min.js\"></script>\r\n    <script src=\"/js/app.js\"></script>\r\n</head>\r\n<body>\r\n<header class=\"topbar\">\r\n    <div class=\"brand\">SIGU-UCN</div>\r\n    <div class=\"topbar-right\">\r\n        ");
		if (usuarioNombre != null) {
			jteOutput.writeContent("\r\n            <span class=\"user-label\">Bienvenido, ");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(usuarioNombre);
			jteOutput.writeContent("</span>\r\n            <a class=\"logout-btn\" href=\"/logout\">Cerrar sesión</a>\r\n        ");
		}
		jteOutput.writeContent("\r\n    </div>\r\n</header>\r\n\r\n<div class=\"app-container\">\r\n    ");
		if (usuarioNombre != null) {
			jteOutput.writeContent("\r\n        <aside class=\"sidebar\">\r\n            <h3>Módulos</h3>\r\n            <ul class=\"menu-list\">\r\n                <li><a href=\"/home\">Inicio</a></li>\r\n                <li><a href=\"#\">Grupo 1 - Reservas de salas</a></li>\r\n                <li><a href=\"#\">Grupo 2 - Biblioteca</a></li>\r\n                <li><a href=\"#\">Grupo 3 - Talleres</a></li>\r\n                <li><a href=\"#\">Grupo 4 - Soporte</a></li>\r\n                <li><a href=\"#\">Grupo 5 - Inventario</a></li>\r\n                <li><a href=\"#\">Grupo 6 - Eventos</a></li>\r\n                <li><a href=\"#\">Grupo 7 - Cafetería</a></li>\r\n                <li><a href=\"/tutorias\">Grupo 8 - Tutorías</a></li>\r\n                <li><a href=\"#\">Grupo 9 - Estacionamientos</a></li>\r\n            </ul>\r\n        </aside>\r\n    ");
		}
		jteOutput.writeContent("\r\n\r\n    <main class=\"content-area\">\r\n        ");
		jteOutput.setContext("main", null);
		jteOutput.writeUserContent(content);
		jteOutput.writeContent("\r\n    </main>\r\n</div>\r\n</body>\r\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		String title = (String)params.get("title");
		String usuarioNombre = (String)params.get("usuarioNombre");
		Content content = (Content)params.get("content");
		render(jteOutput, jteHtmlInterceptor, title, usuarioNombre, content);
	}
}
