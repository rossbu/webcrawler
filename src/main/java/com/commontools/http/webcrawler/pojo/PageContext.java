package com.commontools.http.webcrawler.pojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tbu
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class PageContext {

    @JsonProperty("pageContexts")
    private List<PageContext> pageContexts;

    @JsonProperty("domainLinks")
    private List<String> domainLinks;

    @JsonProperty("externalLinks")
    private List<String> externalLinks;

    @JsonProperty("mailtoLinks")
    private List<String> mailTos;

    @JsonProperty("javascriptLinks")
    private List<String> javascriptLinks;

    @NonNull
    @JsonProperty("url")
    private String url;

    @JsonProperty("title")
    private String title;
    public void addPageContext(final PageContext pageContext) {
        if (pageContexts == null) {
            pageContexts = new ArrayList<>();
        } else {
            if ( pageContext !=null)
                pageContexts.add(pageContext);
        }
    }
    public void addDomainLinks(String link) {
        if (domainLinks == null) {
            domainLinks = new ArrayList<>();
        } else {
            domainLinks.add(link);
        }
    }

    public void addExternalLinks(String link) {
        if (externalLinks == null) {
            externalLinks = new ArrayList();
        } else {
            externalLinks.add(link);
        }
    }

    public void addMailtos(String link) {
        if (mailTos == null) {
            mailTos = new ArrayList();
        } else {
            mailTos.add(link);
        }
    }

    public void addJavascripts(String link) {
        if (mailTos == null) {
            mailTos = new ArrayList();
        } else {
            mailTos.add(link);
        }
    }
}
