package server.http.exceptions;

import server.http.HTTPHeader;
import server.http.requests.HTTPRequest;

import java.io.IOException;
import java.util.ArrayList;


public class ServerException extends IOException {
    
    /**
     * The ServerException is a wrapper for all expected errors that can occur in the server. This exception is used to
     * get localized, specific error messages and then have the server be able to respond appropriately to these errors.
     */
    
    
    
    private StatusCode errorCode;
    
    // Set default httpVersion to HTTP/1.1
    private HTTPRequest request;
    private String[] requestMainHeader;
    private String httpVersion = "HTTP/1.1";
    private ArrayList<HTTPHeader> headers;
    
    
    public ServerException(HTTPRequest request, String httpVersion, String message, Throwable cause, StatusCode errorCode) {
        super(message, cause);
        this.request = request;
        this.errorCode = errorCode;
        this.httpVersion = httpVersion;
    }
    
    public ServerException(HTTPRequest request, String httpVersion, String message, StatusCode errorCode) {
        super(message);
        this.request = request;
        this.errorCode = errorCode;
        this.httpVersion = httpVersion;
    }
    
    public ServerException(String[] requestMainHeader, String message, StatusCode errorCode) {
        super(message);
        this.requestMainHeader = requestMainHeader;
        this.httpVersion = requestMainHeader[2];
        this.errorCode = errorCode;
    }
    
    
    public ServerException(String message, Throwable cause, StatusCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ServerException(String message, StatusCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public void addHeader(HTTPHeader header) {
        if(headers == null) {
            headers = new ArrayList<>();
        }

        headers.add(header);

    }
    
    public HTTPRequest getRequest() {
        return request;
    }
    
    public StatusCode getErrorCode() {
        return errorCode;
    }
    
    public String getHttpVersion() {
        return httpVersion;
    }
    
    public ArrayList<HTTPHeader> getHeaders() {
        return headers;
    }
    
    public String[] getRequestMainHeader() {
        return requestMainHeader;
    }
    
    public boolean containsHeaders() {
        return headers != null && headers.size() != 0;
    }
}
