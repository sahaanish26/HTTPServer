# httpserver
A  multi-threaded HTTP 1.1 Server implemented in Java with ThreadPool implementation supporting following capabilities
as per IETF RFCs.
1. Cache-headers 
2. E-tags
3. Last-modified 
4. Mime-type
5. Http-range

## Installation
Step 1 - Install Requirements
-----------------------------
To download and compile the source code you need to install: 
- Java JDK v1.8+
- Maven v3.6.3+

Step 2 - Get the source code
----------------------------
Download the project source code from GitHub repository (https://github.com/sahaanish26/httpserver) 

    $ git clone https://github.com/sahaanish26/httpserver.git
Step 3 - Compile it
--------------------
Go to the project root folder and run the following command:

    $ mvn clean package

This will create a "target" folder containing the application jar file: httpserver_1.0.jar  

## Running


The Java Web Server runs on any Operational System with Java (JRE) 1.8+ installed.

To start the Java Web Server, download the application jar file (httpserver_1.0.jar) and run the following command:

    $ java -jar httpserver_1.0.jar 
    $ make sure to create a folder named "web" in the same dir where the jar is downloaded.
      All static files to be served should be kept inside that folder(web).
 
The server runs on port 8080.To stop the Simple Web server, just close  the command line/terminal.
It is recommended to run the server in multicore machines for better performance.

# Planned Enhancements 
See Design And Flow details
See the [FuturePlannedChangeLog.txt file](FuturePlannedChangeLog.txt).