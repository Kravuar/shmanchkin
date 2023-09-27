import React, {useEffect, useState} from 'react';
import {GameLobby} from "./Types";
import {axiosConfigured} from "./Utils";

type JoinFormProps = {
    onSubmit: (gameLobby: GameLobby, username: string) => void
}

export default function JoinForm({onSubmit}: JoinFormProps) {
    const [lobbyName, setLobbyName] = useState<string>('');
    const [username, setUsername] = useState('');
    const [games, setGames] = useState<GameLobby[]>([]);

    useEffect(() => {
        axiosConfigured.get<GameLobby[]>("/games/gameList")
            .then(r => setGames(r.data))
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    })

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        const gameLobby = games.find(game => game.lobbyName === lobbyName);
        if(gameLobby === undefined) {
            console.log("Join.form.handleSubmit: Lobby not found");
            return;
        }

        onSubmit(gameLobby, username);
        setUsername('');
        setLobbyName('');
    };

    return (
        <div>
            <h2>Войти в лобби</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="lobbyName">Lobby Name:</label>
                    <input
                        type="lobbyName"
                        id="lobbyName"
                        value={lobbyName}
                        onChange={(e) => setLobbyName(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="username">Username:</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <button type="submit">Submit</button>
                </div>
            </form>
            <div>
                Игры:
                <ul>
                    {games.map(game =>
                        <li key={game.lobbyName}>
                            <p>Название: {game.lobbyName}</p>
                            <p>Создатель: {game.ownerName}</p>
                            <p>Игроков: {game.playersJoined.length} / {game.maxPlayers}</p>
                        </li>
                    )}
                </ul>
            </div>
        </div>
    );
}