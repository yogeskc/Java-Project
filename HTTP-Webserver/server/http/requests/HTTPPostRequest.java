package server.http.requests;

import server.configuration.HTTPDConf;
import server.configuration.MimeTypes;
import server.http.HTTPHeader;
import server.http.HTTPResponse;
import server.http.exceptions.ServerException;
import server.http.exceptions.StatusCode;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class HTTPPostRequest extends HTTPRequest{
    public HTTPPostRequest(Socket client, boolean isScript, String[] uri, String[] requestMainHeader, HTTPDConf config, MimeTypes mimeTypes) {
        super(client, isScript, uri, requestMainHeader, config, mimeTypes);
    }
    
    @Override
    public void execute() throws ServerException {
        
        try {
            // Get the file requested
            HTTPResponse response = null;
            File file = null;
            // Get the file requested
            try {
                file = this.getFileRequested();
            } catch (ServerException e) {
                // File not found, send 404 Response back to client
                throw e;
            }
        
            if(this.isScript()) {
                try {
                    handleScript(this.getClient(), file);
                    return;
                }catch(ServerException e) {
                    throw e;
                }
            }
            
            
            // Create the HTTPResponse object
            response = new HTTPResponse(this, StatusCode.OK, this.getHttpVersion());
        
            // Get last modified date of file and format to correct date
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd LLL yyyy HH:mm:ss z");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    
            response.addHeader(new HTTPHeader("Last-Modified", dateFormat.format(new Date(file.lastModified()))));
    
            // Get the MIME type and add to response headers
            String contentType = getMimeType();
    
            response.addHeader(new HTTPHeader("Content-Type", contentType));
    
            // Get content length of file and add to response headers
            response.addHeader(new HTTPHeader("Content-Length", String.valueOf(file.length())));
        
            response.addBody(file);
        
            // Send response to client
            response.send(this.getClient());
        
            
        
        } catch(ServerException e) {
            throw e;
        }
    }
    

    
    @Override
    public String getMainRequestHeader() {
        return "POST " + this.getRequestedURI() + " " + this.getHttpVersion();
    }
}
