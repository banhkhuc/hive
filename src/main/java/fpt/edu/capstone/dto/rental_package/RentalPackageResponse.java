package fpt.edu.capstone.dto.rental_package;

import fpt.edu.capstone.entity.Banner;
import fpt.edu.capstone.entity.DetailPackage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RentalPackageResponse {
    private List<Banner> banner;
    private List<DetailPackage> detailPackage;
}
