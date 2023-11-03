import {ArrowLeftIcon, ArrowPathIcon} from "@heroicons/react/24/outline";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import {api} from "@/api";
import {useEvents} from "@/sse/useEvents.ts";
import {Link, useNavigate} from "react-router-dom";
import tw from 'twin.macro'
import {css} from "@emotion/react";
import {Game, PlayerInfo} from "@/types/domain.tsx";
import {IdentifyModal} from "@/widgets/IdentifyModal.tsx";
import {Fragment, useEffect, useState} from "react";
import {AxiosError} from "axios";

const Identify = () => {
    const [open, setOpen] = useState(false)
    const {data, error} = useQuery({
        queryFn: async () => (await api.get('/user/userInfo')).data as PlayerInfo,
        queryKey: ['player'],
    })
    console.log(error)
    useEffect(() => {
        if(error && error instanceof AxiosError && error.response?.status === 401) {
            setOpen(true)
        }
    }, [error])
    return <IdentifyModal onClose={() => setOpen(false)} open={open}/>
}

export const Games = () => {
    const navigate = useNavigate()
    const client = useQueryClient()
    const {data: games, isFetching} = useQuery({
        queryFn: async () => (await api.get('/games')).data as Game[],
        queryKey: ['games'],
    })
    const refresh = () => {
        client.invalidateQueries({queryKey: ['games']})
    }
    useEvents("/api/games/subscribe", {
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
    }, [client])
    const fillRows = (gamesCount: number): Array<null> => {
        if (gamesCount < 8)
            return Array.from({length: 8 - gamesCount})
        return []
    }
    return (
        <Fragment>
            <Identify/>
            <div tw={'text-white bg-stone-800 w-full min-h-screen flex place-items-center'}>
            <div css={[tw`mx-auto max-w-[909px] rounded-[36px] bg-stone-700 text-[20px] pt-6 text-center`,
                css`filter: drop-shadow(0px 0px 110px #1C1917)`
            ]}>
                <div tw={'flex px-12 pb-4 justify-between items-center'}>
                    <Link to={'/'}>
                        <ArrowLeftIcon tw={'w-8 h-8 stroke-[3px]'}/>
                    </Link>
                    <h1 tw={'text-[32px] font-bold'}>
                        Список серверов {games ? `(${games.length})` : null}
                    </h1>
                    <button onClick={refresh}
                            css={[
                                tw`bg-stone-500 w-[48px] h-[48px] rounded-full flex items-center justify-center`,
                                css`filter: drop-shadow(0px 0px 13px rgba(0, 0, 0, 0.25))`
                            ]}
                    >
                        <ArrowPathIcon css={[tw`w-[34px] h-[34px] stroke-[2.3px]`, isFetching && tw`animate-spin`]}/>
                    </button>
                </div>
                <div tw={'max-h-[700px] overflow-y-auto'}>
                    <table tw={'w-full'}>
                        <thead>
                        <tr tw={'top-0 sticky bg-stone-700 font-bold h-[80px] shadow-2xl'}>
                            <th tw={'w-[400px]'}>
                                Название сервера
                            </th>
                            <th tw={'w-[120px]'}>
                                Игроки
                            </th>
                            <th tw={'w-[382px]'}>
                                Создатель
                            </th>
                        </tr>
                        </thead>
                        <tbody tw={'divide-y-4 divide-stone-900'}>
                        {
                            games && games.map(game => (
                                <GameRow onClick={() => navigate(`/games/${game.lobbyName}`)} key={game.lobbyName}>
                                    <td>{game.lobbyName}</td>
                                    <td>{game.playersJoined.length}/4</td>
                                    <td>{game.owner.username}</td>
                                </GameRow>
                            ))
                        }
                        {
                            fillRows(games?.length ?? 0).map((_, i) => (
                                <GameRow key={i}>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                </GameRow>
                            ))
                        }
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        </Fragment>

    )
}

const GameRow = tw.tr`h-[72px] divide-x-4 divide-stone-900`