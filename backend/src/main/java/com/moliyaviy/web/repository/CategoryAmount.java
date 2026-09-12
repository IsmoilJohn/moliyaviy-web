package com.moliyaviy.web.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface CategoryAmount {

    UUID getCategoryId();

    String getCategoryName();

    BigDecimal getTotal();

}
