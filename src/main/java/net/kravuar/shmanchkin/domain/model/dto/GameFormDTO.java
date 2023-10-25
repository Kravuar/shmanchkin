package net.kravuar.shmanchkin.domain.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import net.kravuar.shmanchkin.domain.services.Username;

@Data
public class GameFormDTO {
    @Size(min=3, max=30)
    private String lobbyName;
    @Username
    private String ownerName;
    private int maxPlayers;
}