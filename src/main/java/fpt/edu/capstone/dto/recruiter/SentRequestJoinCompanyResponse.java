package fpt.edu.capstone.dto.recruiter;

import fpt.edu.capstone.entity.Company;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
public class SentRequestJoinCompanyResponse {

    private long id;
    private String status;
    private long senderId;
    private Company company;
    private long approverId;
}
