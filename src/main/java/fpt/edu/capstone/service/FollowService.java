package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.Follow;
import fpt.edu.capstone.utils.ResponseDataPagination;

import java.util.List;
import java.util.Optional;

public interface FollowService {
    void unFollow(long followerId, long followedId, long type);

    Follow insertFollow(Follow follow);

    Boolean isFollowing(long followerId, long followedId, long type);

    ResponseDataPagination getFollowedJobByCandidateID(Integer pageNo, Integer pageSize, long candidateId);

    Optional<List<Follow>> getAllFollowerOfAJob(long jobId);
}
