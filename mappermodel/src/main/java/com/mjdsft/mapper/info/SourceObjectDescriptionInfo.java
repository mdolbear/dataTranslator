package com.mjdsft.mapper.info;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SourceObjectDescriptionInfo {

    private String id;
    private List<String> fields;

}
