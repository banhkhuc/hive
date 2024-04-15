package fpt.edu.capstone.service.impl;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import fpt.edu.capstone.entity.CVImported;
import fpt.edu.capstone.repository.CVImportedRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CVImportedService {

    @Autowired
    private  CVImportedRepository cvImportedRepository;


    //type = IMG || CV
    private boolean isValidFile(MultipartFile file, String type){
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if(type.equals("IMG")) {
            return  Arrays.asList(new String[] {"jpeg","jpg","png"}).contains(fileExtension.toLowerCase()) ;
        }
        return  Arrays.asList(new String[] {"pdf"}).contains(fileExtension.toLowerCase()) ;
    }

    public CVImported save(MultipartFile file, String type, long candidateId) throws IOException {
        if(file.isEmpty()){
            throw new RuntimeException("Failed to store empty file");
        }
        if(!isValidFile(file, type)){
            throw new RuntimeException("File is invalid");
        }
        float fileSizeInMB = file.getSize() / 1_000_000;
        if (fileSizeInMB > 25.0f){
            throw new RuntimeException("File must  be <= 25mb");
        }
        CVImported cvImported = new CVImported();
        cvImported.setName(StringUtils.cleanPath(file.getOriginalFilename()));
        cvImported.setContentType(file.getContentType());
        cvImported.setData(file.getBytes());
        cvImported.setSize(file.getSize());
        cvImported.setCandidateId(candidateId);
        cvImported.setCreateAt(LocalDateTime.now());

        return cvImportedRepository.save(cvImported);
    }

    public Optional<CVImported> findById(String id) {
        return cvImportedRepository.findById(id);
    }

}