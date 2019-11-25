----------------
---USER
----------------

CREATE TABLE IF NOT EXISTS USERS (
  ID_USER INT(11) NOT NULL AUTO_INCREMENT,
  ACTIVO TINYINT(1) NOT NULL, 
  CORREO VARCHAR(100) NOT NULL UNIQUE,
  FECHA_CREACION TIMESTAMP,
  FECHA_ACTUALIZACION TIMESTAMP,
  PRIMARY KEY (ID_USER)
);

----------------
---ROLE
----------------

CREATE TABLE IF NOT EXISTS ROLES(
  ID_ROLE INT(11) NOT NULL AUTO_INCREMENT,
  ROLE VARCHAR(50) NOT NULL,
  ROLE_DESC VARCHAR(250),
  ID_USER INT(11) NOT NULL,
  PRIMARY KEY (ID_ROLE),
    CONSTRAINT FK_USER_ID_ROLE
    FOREIGN KEY (ID_USER)
    REFERENCES USERS (ID_USER)
);

----------------
---CONTRIBUYENTES
----------------

CREATE TABLE IF NOT EXISTS CONTRIBUYENTES (
  ID_CONTRIBUYENTE INT(11) NOT NULL AUTO_INCREMENT,
  RFC VARCHAR(15) NOT NULL,
  GIRO VARCHAR(50),
  NOMBRE VARCHAR(50),
  CURP VARCHAR(20),
  RAZON_SOCIAL VARCHAR(100) ,
  REGIMEN_FISCAL VARCHAR(500) ,
  CALLE VARCHAR(100) ,
  NO_EXTERIOR VARCHAR(50) ,
  NO_INTERIOR VARCHAR(50) ,
  COLONIA VARCHAR(50) ,
  MUNICIPIO VARCHAR(50) ,
  LOCALIDAD VARCHAR(50) ,
  ESTADO VARCHAR(50) ,
  PAIS VARCHAR(50) ,
  COO VARCHAR(50) ,
  CODIGO_POSTAL VARCHAR(50) ,
  CORREO VARCHAR(100) ,
  TELEFONO VARCHAR(15) ,
  FECHA_CREACION TIMESTAMP,
  FECHA_ACTUALIZACION TIMESTAMP,
  PRIMARY  KEY (ID_CONTRIBUYENTE),
  UNIQUE (RFC)
);

----------------
---EMPRESAS
----------------

CREATE TABLE IF NOT EXISTS EMPRESAS (
  ID_EMPRESA INT(11) NOT NULL AUTO_INCREMENT,
  LINEA VARCHAR(5) NOT NULL,
  GIRO_ID INT(10) NOT NULL,
  ACTIVO TINYINT(1),
  RFC VARCHAR(15) NOT NULL,
  REGIMEN_FISCAL VARCHAR(15) NOT NULL,
  CONTACTO_ADMIN VARCHAR(50),
  REFERENCIA VARCHAR(200),
  SUCURSAL VARCHAR(50),
  LUGAR_EXPEDICION VARCHAR(100),
  PW_CORREO VARCHAR(50),
  WEB VARCHAR(150),
  CORREO VARCHAR(50),
  ENCABEZADO VARCHAR(250) ,
  PIE_DE_PAGINA VARCHAR(250) ,
  LOGOTIPO TEXT,
  CERTIFICADO TEXT,
  NO_CERTIFICADO VARCHAR(30),
  LLAVE_PRIVADA TEXT,
  PW_SAT VARCHAR(50),
  
  FECHA_CREACION TIMESTAMP,
  FECHA_ACTUALIZACION TIMESTAMP,
  PRIMARY KEY (ID_EMPRESA),
  UNIQUE (RFC)
);

----------------
---CLIENT
----------------

CREATE TABLE IF NOT EXISTS CLIENTES (
  ID_CLIENTE INT(11) NOT NULL AUTO_INCREMENT,
  RFC VARCHAR(50) UNIQUE NOT NULL,
  CORREO_PROMOTOR VARCHAR(50) NOT NULL,
  CORREO_CONTACTO VARCHAR(50) NOT NULL,
  ACTIVO TINYINT(1),
  PORCENTAJE_PROMOTOR INT(3),
  PORCENTAJE_CLIENTE  INT(3),
  PORCENTAJE_CONTACTO INT(3),
  PORCENTAJE_DESPACHO INT(3),
  FECHA_CREACION TIMESTAMP,
  FECHA_ACTUALIZACION TIMESTAMP,
  PRIMARY KEY (ID_CLIENTE),
  UNIQUE (RFC)
);

----------------
---FACTURA
----------------

CREATE TABLE IF NOT EXISTS FACTURAS(
  ID_FACTURA INT(11) NOT NULL AUTO_INCREMENT,
  RFC_EMISOR VARCHAR(15),
  RFC_REMITENTE VARCHAR(15),
  RAZON_SOCIAL_EMISOR VARCHAR(100),
  RAZON_SOCIAL_REMITENTE VARCHAR(100),
  FOLIO VARCHAR(40) NOT NULL UNIQUE,
  FOLIO_PADRE VARCHAR(40) ,
  UUID VARCHAR(40),
  STATUS_FACTURA INTEGER(5) NOT NULL,
  STATUS_PAGO INTEGER(5) NOT NULL,
  STATUS_DEVOLUCION INTEGER(5) NOT NULL,
  STATUS_DETAIL VARCHAR(100),
  TIPO_DOCUMENTO VARCHAR(50) NOT NULL,
  FORMA_PAGO VARCHAR(10) NOT NULL,
  METODO_PAGO VARCHAR(10),
  PACK_FACTURACION VARCHAR(15),
  NOTAS VARCHAR(400),
  TOTAL DOUBLE,
  SUBTOTAL DOUBLE,
  DESCUENTO DOUBLE,
  FECHA_TIMBRADO  TIMESTAMP,
  FECHA_ACTUALIZACION TIMESTAMP,
  FECHA_CREACION TIMESTAMP,
  PRIMARY KEY (ID_FACTURA),
  UNIQUE (FOLIO)
);

CREATE TABLE IF NOT EXISTS FACTURA_ARCHIVO(
  ID_FACTURA_ARCHIVO INT(11) NOT NULL AUTO_INCREMENT,
  FOLIO VARCHAR(40) NOT NULL UNIQUE,
  ARCHIVO_XML TEXT,
  ARCHIVO_PDF TEXT,
  ARCHIVO_QR TEXT,
  FECHA_ACTUALIZACION TIMESTAMP,
  PRIMARY KEY (ID_FACTURA_ARCHIVO),
  UNIQUE (FOLIO)
);

CREATE TABLE IF NOT EXISTS PAGOS(
  ID_PAGO INT(11) NOT NULL AUTO_INCREMENT,
  FOLIO VARCHAR(40) NOT NULL,
  FOLIO_PADRE VARCHAR(40) ,
  MONEDA VARCHAR(5) NOT NULL,
  BANCO VARCHAR(40) NOT NULL,
  DOCUMENTO TEXT,
  TIPO_CAMBIO DOUBLE NOT NULL,
  MONTO DOUBLE NOT NULL,
  REVISION_1 TINYINT,
  REVISION_2 TINYINT,
  STATUS_PAGO VARCHAR(20),
  COMENTARIO_PAGO VARCHAR(250),
  FORMA_PAGO VARCHAR(40) NOT NULL,
  TIPO_PAGO VARCHAR(40) NOT NULL,
  FECHA_CREACION TIMESTAMP,
  FECHA_PAGO TIMESTAMP,
  FECHA_ACTUALIZACION TIMESTAMP,
  PRIMARY KEY (ID_PAGO)
);


CREATE TABLE IF NOT EXISTS DEVOLUCIONES(
  ID_DEVOLUCION INT(11) NOT NULL AUTO_INCREMENT,
  ID_PAGO INT(11),
  FOLIO VARCHAR(40) NOT NULL,
  MONTO DOUBLE NOT NULL,
  STATUS_DEVOLUCION VARCHAR(20),
  ID_RECEPTOR VARCHAR(40) NOT NULL,
  TIPO_RECEPTOR VARCHAR(40) NOT NULL,
  FECHA_CREACION TIMESTAMP,
  FECHA_ACTUALIZACION TIMESTAMP,
  PRIMARY KEY (ID_DEVOLUCION)
);

----------------
---CFDI
----------------

CREATE TABLE IF NOT EXISTS CFDI(
  ID_CFDI INT(11) NOT NULL AUTO_INCREMENT,
  VERSION VARCHAR(10) NOT NULL,
  SERIE VARCHAR(40) NOT NULL,
  FOLIO VARCHAR(40) NOT NULL,
  SELLO VARCHAR(1000),
  NO_CERTIFICADO VARCHAR(30),
  CERTIFICADO VARCHAR(1000),
  MONEDA VARCHAR(10),
  TIPO_COMPROBANTE VARCHAR(50),
  USO_CFDI VARCHAR(5),
  REGIMEN_FISCAL VARCHAR(10),
  RFC_PROV_CERTIF VARCHAR(50),
  SELLO_CFD VARCHAR(1000),
  FECHA_TIMBRADO  TIMESTAMP,
  NO_CERTIFICADO_SAT VARCHAR(30),
  SELLO_SAT VARCHAR(1000),
  PRIMARY KEY (ID_CFDI),
  UNIQUE(FOLIO)
);

CREATE TABLE IF NOT EXISTS CONCEPTOS(
  ID_CONCEPTO INT(11) NOT NULL AUTO_INCREMENT,
  CLAVE_PROD_SERV VARCHAR(10),
  NO_IDENTIFICACION VARCHAR(10),
  CANTIDAD INT(10),
  CLAVE_UNIDAD VARCHAR(5),
  UNIDAD VARCHAR(100),
  DESCRIPCION VARCHAR(200),
  VALOR_UNITARIO DOUBLE(10),
  IMPORTE DOUBLE(10),
  DESCUENTO DOUBLE(10),
  ID_CFDI INT(11),
  PRIMARY KEY(ID_CONCEPTO) 
);

CREATE TABLE IF NOT EXISTS IMPUESTOS(
  ID_IMPUESTO INT(11) NOT NULL AUTO_INCREMENT,
  BASE DOUBLE(10),
  IMPUESTO VARCHAR(4),
  TIPO_FACTOR VARCHAR(10),
  TASA_CUOTA DOUBLE(10),
  IMPORTE DOUBLE(10),
  ID_CONCEPTO INT(11),
  PRIMARY KEY (ID_IMPUESTO) 
);

CREATE TABLE IF NOT EXISTS RETENCIONES(
  ID_RETENCION INT(11) NOT NULL AUTO_INCREMENT,
  BASE VARCHAR(10),
  IMPUESTO DOUBLE(10),
  TIPO_FACTOR VARCHAR(10),
  TASA_CUOTA DOUBLE(10),
  IMPORTE DOUBLE(10),
  ID_CONCEPTO INT(11),
  PRIMARY KEY (ID_RETENCION) 
);

----------------
---CATALOGOS
----------------

CREATE TABLE IF NOT EXISTS CLAVE_UNIDAD(
	CLAVE VARCHAR(5) NOT NULL UNIQUE,
	TIPO VARCHAR(50) NOT NULL,
	DESCRIPCION VARCHAR(100),
	NOMBRE VARCHAR(50) NOT NULL,
  PRIMARY KEY (CLAVE) 
);

CREATE TABLE IF NOT EXISTS REGIMEN_FISCAL(
	CLAVE INT(10) NOT NULL UNIQUE,
	DESCRIPCION VARCHAR(100) NOT NULL,
	P_MORAL TINYINT(1) NOT NULL,
	P_FISICA TINYINT(1) NOT NULL,
	INICIO_VIGENCIA TIMESTAMP,
  PRIMARY KEY (CLAVE) 
);

CREATE TABLE IF NOT EXISTS USO_CFDI(
	CLAVE VARCHAR(10) NOT NULL UNIQUE,
	DESCRIPCION VARCHAR(100) NOT NULL,
	P_MORAL TINYINT(1) NOT NULL,
	P_FISICA TINYINT(1) NOT NULL,
	INICIO_VIGENCIA TIMESTAMP,
  PRIMARY KEY (CLAVE) 
);

CREATE TABLE IF NOT EXISTS CLAVE_PROD_SERV(
	CLAVE INT(10) NOT NULL UNIQUE,
	DESCRIPCION VARCHAR(500) NOT NULL,
	SIMILARES VARCHAR(500),
	INICIO_VIGENCIA TIMESTAMP,
  PRIMARY KEY (CLAVE) 
);

CREATE TABLE IF NOT EXISTS STATUS_FACTURAS(
	ID_STATUS_FACTURA INT(10) NOT NULL UNIQUE,
	STATUS_VALIDACION VARCHAR(100) NOT NULL,
  STATUS_PAGO VARCHAR(100) NOT NULL,
  STATUS_DEVOLUCION VARCHAR(100) NOT NULL,
	FECHA_CREACION TIMESTAMP,
  FECHA_ACTUALIZACION TIMESTAMP,
  PRIMARY KEY (ID_STATUS_FACTURA) 
);

CREATE TABLE IF NOT EXISTS STATUS_PAGO(
	ID_STATUS_PAGO INT(10) NOT NULL UNIQUE,
	VALUE VARCHAR(50) NOT NULL,
  PRIMARY KEY (VALUE) 
);

CREATE TABLE IF NOT EXISTS STATUS_EVENTO(
	ID_STATUS_EVENTO INT(10) NOT NULL UNIQUE,
	VALUE VARCHAR(50) NOT NULL,
  PRIMARY KEY (VALUE) 
);
CREATE TABLE IF NOT EXISTS STATUS_DEVOLUCION(
	ID_STATUS_DEVOLUCION INT(10) NOT NULL UNIQUE,
	VALUE VARCHAR(50) NOT NULL,
  PRIMARY KEY (VALUE) 
);

CREATE TABLE IF NOT EXISTS GIROS(
	ID_GIRO INT(10) NOT NULL UNIQUE,
	NOMBRE VARCHAR(50) NOT NULL,
  FECHA_CREACION TIMESTAMP,
  FECHA_ACTUALIZACION TIMESTAMP
);

CREATE TABLE IF NOT EXISTS STATUS_REVISION(
	ID_STATUS_REVISION INT(10) NOT NULL UNIQUE,
	VALUE VARCHAR(50) NOT NULL,
  PRIMARY KEY (VALUE) 
);

CREATE TABLE IF NOT EXISTS RESOURCE_FILES(
	FILE_ID  INT(11) NOT NULL AUTO_INCREMENT,
	REFERENCIA VARCHAR(40) NOT NULL,
	TIPO_ARCHIVO VARCHAR(10) NOT NULL,
	TIPO_RECURSO VARCHAR(10) NOT NULL,
	DATA LONGBLOB NULL,
	FECHA_CREACION TIMESTAMP,
	PRIMARY KEY (FILE_ID)
);

CREATE TABLE IF NOT EXISTS FACTURA_FILES(
	FILE_ID  INT(11) NOT NULL AUTO_INCREMENT,
	FOLIO VARCHAR(40) NOT NULL,
	TIPO_ARCHIVO VARCHAR(10) NOT NULL,
	DATA LONGBLOB NULL,
	FECHA_CREACION TIMESTAMP,
	PRIMARY KEY (FILE_ID)
);