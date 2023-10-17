package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;

@Data
public class GameFormDTO {
    private String lobbyName;
    private String ownerName;
    private int maxPlayers;
}