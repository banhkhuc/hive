package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.MajorLevel;
import fpt.edu.capstone.repository.MajorLevelRepository;
import fpt.edu.capstone.service.MajorLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MajorLevelServiceImpl implements MajorLevelService {

    @Autowired
    MajorLevelRepository majorLevelRepository;

    @Override
    public List<MajorLevel> getListMajorLevelByCvId(Long cvId) {
        return majorLevelRepository.getListMajorLevelByCvId(cvId);
    }

    @Override
    public MajorLevel insertNewMajorLevel(MajorLevel majorLevel) {
         return  majorLevelRepository.save(majorLevel);
    }

    @Override
    public void deleteMajorLevel(MajorLevel majorLevel) {
        majorLevelRepository.delete(majorLevel);
    }

    @Override
    public void updateMajorLevel(MajorLevel majorLevel) {
        majorLevelRepository.updateNewMajorLevel(majorLevel.getFieldId(), majorLevel.getMajorId(), majorLevel.getLevel(), majorLevel.isStatus(), majorLevel.getId());
    }

    @Override
    public Optional<MajorLevel> getMajorLevelById(long id) {
        return majorLevelRepository.findById(id);
    }

}
