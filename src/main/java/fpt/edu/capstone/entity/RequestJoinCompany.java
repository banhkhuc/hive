package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_join_company")
@Getter
@Setter
public class RequestJoinCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "status") //Pending, Approve, Deny
    private String status;

    @Column(name = "sender_id") //id recruiter gửi request join công ty
    private long senderId;

    @Column(name = "company_id") //id công ty
    private long companyId;

    @Column(name = "approver_id")  //Id của recruiter tạo công ty
    private long approverId;

    //Xóa sau 1 tháng request|| 3 ngày sau khi approval or deny
}
