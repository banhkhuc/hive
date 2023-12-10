package fpt.edu.capstone.utils;

public class ResponseData {
  protected Object data;
  protected String status;
  protected String message;

  public ResponseData() {
  }

  public ResponseData(Enums.ResponseStatus status, String message) {
    this.message = message;
    this.status = status.getStatus();
  }

  public ResponseData(String status, Object data) {
    this.data = data;
    this.status = status;
  }

  public ResponseData(String status, String message, Object data) {
    this.data = data;
    this.status = status;
    this.message = message;
  }

  public ResponseData(String status, String message) {
    this.status = status;
    this.message = message;
  }

  public ResponseData(Enums.ResponseStatus status, String message, Object data) {
    this.data = data;
    this.status = status.getStatus();
    this.message = message;
  }

  public ResponseData(Enums.ResponseStatus status, Object data) {
    this.data = data;
    this.status = status.getStatus();
  }

  public ResponseData(String status) {
    this.status = status;
  }

  public Object getData() {
    return data;
  }

  public ResponseData setData(Object data) {
    this.data = data;
    return this;
  }

  public String getStatus() {
    return status;
  }

  public ResponseData setStatus(String status) {
    this.status = status;
    return this;
  }

  public ResponseData setStatus(Enums.ResponseStatus status) {
    this.status = status.getStatus();
    return this;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
