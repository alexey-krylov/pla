package org.nthdimenzion.presentation;

/**
 * https://github.com/FasterXML/jackson-datatype-joda/issues/12
 * <p/>
 * Need to add custom serialiser and deserialiser to handle joda date formats
 * <p/>
 * Locale based formats hacks http://stackoverflow.com/questions/25551011/change-date-pattern-from-shortdate-format-in-jodatime
 */

//@Component
public class JacksonConfig {
/*

    private static DateTimeFormatter formatter = getDateTimeFormat();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof MappingJackson2HttpMessageConverter) {
            MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) bean;
            ObjectMapper objectMapper = jsonConverter.getObjectMapper();
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            final JodaModule jodaModule = new JodaModule();
            jodaModule.addSerializer(DateTime.class, new DateTimeAsJavascriptDateSerializer());
            jodaModule.addSerializer(LocalDate.class, new LocalDateSerializer());
            jodaModule.addDeserializer(LocalDate.class, new LocalDateDeSerializer());
            jodaModule.addSerializer(Money.class, new MoneySerializer());
            jodaModule.addDeserializer(Money.class, new MoneyDeSerializer());
            objectMapper.registerModule(jodaModule);
        }
        return bean;
    }
    private class DateTimeAsJavascriptDateSerializer extends JsonSerializer<DateTime> {

        @Override
        public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(formatter.print(value));
        }
    }

    private class LocalDateSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(formatter.print(value));
        }
    }

    private class LocalDateDeSerializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return formatter.parseLocalDate(jp.getValueAsString());
        }
    }

    private class MoneySerializer extends JsonSerializer<Money> {

        @Override
        public void serialize(Money value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(stripCurrencyUnit(MONEY_FORMATTER.print(value)));
        }
    }

    private class MoneyDeSerializer extends JsonDeserializer<Money> {

        @Override
        public Money deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return MONEY_FORMATTER.parseMoney(prependCurrencyUnit(jp.getValueAsString()));
        }
    }
*/

}


