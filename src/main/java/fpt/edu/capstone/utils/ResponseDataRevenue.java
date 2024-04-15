package fpt.edu.capstone.utils;

public class ResponseDataRevenue {
    protected Object data;
    protected String status;
    protected String message;
    protected long totalRevenue;

    public ResponseDataRevenue() {
    }

    public ResponseDataRevenue(Object data, String status, String message, long totalRevenue) {
        this.data = data;
        this.status = status;
        this.message = message;
        this.totalRevenue = totalRevenue;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
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

    public long getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(long totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
