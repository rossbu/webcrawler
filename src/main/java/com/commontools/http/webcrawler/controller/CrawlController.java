package com.commontools.http.webcrawler.controller;

import com.commontools.http.webcrawler.pojo.PageContext;
import com.commontools.http.webcrawler.pojo.WebResponse;
import com.commontools.http.webcrawler.service.CrawlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * The entry controller
 * @author tbu
 */
@Controller
@Slf4j
public class CrawlController {

    @Autowired
    CrawlService crawlService;

    @GetMapping(value = "/crawl", produces = { MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PageContext> crawl(
            @RequestParam(value = "url", required = false,defaultValue = "https://wiprodigital.com") String url,
            @RequestParam(value = "depth", required = false,defaultValue = "1") Integer depth,
            @RequestParam(value = "flat", required = false,defaultValue = "true") Boolean flat) {
        WebResponse webResponse = crawlService.visit(url, depth,flat);
        return new ResponseEntity(webResponse, HttpStatus.OK);
    }
}
