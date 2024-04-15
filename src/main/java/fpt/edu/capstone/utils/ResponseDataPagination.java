package fpt.edu.capstone.utils;

public class ResponseDataPagination extends ResponseData {

  private Object pagination;

  public ResponseDataPagination() {
    super();
  }

  public Object getPagination() {
    return pagination;
  }

  public ResponseDataPagination setPagination(Object pagination) {
    this.pagination = pagination;
    return this;
  }

  public ResponseDataPagination(String code, String message, Object data, Object pagination) {
    super(code, message, data);
    this.pagination = pagination;
  }

}
