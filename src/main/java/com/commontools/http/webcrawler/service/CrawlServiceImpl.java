package com.commontools.http.webcrawler.service;


import com.commontools.http.webcrawler.pojo.WebResponse;
import org.springframework.stereotype.Service;

/**
 * The main service responsible for crawling the pages and collect Href information
 * @author tbu
 */
@Service
public class CrawlServiceImpl implements CrawlService{
    @Override
    public WebResponse visit(String url, int depth, boolean flat) {
        return null;
    }
}
