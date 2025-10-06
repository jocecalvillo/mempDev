package com.mx.feenicia.memphis.commom.entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "tbl_merchant", catalog = "munin",
        uniqueConstraints = @UniqueConstraint(columnNames = { "merchant", "affiliation" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "tblMerchantCredential") // Evita recursión en toString
public class TblMerchant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_credential_id", nullable = false)
    private TblMerchantCredential tblMerchantCredential;

    @Column(name = "merchant", nullable = false, length = 16)
    private String merchant;

    @Column(name = "affiliation", nullable = false, length = 10)
    private String affiliation;

    @Column(name = "currency", nullable = false, length = 5)
    private String currency;


}
