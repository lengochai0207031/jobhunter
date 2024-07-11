package vn.hoidanit.jobhunter.domain.DTO;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResCreateUserDTO {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private Instant createdAt;
    private String address;
    private GenderEnum gender;
    private CompanyUser company;

    // Getters and setters
    @Getter
    @Setter
    public static class CompanyUser {
        private Long id;
        private String name;

    }
}
