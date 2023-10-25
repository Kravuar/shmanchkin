export interface LobbyUpdate {
    userInfo: UserInfo,
    action: 'joined' | 'left'
}

export interface UserInfo {
    username: string
}

export interface GameLobby {
    lobbyName: string,
    owner: UserInfo,
    maxPlayers: number,
    playersJoined: UserInfo[],
}