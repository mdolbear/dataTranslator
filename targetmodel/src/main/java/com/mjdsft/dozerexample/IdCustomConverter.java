package com.mjdsft.dozerexample;

import com.github.dozermapper.core.CustomConverter;

import java.util.UUID;

public class IdCustomConverter implements CustomConverter {

    /**
     * Answer a default instance
     */
    public IdCustomConverter() {

        super();
    }

    /**
     * Convert between types of ids
     * @param existingDestinationFieldValue
     * @param sourceFieldValue
     * @param destinationClass
     * @param sourceClass
     * @return Object
     */
    @Override
    public Object convert(Object existingDestinationFieldValue,
                          Object sourceFieldValue,
                          Class<?> destinationClass,
                          Class<?> sourceClass) {

        Object  tempResult = null;

        if (sourceFieldValue != null) {

            if (sourceClass.equals(String.class)) {

                tempResult = UUID.fromString((String)sourceFieldValue);
            }
            else if (sourceClass.equals(UUID.class)) {

                tempResult = ((UUID)sourceFieldValue).toString();
            }
            else {

                throw new IllegalArgumentException("Invalid input value for "
                                                   + this.getClass().getSimpleName());

            }
        }
        return tempResult;
    }
}
