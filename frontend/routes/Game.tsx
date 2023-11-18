import {useParams} from "react-router-dom";
import {useEvents} from "@/sse/useEvents.ts";
import {useRef, useState} from "react";
import {PaperAirplaneIcon} from "@heroicons/react/24/outline";
import {useForm} from "react-hook-form";
import {useMutation} from "@tanstack/react-query";
import {api} from "@/api.ts";
import {Player} from "@/types/domain.tsx";
import useResizeObserver from "use-resize-observer";
import {useAlertStore} from "@/alert/useAlert.tsx";
import {nanoid} from "nanoid";
import {usePlayer} from "@/usePlayer.ts";

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

export const Game = () => {
    const player = usePlayer(state => state.player)!
    const pushAlert = useAlertStore(state => state.push)
    const {lobbyName} = useParams()
    const [messages, setMessage] = useState<MessageEventData[]>([])
    console.log('messages', messages)

    const [players, setPlayers] = useState<Player[]>([])
    console.log('players', players)

    useEvents(`/api/games/join/${lobbyName}`, {
        'player-message': (e: MessageEvent<string>) => {
            console.log('message', e.data)
            const message = JSON.parse(e.data) as MessageEventData
            setMessage(messages => [...messages, {
                ...message,
                sender: {
                    ...message.sender,
                    username: message.sender.username === player.username ? 'Вы' : message.sender.username
                }
            } as MessageEventData])
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
                 className={'mx-auto w-full max-w-[909px] rounded-[36px] bg-stone-800 text-[20px] py-6 px-[72px] text-center'}
            >
                <h1 className={'mt-10 font-bold text-4xl'}>
                    Игра: {lobbyName}
                </h1>
                {/*<h2 className={'mt-6 text-lg flex gap-3'}>*/}
                {/*    <div>*/}
                {/*        Создатель:*/}
                {/*    </div>*/}
                {/*    <div>*/}
                {/*        Говно2000ИзЖопы*/}
                {/*    </div>*/}
                {/*</h2>*/}
                <div className={'w-full mt-8'}>
                    <div className={'bg-stone-700 w-full flex flex-col h-[300px] rounded-3xl px-10 py-3'}>
                        <div className={'h-full overflow-y-auto flex flex-col gap-3 p-2'}>
                            <div ref={messagesRef}>
                                {
                                    messages.map(message => (
                                        <div className={'w-full flex flex-col gap-1 text-start'}>
                                            <div className={'text-base'}>
                                                {message.sender.username}:
                                            </div>
                                            <div className={'ps-6 text-sm'}>
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
                </div>
            </div>
        </div>
    )
}