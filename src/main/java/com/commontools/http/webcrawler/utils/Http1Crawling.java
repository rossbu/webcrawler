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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Http1Crawling {

    static ExecutorService executor = Executors.newFixedThreadPool(6, (Runnable r) -> {
        return new Thread(r);
    });

    public static void main(String[] args) throws Exception {

        downloadWithExecutorServices();
//        downloadWithCompletableFutureSupplyAsync();
    }

    /*
        CompletableFuture delegates to default ForkJoin.commonPool().  -ForkJoinPool
        which is a default size to the number of CPUs on your system
        CPU intensive, using the commonPool makes most sense.
        IO intensive (reading and writing to the file system) you should define the thread pool differently.
     */
    private static void downloadWithCompletableFutureSupplyAsync() throws IOException, InterruptedException {
        System.out.println("downloadWithCompletableFutureSupplyAsync Running HTTP/1.1 example...");
        System.out.println("# of processors: " + Runtime.getRuntime().availableProcessors());
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(Version.HTTP_1_1)
                    .build();

            long start = System.currentTimeMillis();

            HttpRequest pageRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://http1.golang.org/gophertiles"))
                    .build();

            HttpResponse<String> pageResponse = httpClient.send(pageRequest, BodyHandlers.ofString());

            System.out.println("Page response status code: " + pageResponse.statusCode());
            System.out.println("Page response headers: " + pageResponse.headers());
            String responseBody = pageResponse.body();
            System.out.println(responseBody);

            List<CompletableFuture<?>> futures = new ArrayList<>();
            AtomicInteger atomicInt = new AtomicInteger(1);

            Document doc = Jsoup.parse(responseBody);
            Elements imgs = doc.select("img[width=32]"); // img with width=32

            // Send request on a separate thread for each image in the page,
            imgs.forEach(img -> {
                String image = img.attr("src");
                CompletableFuture<?> imgFuture = CompletableFuture.supplyAsync(() -> {
                    HttpRequest imgRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://http1.golang.org" + image))
                            .build();
                    try {
                        HttpResponse<String> imageResponse = httpClient.send(imgRequest, BodyHandlers.ofString());
                        System.out.println("Thread:"+Thread.currentThread().getName()+" - [" + atomicInt.getAndIncrement() + "] Loaded " + image + ", status code: " + imageResponse.statusCode());
                    } catch (IOException | InterruptedException ex) {
                        System.out.println("Error loading image " + image + ": " + ex.getMessage());
                    }
                    return "OK";
                },executor); // you can specify the executor defined by your self. or use default ForkJoinPool, which use as many threads as your cpus
                futures.add(imgFuture);
                System.out.println("Adding future for image " + image);
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


    /*
    You use the Future when you want the executing thread to wait for async computation response.
    An example of this is with a parallel merge/sort.
    Sort left asynchronously, sort right synchronously, wait on left to complete (future.get()), merge results
     */
    private static void downloadWithExecutorServices() throws IOException, InterruptedException {
        System.out.println("downloadWithExecutorServices Running HTTP/1.1 example...");
        System.out.println("# of processors: " + Runtime.getRuntime().availableProcessors());
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(Version.HTTP_1_1)
                    .build();

            long start = System.currentTimeMillis();

            HttpRequest pageRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://http1.golang.org/gophertiles"))
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
                String image = img.attr("src");
                Future<?> imgFuture = executor.submit(() -> {
                    HttpRequest imgRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://http1.golang.org" + image))
                            .build();
                    try {
                        HttpResponse<String> imageResponse = httpClient.send(imgRequest, BodyHandlers.ofString());
                        System.out.println("Thread:"+Thread.currentThread().getName()+" - [" + atomicInt.getAndIncrement() + "] Loaded " + image + ", status code: " + imageResponse.statusCode());
                    } catch (IOException | InterruptedException ex) {
                        System.out.println("Error loading image " + image + ": " + ex.getMessage());
                    }
                    return "OK";
                });
                futures.add(imgFuture);
                System.out.println("Adding future for image " + image);
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
