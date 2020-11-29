package com.commontools.http.webcrawler.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
/*
    https://www.dariawan.com/tutorials/java/multiple-requests-using-http11-vs-http2/
 */
public class Http2Crawling {

    static ExecutorService executor = Executors.newFixedThreadPool(6, (Runnable r) -> {
        return new Thread(r);
    });

    public static void main(String[] args) throws Exception {
        downloadWithExecutorServices();
    }

    private static void downloadWithExecutorServices() throws IOException, InterruptedException {
        System.out.println("Running HTTP/2 example...");
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(Version.HTTP_2)
                    .build();

            long start = System.currentTimeMillis();

            HttpRequest pageRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://http2.golang.org/gophertiles"))
                    .build();

            HttpResponse<String> pageResponse = httpClient.send(pageRequest, BodyHandlers.ofString());

            System.out.println("Page response status code: " + pageResponse.statusCode());
            System.out.println("Page response headers: " + pageResponse.headers());
            String responseBody = pageResponse.body();
            System.out.println(responseBody);

            List<Future<?>> futures = new ArrayList<>();
            AtomicInteger atomicInt = new AtomicInteger(1);

            Document doc = Jsoup.parse(responseBody);
            Elements imgs = doc.select("img[width=32]"); // img with width=32

            // Send request on a separate thread for each image in the page,
            imgs.forEach(img -> {
                String src = img.attr("src");
                Future<?> imgFuture = executor.submit(() -> {
                    HttpRequest imgRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://http2.golang.org" + src))
                            .build();
                    try {
                        HttpResponse<String> imageResponse = httpClient.send(imgRequest, BodyHandlers.ofString());
                        System.out.println("Thread:"+Thread.currentThread().getName()+" - [" + atomicInt.getAndIncrement() + "] Loaded " + src + ", status code: " + imageResponse.statusCode());
                    } catch (IOException | InterruptedException ex) {
                        System.out.println("Error loading image " + src + ": " + ex.getMessage());
                    }
                });
                futures.add(imgFuture);
                System.out.println("Adding future for image " + src);
            });

            // Wait for image loads to be completed
            futures.forEach(f -> {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException ex) {
                    System.out.println("Exception during loading images: " + ex.getMessage());
                }
            });

            long end = System.currentTimeMillis();
            System.out.println("Total load time: " + (end - start) + " ms");
            System.out.println(atomicInt.get() - 1 + " images loaded");
        } finally {
            executor.shutdown();
        }
    }
}
