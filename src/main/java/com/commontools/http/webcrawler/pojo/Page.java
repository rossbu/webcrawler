/**
 *
 */
package com.commontools.http.webcrawler.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jsoup.select.Elements;

import java.io.Serializable;

/**
 * Hold one page metadata and elements ( which is an Array of Page elements )
 * @author tbu
 */
@Data
@AllArgsConstructor
public class Page implements Serializable {

    private String title;

    private String url;

    private Elements elements;
}
