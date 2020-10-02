# Web Crawler

## Table of content
- [Installation](#installation)
- [Build](#build)
- [Testing](#testing)
- [To Be Enhanced](#To Be Enhanced)
- [Additional Notes](#Additional Notes)

## Installation
1. If you haven't done it already, [make a fork of this repo](http://github.com/rossbu/webcrawler/fork).
1. Clone to your local computer using `git`.
1. Make sure you have `JDK 1.8+` installed, see instructions [here](https://www.java.com/en/download/).
1. Make sure you have `Maven 3.5+` installed. See instructions [here](https://maven.apache.org/download.cgi).
1. Make sure you have `Postman` installed ( Testing ). See instructions [here](https://www.postman.com/).


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

### start web server
    mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080 , --customArgument=--timeout=3000 --followRedirects=true"

### Settings
  ```
    Request Params  ( for http request)
        flat  :  When true , return flat data hierarchy.  when false, return response in a hierarchical way
        depth :  
                 <=0 won't trigger crawling,  
                 1  : home page, 
                 2+ : subpages (of home page) 
                 6 : maximum ( Not to hack 3rd party website when crawling when using this service. 
                    take w3school for example: depth=6 means 1M for only links)
        url  :   the website for crawling e.g. http://wiprodigital.com
  
    External Config  ( for developers only )
        server.port      : default 8080, change to another port if conflicts in your local.
        timeout          : default 3000, increase timeout if network lag occurs. ( millisecond)
        followRedirects  : default true, keep it true to comply with web server|gateway redirection.
        ignoreHttpErrors : default true, change to false only if logging errors is needed, but will be lengthy when throw exceptions 
                           when a HTTP error occurs. (4xx - 5xx, e.g. 404 or 500)
    
  ```

### TC1: w3schools.com

    chrome:    
            open http://localhost:8888/webcrawler/crawl?flat=true&url=https://www.w3schools.com&depth=2
    curl  :    
            curl -H "Content-Type: application/json" 'localhost:8080/webcrawler/crawl?flat=true&url=https://www.w3schools.com&depth=1' | jq '.'              -- return all
            curl 'localhost:8080/webcrawler/crawl?flat=true&url=https://www.w3schools.com&depth=1' | jq '.domainLinks'   -- return domainlinks only
            curl 'localhost:8080/webcrawler/crawl?flat=true&url=https://www.w3schools.com&depth=1' | jq '.externalLinks' -- return externalLinks Only
    
    postman ( perferred) :
            please download the powman collection for testing this project.

### TC2: crawl wiprodigital.com

    chrome:    
        open http://localhost:8080/webcrawler/crawl?flat=true&url=https://wiprodigital.com&depth=2 in 
    curl  :    
        curl 'localhost:8080/webcrawler/crawl?flat=true&url=https://wiprodigital.com&depth=1' | jq '.'
    postman ( perferred) :
        

## To Be Enhanced
  * multi-sites
  * multi-threads (multi-cpus)
  * queued requests
  * dynamic pages generation
  * distributed: run on several machines in a distributed manner.

## Additional Notes
    What is jq?
    jq is a lightweight and flexible command-line JSON processor.
    brew update && brew install jq
    
    What is curl?
    CURL is used in command lines or scripts to transfer data.

    How to kill 8080 port ( if the port is still listening|Established|Close_wait After IDE closed )
    netstat -anvp tcp | awk 'NR<3 || /LISTEN/' -> find pid -> kill the pid
