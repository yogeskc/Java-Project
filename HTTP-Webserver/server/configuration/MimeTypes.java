package server.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MimeTypes extends ConfigurationReader{
    
    Map<String, String> mimeTypes;
    
    public MimeTypes(String filePath) throws IOException{
        super(filePath);
        
        this.mimeTypes = new HashMap<>();
        
        this.load();
    }
    
    public boolean containsFileExtension(String fileExtension) {
        return mimeTypes.containsKey(fileExtension);
    }
    
    public String getMimeType(String fileExtension) {
        return mimeTypes.get(fileExtension);
    }
    
    
    
    @Override
    protected void load() throws IOException {
        
        
        BufferedReader reader = getBufferedReaderForFile();

        String line;

        while ((line = reader.readLine()) != null) {
            // If line starts with "#", disregard line because it is a comment.
            if (line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }
    
            // Split line by whitespace
            // 0th index will always be the value, the MIME type
            // 1...N-nth index will always be a key, the file extension type
            String[] keyValuePairs = line.split("\\s+");
            int length;
            if ((length = keyValuePairs.length) > 1) {
                // There is at least one file extension type that corresponds to a MIME type
        
                // For every file extension type, add to the hashmap
                for (int i = 1; i < length; i++) {
                    mimeTypes.put(keyValuePairs[i], keyValuePairs[0]);
                }
            }
            // Else there isn't a file extension type associated to a MIME type, deal with it later on
    
        }

        // Close BufferedReader to save resources
        reader.close();
        
    }
}
