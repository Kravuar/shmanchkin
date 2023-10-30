package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class GameFormDTO {
    @Length(min=3, max=30)
    private String lobbyName;
}