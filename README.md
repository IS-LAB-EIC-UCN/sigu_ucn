# sigu_ucn

# Estructura del Directorio

```
Directory structure:
└── is-lab-eic-ucn-sigu_ucn/
    ├── README.md
    ├── docker-compose.yml
    ├── index.html
    ├── pom.xml
    ├── db/
    │   └── init/
    │       ├── 01_scheme.sql
    │       └── 02_seed.sql
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── cl/
        │   │       └── ucn/
        │   │           └── app/
        │   │               ├── Main.java
        │   │               ├── controller/
        │   │               ├── model/
        │   │               ├── repository/
        │   │               └── service/
        │   └── resources/
        │       └── META-INF/
        │           └── persistence.xml
        └── test/
            └── java/
                └── cl/
                    └── ucn/
                        └── app/
                            └── service/
                                └── ReservaServiceTest.java

```


# Levantar Base de datos

## Requisitos:
* Tener instalado Docker

En la carpeta raiz del proyecto ejecutar el siguiente comando en la terminal:

Para levantar la base de datos

```
docker-compose up -d
```

Para eliminar la base de datos

```
docker-compose down -v
```


# Ejecutar codigo

## Requisitos:
* IDE listo para Java
* Java 17 o superior
* Maven 3.9 o superior

Para ejecutar el programa se debe ejecutar el archivo Main.java

### Ubicacion

```
Directory structure:
└── is-lab-eic-ucn-sigu_ucn/
    ├── index.html
    ├── db/
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── cl/
        │   │       └── ucn/
        │   │           └── app/
        │   │               ├── Main.java  (EJECUTAR ACA)
        │   │               ├── controller/
        │   │               ├── model/
        │   │               ├── repository/
        │   │               └── service/

```


# Ejecutar test unitarios

Para ejecutar los test unitarios se debe de ejecutar el siguiente archivo ReservaServiceTest.java

### Ubicacion

```
Directory structure:
└── is-lab-eic-ucn-sigu_ucn/
    ├── index.html
    ├── db/
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── cl/
        │   │       └── ucn/
        │   │           └── app/
        │   │               ├── Main.java
        │   │               ├── controller/
        │   │               ├── model/
        │   │               ├── repository/
        │   │               └── service/
        │   └── resources/
        └── test/
            └── java/
                └── cl/
                    └── ucn/
                        └── app/
                            └── service/
                                └── ReservaServiceTest.java  (EJECUTAR ACA)
```


# ENDPOINTS
Si se desean probar los endpoints de la api y base de datos los pasos a seguir son:

* Levantar Bases de datos
* Ejecutar el codigo de Main.java
* Ejecutar el archivo index.html

En el archivo index.html se  pueden probar todos los endpoints de la api creada.
