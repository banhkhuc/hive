package fpt.edu.capstone.dto.job;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppliedJobRequest {
    private long jobId;
    private long candidateId;
    private String cvUrl;
}
