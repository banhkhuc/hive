package fpt.edu.capstone.dto.CV;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ViewCvResponse {
    private long cvId;
    private long viewerId;
    private long candidateId;
}
