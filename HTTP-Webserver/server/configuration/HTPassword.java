package server.configuration;

import server.http.exceptions.ServerException;
import server.http.exceptions.StatusCode;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Base64;
import java.nio.charset.Charset;
import java.security.MessageDigest;

import java.io.IOException;

public class HTPassword extends ConfigurationReader{
    private HashMap<String, String> passwords;
    
    
    public HTPassword( String filePath ) throws IOException {
        super( filePath );

        System.out.println( "Password file: " + filePath );
        
        this.passwords = new HashMap<String, String>();
        this.load();
    }
    
    protected void parseLine( String line ) {
        String[] tokens = line.split( ":" );
        
        if( tokens.length == 2 ) {
            passwords.put( tokens[ 0 ], tokens[ 1 ].replace( "{SHA}", "" ).trim() );
        }
    }
    
    public boolean isAuthorized( String authInfo ) throws ServerException{
        // authInfo is provided in the header received from the client
        // as a Base64 encoded string.
        
        try {
            String credentials = new String(
                    Base64.getDecoder().decode(authInfo),
                    Charset.forName("UTF-8")
            );
    
            // The string is the key:value pair username:password
            String[] tokens = credentials.split( ":" );
    
            return verifyPassword(tokens[0], tokens[1]);
        } catch(IllegalArgumentException e) {
            throw new ServerException("Bad Request", e, StatusCode.BAD_REQUEST);
        }
    }
    
    public String getUserAuthorized( String authInfo ) {
        // authInfo is provided in the header received from the client
        // as a Base64 encoded string.
        String credentials = new String(
                Base64.getDecoder().decode( authInfo ),
                Charset.forName( "UTF-8" )
        );
    
        // The string is the key:value pair username:password
        String[] tokens = credentials.split( ":" );
        
        return passwords.get(tokens[0]);
    }
    
    private boolean verifyPassword( String username, String password ) {
        // encrypt the password, and compare it to the password stored
        // in the password file (keyed by username)
        String encryptedPassword = encryptClearPassword(password);
        
        if(passwords.containsKey(username)) {
            // Valid username, now check if passwords match
            String actualPassword = passwords.get(username);
            
            return actualPassword.equals(encryptedPassword);
        }
        
        return false;
    }
    
    private String encryptClearPassword( String password ) {
        // Encrypt the cleartext password (that was decoded from the Base64 String
        // provided by the client) using the SHA-1 encryption algorithm
        try {
            MessageDigest mDigest = MessageDigest.getInstance( "SHA-1" );
            byte[] result = mDigest.digest( password.getBytes() );
            
            return Base64.getEncoder().encodeToString( result );
        } catch( Exception e ) {
            return "";
        }
    }
    
    @Override
    protected void load() throws IOException {
    
        BufferedReader reader = getBufferedReaderForFile();
        
        String line;
        
        while((line = reader.readLine()) != null && line.length() > 0) {
            
            // Line is commented
            if(line.charAt(0) == '#') {
                continue;
            }
            
            parseLine(line);
        }
    
    
    }
    
    
}
