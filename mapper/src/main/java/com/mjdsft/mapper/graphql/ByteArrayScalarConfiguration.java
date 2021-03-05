package com.mjdsft.mapper.graphql;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;

//Stolen from web - and converted for date
@Configuration
public class ByteArrayScalarConfiguration {

    @Bean
    public GraphQLScalarType byteArrayScalar() {

        return GraphQLScalarType.newScalar()
                .name("ByteArray")
                .description("Java byte[] as scalar.")
                .coercing(new Coercing<byte[], String>() {
                    @Override
                    public String serialize(final Object dataFetcherResult) {
                        if (dataFetcherResult instanceof byte[]) {
                            return new String((byte[])dataFetcherResult);
                        } else {
                            throw new CoercingSerializeException("Expected a Date object.");
                        }
                    }

                    @Override
                    public byte[] parseValue(final Object input) {

                        if (input instanceof String) {
                            return (((String) input).getBytes());
                        }
                        else {
                            throw new CoercingParseValueException("Expected a String");
                        }

                    }

                    @Override
                    public byte[] parseLiteral(final Object input) {
                        if (input instanceof StringValue) {

                                return (((String) input).getBytes());

                        }
                        else {
                            throw new CoercingParseLiteralException("Expected a StringValue.");
                        }
                    }
                }).build();
    }
}
