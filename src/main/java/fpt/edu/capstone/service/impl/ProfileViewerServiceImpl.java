package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.ProfileViewer;
import fpt.edu.capstone.repository.ProfileViewerRepository;
import fpt.edu.capstone.service.ProfileViewerService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProfileViewerServiceImpl implements ProfileViewerService {

    private final ProfileViewerRepository profileViewerRepository;

    @Override
    public Page<ProfileViewer> getProfileViewerOfCv(Pageable pageable, long cvId) {
        return profileViewerRepository.getAllByCvId(pageable, cvId);
    }

    @Override
    public ProfileViewer getByCvIdAndViewerId(long cvId, long viewerId) {
        return profileViewerRepository.getByCvIdAndViewerId(cvId, viewerId);
    }

    @Override
    public List<ProfileViewer> findAll(PageRequest pageRequest) {
        Page<ProfileViewer> profileViewerPage = profileViewerRepository.findAll(pageRequest);
        return profileViewerPage.getContent();
    }

    @Override
    public Optional<ProfileViewer> getByCvIdAndViewerIdOptional(long cvId, long viewerId) {
        return profileViewerRepository.getByCvIdAndViewerIdOptional(cvId, viewerId);
    }

    @Override
    public void updateIsSave(boolean isSave, long id, long recruiterId) {
        profileViewerRepository.updateIsSave(isSave, id, recruiterId);
    }
}
