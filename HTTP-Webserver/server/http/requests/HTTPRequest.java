package server.http.requests;


import server.configuration.HTTPDConf;
import server.configuration.MimeTypes;
import server.http.HTTPHeader;
import server.http.HTTPResponse;
import server.http.exceptions.ServerException;
import server.http.exceptions.StatusCode;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class HTTPRequest {
    
    private Socket client;
    private String user;
    private boolean isScript;
    private String[] uri;
    private String requestedURI;
    private String httpVersion;
    private Map<String, String> headers;
    private String body;
    
    // Server configs
    private HTTPDConf config;
    private MimeTypes mimeTypes;
    

    
    public HTTPRequest(Socket client, boolean isScript, String[] uri, String[] requestMainHeader, HTTPDConf config, MimeTypes mimeTypes) {
        this.client = client;
        this.isScript = isScript;
        this.uri = uri;
        this.requestedURI = requestMainHeader[1];
        this.httpVersion = requestMainHeader[2];
        this.config = config;
        this.mimeTypes = mimeTypes;
    }
    
    
    public abstract void execute() throws ServerException;
    
    public abstract String getMainRequestHeader();
    
    protected void handleScript(Socket client, File file) throws ServerException {
        
        
        try {
            List<String> commands = new ArrayList<>();
    
            BufferedReader reader = new BufferedReader(new FileReader(file));
            
            String line;
            
            while((line = reader.readLine()) != null) {
                if(line.contains("#!")) {
                    // Found shebang line, should always be first to characters
                    String[] lineArr = line.substring(2).split("\\s");
    
                    Collections.addAll(commands, lineArr);
                    break;
                }
            }
            
            commands.add(file.getCanonicalPath());
            
            // Create the ProcessBuilder object
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            
            // Get the process builder environment
            Map<String, String> env = processBuilder.environment();
            
            // Add all of the request headers into the environment with HTTP_ prepended and capitalized
            Map<String, String> requestHeaders = this.getHeaders();
            for (String key : requestHeaders.keySet()) {
                env.put("HTTP_" + key.toUpperCase(), requestHeaders.get(key));
            }
            
            Process process = processBuilder.start();
            
            // Create HTTP Response
            HTTPResponse response = new HTTPResponse(this, StatusCode.OK, this.getHttpVersion());
            response.setRequestedResource(file);
            // Send status line to client, rest of response will be dealt with later after CGI Script is run
            response.sendStatusLine(client);
            
            // Get input stream for process
            OutputStream stdin = process.getOutputStream();
            
            // Read body from HTTP Request into process
            if (this.getBody() != null) {
                stdin.write(this.getBody().getBytes());
                stdin.flush();
                stdin.close();
            }
            
            // Get output stream from process
            InputStream stdout = process.getInputStream();
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));

            List<String> body = new ArrayList<>();
            while((line = stdoutReader.readLine()) != null) {
                if(line.length() < 1) {
                    // If empty line, continue
                    continue;
                }
                
                if(line.contains("Content-Type: ")) {
                    String[] lineArr = line.split("Content-Type: ");
                    response.addHeader(new HTTPHeader("Content-Type", lineArr[1]));
                    continue;
                }
                
                body.add(line);
            }
            List<Byte> bodyBytes = new ArrayList<>();
            for(String bodyLine : body) {
                byte[] tmp = bodyLine.getBytes();
                for(byte data: tmp) {
                    bodyBytes.add(data);
                }
            }
            
            response.setBody(bodyBytes);
            
            response.sendHeaders(client);
            response.sendBody(client);
            
        } catch(ServerException e) {
            throw e;
        } catch(IOException e) {
            throw new ServerException(this, this.getHttpVersion(), "Error running CGI Script", StatusCode.SERVER_ERROR);
        }
    }
    
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    public void setBody(char[] body) {
        this.body = new String(body);
    }
    
    /**
     * This function returns the MIME type that the extension corresponds to.
     *  Assumptions made when implementing this function:
     *      - URI does not have any "." characters besides the actual file requested
     *
     * @return The MIME type of the requested URI
     */
    protected String getMimeType() throws ServerException {
        
        String[] pathSplit = uri[1].split("\\.");
        
        if(pathSplit.length != 2) {
            throw new ServerException("URI given was not properly formatted", StatusCode.SERVER_ERROR);
        }
        
        // Get extensionType
        String extensionType = pathSplit[1];
        
        // If unknown extension type, default to text/text
        return mimeTypes.containsFileExtension(extensionType) ? mimeTypes.getMimeType(extensionType) : "text/text";
    }
    
    /**
     * This function returns the requested file based off of the String[] uri
     * @return requested file
     */
    protected File getFileRequested() throws ServerException {
        // Get the file requested
        String path = uri[0] + uri[1];
        
        File file = new File(path);
    
        
        if(!file.exists() || file.isDirectory()) {
            // Make sure file is not a directory, have bad memories of deleting everything
            // File does not exist, throw ServerException
            throw new ServerException(this, httpVersion, "File does not exist", StatusCode.NOT_FOUND);
        }
        
        return file;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public String getUser() {
        return this.user != null ? this.user : "-";
    }
    
    public Socket getClient() {
        return client;
    }
    
    protected String getBody() {
        return body;
    }
    
    protected HTTPDConf getConfig() {
        return config;
    }
    
    protected MimeTypes getMimeTypes() {
        return mimeTypes;
    }
    
    
    protected boolean isScript() {
        return isScript;
    }
    
    protected String[] getUri() {
        return uri;
    }
    
    protected String getRequestedURI() {
        return requestedURI;
    }
    
    protected String getHttpVersion() {
        return httpVersion;
    }
    
    protected Map<String, String> getHeaders() {
        return headers;
    }
}
