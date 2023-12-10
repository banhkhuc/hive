package fpt.edu.capstone.dto.banner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadBannerRequest {
    private long paymentId;
    String spotLightImage;
    String homepageBannerAImage;
    String homepageBannerBImage;
    String homepageBannerCImage;
    String jobBannerAImage;
    String jobBannerBImage;
    String jobBannerCImage;
}
