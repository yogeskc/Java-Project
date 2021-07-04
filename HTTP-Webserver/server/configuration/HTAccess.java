package server.configuration;

import server.http.HTTPHeader;
import server.http.exceptions.ServerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HTAccess extends ConfigurationReader{
    
    private String htPasswordFilePath;
    private Map<String, String> headers;
    private HTPassword htPassword;
    
    public HTAccess(String filePath) throws IOException{
        super(filePath + ".htaccess");
    
        headers = new HashMap<>();
        
        // Load and parse HTAccess file
        this.load();
        
        // Create HTPassword object that accompanies HTAccess file
        htPassword = new HTPassword(htPasswordFilePath);
    }
    
    /**
     * Get the correctly formatted Authorization HTTPHeader to be sent in an HTTP 401 Response
     * @return
     */
    public HTTPHeader getAuthorizationHeader() {
        return new HTTPHeader("WWW-Authenticate", headers.get("AuthType") + " realm=\"" + headers.get("AuthName") + "\"");
    }
    
    
    public boolean checkAuthorization(String authorizationValue) throws ServerException {
        
        // Authorization value is the authentication type and encrypted user:password
        // Separated by space
        String[] authorizationArr = authorizationValue.split("\\s+");
        
        // Send encrypted user:password to HTPassword for decryption and check if user is valid
        return htPassword.isAuthorized(authorizationArr[1]);
    }
    
    public String getUser(String authorizationValue) {
        // Authorization value is the authentication type and encrypted user:password
        // Separated by space
        String[] authorizationArr = authorizationValue.split("\\s+");
        
        return htPassword.getUserAuthorized(authorizationArr[1]);
    }
    
    
    
    /**
     * This function parses the .htacess file for the WebServer. This function assumes that the .htaccess file follows
     * these rules:
     *
     * - '#' is a comment character and will be the first character of a line when used
     * - Every directive line is split by a space
     * - Every directive line is formatted as: DIRECTIVE VALUE (So every directive line has two values to worry about
     * - Contains directive "AuthUserFile"
     * @throws IOException - Cannot get requested access file
     */
    protected void load() throws IOException {
        
        // Get .htaccess file
        BufferedReader reader = getBufferedReaderForFile();

        if (!reader.ready()) {
            System.out.println("BufferedReader is empty. Something went wrong when getting file");
            System.exit(-1);
        }

        String line;

        
        while ((line = reader.readLine()) != null) {
            // If line starts with '#', disregard line
            if (line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }
    
    
            // Split string, but limit array length to 2 to take into account spaces in path to .htpasswd file
            String[] keyValuePairs = line.split(" ", 2);
    
            if (keyValuePairs[0].equals("AuthUserFile")) {
                // Extract pathToHTPassword, remove quotations
                if (keyValuePairs[1].contains("\"")) {
                    
                    htPasswordFilePath = keyValuePairs[1].substring(1, keyValuePairs[1].length() - 1);
                } else {
                    htPasswordFilePath = keyValuePairs[1];
                }
            } else {
                // These will be authentication headers that will be sent to the client
                headers.put(keyValuePairs[0], keyValuePairs[1]);
            }
    
        }
        
        reader.close();
        
    }
}
