package fpt.edu.capstone.dto.banner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApproveBannerRequest {
    private long bannerActiveId;
    private String approvalStatus;
}
