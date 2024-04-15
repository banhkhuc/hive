package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.company.ListCompany;
import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.entity.Image;
import fpt.edu.capstone.repository.CompanyRepository;
import fpt.edu.capstone.service.CompanyService;
import fpt.edu.capstone.service.ImageService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    private final ImageService imageService;

    private final ModelMapper modelMapper;

    @Override
    public Company getCompanyById(long id) {
        return companyRepository.getCompanyById(id);
    }

    @Override
    public boolean existById(long id) {
        return companyRepository.existsById(id);
    }

    @Override
    public List<ListCompany> getAllCompany() {
        List<Company> companyList = companyRepository.findAll();
        List<ListCompany> list = companyList.stream().map(company -> modelMapper.
                map(company, ListCompany.class)).collect(Collectors.toList());
        for (ListCompany c: list) {
            Optional<Image> image = imageService.getImageCompany(c.getId(), true);
            if (image.isPresent()) {
                c.setAvatar(image.get().getUrl());
            }
        }
        return list;
    }

    @Override
    public Optional<Company> findById(long companyId) {
        return companyRepository.findById(companyId);
    }

    @Override
    public Page<Company> searchCompany(Pageable pageable, String companyName) {
        return companyRepository.getAllByName(pageable, companyName);
    }

    @Override
    public List<Company> getAdditionCompanies(int additionSize, List<Long> existsId) {
        return companyRepository.getAdditionCompanies(additionSize, existsId);
    }
}
