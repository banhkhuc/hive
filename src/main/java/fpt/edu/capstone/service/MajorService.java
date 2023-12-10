package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.Major;

import java.util.List;

public interface MajorService {
    String getNameByMajorId(long majorId);

    List<Major> getAllMajorByFieldId(long fieldId);

    List <String> getMajorNameByCVId(long cvId);
}
