export interface LobbyUpdate {
    username: string,
    action: 'joined' | 'left'
}

export interface GameLobby {
    lobbyName: string,
    ownerName: string,
    maxPlayers: number,
    playersJoined: string[],
}

export interface TestMessage {
    username: string,
    message: string
}