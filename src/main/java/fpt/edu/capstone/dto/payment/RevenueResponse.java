package fpt.edu.capstone.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RevenueResponse {
    private long id;
    private long recruiterId;
    private String recruiterName;
    private long jobId;
    private long detailPackageId;
    private String rentalPackageName;
    private long bannerId;
    private String transactionCode;
    private int amount;
    private String description;
    private String orderType;
    private String bankCode;
    private String command;
    private String currCode;
    private String local;
    private LocalDateTime expiredDate;
    private boolean expiredStatus;
    private LocalDateTime createdAt;
}
