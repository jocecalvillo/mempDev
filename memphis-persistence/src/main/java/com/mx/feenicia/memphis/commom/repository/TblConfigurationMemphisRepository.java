package com.mx.feenicia.memphis.commom.repository;

import com.mx.feenicia.memphis.commom.entity.TblConfigurationMemphis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TblConfigurationMemphisRepository  extends JpaRepository<TblConfigurationMemphis, String> {
    Optional<TblConfigurationMemphis> findByProcesador(String procesador);



}
