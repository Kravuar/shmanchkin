import {Link, useNavigate, useParams} from "react-router-dom";
import {useEvents} from "@/sse/useEvents.ts";
import {Fragment, useRef, useState} from "react";
import {PaperAirplaneIcon} from "@heroicons/react/24/outline";
import {useForm} from "react-hook-form";
import {useMutation, useQuery} from "@tanstack/react-query";
import {api} from "@/api.ts";
import {Player} from "@/types/domain.tsx";
import useResizeObserver from "use-resize-observer";
import {useAlertStore} from "@/alert/useAlert.tsx";
import {nanoid} from "nanoid";
import {usePlayer} from "@/usePlayer.ts";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import tw from 'twin.macro'
import {Spinner} from "@/components/Spinner.tsx";
import {RedButton, YellowButton} from "@/components/Button.tsx";

type ChatFormValues = {
    message: string
}

type MessageEventData = {
    message: string
    when: string
    sender: Player
}

type PlayersFullUpdateEventData = {
    players: Array<Player>
}

type LobbyInfo = {
    lobbyName: string
    owner: Player
    playersJoined: Player[]
}

export const Game = () => {
    const navigate = useNavigate()
    const player = usePlayer(state => state.player)!
    const pushAlert = useAlertStore(state => state.push)
    const {lobbyName} = useParams()
    const {data: lobbyInfo} = useQuery({
        queryFn: async () => (await api.get('/games/info')).data as LobbyInfo,
        queryKey: ['game/info', lobbyName]
    })
    const closeLobby = useMutation({
        mutationFn: () => api.delete('/games/close'),
        onSuccess: () => {
            pushAlert({
                id: nanoid(),
                type: "success",
                header: "Лобби распущено",
                message: ""
            })
        },
        onError: () => {
            pushAlert({
                id: nanoid(),
                type: "error",
                header: "Ошибка",
                message: ""
            })
        }
    })
    const startLobby = useMutation({
        mutationFn: () => api.put('/games/start'),
        onSuccess: () => {
            pushAlert({
                id: nanoid(),
                type: "success",
                header: "Игра начата",
                message: ""
            })
        },
        onError: () => {
            pushAlert({
                id: nanoid(),
                type: "error",
                header: "Ошибка",
                message: ""
            })
        }
    })

    const [messages, setMessage] = useState<MessageEventData[]>([])
    console.log('messages', messages)

    const [players, setPlayers] = useState<Player[]>([])
    console.log('players', players)

    useEvents(`/api/games/join/${lobbyName}`, {
        'player-message': (e: MessageEvent<string>) => {
            console.log('message', e.data)
            const message = JSON.parse(e.data) as MessageEventData
            setMessage(messages => [...messages, message])
        },
        'player-kicked': () => {
            pushAlert({
                id: nanoid(),
                type: "info",
                header: "Вас кикнули",
                message: ""
            })
            navigate('/games')
        },
        'game-status-change': (e: MessageEvent<string>) => {
            console.log('game status', e.data)
        },
        'players-full-update': (e: MessageEvent<string>) => {
            console.log('players update', e.data)
            const data = JSON.parse(e.data) as PlayersFullUpdateEventData
            setPlayers(data.players)
        },
        'error': (e: Event) => {
            pushAlert({
                id: nanoid(),
                type: "error",
                header: "Ошибка " + e.type,
                message: JSON.stringify(e)
            })
        }
    }, [])

    const {reset, register, handleSubmit} = useForm<ChatFormValues>()
    const sendMessageMutation = useMutation({
        mutationFn: (data: ChatFormValues) => api.post("/games/sendMessage", data.message, {
            headers: {"Content-Type": "text/plain"}
        }),
        onSuccess: () => {
            reset();
        }
    })
    const messagesRef = useRef<HTMLDivElement | null>(null)
    useResizeObserver({
        ref: messagesRef,
        onResize: () => {
            const chat = messagesRef.current?.parentNode as HTMLDivElement | null;
            if (chat)
                chat.scrollTop = chat.scrollHeight - chat.clientHeight;
        }
    })

    return (
        <div className={'text-white bg-stone-800 w-full min-h-screen flex place-items-center'}>
            <div style={{
                boxShadow: "0px 0px 70px 0px #1C1917"
            }}
                 className={'mx-auto w-full max-w-[909px] rounded-[36px] bg-stone-800 text-[20px] py-6 px-[72px]'}
            >
                <div className={'flex flex-col items-center'}>
                    <h1 className={'mt-10 font-bold text-4xl'}>
                        Игра: {lobbyName}
                    </h1>
                    <h2 className={'mt-6 text-xl flex gap-2'}>
                        Создатель: { lobbyInfo?.owner.username ?? <Spinner/>}
                    </h2>
                </div>
                <div className={'w-full mt-8 flex gap-[21px]'}>
                    <div className={'bg-stone-700 basis-[750px] flex flex-col h-[300px] rounded-3xl px-10 py-3'}>
                        <div className={'h-full overflow-y-auto p-2'}>
                            <div ref={messagesRef} className={'flex flex-col gap-3'}>
                                {
                                    messages.map(message => (
                                        <div className={`px-1 py-0.5 w-full flex flex-col gap-1 text-start border border-stone-400 rounded`}>
                                            <div css={[
                                                tw`text-base`,
                                                message.sender.username === player.username && tw`text-amber-300`
                                            ]}>
                                                {
                                                    message.sender.username === player.username ? 'Вы' : message.sender.username
                                                }:
                                            </div>
                                            <div className={'px-6 text-sm'}>
                                                {message.message}
                                            </div>
                                        </div>
                                    ))
                                }
                            </div>
                        </div>
                        <form onSubmit={handleSubmit(data => sendMessageMutation.mutate(data))}
                              className={'px-3 py-1 flex gap-2.5 bg-stone-600 rounded-xl'}>
                            <input {...register('message')} className={'w-full bg-transparent'}/>
                            <button type={'submit'}>
                                <PaperAirplaneIcon className={'w-5 h-5 text-amber-300'}/>
                            </button>
                        </form>
                    </div>
                    <div className={'bg-stone-700 basis-[350px] flex flex-col items-start px-12 py-8 rounded-3xl'}>
                        {
                            players.map((player) => (
                                <div key={player.uuid}>
                                    {player.username}
                                </div>
                            ))
                        }
                    </div>
                </div>
                <div className={'mt-7 flex flex-wrap gap-2'}>
                    {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
                    {/* @ts-ignore */}
                        <RedButton as={Link} to={'/games'}>
                            Покинуть
                        </RedButton>
                    {
                        lobbyInfo?.owner.uuid === player.uuid ? (
                            <Fragment>
                                <YellowButton onClick={() => startLobby.mutate()} disabled={startLobby.isPending}>
                                    Запустить
                                </YellowButton>
                                <RedButton onClick={() => closeLobby.mutate()} disabled={closeLobby.isPending}>
                                    Распустить
                                </RedButton>
                            </Fragment>
                        ) : null
                    }
                </div>
            </div>
        </div>
    )
}