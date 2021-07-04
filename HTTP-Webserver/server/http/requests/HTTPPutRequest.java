package server.http.requests;

import server.configuration.HTTPDConf;
import server.configuration.MimeTypes;
import server.http.HTTPResponse;
import server.http.exceptions.ServerException;
import server.http.exceptions.StatusCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HTTPPutRequest extends HTTPRequest{
    public HTTPPutRequest(Socket client, boolean isScript, String[] uri, String[] requestMainHeader, HTTPDConf config, MimeTypes mimeTypes) {
        super(client, isScript, uri, requestMainHeader, config, mimeTypes);
    }
    
    @Override
    public void execute() throws ServerException {
        
        
        File file = null;
        
        
    
        HTTPResponse response = null;
        try {
            // Get the file requested
    
            file = this.getFileRequested();
    
            if(this.isScript()) {
                try {
                    handleScript(this.getClient(), file);
                    return;
                }catch(ServerException e) {
                    throw e;
                }
            }
            
            
            if(this.getBody() != null && this.getBody().length() >  0) {
                try {
                    // Create FileOutputStream that overwrites file
                    FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        
                    fileOutputStream.write(this.getBody().getBytes());
                    fileOutputStream.close();
        
                    
                } catch (IOException e) {
                    throw new ServerException(this, this.getHttpVersion(), "Error writing to file", e, StatusCode.SERVER_ERROR);
                }
            }
            response = new HTTPResponse(this, StatusCode.OK, this.getHttpVersion());
    
        } catch (ServerException e) {
            // File not found, need to create one
            String[] uri = this.getUri();
            file = new File(uri[0] + uri[1]);
            try {
                if(!file.createNewFile()) {
                    System.out.println("Couldn't create file");
                }
    
                if(this.getBody() != null && this.getBody().length() > 0) {
                    // Check to see if there is a request body and if true, write to file
                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
    
    
                    fileOutputStream.write(this.getBody().getBytes());
                    fileOutputStream.close();
                }
                
                response = new HTTPResponse(this, StatusCode.CREATED, e.getHttpVersion());
                response.send(this.getClient());
            } catch(IOException ignored) {
                throw new ServerException(this, this.getHttpVersion(), "Error creating file", e, StatusCode.SERVER_ERROR);
            }
        }
        
        response.send(this.getClient());
    }
    
    @Override
    public String getMainRequestHeader() {
        return "PUT " + this.getRequestedURI() + " " + this.getHttpVersion();
    }
}
