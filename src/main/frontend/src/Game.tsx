import React, {useEffect, useState} from 'react';
import {GameLobby, LobbyUpdate, TestMessage} from "./Types";
import {axiosConfigured} from "./Utils";

type GameProps = {
    gameLobby: GameLobby,
    gameEventSource: EventSource,
    username: string,
    onGameExit: () => void
}

export default function Game({gameEventSource, username, onGameExit, gameLobby}: GameProps) {
    const [gameState, setGameState] = useState<'joining' | 'waiting' | 'playing'>('joining');
    const [players, setPlayers] = useState<string[]>(gameLobby.playersJoined);
    const [eventsReceived, setEventsReceived] = useState<TestMessage[]>([]);
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
        gameEventSource.addEventListener('lobby-players-update', (event) => {
            const lobbyUpdate: LobbyUpdate = JSON.parse(event.data);
            if (lobbyUpdate.action.toLowerCase() === 'joined')
                setPlayers([...players, lobbyUpdate.username]);
            else
                setPlayers(players.filter(player => player !== lobbyUpdate.username));
        });
        gameEventSource.addEventListener('game-start', onGameStart);
        setGameState('waiting');
    }

    function onGameStart() {
        //   remove lobby listeners
        //   add game events listeners (cards, player moves...)
        gameEventSource.addEventListener('test-event', (event) => {
            console.log(event.data);
            setEventsReceived([...eventsReceived, event.data]);
            console.log(eventsReceived.length);
        });
        setGameState('playing');
    }

    function onPublish() {
        axiosConfigured.post(`/games/test/${gameLobby.lobbyName}/${username}`, {message: (Math.random() + 1).toString(36).substring(7)});
    }

    useEffect(() => {
        if (gameEventSource.OPEN)
            onJoin();
        else
            gameEventSource.onopen = onJoin;
    },[gameEventSource]);

    if (gameState === 'playing')
        return (
            <div>
                <button onClick={onExit}>Выйти (2 НЕДЕЛИ ЛОУ ПРИОРИТИ)</button>
                <button onClick={onPublish}>Паблишшш</button>
                Ивенты:
                <ul>
                    {eventsReceived.map((message, i) => {
                        console.log("ee");
                        return <li key={i}>{message.username}: {message.message}</li>
                    })}</ul>
            </div>
        );
    else if (gameState === 'waiting')
        return (
            <div>
                Ожидание игроков: {players.length} / {maxPlayers}:
                <ul>
                    {players.map(player =>
                        <li key={player}>{player === gameLobby.ownerName ? "(HOST)" : ""}{player}</li>
                    )}
                </ul>
                <button onClick={onExit}>Выйти</button>
                {username === gameLobby.ownerName &&
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