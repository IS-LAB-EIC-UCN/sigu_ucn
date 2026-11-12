"""Exporta una presentación PDF desde Markdown. ReportLab es solo una herramienta documental."""
from pathlib import Path
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import landscape,A4
from reportlab.lib import colors
from reportlab.lib.utils import simpleSplit
from reportlab.graphics import renderPDF
from generar_informe import FONT,BOLD,clases
ROOT=Path(__file__).resolve().parents[1]
def main():
    root=ROOT/"docs/entrega"
    slides=(root/"presentacion.md").read_text(encoding="utf-8").split("\n---\n")
    c=canvas.Canvas(str(root/"presentacion.pdf"),pagesize=landscape(A4));w,h=landscape(A4)
    c.setTitle("Presentación · Eventos académicos · Grupo 6");c.setAuthor("Grupo 6")
    for index,slide in enumerate(slides,1):
        lines=[l.strip() for l in slide.splitlines() if l.strip()];title=lines[0].lstrip("# ")
        c.setFillColor(colors.HexColor("#173b70"));c.rect(0,h-110,w,110,fill=1,stroke=0)
        c.setFillColor(colors.white);c.setFont(BOLD,26);c.drawString(45,h-65,title)
        c.setFillColor(colors.HexColor("#26364b"));y=h-160
        for text in lines[1:]:
            if text=="[CLASES]":
                d=clases();factor=.76;d.scale(factor,factor);renderPDF.draw(d,c,(w-500*factor)/2,50);continue
            bullet=text.startswith("- ");text=text[2:] if bullet else text
            if bullet:c.setFillColor(colors.HexColor("#315989"));c.rect(48,y+3,6,6,fill=1,stroke=0)
            c.setFillColor(colors.HexColor("#26364b"));c.setFont(FONT,18)
            for line in simpleSplit(text,FONT,18,w-115):c.drawString(65 if bullet else 48,y,line);y-=26
            y-=18
        c.setStrokeColor(colors.HexColor("#dde5ee"));c.line(45,34,w-45,34)
        c.setFillColor(colors.HexColor("#63738a"));c.setFont(FONT,10);c.drawString(45,19,"Grupo 6 · Material para revisión y defensa");c.drawRightString(w-45,19,str(index)+" / "+str(len(slides)))
        c.showPage()
    c.save();print("Presentación generada:",root/"presentacion.pdf")
if __name__=="__main__":main()
