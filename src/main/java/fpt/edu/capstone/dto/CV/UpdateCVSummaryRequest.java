package fpt.edu.capstone.dto.CV;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateCVSummaryRequest {
    @NotBlank(message = "CvId must not be blank")
    private long cvId;
    private String newSummary;
}
