package fpt.edu.capstone.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileViewerResponse {
    private String viewerName;
    private long companyId;
    private String companyName;
    private LocalDateTime viewDate;
    private String companyAvatar;
}
