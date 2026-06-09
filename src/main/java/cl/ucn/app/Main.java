package cl.ucn.app;

import io.javalin.Javalin;
import cl.ucn.app.controller.TicketController;

public class Main {
    public static void main(String[] args) {
        TicketController ticketController = new TicketController();

        Javalin.start(config -> {
            config.routes.get("/", ctx -> {
                ctx.contentType("text/html; charset=UTF-8");
                String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>SIGU-UCN - Sistema de Tickets</title>
                        <style>
                            body {
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                margin: 0;
                                padding: 20px;
                                background-color: #f4f6f9;
                            }
                            .container {
                                max-width: 800px;
                                margin: 0 auto;
                                background: white;
                                border-radius: 8px;
                                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                                padding: 25px;
                            }
                            h1 {
                                color: #2c3e50;
                                border-bottom: 2px solid #3498db;
                                padding-bottom: 10px;
                            }
                            ul {
                                list-style: none;
                                padding: 0;
                            }
                            li {
                                margin: 15px 0;
                            }
                            a {
                                text-decoration: none;
                                background-color: #3498db;
                                color: white;
                                padding: 10px 20px;
                                border-radius: 5px;
                                display: inline-block;
                                transition: background 0.3s;
                            }
                            a:hover {
                                background-color: #2980b9;
                            }
                            footer {
                                margin-top: 30px;
                                text-align: center;
                                color: #7f8c8d;
                                font-size: 0.9em;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h1>Sistema de Gestion de Tickets de Soporte</h1>
                            <p>Grupo 4 - Taller de Ingenieria de Software</p>
                            <ul>
                                <li><a href="/tickets">Listar todos los tickets</a></li>
                                <li><a href="/tickets/crear">Crear nuevo ticket</a></li>
                                <li><a href="/tickets/1">Ver ticket con ID 1 (ejemplo)</a></li>
                            </ul>
                            <footer>UCN - 2026</footer>
                        </div>
                    </body>
                    </html>
                """;
                ctx.html(html);
            });

            // Formulario de creacion (ASCII puro)
            config.routes.get("/tickets/crear", ctx -> {
                ctx.contentType("text/html; charset=UTF-8");
                String formHtml = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Crear Ticket</title>
                        <style>
                            body { font-family: Arial, sans-serif; margin: 40px; }
                            .form-group { margin-bottom: 15px; }
                            label { display: block; margin-bottom: 5px; font-weight: bold; }
                            input, select, textarea { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
                            button { background-color: #2ecc71; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }
                            button:hover { background-color: #27ae60; }
                        </style>
                    </head>
                    <body>
                        <h2>Crear nuevo ticket</h2>
                        <form action="/tickets" method="post">
                            <div class="form-group">
                                <label>Titulo:</label>
                                <input type="text" name="titulo" required>
                            </div>
                            <div class="form-group">
                                <label>Descripcion:</label>
                                <textarea name="descripcion" rows="4" required></textarea>
                            </div>
                            <div class="form-group">
                                <label>Prioridad:</label>
                                <select name="prioridad">
                                    <option value="BAJA">Baja</option>
                                    <option value="MEDIA">Media</option>
                                    <option value="ALTA">Alta</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Estado:</label>
                                <select name="estado">
                                    <option value="ABIERTO">Abierto</option>
                                    <option value="EN_PROCESO">En proceso</option>
                                    <option value="CERRADO">Cerrado</option>
                                </select>
                            </div>
                            <button type="submit">Enviar ticket</button>
                        </form>
                        <p><a href="/">Volver al menu</a></p>
                    </body>
                    </html>
                """;
                ctx.html(formHtml);
            });

            ticketController.registerRoutes(config);
        });
    }
}