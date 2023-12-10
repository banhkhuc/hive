package fpt.edu.capstone.utils;

public class ResponseDataUser {
    protected Object data;
    protected String token;
    protected String status;
    protected String message;

    public ResponseDataUser(String status, String message,Object data, String token) {
        this.data = data;
        this.token = token;
        this.status = status;
        this.message = message;
    }

    public ResponseDataUser(String status, String message,Object data) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public ResponseDataUser(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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
}
