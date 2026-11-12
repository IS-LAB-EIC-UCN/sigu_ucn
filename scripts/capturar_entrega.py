"""Capturas de demostración. Requiere Playwright y Chrome; no modifica registros."""
from pathlib import Path
import argparse
from playwright.sync_api import sync_playwright


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--base", default="http://localhost:7000")
    parser.add_argument("--salida", default="docs/entrega/capturas")
    args = parser.parse_args()
    destino = Path(args.salida)
    destino.mkdir(parents=True, exist_ok=True)
    with sync_playwright() as p:
        browser = p.chromium.launch(channel="chrome", headless=True)
        for rol in ["admin", "docente", "estudiante"]:
            context = browser.new_context(viewport={"width": 1366, "height": 900})
            page = context.new_page()
            page.goto(args.base + "/login")
            page.locator("#correo").fill(rol + "@sigu.cl")
            page.locator("#password").fill(rol + "123")
            page.locator('button[type="submit"]').click()
            page.wait_for_url("**/home")
            rutas = ["/eventos"]
            if rol == "admin":
                rutas += ["/eventos/catalogos", "/eventos/usuarios", "/eventos/roles"]
            elif rol == "docente":
                rutas += ["/eventos/catalogos"]
            for index, ruta in enumerate(rutas):
                response = page.goto(args.base + ruta)
                assert response.status == 200, (rol, ruta)
                if ruta == "/eventos":
                    assert page.locator('a[href="/eventos?vista=mios"]').count() == (0 if rol == "admin" else 1)
                    assert page.locator('a[href="/eventos/catalogos"]').count() == (0 if rol == "estudiante" else 1)
                if rol == "admin" and ruta.endswith("catalogos"):
                    assert page.get_by_role("heading", name="Expositores").count() == 0
                page.screenshot(path=str(destino / f"{rol}-{index + 1}.png"), full_page=True)
            page.set_viewport_size({"width": 390, "height": 844})
            page.goto(args.base + "/eventos")
            assert page.evaluate("document.documentElement.scrollWidth <= window.innerWidth + 1"), rol
            page.screenshot(path=str(destino / f"{rol}-movil.png"), full_page=True)
            context.close()
            print(rol + ": vistas de escritorio y móvil verificadas")
        browser.close()


if __name__ == "__main__":
    main()
