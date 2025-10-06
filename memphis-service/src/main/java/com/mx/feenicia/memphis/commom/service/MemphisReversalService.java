package com.mx.feenicia.memphis.commom.service;

import com.mx.feenicia.memphis.common.feenicia.AtenaResponse;
import com.mx.feenicia.memphis.common.model.AtenaTxByRequest;

public interface MemphisReversalService {

    AtenaResponse reversalById(AtenaTxByRequest request);
}
