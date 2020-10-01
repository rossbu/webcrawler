package com.commontools.http.webcrawler.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
/**
 * The Response class to hold either flat|hierarchy data to return to the caller
 * @author tbu
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebResponse {

    HREFContext hrefContext;

    PageContext pageContext;
}
