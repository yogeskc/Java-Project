package server.logs;

import server.http.exceptions.StatusCode;
import server.http.requests.HTTPRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;


/**
 * Logger class will be a singleton class with only the reference being publicly facing along with its methods
 */
public class Logger {

    private static Logger logger = null;


    private File logFile;
    private String logPath;
    private Semaphore logFileBinarySemaphore;

    private Logger(String logPath) {
        this();
        this.setLogPath(logPath);
    }

    private Logger() {
        this.logFileBinarySemaphore = new Semaphore(1);
    }

    public void writeToLog(Socket client, File requestedResource, HTTPRequest httpRequest, StatusCode statusCode) throws InterruptedException, IOException {

        String clientIP = client.getRemoteSocketAddress().toString();

        String requestedResourceName = requestedResource != null ? requestedResource.getName() : "-";

        SimpleDateFormat dateFormat = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]");

        String user = httpRequest != null ? httpRequest.getUser() : "-";
        
        String requestHeader = httpRequest != null ? httpRequest.getMainRequestHeader() : "NULL";

        int responseCode = statusCode.getStatusCode();
        long responseLength = requestedResource != null ? requestedResource.length() : 0;

        StringBuilder logMessage = new StringBuilder(clientIP);

        // Build formatted logMessage before entering critical section
        logMessage.append(" ").append(requestedResourceName).append(" ").append(user).append(" ");
        logMessage.append(dateFormat.format(new Date(System.currentTimeMillis())));
        logMessage.append(" \"").append(requestHeader).append("\" ").append(responseCode);
        logMessage.append(" ").append(responseLength == 0 ? "-" : responseLength).append("\n");
    
        // Print to stdout
        System.out.println(logMessage.toString());
        System.out.println("=========================================================");
        
        // Check to see if logFile is available to be used
        logFileBinarySemaphore.acquire();


        // Critical Section: This is where only access and write to file is done
        // Get file


        this.logFile = new File(this.logPath);
        // If logFile does not exist, need to create it
        if (!this.logFile.exists()) {
//            Files.createFile(Paths.get(this.logFile.getCanonicalPath()));
            this.logFile.createNewFile();
        }

        FileWriter logWriter = new FileWriter(logFile, true);

        logWriter.write(logMessage.toString());
        logWriter.flush();
        logWriter.close();

        // Give back logFile to Server, let another thread have access
        logFileBinarySemaphore.release();

    }


    public boolean setLogPath(String logPath) {

        if(this.logPath == null) {
            this.logPath = logPath;
            return true;
        }
        return false;
    }

    /**
     * This function will return the singleton logger. This function will create the logger to the log file if it does not exist
     * @param logPath - path to log file
     * @return - Logger singleton that is configured
     */
    public static Logger getLogger(String logPath) {
        if (logger == null) {
            logger = new Logger(logPath);
        }

        return logger;
    }


    /**
     * This function will return the singleton logger. This function will create the logger if it does not exist. However,
     * must call setLogPath(String logPath) after to set the location of the log file that the logger will write to.
     * @return - Logger singleton that is not configured
     */
    public static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }

        return logger;
    }
}
