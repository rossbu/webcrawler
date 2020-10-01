package com.commontools.http.webcrawler.service;


import com.commontools.http.webcrawler.pojo.WebResponse;

/**
 * @author tbu
 */
public interface CrawlService {
    WebResponse visit(String url, int depth, boolean flat);
}
