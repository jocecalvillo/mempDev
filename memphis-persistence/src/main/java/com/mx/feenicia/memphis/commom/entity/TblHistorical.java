package com.mx.feenicia.memphis.commom.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "tbl_historical", catalog = "memphis",
        uniqueConstraints = @UniqueConstraint(columnNames = "transaction_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "tblMerchant") // Excluimos la relación ManyToOne para evitar recursión
public class TblHistorical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private TblMerchant tblMerchant;

    @Column(name = "transaction_id", unique = true)
    private Long transactionId;

    @Column(name = "ticket_number", length = 40)
    private String ticketNumber;

    @Column(name = "transaction_reference", length = 40)
    private String transactionReference;

    @Column(name = "related_ticket_number", length = 40)
    private String relatedTicketNumber;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "amount", nullable = false, precision = 18, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "transaction_type", nullable = false, length = 8)
    private String transactionType;

    @Column(name = "errorcode", length = 8)
    private String errorcode;

    @Column(name = "errormessage", length = 256)
    private String errormessage;

    @Column(name = "json_feenicia_request", columnDefinition = "TEXT")
    private String jsonFeeniciaRequest;

    @Column(name = "json_memphis_response", columnDefinition = "TEXT")
    private String jsonMemphisResponse;

    @Column(name = "status")
    private String status;

    }
