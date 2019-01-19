package com.mjdsft.dozerexample;

import com.github.dozermapper.core.BeanFactory;
import com.github.dozermapper.core.config.BeanContainer;

import java.util.Map;


public class FinancialObjectBuilder implements BeanFactory {

    //Constants -- most are used for the tests
    public static final String INSTRUMENT_TYPE_KEY = "instrumentType";
    public static final String EXTERNAL_ID_KEY = "externalId";
    public static final String ID_KEY = "id";
    public static final String SYMBOL_KEY = "symbol";

    public static final String UNDERLYING_ID_KEY = "underlyingId";
    public static final String EXPIRATION_DATE_KEY = "expirationDate";
    public static final String EFFECTIVE_DATE_KEY = "effectiveDate";
    public static final String CONTRACT_SIZE_KEY = "contractSize";

    public static final String FUTURE_TYPE_KEY = "futureType";
    public static final String FIRST_NOTICE_DATE_KEY = "firstNoticeDate";
    public static final String LAST_TRADING_DATE_KEY = "lastTradingDate";

    /**
     * Create bean
     * @param source Object
     * @param sourceClass Class
     * @param targetBeanId String
     * @param beanContainer BeanContainer
     * @return Object
     */
    @Override
    public Object createBean(Object source,
                             Class<?> sourceClass,
                             String targetBeanId,
                             BeanContainer beanContainer) {

        return this.createFinancialObject((Map<String, String>)source);

    }

    /**
     * Create a instrument subclass from aMap
     * @param aMap Map<String, String>
     * @return Instrument
     */
    public Instrument createFinancialObject(Map<String, String> aMap) {


        String          tempInstrumentTypeKey;
        Instrument      tempResult = null;

        tempInstrumentTypeKey = aMap.get(INSTRUMENT_TYPE_KEY);

        if (tempInstrumentTypeKey.equals(InstrumentType.FUTURE.name())) {

            tempResult = new Future();
        }

        return tempResult;
    }

}
