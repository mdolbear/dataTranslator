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
public class DateScalarConfiguration  {

    @Bean
    public GraphQLScalarType dateScalar() {

        return GraphQLScalarType.newScalar()
                .name("Date")
                .description("Java Date as scalar.")
                .coercing(new Coercing<Date, String>() {
                    @Override
                    public String serialize(final Object dataFetcherResult) {
                        if (dataFetcherResult instanceof Date) {
                            return dataFetcherResult.toString();
                        } else {
                            throw new CoercingSerializeException("Expected a Date object.");
                        }
                    }

                    @Override
                    public Date parseValue(final Object input) {
                        try {
                            if (input instanceof String) {
                                return (new SimpleDateFormat()).parse((String) input, new ParsePosition(0));
                            } else {
                                throw new CoercingParseValueException("Expected a String");
                            }
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException(String.format("Not a valid date: '%s'.", input), e
                            );
                        }
                    }

                    @Override
                    public Date parseLiteral(final Object input) {
                        if (input instanceof StringValue) {
                            try {
                                return(new SimpleDateFormat()).parse((String) input, new ParsePosition(0));
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException(e);
                            }
                        } else {
                            throw new CoercingParseLiteralException("Expected a StringValue.");
                        }
                    }
                }).build();
    }
}
