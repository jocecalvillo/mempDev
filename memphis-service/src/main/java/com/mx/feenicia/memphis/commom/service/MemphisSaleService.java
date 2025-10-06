package com.mx.feenicia.memphis.commom.service;

import com.mx.feenicia.memphis.common.feenicia.AtenaResponse;
import com.mx.feenicia.memphis.common.model.MemphisSaleRequest;
import org.springframework.security.core.Authentication;

public interface MemphisSaleService {

    AtenaResponse payment(MemphisSaleRequest memphisSaleRequest, Authentication authentication);

}
