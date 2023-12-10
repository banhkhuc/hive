package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.ProfileViewer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProfileViewerService {
    Page<ProfileViewer> getProfileViewerOfCv(Pageable pageable, long cvId);

    ProfileViewer getByCvIdAndViewerId(long cvId, long viewerId);

    List<ProfileViewer> findAll(PageRequest pageRequest);

    Optional<ProfileViewer> getByCvIdAndViewerIdOptional(long cvId, long viewerId);

    void updateIsSave(boolean isSave, long id, long recruiterId);
}
