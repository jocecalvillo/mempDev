package com.mx.feenicia.memphis.commom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_merchant_credential", catalog = "munin",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "merchant_id", "public_merchant_id", "private_merchant_id"
        }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "tblMerchants")
@EqualsAndHashCode(exclude = "tblMerchants")
public class TblMerchantCredential implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "merchant_id", nullable = false, length = 20)
    private String merchantId;

    @Column(name = "public_merchant_id", nullable = false, length = 64)
    private String publicMerchantId;

    @Column(name = "private_merchant_id", nullable = false, length = 64)
    private String privateMerchantId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tblMerchantCredential")
    @Builder.Default
    private Set<TblMerchant> tblMerchants = new HashSet<>(0);
}
