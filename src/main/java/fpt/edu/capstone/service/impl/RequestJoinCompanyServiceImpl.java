package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.recruiter.ReceiveRequestJoinCompanyResponse;
import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.entity.RequestJoinCompany;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.RequestJoinCompanyRepository;
import fpt.edu.capstone.service.CompanyService;
import fpt.edu.capstone.service.RequestJoinCompanyService;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.Pagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RequestJoinCompanyServiceImpl implements RequestJoinCompanyService {

    private final RequestJoinCompanyRepository requestJoinCompanyRepository;

    private final CompanyService companyService;

    @Override
    public RequestJoinCompany createRequest(RequestJoinCompany requestJoinCompany) {
        //Mai: cần check xem recruiter này đã tạo request nào chưa, trạng thái request như thế nào
        Optional<Company> company = companyService.findById(requestJoinCompany.getCompanyId());
        if(!company.isPresent()) {
            throw new HiveConnectException("Không tìm thấy công ty này để gửi yêu cầu.");
        }
        requestJoinCompany.setStatus("Pending");
        requestJoinCompany.setApproverId(company.get().getCreatorId());
        requestJoinCompany.create();
        return requestJoinCompanyRepository.save(requestJoinCompany);
    }

    @Override
    public Optional<RequestJoinCompany> getSentRequest(long senderId) {
        return requestJoinCompanyRepository.findBySenderId(senderId);
    }

    @Override
    public ResponseDataPagination getReceiveRequest(Integer pageNo, Integer pageSize, long approverId) {
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        Page<RequestJoinCompany> companyPage = requestJoinCompanyRepository.findByApproverId(pageable, approverId);

        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(companyPage.getContent());
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(companyPage.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(companyPage.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public void approveRequest(String status, long senderId) {
        requestJoinCompanyRepository.approveRequest(status, senderId);
    }

    @Override
    public Optional<RequestJoinCompany> findById(long id) {
        return requestJoinCompanyRepository.findById(id);
    }

    @Override
    public Page<ReceiveRequestJoinCompanyResponse> getReceiveRequestJoinCompanyWithFilter
            (String fullName, String email, String phone, String status, long approverId, int pageSize, int pageNo){
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        Page<ReceiveRequestJoinCompanyResponse> page = requestJoinCompanyRepository.getReceiveRequestJoinCompanyWithFilter
                ( fullName, email, phone, status, approverId,pageable);
        return page;
    }

    @Override
    public void deleteOldSentRequest(long id) {
        requestJoinCompanyRepository.deleteById(id);
    }
}
