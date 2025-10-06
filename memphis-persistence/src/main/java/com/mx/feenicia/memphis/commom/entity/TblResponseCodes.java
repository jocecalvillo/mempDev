package com.mx.feenicia.memphis.commom.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_response_codes", catalog = "munin",
        uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TblResponseCodes {
    @Id
    @Column(name = "code", unique = true, nullable = false, length = 4)
    private String code;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Column(name = "iso_code", nullable = false, length = 2)
    private String isoCode;
}
