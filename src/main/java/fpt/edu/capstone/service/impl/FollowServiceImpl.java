package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.candidate.FollowingResponse;
import fpt.edu.capstone.dto.job.JobResponse;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.repository.FollowRepository;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.Pagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;

    private final ModelMapper modelMapper;

    private final JobService jobService;

    private CompanyService companyService;

    private ImageService imageService;

    private JobHashTagService jobHashTagService;

    @Override
    public void unFollow(long followerId, long followedId, long type) {
        followRepository.unFollow(followerId, followedId, type);
    }

    @Override
    public Follow insertFollow(Follow follow) {
        return followRepository.save(follow);
    }

    @Override
    public Boolean isFollowing(long followerId, long followedId, long type) {
        Optional<Follow> follow = followRepository.getFollowIfHave(followerId, followedId, type);
        if(follow.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public ResponseDataPagination getFollowedJobByCandidateID(Integer pageNo, Integer pageSize, long candidateId) {
        List<JobResponse> responseList = new ArrayList<>();

        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);

        Page<FollowingResponse> follows = followRepository.getFollowedJobByCandidateID(pageable, candidateId);
        if(follows.hasContent()) {
            for(FollowingResponse follow : follows) {
                Job job = jobService.getJobById(follow.getFollowedId());
                JobResponse jobResponse = modelMapper.map(job, JobResponse.class);
                jobResponse.setJobId(job.getId());
                List<JobHashtag> listJobHashTag = jobHashTagService.getHashTagOfJob(job.getId());
                if (!(listJobHashTag.isEmpty() && listJobHashTag == null)) {
                    List<String> hashTagNameList = listJobHashTag.stream().map(JobHashtag::getHashTagName).collect(Collectors.toList());
                    jobResponse.setListHashtag(hashTagNameList);
                }
                Company company = companyService.getCompanyById(job.getCompanyId());
                if (company != null) {
                    jobResponse.setCompanyName(company.getName());
                }
                Optional<Image> image = imageService.getImageCompany(company.getId(), true);
                if (image.isPresent()) {
                    jobResponse.setCompanyAvatar(image.get().getUrl());
                }
                responseList.add(jobResponse);
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(follows.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(follows.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public Optional<List<Follow>> getAllFollowerOfAJob(long jobId) {
        return followRepository.getAllFollowerOfAJob(jobId);
    }
}
