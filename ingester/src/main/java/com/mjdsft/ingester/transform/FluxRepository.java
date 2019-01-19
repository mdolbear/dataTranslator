package com.mjdsft.ingester.transform;

import com.mjdsft.mapper.model.DataTranslateDefinition;
import lombok.NonNull;

public interface FluxRepository {

    /**
     * Create data ingester
     * @param aDirectoryPath String
     * @param aDataRunId String
     * @param aDefinition DataTranslateDefinition
     *
     */
    void createDataIngester(@NonNull String aDirectoryPath,
                            @NonNull String aDataRunId,
                            @NonNull DataTranslateDefinition aDefinition);
}
