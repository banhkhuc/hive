package fpt.edu.capstone.dto.detail_package;

import fpt.edu.capstone.entity.Banner;
import fpt.edu.capstone.entity.DetailPackage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailPackageResponse {
    Banner bannerPackage;
    DetailPackage normalPackage;
}
