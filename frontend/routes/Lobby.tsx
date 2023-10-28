import {ArrowLeftIcon, ArrowPathIcon} from "@heroicons/react/24/outline";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import {api} from "@/api";
import {useEvents} from "@/sse/useEvents.ts";
import {useMemo} from "react";

type Game = {
    lobbyName: string,
    owner: { username: string },
    playersJoined: Array<{ username: string }>
}

export const Lobby = () => {
    const client = useQueryClient()
    const {data: games, isFetching} = useQuery({
        queryFn: async () => (await api.get('/games')).data as Game[],
        queryKey: ['games'],
    })
    const refresh = () => {
        client.invalidateQueries({queryKey: ['games']})
    }
    const listeners = useMemo(() => ({
        'game-created': (e: MessageEvent<string>) => {
            console.log('created', e.data)
            // client.invalidateQueries({queryKey: ['games']})
        },
        'game-closed': (e: MessageEvent<string>) => {
            console.log('closed', e.data)
        },
        'game-full-update': (e: MessageEvent<string>) => {
            console.log('full update', e.data)
            const data = JSON.parse(e.data) as {
                games: Game[]
            }
            client.setQueryData(['games'], data.games)
        }
    }), [client])
    useEvents("api/games/subscribe", listeners)
    const fillRows = (gamesCount: number): Array<null> => {
        if (gamesCount < 8)
            return Array.from({length: 8 - gamesCount})
        return []
    }
    return (
        <div className={'text-white bg-stone-800 w-full min-h-screen pt-[50px]'}>
            <div style={{
                filter: "drop-shadow(0px 0px 110px #1C1917)"
            }}
                 className={'mx-auto max-w-[909px] rounded-[36px] bg-stone-700 text-[20px] pt-6 text-center'}>
                <div className={'flex px-12 pb-4 justify-between'}>
                    <button onClick={() => console.log('go back')}>
                        <ArrowLeftIcon className={'w-8 h-8 stroke-[3px]'}/>
                    </button>
                    <h1 className={'text-[32px] font-bold'}>
                        Список серверов {games ? `(${games.length})` : null}
                    </h1>
                    <button onClick={refresh}
                            className={'bg-stone-500 w-[48px] h-[48px] rounded-full flex items-center justify-center'} style={{
                        filter: "drop-shadow(0px 0px 13px rgba(0, 0, 0, 0.25))"
                    }}>
                        <ArrowPathIcon className={'w-[34px] h-[34px] stroke-[2.3px]' + (isFetching ? ' animate-spin' : '')}/>
                    </button>
                </div>
                <div className={'max-h-[700px] overflow-y-auto'}>
                    <table className={'w-full'}>
                        <thead>
                        <tr className={'top-0 sticky bg-stone-700 font-bold h-[80px] shadow-2xl'}>
                            <th className={'w-[400px]'}>
                                Название сервера
                            </th>
                            <th className={'w-[120px]'}>
                                Игроки
                            </th>
                            <th className={'w-[382px]'}>
                                Создатель
                            </th>
                        </tr>
                        </thead>
                        <tbody className={'divide-y-4 divide-stone-900'}>
                        {
                            games && games.map(game => (
                                <tr key={game.lobbyName} className={'h-[72px] divide-x-4 divide-stone-900 '}>
                                    <td>{game.lobbyName}</td>
                                    <td>{game.playersJoined.length}/4</td>
                                    <td>{game.owner.username}</td>
                                </tr>
                            ))
                        }
                        {
                            fillRows(games?.length ?? 0).map((_, i) => (
                                <tr key={i} className={'h-[72px] divide-x-4 divide-stone-900'}>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                </tr>
                            ))
                        }
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )
}