package com.mx.feenicia.memphis.commom.repository;


import com.mx.feenicia.memphis.commom.entity.TblMerchantCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TblMerchantCredentialRepository extends JpaRepository<TblMerchantCredential, Long> {
}
