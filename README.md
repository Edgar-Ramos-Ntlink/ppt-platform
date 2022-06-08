# invoice manager

<!-- ABOUT THE PROJECT -->
## Acerca del proyecto

El invoice manager es un sistema que administra en un portal web las facturas generadas por una empresa, por medio de diferentes roles, los cuales tienen diferentes permisos para crear, aprobar y timbrar facturas.


Los roles principales son:
* Promotor : Genera cotizaciones,carga pagos y da de alta a los clientes.
* Operaciones: Valida que la cotizacón se halla realizado correctamente validando requerimientso fiscales,si todo es correcto se procede a timbrar la cotizacion convirtiendola en una factura.
* Tesoreria: Valida pagos y asegura que las entradas y salidas de flujo de capital sean correctas
* Contabilidad: Valida, extrae y verifica todos los temas relacionados a contabilidad y declaraciones fiscales
* Legal: Se encarga de dar de alta a las empresas, y administrar los datos referentes a las empresas
* Administrador: Puede realizar todas acciones de los anteriores, aparate de permitir el alta/desactivacion/ cambio de password de usuarios, revision de historico de pagos, alta de empresas, etc.



### Construido con

El poryecto fue realizado pricnipalmente con Java 11 y Angular 8
* [java 11](https://www.oracle.com/mx/java/technologies/javase-jdk11-downloads.html)
* [Angular 12](https://angular.io)



<!-- GETTING STARTED -->
## Para iniciar

* Construye el proyecto desde el folder raíz evitando creacion de imagen de docker
  ```sh
  mvn clean package install -Ddockerfile.skip
  ```

* Inicializa los servicios de backend, desde la carpeta de <code>invoice-manager-services</code>
  ```sh
  mvn spring-boot:run
  ```
  
* Inicializa el front de angular desde <code>ntlink-manager-ui</code>
  ```
  npm start
  ```

* Ingresa en tu navegador
  ```sh
  http://localhost:4200
  ```

## Construccion del proyecto

* PASO 1 Compilar interfaz grafica
  ``` sh
  npm run build:prod
  ```
* Compila los modulos del proyecto (package copia los archivos estaticos creados por angular) y genera la imagen de docker
  ```sh
  mvn clean package install
  ```

* Corre el proyecto completo desde el fat jar
 ```sh
  java -jar ./invoice-manager-services/tarjet/invoice-manager-services-3.x.x.jar
  ```
* Ingresa en tu navegador
  ```sh
  http://localhost:8080
  ```


### Prerequisites

Para desarrollar este proyecto es necesario

* java 11
* node js 12
* maven 3.5 o mayor
* angular 12
* docker

<!-- ROADMAP -->
## Roadmap

- [x] 1.0.0 First Phase
- [x] 2.0.0 Second Phase
- [x] 3.0.0 Empresas component Upgrade
  - [x] 3.1.0 Empresas component Upgrade Design 2
  - [x] 3.2.0 Empresas component Upgrade Design 2
  - [x] 3.3.0 Empresas component Upgrade Design 2
  - [x] 3.4.0 Cancelaciones 2.0 SAT
  - [x] 3.5.0 Linea D , new Regimen Fiscal and operative logic for Empresas
    - [x] 3.5.1 Empresas requeriment Fixes 
    - [x] 3.5.2 Facturas Filters and Read me
    - [x] 3.5.3 Sustitucion for Facturas Timbradas
    - [x] 3.5.4 Factura reportes with Folio Fiscal
- [x] 4.0.0 Angular 12 update, CFDI 4.0 models integration, dynamic dashboard
  - [x] 4.1.0 Removing External SDK module
  - [x] 4.2.0 Removing S3 dependencies
  - [x] 4.3.0 Factura Reportes CFDI 4.0
  - [x] 4.4.0 Payment Complement version 2.0
  - [x] 4.4.1 Comeplementos link
  - [x] 4.5.0 Docker integration
  - [x] 4.5.1 Disable security for actuator
  - [x] 4.5.2 Cancel compelemento
  - [x] 4.5.3 Errase compelemento payment
  - [x] 4.5.4 sustitution feature
  - [x] 4.5.5 Multi Complement feature
  - [x] 4.5.6 Contabilidad Payments
  - [x] 4.5.7 Clients DB model refactor to flat object
  - [x] 4.5.8 Enable Json Logs
  - [x] 4.5.9 database pool setup
  - [x] 4.5.10 bug fixes
  - [x] 4.5.11 Pdf & General fixes
  - [x] 4.5.12 Companies improvements
