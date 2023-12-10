package fpt.edu.capstone.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NormalPaymentPackage {
    private long groupPackageId;
    private String groupPackageName;
    private String detailPackageName;
    private long price;
    private long discountPrice;
    private String timeExpired;
    private boolean isRelatedJob;
    private boolean isSuggestJob;
    //banner package group only use this field
    private long maxCvView;
}
