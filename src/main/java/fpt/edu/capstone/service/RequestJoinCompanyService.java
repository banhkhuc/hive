package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.recruiter.ReceiveRequestJoinCompanyResponse;
import fpt.edu.capstone.entity.RequestJoinCompany;
import fpt.edu.capstone.utils.ResponseDataPagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RequestJoinCompanyService {

    RequestJoinCompany createRequest(RequestJoinCompany requestJoinCompany);

    Optional<RequestJoinCompany> getSentRequest(long senderId);

    ResponseDataPagination getReceiveRequest(Integer pageNo, Integer pageSize, long approverId);

    void approveRequest(String status, long senderId);

    Optional<RequestJoinCompany> findById(long id);

    void deleteOldSentRequest(long id);

    Page<ReceiveRequestJoinCompanyResponse> getReceiveRequestJoinCompanyWithFilter
            (String fullName, String email, String phone, String status, long approverId, int pageSize, int pageNo);
}
