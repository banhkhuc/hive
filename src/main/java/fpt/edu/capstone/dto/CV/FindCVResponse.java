package fpt.edu.capstone.dto.CV;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FindCVResponse {
    private long id;
    private long candidateId;
    private long isDeleted;
    private String summary;
    private String totalExperienceYear;
    private List<String> techStack;
    private String address;
    private String fullName;
    private String avatarUrl;
}
