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
  npm run build
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
  - [x] 4.5.13 Fix invoice and payment complements
  - [x] 4.5.14 Linea E functionalities
  - [x] 4.5.15 Email stamping Contabilidad
  - [x] 4.5.16 Fix Pre folio calculation
  - [x] 4.5.17 Enable CFDI creation on promotor view
  - [x] 4.5.18 Tickets NTLINK-148 NTLINK-149 NTLINK-158
  - [x] 4.5.19 habilitar modulo de clientes en operaciones, correcion de bug colonias en vista de clientes y agregar tooltip en reporte facturas
  - [x] 4.5.20 set Version during  invoice creation
  - [x] 4.5.21 fiscal regimen bugfix correction and enable emisor edition
  - [x] 4.5.22 Permitir edicion  de CFDIs a administradores en modulo contabilidad
  - [x] 4.5.23 Se repar issue de total de pago de complemento para cancelacion
  - [x] 4.5.24 Cambios en vista CFDI (se agerga prefolio, UUID, version, promotor y fecha de cracion), se corrige null pointer cancelacion PPD, se habilita lugar de expedicion y se corrigen redireccion entre CFDI relacionados plataforma
  - [x] 4.5.25 Solucion de multiples bugs
    - Enable stamp complements on accounting module 
    - Fix base factura fields bugs 
    - Redirect when factura is replaced 
    - Asign stamp status on complements 
    - Fix payments validations on Tesoreria 
    - Fix payments data when payment is created 
    - Allow edition on invoice status 1,2 and 4 
    - fix name assignation on pre-cfdi
  - [x] 4.5.26 Enable lock and unlock logic on companies only for legal and admin areas and account creation validation
  - [x] 4.5.27 Multicomplementos upgrades, payments upgrades, fix complement generation, fix status invoice updates
  - [x] 4.5.28 Fix null pointer on payment delete
  - [x] 4.5.29 Multicomplments view simplification,Fix on multicomplements payment delete, disable regimen fiscal, inactive client validation, allow on line-x search all client promotors, minor UI fixes
  - [x] 4.5.30 Correccion en el borrado de complementos pago
  - [x] 4.5.31 Removing CFDI 33 invoice references and fix payform cfdi logic selection
  - [x] 4.5.32 Fix complement creation from PPD 3.3, enable update complements, adding folio PPD on complements view and minor UI fixes
  - [x] 4.5.33 Setup max and min memory jvm heap,adding logs when a mail is sent, removing dead code and enable payments visualization
  - [x] 4.6.0  Fix dashboard load bug, recover payment complement data from companies and clients, by default from parent invoice, fix mail sent to clients
  - [x] 4.6.1  Fix Stamp date in Complement csv export from the tables
  - [x] 4.6.2  Fix payment complement on line A
  - [x] 4.6.3  Fix complement reports(folio,UUID and payment form)
  - [x] 4.6.4 Addinng JSON editor on accounting module
  - [x] 4.6.5 Lock cfdi emisor fields edition and fix pending debt issue
  - [x] 4.6.6 Fix Invoices without taxes 
  - [x] 4.6.7 Improvements on client creation validation, pdf regeneration and invoice amount reduction improvements 
  - [x] 4.6.8 Reactive concept validation(6 decimal validation) and fix update missing invoice amount on concepts update. adding extra logs
  - [x] 4.6.9 Adding complement parent UUID in report
  - [x] 4.6.10 Support module for  users support request
  - [x] 4.6.11 Custom date serialization on invoice reports and allow manage spring active profile externally}
  - [x] 4.6.12 Fixing Complementos payment form and  massive charge in contabilidad
  - [x] 4.7.0 Importing ppt-utils and NTlink sdk refactor
  - [x] 4.7.1 Adding Pue Default payment method
  - [x] 4.7.2 Adding ppd payment uso cfdi Default
  - [x] 4.7.3 Stamp timeout and report status as a text
  - [x] 4.7.4 Support module validation
  - [x] 4.7.5 Fix complementos PDF total calculation