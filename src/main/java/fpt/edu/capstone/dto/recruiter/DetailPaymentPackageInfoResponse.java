package fpt.edu.capstone.dto.recruiter;

import fpt.edu.capstone.entity.Banner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailPaymentPackageInfoResponse {
    NormalPaymentPackage normalPaymentPackage;
    Banner bannerPaymentPackage;
}
