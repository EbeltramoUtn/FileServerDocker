package FileServer.GetFile.Configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfig {

    /**
     * Creates a {@link ModelMapper} bean that provides a default configuration
     * for mapping between objects. ModelMapper is used for converting between
     * domain models and DTOs or entities.
     *
     * @return a new instance of {@link ModelMapper}
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Creates a specialized {@link ModelMapper} bean named "mergerMapper"
     * that only maps properties that are not null. This is useful for merging
     * data where only non-null properties should override the existing ones.
     *
     * @return a new instance of {@link ModelMapper} with a condition to only map non-null properties
     */
    @Bean("mergerMapper")
    public ModelMapper mergerMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull());
        return mapper;
    }

    /**
     * Creates a custom {@link ObjectMapper} bean for handling JSON serialization and deserialization.
     * This ObjectMapper registers the {@link JavaTimeModule} to handle Java 8 date and time classes
     * such as {@link java.time.LocalDateTime} properly during JSON conversion.
     *
     * @return a configured instance of {@link ObjectMapper}
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Register module to handle Java 8 Date/Time API (JSR-310)
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}