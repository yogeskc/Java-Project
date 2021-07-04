package server.configuration;

import server.logs.Logger;
import server.alias.Alias;
import server.alias.ScriptAlias;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPDConf extends ConfigurationReader{
    
    private Map<String, String> config;
    private Map<String, Alias> aliases;
    private List<String> authFilePaths;
    
    public HTTPDConf(String filePath) throws IOException {
        super(filePath);
        
        this.config = new HashMap<>();
        this.aliases = new HashMap<>();
        this.authFilePaths = new ArrayList<>();
        
        this.load();
    }
    
    public List<String> getAuthFilePaths() {
        return authFilePaths;
    }
    
    public boolean containsConfig(String configDirective) {
        return config.containsKey(configDirective);
    }
    
    public String getConfig(String configDirective) {
        return config.get(configDirective);
    }
    
    public boolean containsAlias(String aliasPath) {
        return aliases.containsKey(aliasPath);
    }
    
    public Alias getAlias(String aliasPath) {
        return aliases.get(aliasPath);
    }
    
    /**
     * This function checks if there are any .htaccess files at or above the directory level of the uri
     * @param uri - Path to resource not including resource specified
     * @return
     */
    public String getAuthenticationFile(String uri) {
        // Base case, end at document root
        if(uri.equals(config.get("DocumentRoot"))) {
            // Check if document root contains a .htaccess file
            if(authFilePaths.contains(uri)) {
                return uri;
            }
            return null;
        }
        
        if(authFilePaths.contains(uri)) {
            return uri;
        }
        
        int index = uri.lastIndexOf("/", uri.length() - 2);
        return getAuthenticationFile(uri.substring(0, index + 1));
    }
    
    
    @Override
    protected void load() throws IOException {
        // This should be 20 lines of code
        
        BufferedReader reader = getBufferedReaderForFile();

        String line;

        while ((line = reader.readLine()) != null) {
    
            // If line starts with "#", disregard line because it is a comment
            if (line.charAt(0) == '#') {
                continue;
            }
    
            // Split line by space and have maximum array of 3
            // 0th Index - Holds the Key
            // 1st Index - Holds the absolute path or Symbolic path depending on if length > 2
            // 2nd Index - Holds the absolute path if length > 2
            String[] keyValuePairs = line.split(" ", 3);
    
            if (isAliasConfig(keyValuePairs)) {
                // Assume is an alias, therefore put key value pairs in alias hashmap
        
                // Need to get rid of quotations from absolute path
                String absolutePath = keyValuePairs[2].contains("\"")
                        ? keyValuePairs[2].substring(1, keyValuePairs[2].length() - 1)
                        : keyValuePairs[2];
                if (keyValuePairs[0].equals("Alias")) {
                    // Create Alias
                    Alias alias = new Alias(absolutePath);
            
                    // Add alias to aliases hashmap
                    aliases.put(keyValuePairs[1], alias);
                } else {
                    // Create Script Alias
                    ScriptAlias scriptAlias = new ScriptAlias(absolutePath);
            
                    // Add scriptAlias to aliases hashmap
                    aliases.put(keyValuePairs[1], scriptAlias);
                }
            } else if (isAuthenticationConfig(keyValuePairs)) {
                // Store locations of access files
                if(keyValuePairs.length > 2) {
                    // AccessFile path contains spaces, also need to remove quotations
    
                    
                    String path = keyValuePairs[1].substring(1) + " " + keyValuePairs[2].substring(0, keyValuePairs[2].length() - 1);
                    
                    
                    
                    
                    authFilePaths.add(path.substring(0, path.lastIndexOf("/") + 1));
                } else {
                    // AccessFile path contains no spaces
                    
                    String path = keyValuePairs[1].substring(1, keyValuePairs[1].length() - 1);
                    
                    authFilePaths.add(path.substring(0, path.lastIndexOf("/") + 1));
                }
        
            } else {
                // Assume not an alias or authentication config, therefore put key value pairs in config hashmap
        
                // Since there may be paths in quotes, and these quotations are delimited, need to check if
                // keyValuePairs[1] contains quotations, then assume it is a path, so need to get rid of these
                // quotations.
                if (keyValuePairs[1].contains("\"")) {
                    if (keyValuePairs.length > 2) {
                        // Path in config file contains spaces, so length of keyValuePairs is 3
                        
                        String path = keyValuePairs[1].substring(1) + " " + keyValuePairs[2].substring(0, keyValuePairs[2].length() - 1);
                        
                        config.put(keyValuePairs[0], path);
                    } else {
                        
                        String path = keyValuePairs[1].substring(1, keyValuePairs[1].length() - 1);
                        
                        config.put(keyValuePairs[0], path);
                    }
                } else {
                    
                    
                    config.put(keyValuePairs[0], keyValuePairs[1]);
                }
            }
        }

        // Create and set Logger logfile location
        if (config.containsKey("LogFile")) {
            Logger logger = Logger.getLogger(config.get("LogFile"));
        }


        // Close BufferedReader to save resources
        reader.close();
    }
    
    /**
     * This function checks whether a line contains the Alias keyword
     * @param lineArr - A line from readLine() that was split by a delimiter
     * @return whether or not the lineArr should be considered an Alias config
     */
    private boolean isAliasConfig(String[] lineArr) {
        return lineArr[0].contains("Alias");
    }
    
    /**
     * This function checks whether a line directive is for authentication access
     * @param lineArr - A line from readLine() that was split by a delimiter
     * @return whether or not the lineArr should be considered an authentication config
     */
    private boolean isAuthenticationConfig(String[] lineArr) {
        return lineArr[0].contains("AccessFile");
    }
}
