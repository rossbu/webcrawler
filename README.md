# Web Crawler

## Table of content
- [Prerequisite](#prerequisite)
- [Quick Start](#quick-start)
- [Build](#build)
- [Testing](#testing)
- [To Be Enhanced](#to-be-enhanced)
- [Additional Notes](#additional-notes)

## Prerequisite
1. If you haven't done it already, [make a fork of this repo](http://github.com/rossbu/webcrawler/fork).
1. Clone to your local computer using `git`.
1. Make sure you have `JDK 1.8` installed, see instructions [here](https://www.java.com/en/download/).
    1. If you have jdk11 or higher in use on your machine, please update <java.version>1.8</java.version> to <java.version>11</java.version> or highter in pom file. 
    1. This crawler was tested on JDK8 and JDK11.
1. Make sure you have `Maven 3.5+` installed. See instructions [here](https://maven.apache.org/download.cgi).
1. Make sure you have `Postman` installed ( Testing ). See instructions [here](https://www.postman.com/).


 
## Quick Start
If you would like to try the crawler quickly before step-by-step setup, you can use docker as below:
    
    docker run -e "SPRING_PROFILES_ACTIVE=dev" -p 8080:8080 -t rossbu/webcrawler
    Open http://localhost:8080/webcrawler/crawl?flat=true&url=https://wiprodigital.com&depth=1 in your browser

## Build

    With Maven (3.5+)
        mvn clean install
        
    W/O Maven
        ./mvnw  clean install

    Both will execute test cases and make the build and if the maven command runs properly, you should see below output
    
    ...
    Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
    BUILD SUCCESS

## Testing
This section shows you step-by-step to start the web server, config params, and crawl web sites.

### Start Web Server
    CLI : 
        mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080 , --customArgument=--timeout=3000 --followRedirects=true"
        or
        mvn package && java -jar target/webcrawler-1.0.0-SNAPSHOT.jar
    IDE:  
        Open WebcrawlerApplication.java, Run as Spring boot Application 
    
    - Web Server will be running on http://localhost:8080/webcrawler/
    - Open http://localhost:8080/webcrawler/crawl?flat=true&url=https://wiprodigital.com&depth=1 in your browser, a json response should be displayed.

### Settings
    Request Params  ( for http request)
        flat  :  When true , return flat data hierarchy.  when false, return response in a hierarchical way
        depth :  
                 <=0 won't trigger crawling,  
                 1  : home page, 
                 2+ : subpages (of home page) 
                 6 : maximum ( Not to hack 3rd party website when crawling when using this service. 
                    take w3school for example: depth=6 means 1M for only links)
        url  :   the website for crawling e.g. http://wiprodigital.com
  
    External Config  ( add below '--customArgument' in the mvn command )
        server.port      : default 8080, change to another port if conflicts in your local.
        timeout          : default 3000, increase timeout if network lag occurs. ( millisecond)
        followRedirects  : default true, keep it true to comply with web server|gateway redirection.
        ignoreHttpErrors : default true, change to false only if logging errors is needed, but will be lengthy when throw exceptions 
                           when a HTTP error occurs. (4xx - 5xx, e.g. 404 or 500)

            
### Crawl wiprodigital.com
    postman ( perferred) :
        please download postman collection in sourcecode [Postman-WebCrawler.json]
        
    curl  :    
        curl 'localhost:8080/webcrawler/crawl?flat=true&url=https://wiprodigital.com&depth=1' | jq '.'
    
    chrome:    
        open http://localhost:8080/webcrawler/crawl?flat=true&url=https://wiprodigital.com&depth=2

### Crawl w3schools.com
    postman ( perferred) :
       please download postman collection in sourcecode [Postman-WebCrawler.json]
       
    curl  :    
        - return all:
        curl -H "Content-Type: application/json" 'localhost:8080/webcrawler/crawl?flat=true&url=https://www.w3schools.com&depth=2' | jq '.'
            
        - show domain links  (w3schools.com)
        curl 'localhost:8080/webcrawler/crawl?flat=true&url=https://wiprodigital.com&depth=1' | jq '.hrefContext.domainLinks'
            
        - show external links ( not including 'mailto' and 'javascript:void..)              
        curl 'localhost:8080/webcrawler/crawl?flat=true&url=https://www.w3schools.com&depth=1' | jq '.hrefContext.externalLinks'
    
    chrome:
            - Install chrome ext: 'JSONView' to parse Json in your browser.
            open http://localhost:8888/webcrawler/crawl?flat=true&url=https://www.w3schools.com&depth=2
            
## To Be Enhanced
    * web security && more errors handling
    * multi-sites crawling
    * multi-threads (multi-cpus)
    * queued requests
    * distributed: run on several nodes in a distributed manner.

## Additional Notes
    What is jq?
    jq is a lightweight and flexible command-line JSON processor.
    brew update && brew install jq
    
    What is curl?
    CURL is used in command lines or scripts to transfer data.

    How to kill 8080 port ( if the port is still listening|Established|Close_wait After IDE closed )
    netstat -anvp tcp | awk 'NR<3 || /LISTEN/' -> find pid -> kill the pid
