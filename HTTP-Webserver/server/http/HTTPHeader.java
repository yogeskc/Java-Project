package server.http;

public class HTTPHeader {
    
    private String headerName;
    private String value;
    
    
    public HTTPHeader(String headerName, String value) {
        this.headerName = headerName;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.headerName + ": " + this.value;
    }
}
