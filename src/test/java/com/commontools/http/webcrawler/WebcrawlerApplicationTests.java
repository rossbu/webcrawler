package com.commontools.http.webcrawler;

import com.commontools.http.webcrawler.pojo.HREFContext;
import com.commontools.http.webcrawler.pojo.WebResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring boot execute below test cases when mvn clean install or spring:boot run for testing.
 * @author tbu
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebcrawlerApplicationTests {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	void when_flat_true_expect_hrefContext_to_return() {
		WebResponse webResponse = testRestTemplate.getForObject("http://localhost:" + port + "/webcrawler/crawl?flat=true&url=https://www.w3schools.com&depth=1", WebResponse.class);
		assertThat(webResponse).isNotNull().satisfies(
				response -> {
					assertThat(response.getHrefContext()!=null);
					assertThat(response.getPageContext()==null);
				}
		);
	}
	@Test
	void when_flat_false_expect_pageContext_to_return() {
		WebResponse webResponse = testRestTemplate.getForObject("http://localhost:" + port + "/webcrawler/crawl?flat=false&url=https://www.w3schools.com&depth=1", WebResponse.class);
		assertThat(webResponse).isNotNull().satisfies(
				response -> {
					assertThat(response.getPageContext()!=null);
					assertThat(response.getHrefContext()==null);
				}
		);
	}
}
