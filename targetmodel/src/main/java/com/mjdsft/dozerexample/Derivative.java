package com.mjdsft.dozerexample;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public abstract class Derivative extends Instrument {

    @NonNull UUID underlyingId;
    LocalDate expirationDate;
    LocalDate effectiveDate;
    private BigDecimal contractSize;

    /**
     * Default constructor
     */
    public Derivative() {

        super();
    }

}
