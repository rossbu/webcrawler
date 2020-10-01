package com.commontools.http.webcrawler;

import com.commontools.http.webcrawler.config.PropertyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


/**
 * @author tbu
 */
@SpringBootApplication
@Import({PropertyConfig.class})
public class WebcrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebcrawlerApplication.class, args);
	}

}
