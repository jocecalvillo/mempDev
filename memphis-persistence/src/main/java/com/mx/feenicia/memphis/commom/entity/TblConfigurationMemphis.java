package com.mx.feenicia.memphis.commom.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity

@Table(name = "tbl_configuration_memphis", catalog = "memphis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TblConfigurationMemphis {


    @Id
    @Column(name = "afiliacion", length = 16, nullable = false)
    private String afiliacion;

    @Column(name = "nombre_comercio", length = 100, nullable = false)
    private String nombreComercio;

    @Column(name = "tkr", length = 16, nullable = false)
    private String tkr;

    @Column(name = "uri_memphis", length = 100, nullable = false)
    private String uriMemphis;

    @Column(name = "uri_relative_memphis", length = 100, nullable = false)
    private String uriRelativeMemphis;

    @Column(name = "procesador", length = 100, nullable = false)
    private String procesador;


}
