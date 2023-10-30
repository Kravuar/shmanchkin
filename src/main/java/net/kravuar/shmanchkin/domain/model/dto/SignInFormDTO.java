package net.kravuar.shmanchkin.domain.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class SignInFormDTO {
    @NotBlank
    @Length(min=3, max=20)
    private String username;
}
