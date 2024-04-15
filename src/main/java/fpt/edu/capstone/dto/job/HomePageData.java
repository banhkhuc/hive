package fpt.edu.capstone.dto.job;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HomePageData {
    List<JobHomePageResponse> remoteJob;
    List<JobHomePageResponse> parttimeJob;
    List<JobHomePageResponse> fulltimeJob;
    List<JobHomePageResponse> popularJob;
    List<JobHomePageResponse> urgentJob;
    List<JobHomePageResponse> newJob;
    List<JobHomePageResponse> jobByFields;
    List<JobHomePageResponse> suggestJobs;
}
