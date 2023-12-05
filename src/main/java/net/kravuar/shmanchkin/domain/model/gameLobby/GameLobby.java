package net.kravuar.shmanchkin.domain.model.gameLobby;

import lombok.Getter;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.exceptions.GameIsFullException;
import net.kravuar.shmanchkin.domain.model.exceptions.IllegalLobbyStatusException;
import net.kravuar.shmanchkin.domain.model.exceptions.NotEnoughPlayersException;
import net.kravuar.shmanchkin.domain.model.exceptions.UsernameTakenException;
import net.kravuar.shmanchkin.domain.model.game.Game;
import net.kravuar.shmanchkin.domain.model.game.character.CharacterImpl;

import java.util.*;

public class GameLobby {
    public enum LobbyStatus {
        IDLE,
        ACTIVE,
        CLOSED
    }
    private final int minPlayers;
    private final int maxPlayers;

    private final Game game;
    @Getter
    private final String lobbyName;
    @Getter
    private final UserInfo owner;
    @Getter
    private LobbyStatus lobbyStatus = LobbyStatus.IDLE;
    protected final Map<UUID, UserInfo> playersJoined = new HashMap<>();

    public GameLobby(String lobbyName, UserInfo owner, int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.lobbyName = lobbyName;
        this.owner = owner;
        this.game = new Game(lobbyName);
    }

    public Collection<UserInfo> getPlayers() {
        return playersJoined.values();
    }
    public UserInfo getPlayer(UUID uuid) {
        return playersJoined.get(uuid);
    }
    public UserInfo getPlayer(String username) {
        return playersJoined.values().stream()
                .filter(somePlayer -> somePlayer.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
    public boolean isFull() {
        return playersJoined.size() == maxPlayers;
    }

    public synchronized void addPlayer(UserInfo player) {
        if (isFull())
            throw new GameIsFullException(lobbyName);
        if (lobbyStatus == LobbyStatus.ACTIVE)
            throw new IllegalLobbyStatusException(lobbyName, LobbyStatus.ACTIVE);
        var usernameTaken = playersJoined.values().stream()
                .anyMatch(somePlayer -> somePlayer.getUsername().equals(player.getUsername()));
        if (usernameTaken)
            throw new UsernameTakenException(lobbyName, player.getUsername());

        playersJoined.put(player.getUuid(), player);
        game.addCharacter(player.getUsername(), new CharacterImpl());
    }
    public synchronized boolean removePlayer(UserInfo player, boolean kicked) {
        if (playersJoined.remove(player.getUuid()) != null) {
            game.removeCharacter(player.getUsername());
            return true;
        }
        return false;
    }

    public synchronized void start() {
        if (lobbyStatus != LobbyStatus.IDLE)
            throw new IllegalLobbyStatusException(lobbyName, lobbyStatus);
        if (playersJoined.size() < minPlayers)
            throw new NotEnoughPlayersException(lobbyName, minPlayers - playersJoined.size());
        lobbyStatus = LobbyStatus.ACTIVE;
        game.start();
    }
    public synchronized void close() {
        for (var player : new ArrayList<>(playersJoined.values()))
            removePlayer(player, true);
        lobbyStatus = LobbyStatus.CLOSED;
    }
}
