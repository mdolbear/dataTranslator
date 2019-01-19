package com.mjdsft.mapper.info;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class NodeDefinitionInfo {

    private String id;
    private String filename;
}
