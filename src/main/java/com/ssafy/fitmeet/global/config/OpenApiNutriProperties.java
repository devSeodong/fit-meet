package com.ssafy.fitmeet.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "open-api.nutri")
public class OpenApiNutriProperties {
    private String endPoint;
    private String key;
}
