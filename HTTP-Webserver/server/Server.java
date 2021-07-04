package server;

import server.configuration.HTAccess;
import server.configuration.HTTPDConf;
import server.configuration.MimeTypes;


import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Server {
    
    private final int DEFAULT_PORT = 8080;
    
    
    private ServerSocket serverSocket;
    
    private HTTPDConf config;
    private MimeTypes mimeTypes;
    private Map<String, HTAccess> authentication;
    
    // Read/Parse config files
    public Server() throws IOException {
        
        config = new HTTPDConf("conf" + File.separator + "httpd.conf");
        mimeTypes = new MimeTypes("conf" + File.separator + "mime.types");
        
        authentication = new HashMap<>();
        
        for(String authFilePath : config.getAuthFilePaths()) {
            authentication.put(authFilePath, new HTAccess(authFilePath));
        }
        
        
    }
    
    /**
     * This function starts the server by creating/setting the serverSocket to the correct port and blocks, waiting for
     * client requests
     */
    public void start() throws IOException {
        System.out.println("Booting up Server...");
        int portNumber = config.containsConfig("Listen") ? Integer.parseInt(config.getConfig("Listen")) : DEFAULT_PORT;
        serverSocket = new ServerSocket(portNumber);
        Socket client = null;
        System.out.println("Server Listening for Client Connections at Port " + portNumber + " ...");
        while(true) {
            
            // Wait/Get client HTTP Request
            client = serverSocket.accept();
            System.out.println("Received connection");
            // Create ServerRequest
            ServerRequest serverRequest = new ServerRequest(client, config, mimeTypes, authentication);
            
            // Run ServerRequest
            serverRequest.run();
            
            // Close client connection
            client.close();
        }
        
    }
}
