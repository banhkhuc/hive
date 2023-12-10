package fpt.edu.capstone.repository;

import fpt.edu.capstone.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    @Query(value = "select * from Certificate c where c.cv_id = ?1", nativeQuery = true)
    List<Certificate> getListCertificateByCvId(Long cvId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO certificate (certificate_name, certificate_url, status, cv_id) VALUES(?1, ?2, ?3, ?4)", nativeQuery = true)
    void insertCertificate(String certificateName, String certificateUrl, long status, long cvId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.certificate SET certificate_name=?1, certificate_url=?2, status=?3 WHERE id=?4", nativeQuery = true)
    void updateCertificate(String certificateName, String certificateUrl, long status, long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM public.certificate WHERE id= ?1", nativeQuery = true)
    void deleteCertificate(long id);

}
