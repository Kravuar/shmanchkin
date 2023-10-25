package net.kravuar.shmanchkin.domain.model.dto;

import lombok.Data;
import net.kravuar.shmanchkin.domain.model.game.Player;

@Data
public class PlayerDTO {
    private final String username;

    public PlayerDTO(Player player) {
        this.username = player.getUsername();
    }
}
