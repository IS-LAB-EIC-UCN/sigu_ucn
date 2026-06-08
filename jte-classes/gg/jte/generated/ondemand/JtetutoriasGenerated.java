package gg.jte.generated.ondemand;
@SuppressWarnings("unchecked")
public final class JtetutoriasGenerated {
	public static final String JTE_NAME = "tutorias.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,3,3,7,7,16,16,16,16,16,0,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, String title, String usuarioNombre) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.layout.JtemainGenerated.render(jteOutput, jteHtmlInterceptor, title, usuarioNombre, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <section class=\"dashboard\">\r\n        <h1>Panel de tutorías</h1>\r\n        <p>En esta seccion podras tanto ofrecer como buscar tutorías.</p>\r\n\r\n        <div class=\"card-grid\">\r\n\r\n        </div>\r\n    </section>\r\n");
			}
		});
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		String title = (String)params.get("title");
		String usuarioNombre = (String)params.get("usuarioNombre");
		render(jteOutput, jteHtmlInterceptor, title, usuarioNombre);
	}
}
