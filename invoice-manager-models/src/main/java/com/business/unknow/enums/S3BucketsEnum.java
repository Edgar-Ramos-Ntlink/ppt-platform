package com.business.unknow.enums;

public enum S3BucketsEnum {
    CFDIS,
    EMPRESAS,
    PAGOS,
    NOT_VALID;

    public static S3BucketsEnum findByValor(String valor) {
        for (S3BucketsEnum v : values()) {
            if (v.name().equals(valor)) {
                return v;
            }
        }
        return NOT_VALID;
    }
}
