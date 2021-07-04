package server.configuration;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class ConfigurationReader {
    
    private String filePath;
    
    protected ConfigurationReader(String filePath) throws IOException {
        this.filePath = filePath;
    }
    
    protected abstract void load() throws IOException;
    
    protected BufferedReader getBufferedReaderForFile() throws IOException {
        
    
        File file = new File(filePath);
        
        BufferedReader reader = new BufferedReader(new FileReader(file));
    
        if(!reader.ready()) {
            System.out.println("BufferedReader is empty. Something went wrong when getting httpd.conf file");
            System.exit(-1);
        }
        
        
        return reader;
    }
}
