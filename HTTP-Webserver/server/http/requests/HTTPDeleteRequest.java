package server.http.requests;

import server.configuration.HTTPDConf;
import server.configuration.MimeTypes;
import server.http.HTTPResponse;
import server.http.exceptions.ServerException;
import server.http.exceptions.StatusCode;

import java.io.File;
import java.net.Socket;

public class HTTPDeleteRequest extends HTTPRequest{
    public HTTPDeleteRequest(Socket client, boolean isScript, String[] uri, String[] requestMainHeader, HTTPDConf config, MimeTypes mimeTypes) {
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
        
        
        
        // Delete the file
        if(file.delete()) {
            // File successfully deleted
            System.out.println("Deleted File: " + file.getName());
            response = new HTTPResponse(this, StatusCode.NO_CONTENT, this.getHttpVersion());
        } else {
            // File failed to be deleted for some reason
            System.out.println("Failed to Delete File");
            response = new HTTPResponse(this, StatusCode.SERVER_ERROR, this.getHttpVersion());
        }
    
        response.send(this.getClient());
    }
    
    @Override
    public String getMainRequestHeader() {
        return "DELETE " + this.getRequestedURI() + " " + this.getHttpVersion();
    }
}
