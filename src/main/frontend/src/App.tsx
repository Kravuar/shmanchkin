import React, {useState} from 'react';
import JoinForm from "./JoinForm";
import Game from "./Game";
import {GameLobby} from "./Types";
import HostForm from "./HostForm";

function App() {
  const [gameEventSource, setGameEventSource] = useState<EventSource | null>(null)
  const [username, setUsername] = useState<string | null>(null);
  const [gameLobby, setGameLobby] = useState<GameLobby | null>(null);

  function join(gameLobby: GameLobby, username: string) {
    const source = new EventSource(`http://localhost:8080/games/join/${gameLobby.lobbyName}/${username}`);
    source.onerror = (e) => console.log(`App.join: Couldn't subscribe: ${e}`);
    setGameEventSource(source);
    setUsername(username);
    setGameLobby(gameLobby);
  }

  function exit() {
    setGameEventSource(null);
    setUsername(null);
    setGameLobby(null);
  }

  if (gameLobby && gameEventSource && username)
    return <Game gameLobby={gameLobby} gameEventSource={gameEventSource} username={username} onGameExit={exit}/>;
  else
    return (
        <div>
          {/* Wait before game is created*/}
          <HostForm onSubmit={join}/>
          <JoinForm onSubmit={join} />
        </div>
    );
}

export default App;
