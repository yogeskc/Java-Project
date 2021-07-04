package server.http.requests;

import server.configuration.HTTPDConf;
import server.configuration.MimeTypes;
import server.http.HTTPHeader;
import server.http.HTTPResponse;
import server.http.exceptions.ServerException;
import server.http.exceptions.StatusCode;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HTTPGetRequest extends HTTPRequest{
    
    public HTTPGetRequest(Socket client, boolean isScript, String[] uri, String[] requestMainHeader, HTTPDConf config, MimeTypes mimeTypes) {
        super(client, isScript, uri, requestMainHeader, config, mimeTypes);
    }
    
    @Override
    public void execute() throws ServerException {
        

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
            
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd LLL yyyy HH:mm:ss z");
            Date lastModified = new Date(file.lastModified());
            if(this.getHeaders().containsKey("If-Modified-Since")) {
    
                // Need to check if last modified is after if modified since
                
                try {
                    Date ifModifiedSince = dateFormat.parse(this.getHeaders().get("If-Modified-Since"));
    
                    if (ifModifiedSince.after(lastModified)) {
                        // File has not been updated. Browser can use its cached resource.
        
                        // Create the 304 HTTPResponse object
                        response = new HTTPResponse(this, StatusCode.NOT_MODIFIED, this.getHttpVersion());
                    } else {
        
                        // Create the 200 HTTPResponse object
                        response = new HTTPResponse(this, StatusCode.OK, this.getHttpVersion());
        
                        // Need to add body to response
                        response.addBody(file);
                    }
                } catch(ParseException e) {
                    throw new ServerException(this, this.getHttpVersion(), "Error Parsing If-Modified-Since header", e, StatusCode.SERVER_ERROR);
                }
                
            } else {
//             Create the 200 HTTPResponse object
                response = new HTTPResponse(this, StatusCode.OK, this.getHttpVersion());
    
                // Need to add body to response
                response.addBody(file);
            }
            
    
            // Get last modified date of file and format to correct date
            
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    
            response.addHeader(new HTTPHeader("Last-Modified", dateFormat.format(lastModified)));
    
            // Get the MIME type and add to response headers
            String contentType = getMimeType();
    
            response.addHeader(new HTTPHeader("Content-Type", contentType));
    
            // Get content length of file and add to response headers
            response.addHeader(new HTTPHeader("Content-Length", String.valueOf(file.length())));
    
            
    
            // Send response to client
            response.send(this.getClient());
            
    }
    
    @Override
    public String getMainRequestHeader() {
        return "GET " + this.getRequestedURI() + " " + this.getHttpVersion();
    }
}
