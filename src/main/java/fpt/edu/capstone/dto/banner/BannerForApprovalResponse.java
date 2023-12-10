package fpt.edu.capstone.dto.banner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannerForApprovalResponse {
    private long bannerActiveId;
    private String displayPosition;
    private String packageName;
    private String recruiterName;
    private long companyId;
    private String companyName;
    private LocalDateTime applyStartDate;
    private LocalDateTime applyEndDate;
    private LocalDateTime buyDate;
    private LocalDateTime approvalDate;
    private String approvalStatus;
    private String bannerImageUrl;
    private LocalDateTime updatedAt;
}
