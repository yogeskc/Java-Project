package server;

import server.configuration.HTAccess;
import server.configuration.HTTPDConf;
import server.configuration.MimeTypes;
import server.http.HTTPHeader;
import server.http.HTTPResponse;
import server.http.requests.HTTPRequest;
import server.http.requests.HTTPRequestFactory;
import server.http.exceptions.ServerException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ServerRequest implements Runnable {
// public class ServerRequest {
    
    // TODO: Uncomment Runnable class line for multithread, already tested, it works
    
    // Needs to have a reference to the client socket that made the request
    private Socket client = null;
    
    // Needs the HTTP Request
    private HTTPRequest request;
    
    
    public ServerRequest(Socket client, HTTPDConf config, MimeTypes mimeTypes, Map<String, HTAccess> authentication) {
        this.client = client;
        PrintWriter out;
        try {
            out = new PrintWriter(client.getOutputStream(), false);
        } catch(IOException e) {
            System.out.println("Client socket error: Could not get output stream");
            System.exit(-1);
        }
        this.request = null;
        
        try {
            // Ask HTTPRequestFactory to create a HTTPRequest object
            this.request = HTTPRequestFactory.createHTTPRequest(client, config, mimeTypes, authentication);
    
        } catch(ServerException e) {
            HTTPResponse response = new HTTPResponse(e.getRequest(), e.getErrorCode(), e.getHttpVersion());
            
            if(e.containsHeaders()) {
                // If ServerException contains headers that need to be sent, add them to HTTPResponse
                for(HTTPHeader header : e.getHeaders()) {
                    response.addHeader(header);
                }
            }
            
            
            try {
                response.send(this.client);
            } catch(ServerException ignored) {
            }
        }
    }
    
   @Override
    public void run() {
        // Execute the HTTPRequest if it was created
        try {
            if(request == null) {
                return;
            }
            
            request.execute();
        } catch (ServerException e) {
            HTTPResponse response = new HTTPResponse(e.getRequest(), e.getErrorCode(), e.getHttpVersion());
            
            
            try {
                response.send(this.client);
            } catch(ServerException ignored) {
            
            }
        }
    }
    







}
