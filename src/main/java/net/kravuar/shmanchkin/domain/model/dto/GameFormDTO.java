package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
public class GameFormDTO {
    private String lobbyName;
    private String ownerName;
    private int maxPlayers;
}
