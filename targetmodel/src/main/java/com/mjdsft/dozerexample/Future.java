package com.mjdsft.dozerexample;

import com.mjdsft.ingester.modelinterface.TargetObject;
import lombok.*;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
public class Future extends Derivative  implements TargetObject {

    @NonNull FutureType futureType;
    LocalDate firstNoticeDate;
    LocalDate lastTradingDate;


    /**
     * Override no argument constructor to initialize instrument type
     */
    public Future() {

        super();
        this.setInstrumentType(InstrumentType.FUTURE);
    }

    /**
     * Pre-validate. This is the first step to be invoked, and provides a hook to determine
     * that data looks good after conversion process.
     */
    @Override
    public void preValidate() {

    }

    /**
     * This provides a reconciliation step for the target model. If there is dependent data in the model,
     * this can be used as a place to generate it
     */
    @Override
    public void reconcile() {

    }

    /**
     * This does the full validation step after preValidate and reconciliation have been completed
     */
    @Override
    public void validate() {

    }
}