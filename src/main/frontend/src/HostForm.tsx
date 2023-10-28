import React, {useState} from 'react';
import {GameLobby} from "./Types";
import {axiosConfigured} from "./Utils";

type HostFormProps = {
    onSubmit: (gameLobby: GameLobby, username: string) => void
}

export default function HostForm({onSubmit}: HostFormProps) {
    const [lobbyName, setLobbyName] = useState<string>('');
    const [username, setUsername] = useState('');
    const [maxPlayers, setMaxPlayers] = useState(0);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        axiosConfigured.post("/games/create", {
            lobbyName: lobbyName,
            ownerName: username,
            maxPlayers: maxPlayers
        }).then(() => {
            console.log("Hostform.handleSubmit: GOOD");
            onSubmit({
                lobbyName: lobbyName,
                owner: {username: username},
                maxPlayers: maxPlayers,
                playersJoined: [{username: username}],
            }, username);
        }
        ).catch((error) => console.log(`Hostform.handleSubmit: Bad: ${error}`));
    };

    return (
        <div>
            <h2>Создать лобби</h2>
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
                    <label htmlFor="maxPlayers">MaxPlayers:</label>
                    <input
                        type="number"
                        id="maxPlayers"
                        min={3}
                        max={6}
                        value={maxPlayers}
                        onChange={(e) => setMaxPlayers(+e.target.value)}
                        required
                    />
                </div>
                <div>
                    <button type="submit">Создать</button>
                </div>
            </form>
        </div>
    );
}