package com.commontools.http.webcrawler.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author tbu
 */
@Component
@NoArgsConstructor
@Data
public class PropertyConfig {

    @Value("${followRedirects}")
    public Boolean followRedirects;

    @Value("${timeout}")
    public Integer timeout;

    @Value("${ignoreHttpErrors}")
    public Boolean ignoreHttpErrors;

    @Value("${maxBodySize}")
    public Integer maxBodySize;

}
