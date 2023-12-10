package fpt.edu.capstone.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private long recruiterId;
    private long detailPackageId;
    private long bannerId; // banner package id
    private long jobId;
    private int amount;
    private String orderType;
    private String description;
    private String bankCode;
}
