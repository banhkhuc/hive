package fpt.edu.capstone.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CVBaseInformationRequest {
    long userId;
    String name;
    String phoneNumber;
    boolean gender;
    LocalDateTime birthDate;
    String country;
    String address;
    String socialLink;
}
