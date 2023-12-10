package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.admin.AdminResponse;
import fpt.edu.capstone.dto.admin.user.AdminManageResponse;
import fpt.edu.capstone.dto.admin.user.CandidateManageResponse;
import fpt.edu.capstone.dto.admin.user.RecruiterManageResponse;
import fpt.edu.capstone.dto.admin.user.ReportedUserResponse;
import fpt.edu.capstone.entity.Admin;
import fpt.edu.capstone.entity.Users;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.AdminRepository;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.Pagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    private final UserService userService;

    @Override
    public ResponseDataPagination getListAdmin(Integer pageNo, Integer pageSize) throws Exception {
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        Page<Admin> admin = adminRepository.getListAdminByFilter(pageable);
        List<AdminResponse> adminResponse = new ArrayList<>();
        if (admin.hasContent()) {
            for (Admin a : admin.getContent()) {
                long userId = a.getUserId();
                Users user = userService.getUserById(userId);
                AdminResponse ar = AdminResponse.fromEntity(a, user);
                adminResponse.add(ar);
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(adminResponse);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(admin.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(admin.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);

        return responseDataPagination;
    }

    @Override
    public Optional<Admin> findAdminByUserId(long userId) {
        return adminRepository.findByUserId(userId);
    }

    @Override
    public void insertAdmin(long userId, String fullName) {
        adminRepository.insertAdmin(userId, fullName);
    }

    @Override
    public Page<AdminManageResponse> searchAdmins(Pageable pageable, String username, String email, String fullName,
                                                  long userId, boolean isLocked) {
        return adminRepository.searchAdmin(pageable, username, email,fullName,userId, isLocked);
    }
}
