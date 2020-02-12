package com.business.unknow.model.cfdi;

import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

public class CfdiTest {

	@Test
	public void validateObject() {
		Cfdi fact = new Cfdi();
		fact.setVersion("3.3");
		fact.setSello(
				"QM8VOArjf/u8vAqkpdPonY+1kJT8n9w7gXhdiqd+Ah6c+TjQsdCKnR8En7uQebxP5PzOhZVgE0YoIkkHYYu4tuMNNP4+YZyDydJH6Lk+Tvp9xSUMUdXiCOgz36kx4Tx7HGIKhXDDVDY6LWnrOlnMe/K3VieZsK2QZJIR4eFdpJ3UWJBNsvgBdmHT4/TCcBsapDmT527i/8/3als5JSPfVcPAjzo5GqoPupduzVmQOhCYbelWk5IlMxtXkmRFPZPCT6jGrlfMv6NQoMDW0qOlB+Yv3I3BYiWAVBRrSG+GyZcPa2vsj65N5dUFrw+G4zbyAplFrp7taSEIbBcFbC6Tnw==");
		fact.setSerie("serie");
		fact.setFolio("123456");
		fact.setFormaPago("03");
		fact.setNoCertificado("20001000000300022815");
		fact.setCertificado(
				"MIIFxTCCA62gAwIBAgIUMjAwMDEwMDAwMDAzMDAwMjI4MTUwDQYJKoZIhvcNAQELBQAwggFmMSAwHgYDVQQDDBdBLkMuIDIgZGUgcHJ1ZWJhcyg0MDk2KTEvMC0GA1UECgwmU2VydmljaW8gZGUgQWRtaW5pc3RyYWNpw7NuIFRyaWJ1dGFyaWExODA2BgNVBAsML0FkbWluaXN0cmFjacOzbiBkZSBTZWd1cmlkYWQgZGUgbGEgSW5mb3JtYWNpw7NuMSkwJwYJKoZIhvcNAQkBFhphc2lzbmV0QHBydWViYXMuc2F0LmdvYi5teDEmMCQGA1UECQwdQXYuIEhpZGFsZ28gNzcsIENvbC4gR3VlcnJlcm8xDjAMBgNVBBEMBTA2MzAwMQswCQYDVQQGEwJNWDEZMBcGA1UECAwQRGlzdHJpdG8gRmVkZXJhbDESMBAGA1UEBwwJQ295b2Fjw6FuMRUwEwYDVQQtEwxTQVQ5NzA3MDFOTjMxITAfBgkqhkiG9w0BCQIMElJlc3BvbnNhYmxlOiBBQ0RNQTAeFw0xNjEwMjUyMTUyMTFaFw0yMDEwMjUyMTUyMTFaMIGxMRowGAYDVQQDExFDSU5ERU1FWCBTQSBERSBDVjEaMBgGA1UEKRMRQ0lOREVNRVggU0EgREUgQ1YxGjAYBgNVBAoTEUNJTkRFTUVYIFNBIERFIENWMSUwIwYDVQQtExxMQU43MDA4MTczUjUgLyBGVUFCNzcwMTE3QlhBMR4wHAYDVQQFExUgLyBGVUFCNzcwMTE3TURGUk5OMDkxFDASBgNVBAsUC1BydWViYV9DRkRJMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgvvCiCFDFVaYX7xdVRhp/38ULWto/LKDSZy1yrXKpaqFXqERJWF78YHKf3N5GBoXgzwFPuDX+5kvY5wtYNxx/Owu2shNZqFFh6EKsysQMeP5rz6kE1gFYenaPEUP9zj+h0bL3xR5aqoTsqGF24mKBLoiaK44pXBzGzgsxZishVJVM6XbzNJVonEUNbI25DhgWAd86f2aU3BmOH2K1RZx41dtTT56UsszJls4tPFODr/caWuZEuUvLp1M3nj7Dyu88mhD2f+1fA/g7kzcU/1tcpFXF/rIy93APvkU72jwvkrnprzs+SnG81+/F16ahuGsb2EZ88dKHwqxEkwzhMyTbQIDAQABox0wGzAMBgNVHRMBAf8EAjAAMAsGA1UdDwQEAwIGwDANBgkqhkiG9w0BAQsFAAOCAgEAJ/xkL8I+fpilZP+9aO8n93+20XxVomLJjeSL+Ng2ErL2GgatpLuN5JknFBkZAhxVIgMaTS23zzk1RLtRaYvH83lBH5E+M+kEjFGp14Fne1iV2Pm3vL4jeLmzHgY1Kf5HmeVrrp4PU7WQg16VpyHaJ/eonPNiEBUjcyQ1iFfkzJmnSJvDGtfQK2TiEolDJApYv0OWdm4is9Bsfi9j6lI9/T6MNZ+/LM2L/t72Vau4r7m94JDEzaO3A0wHAtQ97fjBfBiO5M8AEISAV7eZidIl3iaJJHkQbBYiiW2gikreUZKPUX0HmlnIqqQcBJhWKRu6Nqk6aZBTETLLpGrvF9OArV1JSsbdw/ZH+P88RAt5em5/gjwwtFlNHyiKG5w+UFpaZOK3gZP0su0sa6dlPeQ9EL4JlFkGqQCgSQ+NOsXqaOavgoP5VLykLwuGnwIUnuhBTVeDbzpgrg9LuF5dYp/zs+Y9ScJqe5VMAagLSYTShNtN8luV7LvxF9pgWwZdcM7lUwqJmUddCiZqdngg3vzTactMToG16gZA4CWnMgbU4E+r541+FNMpgAZNvs2CiW/eApfaaQojsZEAHDsDv4L5n3M1CC7fYjE/d61aSng1LaO6T1mh+dEfPvLzp7zyzz+UgWMhi5Cs4pcXx1eic5r7uxPoBwcCTt3YI1jKVVnV7/w=");
		fact.setMoneda("MXN");
		fact.setTipoDeComprobante("I");
		fact.setMetodoPago("PUE");
		fact.setLugarExpedicion("68050");
		fact.setFecha("2019-09-24T14:47:08");

		Emisor emisor = new Emisor();
		emisor.setNombre("emisor");
		emisor.setRfc("LAN7008173R5");
		emisor.setRegimenFiscal("601");

		Receptor receptor = new Receptor();
		receptor.setNombre("receptor");
		receptor.setRfc("SAF080804RH7");
		receptor.setUsoCfdi("G01");

		Impuesto impuesto = new Impuesto();
//		impuesto.setTotalImpuestosTrasladados(268.0);

		Translado traslado = new Translado();
//		traslado.setImporte(268.0);
		traslado.setImpuesto("002");
		traslado.setTasaOCuota("0.160000");
		traslado.setTipoFactor("Tasa");
		impuesto.setTranslados(Arrays.asList(traslado));

		Concepto concepto1 = new Concepto();
		concepto1.setClaveProdServ("01010101");
		concepto1.setNoIdentificacion("AULOG001");
		concepto1.setCantidad(5);
		concepto1.setClaveUnidad("H87");
		concepto1.setUnidad("Pieza");
		concepto1.setDescripcion("Aurriculares USB Logitech");
//		concepto1.setValorUnitario(350.0);
//		concepto1.setImporte(1750.00);
//		concepto1.setDescuento(175.00);

		Impuesto impuestoConcepto1 = new Impuesto();
		Translado traslado1 = new Translado();
//		traslado1.setImporte(252.00);
		traslado1.setImpuesto("002");
		traslado1.setTasaOCuota("0.160000");
		traslado1.setTipoFactor("Tasa");
//		traslado1.setBase(1575.00);
		impuestoConcepto1.setTranslados(Arrays.asList(traslado1));

		concepto1.setImpuestos(impuestoConcepto1);

		Concepto concepto2 = new Concepto();
		concepto2.setClaveProdServ("43201800");
		concepto2.setNoIdentificacion("USB");
		concepto2.setCantidad(1);
		concepto2.setClaveUnidad("H87");
		concepto2.setUnidad("Pieza");
		concepto2.setDescripcion("Memoria USB 32gb marca Kingston");
//		concepto2.setValorUnitario(100.0);
//		concepto2.setImporte(100.00);

		Impuesto impuestoConcepto2 = new Impuesto();
		Translado traslado2 = new Translado();
//		traslado2.setImporte(16.00);
		traslado2.setImpuesto("002");
		traslado2.setTasaOCuota("0.160000");
		traslado2.setTipoFactor("Tasa");
//		traslado2.setBase(100.00);
		impuestoConcepto2.setTranslados(Arrays.asList(traslado2));
		concepto2.setImpuestos(impuestoConcepto2);

		fact.setEmisor(emisor);
		fact.setReceptor(receptor);
		fact.setConceptos(Arrays.asList(concepto1, concepto2));
		fact.setImpuestos(impuesto);

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(Cfdi.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(fact, sw);
			assertNotNull(fact);
			assertNotNull(sw.toString());
			System.out.println(sw.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
