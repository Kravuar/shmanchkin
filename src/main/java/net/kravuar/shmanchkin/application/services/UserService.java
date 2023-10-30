package net.kravuar.shmanchkin.application.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final GameService gameService;

    public UserInfo getActiveUser(@NonNull UUID uuid) {
        UserInfo user = null;
        for (var game: gameService.getGames().values()) {
            if (game.getOwner().getUuid().equals(uuid)) {
                user = game.getOwner();
                break;
            }
            user = game.getPlayer(uuid);
            if (user != null)
                break;
        }
        return user;
    }
}
