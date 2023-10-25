import React, {useEffect, useState} from 'react';
import {GameLobby, LobbyUpdate, UserInfo} from "./Types";
import {axiosConfigured} from "./Utils";

type GameProps = {
    gameLobby: GameLobby,
    gameEventSource: EventSource,
    username: string,
    onGameExit: () => void
}

export default function Game({gameEventSource, username, onGameExit, gameLobby}: GameProps) {
    const [gameState, setGameState] = useState<'joining' | 'waiting' | 'playing'>('joining');
    const [players, setPlayers] = useState<UserInfo[]>(gameLobby.playersJoined);
    const maxPlayers: number = gameLobby.maxPlayers;

    function onExit() {
        gameEventSource.close();
        onGameExit();
    }

    function onClose() {
        axiosConfigured.delete(`/games/close/${gameLobby.lobbyName}`)
            .then(r => console.log(`Game.onClose: Closed: ${r.data}`))
            .catch(e => console.log(e));
        onExit();
    }

    function onJoin() {
        gameEventSource.addEventListener('lobby-update', (event) => {
            const lobbyUpdate: LobbyUpdate = JSON.parse(event.data);
            if (lobbyUpdate.action.toLowerCase() === 'joined')
                setPlayers([...players, lobbyUpdate.userInfo]);
            else
                setPlayers(players.filter(player => player !== lobbyUpdate.userInfo));
        });
        gameEventSource.addEventListener('game-start', onGameStart);
        setGameState('waiting');
    }

    function onGameStart() {
        //   remove lobby listeners
        //   add game events listeners (cards, player moves...)
        setGameState('playing');
    }

    useEffect(() => {
        if (gameEventSource.OPEN)
            onJoin();
        else
            gameEventSource.onopen = onJoin;
    },[gameEventSource]);

    if (gameState === 'waiting')
        return (
            <div>
                Ожидание игроков: {players.length} / {maxPlayers}:
                <ul>
                    {players.map(player =>
                        <li key={player.username}>
                            <p>{player === gameLobby.owner ? `(HOST) ${player.username}` : ""}</p>
                        </li>
                    )}
                </ul>
                <button onClick={onExit}>Выйти</button>
                {username === gameLobby.owner.username &&
                    <div>
                        <button onClick={onClose}>Закрыть</button>
                        <button onClick={onGameStart}>Старт</button>
                    </div>
                }
            </div>
        );
    else
        return <div>'ЗАГРУЗКАААААА'</div>;
}