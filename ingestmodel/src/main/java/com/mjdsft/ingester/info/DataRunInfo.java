package com.mjdsft.ingester.info;

import com.mjdsft.ingester.model.DataRunState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Date;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class DataRunInfo {

    private String id;
    private String userProfileIdentifier;
    private Date creationDate;
    private DataRunState runState;
    private int numberOfSuccessfulRecordConversions;
    private int numberOfFailedRecordConversions;
    private boolean stopOnFailure;

}
