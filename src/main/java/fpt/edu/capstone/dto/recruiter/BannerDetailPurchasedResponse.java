package fpt.edu.capstone.dto.recruiter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BannerDetailPurchasedResponse {
    private List<BannerPositionDetailResponse> bannerPosRes;
}
