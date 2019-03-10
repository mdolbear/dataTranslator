package com.mjdsft.k8provision.info;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class DeleteJobInfo {

    private boolean unscheduleCondition;
    private boolean deleteCondition;

}
