package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Certificate;
import fpt.edu.capstone.repository.CertificateRepository;
import fpt.edu.capstone.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CertificateServiceImpl implements CertificateService {
    @Autowired
    CertificateRepository certificateRepository;

    @Override
    public List<Certificate> getListCertificateByCvId(Long cvId) {
        return certificateRepository.getListCertificateByCvId(cvId);
    }

    @Override
    public Certificate insertCertificate(Certificate newCertificate) {
        return certificateRepository.save(newCertificate);
    }

    @Override
    public void updateService(Certificate certificate) {
        certificateRepository.updateCertificate(certificate.getCertificateName(), certificate.getCertificateUrl(), certificate.getStatus(), certificate.getId());
    }

    @Override
    public void deleteCertificate(Certificate certificate) {
        certificateRepository.deleteCertificate(certificate.getId());
    }

    @Override
    public Optional<Certificate> getCertificateById(long id) {
        return  certificateRepository.findById(id);
    }
}
