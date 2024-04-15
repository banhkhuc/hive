package fpt.edu.capstone.utils.enums;

/**
 * @author hungnd on 05-Feb-2021
 */
public class Enums {
  public enum ResponseStatus {
    SUCCESS("Success"), ERROR("Error");

    ResponseStatus(String status) {
      this.status = status;
    }

    public String getStatus() {
      return status;
    }

    private String status;

  }

  public enum DateTimeStatus {
    Today, Week, Month
  }

  public enum UserStatus {
     Activated(0), Deleted(1),Inactive(2), Locked(3);

    UserStatus(int status) {
      this.status = status;
    }

    private int status;

    public int status() {
      return status;
    }
  }

  public enum UserRefreshTokenStatus {
    NotExpire(0), Expired(1);

    UserRefreshTokenStatus(int userRefreshTokenStatus) {
      this.userRefreshTokenStatus = userRefreshTokenStatus;
    }

    private int userRefreshTokenStatus;

    public int value() {
      return userRefreshTokenStatus;
    }
  }

  public enum Scopes {
    ACCESS_TOKEN, REFRESH_TOKEN;

    public String authority() {
      return "ROLE_" + this.name();
    }
  }

  public enum TransactionType {
    Withdraw, Deposit, Transfer, Advances, Allowance
  }

  public enum TransactionStatus {
    Completed, Pending, Reject, Approved, Canceled, Created, Refund, Receive, Bonus, AllowanceTransfer, Pending_Order
  }

  public enum SocialProvider {
     GOOGLE, FACEBOOK, ZALO, APPLE
  }

  public enum OrderStatus {
    Completed, Pending, Accepted, Rejected, Cancel, Shipping, Exported, Created
  }

  public enum OrderType {
    Give("Give"), Delivery("Delivery"), TakeAway("Take Away");

    OrderType(String status) {
      this.status = status;
    }

    public String getStatus() {
      return status;
    }

    private String status;

  }

  public enum DetailMaterialTable {
    Size, Size_Material, Default_Material, Optional_Material
  }

  public enum NotificationType {
    Wallet, Order, Promotion
  }

  public enum PromotionStatus {
    Pending, Active, Inactive, Finished
  }
}
