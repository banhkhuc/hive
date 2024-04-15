package fpt.edu.capstone.dto.candidate;

public interface FollowingResponse {
    long getId();

    long getType();

    long getFollowedId();

    String getJobName();
}