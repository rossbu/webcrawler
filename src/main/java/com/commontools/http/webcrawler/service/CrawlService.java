package com.commontools.http.webcrawler.service;


/**
 * @author tbu
 */
public interface CrawlService {
    Object visit(String url, int depth, Boolean flat);
}
