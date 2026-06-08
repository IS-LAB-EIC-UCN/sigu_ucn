package gg.jte.generated.ondemand;
@SuppressWarnings("unchecked")
public final class JteloginGenerated {
	public static final String JTE_NAME = "login.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,16,16,16,18,18,18,20,20,48,48,48,0,0,0,0};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, String error) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"es\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n    <title>Iniciar sesión</title>\r\n    <link rel=\"stylesheet\" href=\"/css/styles.css\">\r\n</head>\r\n<body>\r\n<div class=\"login-container\">\r\n    <div class=\"login-card\">\r\n        <h1>SIGU-UCN</h1>\r\n        <h2>Iniciar sesión</h2>\r\n\r\n        ");
		if (error != null && !error.isBlank()) {
			jteOutput.writeContent("\r\n            <div class=\"error-message\">\r\n                ");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(error);
			jteOutput.writeContent("\r\n            </div>\r\n        ");
		}
		jteOutput.writeContent("\r\n\r\n        <form method=\"post\" action=\"/login\" class=\"login-form\">\r\n            <div class=\"form-group\">\r\n                <label for=\"correo\">Correo</label>\r\n                <input\r\n                        type=\"email\"\r\n                        id=\"correo\"\r\n                        name=\"correo\"\r\n                        placeholder=\"Ingrese su correo\"\r\n                        required>\r\n            </div>\r\n\r\n            <div class=\"form-group\">\r\n                <label for=\"password\">Contraseña</label>\r\n                <input\r\n                        type=\"password\"\r\n                        id=\"password\"\r\n                        name=\"password\"\r\n                        placeholder=\"Ingrese su contraseña\"\r\n                        required>\r\n            </div>\r\n\r\n            <button type=\"submit\" class=\"btn-login\">Ingresar</button>\r\n        </form>\r\n    </div>\r\n</div>\r\n</body>\r\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		String error = (String)params.get("error");
		render(jteOutput, jteHtmlInterceptor, error);
	}
}
