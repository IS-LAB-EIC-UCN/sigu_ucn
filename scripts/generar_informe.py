"""Genera PDF y diagramas vectoriales a partir del informe Markdown. Requiere reportlab."""
from pathlib import Path
import re
from xml.sax.saxutils import escape
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet,ParagraphStyle
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import SimpleDocTemplate,Paragraph,Spacer,PageBreak,Table,TableStyle
from reportlab.graphics.shapes import Drawing,Rect,Line,String,Polygon,Circle,Ellipse
from reportlab.graphics import renderSVG

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/"docs/entrega"
AZUL=colors.HexColor("#173b70");GRIS=colors.HexColor("#5d7088");FONDO=colors.HexColor("#edf3fa")
FONT="Helvetica";BOLD="Helvetica-Bold"
if Path("C:/Windows/Fonts/arial.ttf").exists():
    pdfmetrics.registerFont(TTFont("UI","C:/Windows/Fonts/arial.ttf"));pdfmetrics.registerFont(TTFont("UI-Bold","C:/Windows/Fonts/arialbd.ttf"));FONT="UI";BOLD="UI-Bold"

def label(d,x,y,text,size=9,anchor="middle",color=AZUL):
    for i,line in enumerate(text.split("\n")):d.add(String(x,y-i*(size+3),line,fontName=FONT,fontSize=size,textAnchor=anchor,fillColor=color))
def arrow(d,x1,y1,x2,y2,text="",dashed=False):
    d.add(Line(x1,y1,x2,y2,strokeColor=GRIS,strokeWidth=.9,strokeDashArray=[3,3] if dashed else None))
    import math
    a=math.atan2(y2-y1,x2-x1);p=[x2,y2,x2-6*math.cos(a-.4),y2-6*math.sin(a-.4),x2-6*math.cos(a+.4),y2-6*math.sin(a+.4)]
    d.add(Polygon(p,fillColor=GRIS,strokeColor=GRIS))
    if text:label(d,(x1+x2)/2,(y1+y2)/2+6,text,7.5)
def box(d,x,y,w,h,title,lines=()):
    d.add(Rect(x,y,w,h,rx=5,ry=5,fillColor=colors.white,strokeColor=AZUL,strokeWidth=.8))
    d.add(Rect(x,y+h-24,w,24,fillColor=FONDO,strokeColor=None))
    label(d,x+9,y+h-16,title,10,"start")
    for i,text in enumerate(lines):label(d,x+8,y+h-40-i*13,text,8,"start",GRIS)
def clases():
    d=Drawing(500,450)
    box(d,0,350,145,85,"Rol",["id, nombre"])
    box(d,180,350,145,85,"Usuario",["nombre, correo, passwordHash","activo, rol"])
    box(d,355,350,145,85,"Espacio",["nombre, tipo, capacidad","disponible"])
    box(d,0,175,145,85,"Categoria",["id, nombre"])
    box(d,180,175,145,100,"Evento",["titulo, fecha, horas, estado","cupoMaximo, organizador","espacio, categoria"])
    box(d,0,0,145,90,"Inscripcion",["usuario, evento, fecha","estado, asistencia"])
    box(d,355,0,145,90,"Expositor",["nombre, correo, institucion","especialidad"])
    arrow(d,145,393,180,393,"1 : *")
    arrow(d,250,350,250,275,"1 organiza *")
    arrow(d,390,350,315,275,"1 : *")
    arrow(d,145,218,180,218,"1 : *")
    arrow(d,145,75,180,175,"* : 1 evento")
    d.add(Line(145,40,160,40,strokeColor=GRIS));d.add(Line(160,40,160,375,strokeColor=GRIS));arrow(d,160,375,180,375)
    label(d,164,310,"* : 1 usuario",7.5,"start")
    arrow(d,355,90,315,175,"* : *")
    label(d,365,120,"evento_expositores",7.5,"start")
    return d

def casos():
    d=Drawing(500,410);d.add(Rect(125,5,370,400,rx=6,ry=6,fillColor=colors.white,strokeColor=AZUL));label(d,310,388,"Módulo 6 — usuarios autenticados",11)
    actors=[("ADMIN",345,["Usuarios, roles, salas y categorías","Supervisar eventos publicados"]),("DOCENTE",220,["Programar y publicar eventos","Expositores, inscritos y asistencia"]),("ESTUDIANTE",95,["Consultar agenda e inscribirse","Cancelar inscripción en plazo"])]
    for actor,y,items in actors:
        d.add(Circle(50,y+13,8,fillColor=None,strokeColor=GRIS));d.add(Line(50,y+5,50,y-18,strokeColor=GRIS));d.add(Line(33,y-2,67,y-2,strokeColor=GRIS));d.add(Line(50,y-18,35,y-33,strokeColor=GRIS));d.add(Line(50,y-18,65,y-33,strokeColor=GRIS));label(d,55,y-48,actor,8)
        for j,item in enumerate(items):
            cy=y+5-j*45;d.add(Ellipse(305,cy,157,18,fillColor=FONDO,strokeColor=AZUL));label(d,305,cy-3,item,8.2);d.add(Line(70,y-3,148,cy,strokeColor=GRIS))
    return d

def secuencia(evento=False):
    names=["Organizador","Controlador","Servicio","Repositorio","PostgreSQL"] if evento else ["Participante","Controlador","Repositorio","PostgreSQL"]
    xs=[45+i*(410/(len(names)-1)) for i in range(len(names))]
    d=Drawing(500,350)
    for x,name in zip(xs,names):
        d.add(Rect(x-43,318,86,25,fillColor=FONDO,strokeColor=AZUL));label(d,x,327,name,8);d.add(Line(x,318,x,10,strokeColor=GRIS,strokeDashArray=[3,4]))
    messages=[(0,1,"POST formulario"),(1,2,"guardar(datos)"),(2,2,"Validar fecha, sala y cupo"),(2,3,"save / update"),(3,4,"BEGIN + bloqueo agenda"),(3,4,"Comprobar horario"),(4,3,"Sin conflicto"),(3,4,"INSERT / UPDATE + COMMIT"),(3,2,"Evento guardado"),(2,1,"Resultado"),(1,0,"Redirección y confirmación")] if evento else [(0,1,"POST inscripción"),(1,2,"inscribir(evento,usuario)"),(2,3,"BEGIN + bloquear evento"),(3,2,"Evento actual"),(2,3,"Validar estado, plazo y cupo"),(3,2,"Resultados"),(2,3,"INSERT / reactivar registro"),(2,3,"COMMIT"),(2,1,"Operación confirmada"),(1,0,"Redirección y confirmación")]
    for i,(a,b,text) in enumerate(messages):
        y=298-i*25
        if a==b:
            d.add(Line(xs[a],y,xs[a]+20,y,strokeColor=GRIS));d.add(Line(xs[a]+20,y,xs[a]+20,y-9,strokeColor=GRIS));arrow(d,xs[a]+20,y-9,xs[a],y-9);label(d,xs[a]+24,y-3,text,7,"start")
        else:arrow(d,xs[a],y,xs[b],y,text,dashed=a>b)
    return d

def actividad(asistencia=False):
    d=Drawing(500,400);x=210
    d.add(Circle(x,385,5,fillColor=AZUL,strokeColor=AZUL))
    def node(y,text):box(d,x-115,y,230,34,text)
    node(325,"Abrir inscritos" if asistencia else "Consultar agenda y detalle");arrow(d,x,380,x,359)
    node(260,"Elegir participante" if asistencia else "Confirmar inscripción");arrow(d,x,325,x,294)
    points=[x,230,x+120,195,x,160,x-120,195];d.add(Polygon(points,fillColor=FONDO,strokeColor=AZUL))
    label(d,x,200,"¿Organizador responsable" if asistencia else "¿Publicado, en plazo,",9);label(d,x,186,"y registro activo?" if asistencia else "sin duplicado y con cupo?",9)
    arrow(d,x,260,x,230);arrow(d,x,160,x,125,"Sí")
    node(90,"Guardar/corregir asistencia" if asistencia else "Guardar inscripción y COMMIT")
    node(25,"Actualizar lista" if asistencia else "Confirmar cupo reservado");arrow(d,x,90,x,59)
    d.add(Circle(x,8,5,fillColor=AZUL,strokeColor=AZUL));arrow(d,x,25,x,13)
    box(d,355,175,140,45,"Informar motivo",["No modificar registro"]);arrow(d,x+120,195,355,195,"No")
    return d

def main():
    dibujos={"casos":casos(),"clases":clases(),"secuencia-inscripcion":secuencia(),"secuencia-evento":secuencia(True),"actividad-inscripcion":actividad(),"actividad-asistencia":actividad(True)}
    for name,d in dibujos.items():renderSVG.drawToFile(d,str(OUT/"diagramas"/(name+".svg")))
    styles=getSampleStyleSheet()
    styles.add(ParagraphStyle(name="Texto",fontName=FONT,fontSize=10,leading=15,textColor=colors.HexColor("#26364b"),spaceAfter=9))
    styles.add(ParagraphStyle(name="Titulo",fontName=BOLD,fontSize=29,leading=34,textColor=AZUL,spaceAfter=22))
    styles.add(ParagraphStyle(name="Seccion",fontName=BOLD,fontSize=19,leading=24,textColor=AZUL,spaceAfter=18))
    styles.add(ParagraphStyle(name="Celda",fontName=FONT,fontSize=8.2,leading=11))
    def para(text,style="Texto"):
        text=escape(text);text=re.sub(r"\*\*(.*?)\*\*",r"<b>\1</b>",text);return Paragraph(text,styles[style])
    story=[];lines=(OUT/"informe-modulo-6.md").read_text(encoding="utf-8").splitlines();i=0;buffer=[]
    def flush():
        if buffer:story.append(para(" ".join(buffer)));buffer.clear()
    while i<len(lines):
        line=lines[i].strip()
        if not line:flush();i+=1;continue
        if line.startswith("!["):
            flush();name=re.search(r"diagramas/([^)]+)\.svg",line)[1];story.append(dibujos[name]);story.append(Spacer(1,14));i+=1;continue
        if line.startswith("|"):
            flush();rows=[]
            while i<len(lines) and lines[i].strip().startswith("|"):
                cells=[v.strip() for v in lines[i].strip().strip("|").split("|")]
                if not all(re.fullmatch(r"[: -]+",v) for v in cells):rows.append([para(v,"Celda") for v in cells])
                i+=1
            n=len(rows[0]);widths=[65]+[(500-65)/(n-1)]*(n-1)
            table=Table(rows,colWidths=widths,repeatRows=1,hAlign="LEFT")
            table.setStyle(TableStyle([("BACKGROUND",(0,0),(-1,0),FONDO),("VALIGN",(0,0),(-1,-1),"TOP"),("GRID",(0,0),(-1,-1),.4,colors.HexColor("#dbe4ee")),("TOPPADDING",(0,0),(-1,-1),8),("BOTTOMPADDING",(0,0),(-1,-1),8)]));story.append(table);story.append(Spacer(1,15));continue
        if line.startswith("## "):
            flush()
            if re.match(r"## \d+\.",line):story.append(PageBreak())
            story.append(para(line[3:],"Seccion"));i+=1;continue
        if line.startswith("# "):flush();story.append(Spacer(1,35));story.append(para(line[2:],"Titulo"));i+=1;continue
        if line.startswith("- "):flush();story.append(para("• "+line[2:]));i+=1;continue
        buffer.append(line);i+=1
    flush()
    def pie(canvas,doc):
        canvas.setStrokeColor(colors.HexColor("#dbe4ee"));canvas.line(42,37,553,37);canvas.setFont(FONT,8);canvas.setFillColor(GRIS);canvas.drawString(42,24,"SIGU-UCN · Grupo 6 · Informe técnico para revisión");canvas.drawRightString(553,24,str(doc.page))
    doc=SimpleDocTemplate(str(OUT/"informe-modulo-6.pdf"),pagesize=A4,leftMargin=45,rightMargin=45,topMargin=45,bottomMargin=50,title="Informe técnico · Módulo 6 · SIGU-UCN",author="Grupo 6")
    doc.build(story,onFirstPage=pie,onLaterPages=pie)
    print("PDF y seis diagramas SVG generados en",OUT)

if __name__=="__main__":main()
