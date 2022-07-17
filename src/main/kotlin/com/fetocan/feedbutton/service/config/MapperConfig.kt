package com.fetocan.feedbutton.service.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
open class MapperConfig {

    @Bean
    fun mapper() = ObjectMapper()

    @Bean
    @DependsOn(value = ["mapper"])
    fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val mapper = mapper()
        mapper.findAndRegisterModules()
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        return MappingJackson2HttpMessageConverter(mapper)
    }
}
