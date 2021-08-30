import { Injectable } from '@angular/core';
import { Empresa } from '../../models/empresa';

@Injectable({
  providedIn: 'root',
})
export class CompaniesValidatorService {

  camposObligatoriosEmpresa = [
    {
      field: 'rfc',
      description: 'RFC',
    },
    {
      field: 'razonSocial',
      description: 'Razón Social',
    },
    {
      field: 'nombre',
      description: 'Nombre corto',
    },
    {
      field: 'cp',
      description: 'Código Postal',
    },
    {
      field: 'municipio',
      description: 'Municipio',
    },
    {
      field: 'estado',
      description: 'Estado',
    },
    {
      field: 'calle',
      description: 'Calle',
    },
    {
      field: 'tipo',
      description: 'Linea',
    },
  ];

  camposObligatoriosInformacionFiscal = [
    
  ];

  public validarEmpresa(empresa: Empresa) {
    const messages: string[] = [];

    if (empresa == null) {
      throw 'La empresa a validar tiene un valor de null';
    }

    if (empresa.giro == null || empresa.giro === "*" || empresa.giro.length < 1) {
      messages.push("Se debe seleccionar un giro de empresa");
    }

    if (empresa.tipo == null || empresa.tipo === "*" || empresa.tipo.length < 1) {
      messages.push("Se debe seleccionar la linea de empresa");
    }


    this.camposObligatoriosEmpresa.forEach(campo => {
      if (empresa[campo.field] === null
        || empresa[campo.field] === undefined
        || empresa[campo.field] === '') {
          messages.push(`El campo '${campo.description}' es obligatorio`);
        }
    });

    return messages;
  }
}
