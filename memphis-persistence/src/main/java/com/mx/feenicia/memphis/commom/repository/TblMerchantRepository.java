package com.mx.feenicia.memphis.commom.repository;

import com.mx.feenicia.memphis.commom.entity.TblMerchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TblMerchantRepository extends JpaRepository<TblMerchant, Long> {
    /**
     * @param merchant
     * @param affiliation
     * @return
     */
    Optional<TblMerchant> findByMerchantAndAffiliation(String merchant, String affiliation);



}
