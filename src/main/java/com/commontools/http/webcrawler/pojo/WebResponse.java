package com.commontools.http.webcrawler.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
/**
 * The Response class has either flat (HREFContext) or hierarchy (PageContext) data to return to the caller
 * @author tbu
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebResponse {

    HREFContext hrefContext;

    PageContext pageContext;

    String errorMessage;
}
