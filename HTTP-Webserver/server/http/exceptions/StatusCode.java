package server.http.exceptions;

public enum StatusCode {
    
    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    NOT_MODIFIED(304, "Not Modified"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    SERVER_ERROR(500, "Internal Server Error");
    
    private int statusCode;
    private String reason;
    
    StatusCode(int statusCode, String reason) {
        this.statusCode = statusCode;
        this.reason = reason;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getReason() {
        return reason;
    }
}
