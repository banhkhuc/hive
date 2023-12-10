package fpt.edu.capstone.dto.rental_package;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
@Getter
@Setter
public class RentalPackageRequest {
    private String packageGroup;
    private String description;
}
