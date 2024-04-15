package fpt.edu.capstone.dto.recruiter;

public interface ReceiveRequestJoinCompanyResponse {
    long getSenderId();
    String getFullname();
    String getStatus();
    String getEmail();
    String getPhone();
    long getCompanyId();
}
