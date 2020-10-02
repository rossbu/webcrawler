package com.commontools.http.webcrawler.service;


import com.commontools.http.webcrawler.config.PropertyConfig;
import com.commontools.http.webcrawler.pojo.HREFContext;
import com.commontools.http.webcrawler.pojo.Page;
import com.commontools.http.webcrawler.pojo.PageContext;
import com.commontools.http.webcrawler.pojo.WebResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The main service responsible for crawling the pages and collect Href information
 * @author tbu
 */
@Service
@Slf4j
@Getter
@Setter
public class CrawlServiceImpl implements CrawlService{

    @Autowired
    PropertyConfig propertyConfig;

    public static final String ABOSOLUTE_HREF = "abs:href";
    private static final Pattern FILE_ENDING_EXCLUSION_PATTERN = Pattern.compile(".*(\\.(" +
            "css|js" +
            "|bmp|gif|jpe?g|JPE?G|png|tiff?|ico|nef|raw" +
            "|mid|mp2|mp3|mp4|wav|wma|flv|mpe?g" +
            "|avi|mov|mpeg|ram|m4v|wmv|rm|smil" +
            "|pdf|doc|docx|pub|xls|xlsx|vsd|ppt|pptx" +
            "|swf" +
            "|zip|rar|gz|bz2|7z|bin" +
            "|xml|txt|java|c|cpp|exe" +
            "))$");

    private HashSet<String> visitedUrls;
    private HashSet<String> domainLinks ;
    private HashSet<String> externalLinks ;
    private HashSet<String> mailTos ;
    private HashSet<String> javascriptLinks ;

    @Override
    public WebResponse visit(String url, int depth, boolean flat) {
        WebResponse webResponse = new WebResponse();
        visitedUrls = new HashSet();
        PageContext pageContext = visit(url, depth);

        if (flat) {  // flat output
            HREFContext hrefContext = new HREFContext();
            domainLinks = new HashSet<>();
            externalLinks = new HashSet<>();
            mailTos = new HashSet<>();
            javascriptLinks = new HashSet<>();
            hrefContext.setDomainLinks(domainLinks);
            hrefContext.setExternalLinks(externalLinks);
            hrefContext.setMailTos(mailTos);
            hrefContext.setJavascriptLinks(javascriptLinks);
            flatten(pageContext,hrefContext);
            webResponse.setHrefContext(hrefContext);
        } else { // hierarchy output
            webResponse.setPageContext(pageContext);
        }

        return webResponse;
    }
    /**
     * Flatten all hierarchical nodes In PageContext recursively
     * @param pageContext   holds the 'current' page meta data
     * @param hrefContext holds all links of pages.
     */
    private void flatten(PageContext pageContext, HREFContext hrefContext) {
        if (pageContext == null || pageContext.getPageContexts() == null)
            return;
        Optional.ofNullable(pageContext.getDomainLinks()).ifPresent(hrefContext.getDomainLinks()::addAll);
        Optional.ofNullable(pageContext.getExternalLinks()).ifPresent(hrefContext.getExternalLinks()::addAll);
        Optional.ofNullable(pageContext.getMailTos()).ifPresent(hrefContext.getMailTos()::addAll);
        Optional.ofNullable(pageContext.getJavascriptLinks()).ifPresent(hrefContext.getJavascriptLinks()::addAll);

        pageContext.getPageContexts().forEach(p -> {
            Optional.ofNullable(p.getDomainLinks()).ifPresent(hrefContext.getDomainLinks()::addAll);
            Optional.ofNullable(p.getExternalLinks()).ifPresent(hrefContext.getExternalLinks()::addAll);
            Optional.ofNullable(p.getMailTos()).ifPresent(hrefContext.getMailTos()::addAll);
            Optional.ofNullable(p.getJavascriptLinks()).ifPresent(hrefContext.getJavascriptLinks()::addAll);
            flatten(p, hrefContext);
        });
    }

    /**
     *
     * @param url
     * @param depth
     * @return
     */
    public PageContext visit(String url, int depth) {
        if (depth <= 0) return null;

        Page page = connect(url);
        if (page == null) return null;
        Elements elements = page.getElements();
        log.info("{} links on url : {}", elements.size(), url);
        Supplier<Stream<Element>> elementsSupplier = () -> elements.stream();
        PageContext pageContext = new PageContext(url);
        pageContext.setTitle(page.getTitle());

        // Domain links: http://domain.com
        Predicate<Element> isDomainLink = element -> {
            String href = element.attr(ABOSOLUTE_HREF);
            if (href == null) return false;
            return href != null && getUrlDomain(url).equals(getUrlDomain(href)) && !FILE_ENDING_EXCLUSION_PATTERN.matcher(href).matches();
        };

        // external links: such as http://www.google.com
        Predicate<Element> isExternalLink = element -> {
            String href = element.attr(ABOSOLUTE_HREF);
            return href != null && !getUrlDomain(url).equals(getUrlDomain(href));
        };

        // mailto :  such as  "mailto:dpo-europe@wipro.com" or "mailto:info@wipro.com"
        Predicate<Element> isMailto = element -> {
            String href = element.attr(ABOSOLUTE_HREF);
            return href != null && href.startsWith("mailto");
        };

        // Javascript: <a href='javascript:void(0);
        Predicate<Element> isJavascript = element -> {
            String href = element.attr(ABOSOLUTE_HREF);
            return href != null && href.startsWith("javascript");
        };

        elementsSupplier.get()
                .filter(isMailto)
                .forEach(element -> {
                    pageContext.addMailtos(element.attr(ABOSOLUTE_HREF));
                });

        elementsSupplier.get()
                .filter(isJavascript)
                .forEach(element -> {
                    pageContext.addJavascripts(element.attr(ABOSOLUTE_HREF));
                });

        elementsSupplier.get()
                .filter(isExternalLink)
                .filter(isMailto.negate())
                .filter(isJavascript.negate())
                .forEach(element -> {
                    pageContext.addExternalLinks(element.attr(ABOSOLUTE_HREF));
                });

        elementsSupplier.get()
                .filter(isDomainLink)
                .map(element -> element.attr(ABOSOLUTE_HREF))
                .forEach(link -> {
                    pageContext.addDomainLinks(link);
                    pageContext.addPageContext(visit(link, depth - 1));
                });

        return pageContext;
    }


    /**
     *
     * @param url a url to visit
     * @return  a Page for the url
     */
    private Page connect(String url) {
        if (StringUtils.isEmpty(url)) return null;

        if (visitedUrls.add(url)) {
            try {
                Document doc = Jsoup.connect(url)
                        .maxBodySize(propertyConfig.getMaxBodySize())
                        .ignoreHttpErrors(true)
                        .timeout(propertyConfig.getTimeout())
                        .followRedirects(propertyConfig.getFollowRedirects())
                        .get();
                Elements hrefs = doc.select("a[href]");
                return new Page(doc.title(), url, hrefs);
            } catch (IOException | IllegalArgumentException e) {
                return null;
            }
        } else
            return null;
    }
    /**
     *
     * @param url  http url
     * @return  the host of url
     */
    private static String getUrlDomain(String url) {
        URL aURL = null;
        try {
            aURL = new URL(url);
        } catch (MalformedURLException e) {
            return url;
        }
        return aURL.getHost();
    }
}
