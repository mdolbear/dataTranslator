package com.mjdsft.dozerexample;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(exclude = "id")
public abstract class Instrument {

    private String externalId;
    private UUID id;
    private String symbol;
    private InstrumentType instrumentType;

}
