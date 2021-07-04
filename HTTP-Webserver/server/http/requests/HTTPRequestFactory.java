package server.http.requests;


import server.configuration.HTAccess;
import server.configuration.HTTPDConf;
import server.configuration.MimeTypes;
import server.alias.Alias;
import server.alias.ScriptAlias;
import server.http.exceptions.ServerException;
import server.http.exceptions.StatusCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequestFactory {
    
    private static boolean isScript;
    
    public static HTTPRequest createHTTPRequest(Socket client, HTTPDConf config, MimeTypes mimeTypes, Map<String, HTAccess> authentication) throws ServerException {

        try {
            isScript = false;
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line;
    
            // Get the first line of the HTTP Request, should be METHOD URI HTTPVersion
            line = reader.readLine();
    
            if (line == null) {
                throw new ServerException("Bad Request Header", StatusCode.BAD_REQUEST);
            }
    
            // Split first line of HTTP Request Header by whitespace
            // 0th index - Method
            // 1st index - URI
            // 2nd index - HTTP Version
            String[] lineArr = line.split("\\s+");
            
    
            if (lineArr.length != 3) {
                throw new ServerException("Bad Request Header", StatusCode.BAD_REQUEST);
            }

            
            HTTPRequest request = null;
    
            // Get the absolute path to the resource requested
            String[] uri = parseURL(lineArr, config);
            
            // Create the specific HTTP Method Request Type
            switch (lineArr[0]) {
                case "GET":
                    request = new HTTPGetRequest(client, isScript, uri, lineArr, config, mimeTypes);
                    break;
                case "HEAD":
                    request = new HTTPHeadRequest(client, isScript, uri, lineArr, config, mimeTypes);
                    break;
                case "POST":
                    request = new HTTPPostRequest(client, isScript, uri, lineArr, config, mimeTypes);
                    break;
                case "PUT":
                    request = new HTTPPutRequest(client, isScript, uri, lineArr, config, mimeTypes);
                    break;
                case "DELETE":
                    request = new HTTPDeleteRequest(client, isScript, uri, lineArr, config, mimeTypes);
                    break;
                default:
                    throw new ServerException("Method Not Implemented", StatusCode.SERVER_ERROR);
            }
    
            Map<String, String> headers = new HashMap<>();
    
    
            while ((line = reader.readLine()) != null && line.length() > 0) {
        
                // Now parse headers, headers are split using ": "
                // Should only be length of 2
                lineArr = line.split(": ");
                if (lineArr.length != 2) {
                    // Header's aren't correctly formatted, bad request
                    throw new ServerException(lineArr, "Bad Request Header", StatusCode.BAD_REQUEST);
                }
        

                headers.put(lineArr[0], lineArr[1]);

            }
    
            request.setHeaders(headers);
    
            // Check if requested uri[0] contains a .htaccess file
            String authenticationFilePath;
    
            if((authenticationFilePath = config.getAuthenticationFile(uri[0])) != null) {
                // Get the HTAccess object that corresponds to the authenticationFilePath
                HTAccess htAccess = authentication.get(authenticationFilePath);

                // Authentication required, check for authorization request header
                if(headers.containsKey("Authorization")) {
                    // Authorization request header found, need to check Authorization request in HTAccess object
                    if(htAccess.checkAuthorization(headers.get("Authorization"))) {
                        // User is authorized, can access directory, get user and store in request object
                        String user = htAccess.getUser(headers.get("Authorization"));

                        request.setUser(user);
                    } else {
                        // User does not have permissions, throw 403 Server Exception
                        throw new ServerException(request, request.getHttpVersion(), "User Forbidden", StatusCode.FORBIDDEN);
                    }
                } else {
                    // Create ServerException
                    ServerException e = new ServerException(request, request.getHttpVersion(), "Unauthorized", StatusCode.UNAUTHORIZED);

                    // Get authentication headers from authentication hashmap
                    e.addHeader(htAccess.getAuthorizationHeader());


                    throw e;
                }
            }
    
            // Check if request has body
            if (headers.containsKey("Content-Length")) {
                // Request has body, read rest of request
    
                try {
                    int contentLength = Integer.parseInt(headers.get("Content-Length"));
    
                    char[] body = new char[contentLength];
    
                    reader.read(body, 0, contentLength);
                    // Set HTTPRequest object's body
                    request.setBody(body);
                } catch(NumberFormatException e) {
                    throw new ServerException(request, request.getHttpVersion(), "Error Content-Length value not valid", StatusCode.BAD_REQUEST);
                }
            }
            return request;
            
        } catch(ServerException e) {
            throw e;
        } catch (IOException e) {
            throw new ServerException("Error creating HTTP Request object", StatusCode.SERVER_ERROR);
        }
        
    }
    
    
    /**
     *
     * @param requestHeader - First line of Client request
     * @param config - Configurations
     * @return String[] array of length 2:
     *          - Index 0: path to directory of file
     *          - Index 1: name of file
     */
    private static String[] parseURL(String[] requestHeader, HTTPDConf config) throws ServerException{
        String url = requestHeader[1];
        System.out.println("Client Request URI: " + url);
        
        String documentRoot = config.getConfig("DocumentRoot");
        if(documentRoot == null) {
            throw new ServerException(requestHeader, "Error during server startup, config has no DocumentRoot directive", StatusCode.SERVER_ERROR);
        }
    
        // Check to see if file was provided explicitly in the request
        if(url.equals("/")) {
            // File was not provided explicitly, default to index.html
            return new String[]{documentRoot, "index.html"};
        }
        
        
        // Need to get rid of file name that is being requested, so get index of last "/"
        int fileIndex = url.lastIndexOf("/") + 1;
        String uri = url.substring(0, fileIndex);
        // Check to see if url is aliased
        if(config.containsAlias(uri)) {
            // URL is aliased, need to determine whether it is a script or not
            
            Alias alias = config.getAlias(uri);
            
            if(alias instanceof ScriptAlias) {
                isScript = true;
            }
            
            // Create uri path using alias' absolute path and the file name from request
            
            String resource = url.substring(fileIndex);
            
            return new String[] {
                    alias.getAbsolutePath(),
                    resource.length() > 0 ? resource : "index.html"
            };
        }
        
        
        // URL is unmodified, resolve path using DocumentRoot
        
        // Check if url ends with "/", if true, need to append index.html to uri
        if(fileIndex == url.length()) {
            return new String[] {documentRoot + url.substring(1), "index.html"};
        }
        return new String[] {documentRoot + url.substring(1, fileIndex), url.substring(fileIndex)};
    }
    
    
}
