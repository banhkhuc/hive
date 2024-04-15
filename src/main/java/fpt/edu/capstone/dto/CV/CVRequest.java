package fpt.edu.capstone.dto.CV;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CVRequest {
    private long candidateId;
    private String summary;
    private String totalExperienceYear;
}
