package fpt.edu.capstone.repository;

import com.amazonaws.services.apigateway.model.Op;
import fpt.edu.capstone.dto.candidate.FollowingResponse;
import fpt.edu.capstone.entity.Follow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM public.follow WHERE follower_id = ?1 and followed_id = ?2 and type = ?3", nativeQuery = true)
    void unFollow(long followerId, long followedId, long type);

    @Query(value = "select * from follow where follower_id = ?1 and type = 1", nativeQuery = true)
    Optional<List<Follow>> getListFollowedJobByFollowerId(long followerId);

    @Query(value = "select * from follow where follower_id = ?1 and followed_id = ?2 and type = ?3", nativeQuery = true)
    Optional<Follow> getFollowIfHave(long followerId, long followedId, long type);

    @Query(value = "select f.id as id, f.follower_id  as followerId, f.followed_id as followedId, f.type as type, j.job_name as jobName  from follow f join job j on f.followed_id = j.id where f.follower_id = ?1 and f.type = 1", nativeQuery = true)
    Page<FollowingResponse> getFollowedJobByCandidateID(Pageable pageable, long candidateId);

    @Query(value = "select * from follow where type = 1 and followed_id = ?1", nativeQuery = true)
    Optional<List<Follow>> getAllFollowerOfAJob(long jobId);
}
