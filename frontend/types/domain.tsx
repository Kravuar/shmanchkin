export type Player = {
    uuid: string
    username: string
}

export type Game = {
    lobbyName: string,
    owner: { username: string },
    playersJoined: Array<{ username: string }>
}

export type PlayerInfo = Player & {
    currentGame?: Game
}