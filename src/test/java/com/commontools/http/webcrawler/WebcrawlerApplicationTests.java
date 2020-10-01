package com.commontools.http.webcrawler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

/**
 * Spring boot run will execute below test cases.
 * @author tbu
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebcrawlerApplicationTests {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	void when_flat_true_expect_HrefContext_to_return() {

	}
	@Test
	void when_flat_false_expect_pageContext_to_return() {

	}

}
