package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Major;
import fpt.edu.capstone.repository.MajorRepository;
import fpt.edu.capstone.service.MajorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MajorServiceImpl implements MajorService {
    private final MajorRepository majorRepository;

    @Override
    public String getNameByMajorId(long majorId) {
        return majorRepository.getNameByMajorId(majorId);
    }

    @Override
    public List<Major> getAllMajorByFieldId(long fieldId) {
        return majorRepository.getAllMajorByFieldId(fieldId);
    }

    @Override
    public List<String> getMajorNameByCVId(long cvId) {
        return majorRepository.getMajorNameByCVId(cvId);
    }

}
