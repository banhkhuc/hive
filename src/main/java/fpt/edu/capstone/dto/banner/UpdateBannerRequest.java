package fpt.edu.capstone.dto.banner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBannerRequest {
    private long bannerId;
    private long rentalPackageId;//mặc định thuộc gói duy nhất: gói banner
    private long price;
    private long discount;
    private String timeExpired;
    private String title;
    private String description;
    private String image;
    //Vị trí các banner
    private boolean spotlight;
    private boolean homepageBannerA;
    private boolean homePageBannerB;
    private boolean homePageBannerC;
    private boolean jobBannerA;
    private boolean jobBannerB;
    private boolean jobBannerC;
}
