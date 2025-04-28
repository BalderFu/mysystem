package com.myhousekeepingservice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@MapperScan("com.myhousekeepingservice.mapper")
@SpringBootApplication
public class Runner {

    public static void main(String[] args) {
       SpringApplication.run(Runner.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 启用 Java 8 时间支持
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 以 ISO-8601 格式返回日期
//        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false); // 禁用非 ASCII 转义
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true) ;
        objectMapper.configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), false);
        return objectMapper;
    }
}
