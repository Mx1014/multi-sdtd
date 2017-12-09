package com.rzt.utils;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "my-defined")
@Data
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class YmlConfig {
    private Map<String,String> mapProps = new HashMap<>();
}
