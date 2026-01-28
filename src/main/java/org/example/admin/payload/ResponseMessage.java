package org.example.admin.payload;

public class ResponseMessage<T> {
    private String status;
    private String message;
    private T data;

    public ResponseMessage(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters and setters (optional, depending on Lombok usage)
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
