# Capturas de la versión final

Generadas con Chrome mediante `python scripts/capturar_entrega.py`: escritorio de 1366 × 900 y móvil de 390 × 844. Se verificó que no hubiera desbordamiento horizontal en las agendas móviles. El script solo consulta datos.

| Rol | Pantallas |
| --- | --- |
| ADMIN | [Agenda](capturas/admin-1.png), [Salas y categorías](capturas/admin-2.png), [Usuarios](capturas/admin-3.png), [Roles](capturas/admin-4.png), [Móvil](capturas/admin-movil.png). |
| DOCENTE | [Eventos propios](capturas/docente-1.png), [Expositores y ubicaciones](capturas/docente-2.png), [Móvil](capturas/docente-movil.png). |
| ESTUDIANTE | [Agenda](capturas/estudiante-1.png), [Móvil](capturas/estudiante-movil.png). |

Las capturas pertenecen a las cuentas de demostración. ADMIN carece de Mis actividades y gestión de expositores; ESTUDIANTE carece de acceso a catálogos. El detalle de los eventos conserva ubicación y expositores.

Para regenerarlas se requieren Chrome instalado y Playwright como herramienta de desarrollo (`python -m pip install playwright`); no es una dependencia de la aplicación Java.
