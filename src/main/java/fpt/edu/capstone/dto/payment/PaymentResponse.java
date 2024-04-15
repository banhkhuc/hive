package fpt.edu.capstone.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private long paymentId;
    private long recruiterId;
    private long detailPackageId;
    private String detailPackageName;
    private long bannerId;
    private long jobId;
    private int amount;
    private String orderType;
    private String description;
    private String bankCode;
}
