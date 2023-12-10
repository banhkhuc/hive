package fpt.edu.capstone.dto.detail_package;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePackageRequest {
    private long rentalPackageId;
    private String detailName;
    private long price;
    private long discount;
    private String timeExpired;
    private String description;
    private boolean isRelatedJob;
    private boolean isSuggestJob;
    private int maxCvView;
    private boolean isPopularJob;
    private boolean isNewJob;
    private boolean isUrgentJob;
}
