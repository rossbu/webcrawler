package com.commontools.http.webcrawler;

import com.commontools.http.webcrawler.config.PropertyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


/**
 * @author tbu
 */
@SpringBootApplication
@Import({PropertyConfig.class})
@Slf4j
public class WebcrawlerApplication {

	public static void main(String[] args) {
		log.debug("Runtime processors: "+ Runtime.getRuntime().availableProcessors()); // by default, forked joined will use 1 less than processors
		SpringApplication.run(WebcrawlerApplication.class, args);
	}

}
