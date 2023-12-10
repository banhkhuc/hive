package fpt.edu.capstone.utils;

public class ResponseDataPaginationRevenue extends ResponseDataRevenue {

  private Object pagination;

  public ResponseDataPaginationRevenue() {
    super();
  }

  public Object getPagination() {
    return pagination;
  }

  public ResponseDataPaginationRevenue setPagination(Object pagination) {
    this.pagination = pagination;
    return this;
  }

  public ResponseDataPaginationRevenue(String code, String message, Object data, long totalRevenue, Object pagination) {
    super(data, code, message, totalRevenue);
    this.pagination = pagination;
  }

}
