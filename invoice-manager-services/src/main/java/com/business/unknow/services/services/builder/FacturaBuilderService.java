package com.business.unknow.services.services.builder;

import com.business.unknow.Constants;
import com.business.unknow.Constants.ComplementoPpdDefaults;
import com.business.unknow.enums.FormaPagoEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.dto.cfdi.ComplementoDto;
import com.business.unknow.model.dto.cfdi.ConceptoDto;
import com.business.unknow.model.dto.cfdi.EmisorDto;
import com.business.unknow.model.dto.cfdi.ReceptorDto;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Contribuyente;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.entities.cfdi.Cfdi;
import com.business.unknow.services.entities.cfdi.CfdiPago;
import com.business.unknow.services.mapper.ContribuyenteMapper;
import com.business.unknow.services.mapper.EmpresaMapper;
import com.business.unknow.services.repositories.ContribuyenteRepository;
import com.business.unknow.services.repositories.EmpresaRepository;
import com.business.unknow.services.repositories.facturas.CfdiPagoRepository;
import com.business.unknow.services.repositories.facturas.CfdiRepository;
import com.business.unknow.services.services.FilesService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturaBuilderService {

  @Autowired private EmpresaRepository empresaRepository;

  @Autowired private ContribuyenteRepository contribuyenteRepository;

  @Autowired private CfdiPagoRepository cfdiPagoRepository;

  @Autowired private CfdiRepository cfdiRepository;

  @Autowired private EmpresaMapper empresaMapper;

  @Autowired private ContribuyenteMapper contribuyenteMapper;

  @Autowired private FilesService filesService;

  public FacturaContext buildFacturaContextPagoPpdCreation(
      PagoDto pagoDto, FacturaDto facturaDto, String folio) throws InvoiceManagerException {
    EmpresaDto empresaDto =
        empresaMapper.getEmpresaDtoFromEntity(
            empresaRepository
                .findByRfc(facturaDto.getRfcEmisor())
                .orElseThrow(
                    () ->
                        new InvoiceManagerException(
                            "Pago a credito no encontrado",
                            String.format("No existe El emisor %s", facturaDto.getRfcEmisor()),
                            HttpStatus.SC_NOT_FOUND)));
    return FacturaContext.builder()
        .empresaDto(empresaDto)
        .facturaDto(facturaDto)
        .currentPago(pagoDto)
        .build();
  }

  public FacturaContext buildFacturaContextPagoPueCreation(String folio, PagoDto pagoDto) {
    // List<Pago> pagos = pagoRepository.findByFolio(folio);
    //		Optional<Factura> factura = repository.findByFolio(folio);
    //		Optional<Pago> pagoCredito = pagos.stream()
    //				.filter(p -> p.getFormaPago().equals(FormaPagoEnum.CREDITO.getPagoValue())).findFirst();
    //		return new FacturaContextBuilder().setPagos(Arrays.asList(pagoDto))
    //				.setPagos(pagoMapper.getPagosDtoFromEntities(pagos)).setCurrentPago(pagoDto)
    //				.setFacturaDto(factura.isPresent() ? mapper.getFacturaDtoFromEntity(factura.get()) : null)
    //				.setPagoCredito(pagoCredito.isPresent() ?
    // pagoMapper.getPagoDtoFromEntity(pagoCredito.get()) : null)
    //				.build();
    return null;
  }

  public FacturaDto buildFacturaDtoPagoPpdCreation(FacturaDto factura, PagoDto pago) {
    return FacturaDto.builder()
        .total(pago.getMonto())
        .packFacturacion(factura.getPackFacturacion())
        .saldoPendiente(BigDecimal.ZERO)
        .lineaEmisor(factura.getLineaEmisor())
        .rfcEmisor(factura.getRfcEmisor())
        .metodoPago(ComplementoPpdDefaults.METODO_PAGO)
        .rfcRemitente(factura.getRfcRemitente())
        .lineaRemitente(factura.getLineaRemitente())
        .razonSocialEmisor(factura.getRazonSocialEmisor())
        .razonSocialRemitente(factura.getRazonSocialRemitente())
        .validacionTeso(false)
        .validacionOper(false)
        .solicitante(factura.getSolicitante())
        .tipoDocumento(TipoDocumentoEnum.COMPLEMENTO.getDescripcion())
        .build();
  }

  public CfdiDto buildFacturaComplementoCreation(FacturaContext facturaContext) {
    return CfdiDto.builder()
        .version(ComplementoPpdDefaults.VERSION_CFDI)
        .lugarExpedicion(facturaContext.getEmpresaDto().getCp())
        .moneda(ComplementoPpdDefaults.MONEDA)
        .metodoPago(ComplementoPpdDefaults.METODO_PAGO)
        .formaPago(
            FormaPagoEnum.findByPagoValue(facturaContext.getCurrentPago().getFormaPago())
                .getClave())
        .noCertificado(facturaContext.getEmpresaDto().getNoCertificado())
        .serie(ComplementoPpdDefaults.SERIE)
        .subtotal(new BigDecimal(ComplementoPpdDefaults.SUB_TOTAL))
        .total(new BigDecimal(ComplementoPpdDefaults.TOTAL))
        .complemento(new ComplementoDto())
        .tipoDeComprobante(ComplementoPpdDefaults.COMPROBANTE)
        .emisor(
            EmisorDto.builder()
                .rfc(facturaContext.getFacturaDto().getRfcEmisor())
                .nombre(facturaContext.getFacturaDto().getRazonSocialEmisor())
                .regimenFiscal(
                    facturaContext.getFacturaDto().getCfdi().getEmisor().getRegimenFiscal())
                .direccion(facturaContext.getFacturaDto().getCfdi().getEmisor().getDireccion())
                .build())
        .receptor(
            ReceptorDto.builder()
                .rfc(facturaContext.getFacturaDto().getRfcRemitente())
                .nombre(facturaContext.getFacturaDto().getRazonSocialRemitente())
                .usoCfdi(ComplementoPpdDefaults.USO_CFDI)
                .direccion(facturaContext.getFacturaDto().getCfdi().getReceptor().getDireccion())
                .build())
        .conceptos(buildFacturaComplementoConceptos())
        .build();
  }

  public List<ConceptoDto> buildFacturaComplementoConceptos() {
    List<ConceptoDto> conceptos = new ArrayList<ConceptoDto>();
    ConceptoDto conceptoDto =
        ConceptoDto.builder()
            .cantidad(new BigDecimal(ComplementoPpdDefaults.CANTIDAD))
            .claveProdServ(ComplementoPpdDefaults.CLAVE_PROD)
            .claveUnidad(ComplementoPpdDefaults.CLAVE)
            .descripcion(ComplementoPpdDefaults.DESCRIPCION)
            .importe(new BigDecimal(ComplementoPpdDefaults.IMPORTE))
            .valorUnitario(new BigDecimal(ComplementoPpdDefaults.VALOR_UNITARIO))
            .build();
    conceptos.add(conceptoDto);
    return conceptos;
  }

  public List<CfdiPagoDto> buildFacturaComplementoPagos(
      FacturaDto complemento, PagoDto pagoDto, List<FacturaDto> dtos)
      throws InvoiceManagerException {
    List<CfdiPagoDto> cfdiPagos = new ArrayList<CfdiPagoDto>();
    for (FacturaDto dto : dtos) {
      List<CfdiPago> cfdiPAgos =
          cfdiPagoRepository.findByFolio(dto.getFolio()).stream()
              .filter(a -> a.getValido())
              .collect(Collectors.toList());
      Optional<Cfdi> cfdi = cfdiRepository.findById(dto.getIdCfdi());
      Optional<PagoFacturaDto> pagoFactura =
          pagoDto.getFacturas().stream()
              .filter(a -> a.getFolio().endsWith(dto.getFolio()))
              .findFirst();
      Optional<CfdiPago> cfdipago =
          cfdiPAgos.stream()
              .sorted(
                  (o2, o1) ->
                      Integer.valueOf(o1.getNumeroParcialidad())
                          .compareTo(Integer.valueOf(o2.getNumeroParcialidad())))
              .filter(a -> a.getFolio().endsWith(dto.getFolio()))
              .findFirst();

      if (!cfdipago.isPresent()) {
        cfdipago = Optional.of(new CfdiPago(pagoDto.getMonto(), 0));
      }
      if (pagoFactura.isPresent()) {
        BigDecimal montoPagado;
        if (cfdi.get().getMoneda().equals(pagoDto.getMoneda())) {
          montoPagado = pagoFactura.get().getMonto();
        } else {
          montoPagado =
              pagoFactura
                  .get()
                  .getMonto()
                  .divide(pagoDto.getTipoDeCambio(), 2, RoundingMode.HALF_UP);
        }
        CfdiPagoDto cfdiComplementoPago =
            CfdiPagoDto.builder()
                .version(ComplementoPpdDefaults.VERSION)
                .fechaPago(pagoDto.getFechaPago())
                .formaPago(FormaPagoEnum.findByPagoValue(pagoDto.getFormaPago()).getClave())
                .moneda(pagoDto.getMoneda())
                .monto(pagoDto.getMonto())
                .folio(dto.getFolio())
                .idDocumento(dto.getUuid())
                .importePagado(montoPagado)
                .monedaDr(cfdi.get().getMoneda())
                .moneda(pagoDto.getMoneda())
                .valido(true)
                .metodoPago(ComplementoPpdDefaults.METODO_PAGO)
                .serie(ComplementoPpdDefaults.SERIE_PAGO)
                .numeroParcialidad(cfdipago.get().getNumeroParcialidad() + 1)
                .importeSaldoAnterior(dto.getSaldoPendiente())
                .tipoCambio(
                    cfdi.get().getMoneda().equals(pagoDto.getMoneda())
                        ? pagoDto.getTipoDeCambio()
                        : BigDecimal.valueOf(1))
                .tipoCambioDr(
                    !cfdi.get().getMoneda().equals(pagoDto.getMoneda())
                        ? pagoDto.getTipoDeCambio()
                        : BigDecimal.valueOf(1))
                .importeSaldoInsoluto(dto.getSaldoPendiente().subtract(montoPagado))
                .build();
        cfdiPagos.add(cfdiComplementoPago);
      } else {
        throw new InvoiceManagerException(
            "No tiene relacion de pago la factura",
            String.format("La factura %s not tiene pago relacionado", dto.getFolio()),
            Constants.BAD_REQUEST);
      }
    }
    return cfdiPagos;
  }

  public FacturaContext buildFacturaContextCreateFactura(FacturaDto facturaDto)
      throws InvoiceManagerException {
    Empresa empresa =
        empresaRepository
            .findByRfc(facturaDto.getRfcEmisor())
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        "Emisor de factura no existen en el sistema",
                        String.format(
                            "No se encuentra el RFC %s en el sistema", facturaDto.getRfcEmisor()),
                        Constants.BAD_REQUEST));
    Contribuyente contribuyente =
        contribuyenteRepository
            .findByRfc(facturaDto.getRfcRemitente())
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        "Error al crear factura", "El receptor no exite", Constants.BAD_REQUEST));
    return FacturaContext.builder()
        .facturaDto(facturaDto)
        .empresaDto(empresaMapper.getEmpresaDtoFromEntity(empresa))
        .contribuyenteDto(contribuyenteMapper.getContribuyenteToFromEntity(contribuyente))
        .build();
  }

  public FacturaContext buildEmailContext(String folio, FacturaDto facturaDto)
      throws InvoiceManagerException {
    Empresa empresa =
        empresaRepository
            .findByRfc(facturaDto.getRfcEmisor())
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        "Emisor de factura no existen en el sistema",
                        String.format(
                            "No se encuentra el RFC %s en el sistema", facturaDto.getRfcEmisor()),
                        Constants.BAD_REQUEST));
    Contribuyente contribuyente =
        contribuyenteRepository
            .findByRfc(facturaDto.getRfcRemitente())
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        "Error al crear factura", "El receptor no exite", Constants.BAD_REQUEST));
    FacturaFileDto xml =
        filesService.getFacturaFileByFolioAndType(
            facturaDto.getFolio(), TipoArchivoEnum.XML.name());
    FacturaFileDto pdf =
        filesService.getFacturaFileByFolioAndType(
            facturaDto.getFolio(), TipoArchivoEnum.PDF.name());
    List<FacturaFileDto> archivos = new ArrayList<>();
    archivos.add(xml);
    archivos.add(pdf);
    return FacturaContext.builder()
        .facturaDto(facturaDto)
        .facturaFilesDto(archivos)
        .empresaDto(empresaMapper.getEmpresaDtoFromEntity(empresa))
        .contribuyenteDto(contribuyenteMapper.getContribuyenteToFromEntity(contribuyente))
        .build();
  }
}
