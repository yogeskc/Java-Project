package server.http;

import server.http.exceptions.ServerException;
import server.http.exceptions.StatusCode;
import server.http.requests.HTTPRequest;
import server.logs.Logger;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HTTPResponse {
    
    private String server = "Kao KC";
    
    // HTTPResponse variables
    private StatusCode statusCode;
    private Date date;
    private String httpVersion;
    private List<HTTPHeader> headers = new ArrayList<>();
    private byte[] body;
    
    
    
    // Logger focused variables
    private Logger logger;
    private File requestedResource;
    private HTTPRequest request;
    
    
    public HTTPResponse(HTTPRequest request, StatusCode statusCode, String httpVersion) {
        this.request = request;
        this.statusCode = statusCode;
        this.httpVersion = httpVersion;
        this.logger = Logger.getLogger();
        date = new Date(System.currentTimeMillis());
    }
    
    /**
     * This is a convenient send method that will send all HTTP Response headers and body if it exists for more control
     * over when a certain part of the response is sent, try using sendStatusLine(), sendHeaders(), and sendBody()
     * @param client - Socket that response will be sent to
     * @throws ServerException - Error sending HTTP Response to client
     */
    public void send(Socket client) throws ServerException{
        // Send the response to client, split into two parts
        //  - Send HTTP Response Header as String using PrintWriter
        //  - Send HTTP Response Body as bytes using OutputStream
        
        try {
            OutputStream out = client.getOutputStream();
    
            // Send HTTP Response Header
            PrintWriter outHeader = new PrintWriter(out, false);
            outHeader.write(getStatusLineAndDefaultHeaders());
            outHeader.write(getHeaders());
            outHeader.flush();
            
            // Send HTTP Response Body
            if(body != null && body.length > 0) {
                out.write(body);
                out.flush();
            }
            
    
        } catch(IOException e) {
            throw new ServerException("Error sending response", e, StatusCode.SERVER_ERROR);
        }
        
        try {
            logger.writeToLog(client, requestedResource, request, statusCode);
        } catch (InterruptedException | IOException e) {
            System.out.println("Could not write to log file");
            e.printStackTrace();
        }
    }
    
    public void sendStatusLine(Socket client) throws ServerException {
        try {
            OutputStream out = client.getOutputStream();
            
            // Send HTTP Response Status Line
            PrintWriter outWriter = new PrintWriter(out, false);
            outWriter.write(this.getStatusLineAndDefaultHeaders());
            outWriter.flush();
            
        } catch(IOException e) {
            throw new ServerException("Error sending response status line", e, StatusCode.SERVER_ERROR);
        }
    }
    
    public void sendHeaders(Socket client) throws ServerException {
        try {
            OutputStream out = client.getOutputStream();
            
            // Send HTTP Response Headers
            PrintWriter outWriter = new PrintWriter(out, false);
            outWriter.write(this.getHeaders());
            outWriter.flush();
        } catch(IOException e) {
            throw new ServerException("Error sending response headers", e, StatusCode.SERVER_ERROR);
        }
    
        try {
            logger.writeToLog(client, requestedResource, request, statusCode);
        } catch (InterruptedException | IOException e) {
            System.out.println("Could not write to log file");
            e.printStackTrace();
        }
    }
    
    public void sendBody(Socket client) throws ServerException {
        try {
            OutputStream out = client.getOutputStream();
            
            // Send HTTP Response Body
            if(this.body != null && this.body.length > 0) {
                out.write(body);
                out.flush();
            }
            
        } catch(IOException e) {
            throw new ServerException("Error sending response body", e, StatusCode.SERVER_ERROR);
        }
    }
    
    
    
    public void addHeader(HTTPHeader header) {
        headers.add(header);
    }
    
    /**
     * This function will read the file in bytes and set to body
     *
     * @param resource - Give a file
     */
    public void addBody(File resource) throws ServerException {
        this.requestedResource = resource;
        // Create FileInputStream to read file as bytes
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(resource));
            this.body = reader.readAllBytes();
        } catch(IOException e) {
            throw new ServerException("Error reading File", e, StatusCode.SERVER_ERROR);
        }
    }
    
    public void setBody(List<Byte> body) {
        this.body = new byte[body.size()];
        
        for(int i = 0; i < body.size(); i++) {
            this.body[i] = body.get(i);
        }
        
        this.addHeader(new HTTPHeader("Content-Length", String.valueOf(this.body.length)));
    }
    
    public String getHeaders() {
        StringBuilder builder = new StringBuilder();

        builder.append("Connection: ").append("Close").append("\r\n");

        // Add all headers with new lines, assumed that headers were added in correct order
        for(HTTPHeader header : headers) {
            builder.append(header).append("\r\n");
        }


        //Must add line break in between header and body
        builder.append("\r\n");
        return builder.toString();
    }
    
    /**
     * This function returns a correctly formatted HTTP Response status line and default server headers
     * @return
     */
    private String getStatusLineAndDefaultHeaders() {
        StringBuilder builder = new StringBuilder(httpVersion);
        
        // Build formatted HTTP Response status line
        builder.append(" ").append(this.statusCode.getStatusCode()).append(" ").append(this.statusCode.getReason()).append("\r\n");
        
        // Build default headers
        // Add server name to response
        builder.append("Server: ").append(server).append("\r\n");
    
        // Add date to response with correct format
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd LLL yyyy HH:mm:ss z\r\n");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    
        builder.append("Date: ");
        builder.append(dateFormat.format(date));
        
        return builder.toString();
    }
    
    public StatusCode getStatusCode() {
        return statusCode;
    }
    
    public void setRequestedResource(File file) {
        this.requestedResource = file;
    }
}
