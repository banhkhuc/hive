package fpt.edu.capstone.dto.company;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompanyInforRequest {
    private long companyId;
    private String companyEmail;
    private String companyPhone;
    private String companyDescription;
    private String companyWebsite;
    private String numberEmployees;
    private String companyAddress;
    private String taxCode;
    private String mapUrl;
    private String avatarUrl;
    private String coverImageUrl;
    private List<String> uploadImageUrlList;
    private List<Long> deleteImageIdList;
}
