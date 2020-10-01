package com.commontools.http.webcrawler.pojo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * @author tbu
 *
 * HREFContext will hold all Hrefs of  visited pages
 */
@Data
public class HREFContext {
    @JsonProperty("domainLinks")
    HashSet<String> domainLinks;

    @JsonProperty("externalLinks")
    HashSet<String> externalLinks;

    @JsonProperty("mailTos")
    HashSet<String> mailTos;

    @JsonProperty("javascriptLinks")
    HashSet<String> javascriptLinks;

    public HashSet<String> getDomainLinks() {
        if (domainLinks == null) {
            return new HashSet<>();
        }
        return domainLinks;
    }

    public HashSet<String> getExternalLinks() {
        if (externalLinks == null) {
            return new HashSet<>();
        }
        return externalLinks;
    }

    public HashSet<String> getMailTos() {
        if (mailTos == null) {
            return new HashSet<>();
        }
        return mailTos;
    }

    public HashSet<String> getJavascriptLinks() {
        if (javascriptLinks == null) {
            return new HashSet<>();
        }
        return javascriptLinks;
    }
}
