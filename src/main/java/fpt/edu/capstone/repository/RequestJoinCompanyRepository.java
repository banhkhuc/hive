package fpt.edu.capstone.repository;

import fpt.edu.capstone.dto.recruiter.ReceiveRequestJoinCompanyResponse;
import fpt.edu.capstone.entity.RequestJoinCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface RequestJoinCompanyRepository extends JpaRepository<RequestJoinCompany, Long> {

    @Query(value = "Select * from request_join_company where sender_id = ?1", nativeQuery = true)
    Optional<RequestJoinCompany> findBySenderId(long senderId);

    @Query(value = "Select * from request_join_company where approver_id = ?1", nativeQuery = true)
    Page<RequestJoinCompany> findByApproverId(Pageable pageable, long senderId);

    @Modifying
    @Transactional
    @Query(value = "Update request_join_company set status = ?1 where sender_id = ?2", nativeQuery = true)
    void approveRequest(String status, long senderId);

    @Query(value = "select r.id as senderId, r.fullname as fullname , rjc.status, u.email , u.phone, rjc.company_id as companyId " +
            " from request_join_company rjc " +
            "join recruiter r on r.id  = rjc.sender_id " +
            "join users u on u.id = r.user_id " +
            "where lower(r.fullname)  like lower(concat('%', ?1 ,'%')) " +
            "and lower(u.email) like lower(concat('%', ?2 ,'%')) " +
            "and  (u.phone like concat('%', ?3 ,'%') or u.phone is null ) " +
            "and lower(rjc.status) like lower(concat('%', ?4 ,'%')) and rjc.approver_id = ?5 ", nativeQuery = true)
    Page<ReceiveRequestJoinCompanyResponse> getReceiveRequestJoinCompanyWithFilter
            ( String fullName, String email, String phone, String status, long approverId,  Pageable pageable);
}
