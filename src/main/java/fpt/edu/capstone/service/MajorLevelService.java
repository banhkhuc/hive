package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.MajorLevel;

import java.util.List;
import java.util.Optional;

public interface MajorLevelService {
    List<MajorLevel> getListMajorLevelByCvId(Long cvId);

    MajorLevel insertNewMajorLevel(MajorLevel majorLevel);

    void deleteMajorLevel(MajorLevel majorLevel);

    void updateMajorLevel(MajorLevel majorLevel);

    Optional<MajorLevel> getMajorLevelById(long id);
}
