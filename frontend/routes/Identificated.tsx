import {useQuery} from "@tanstack/react-query";
import {api} from "@/api.ts";
import {PlayerInfo} from "@/types/domain.tsx";
import {usePlayer} from "@/usePlayer.ts";
import {useEffect} from "react";
import {Outlet} from "react-router-dom";
import {AxiosError} from "axios";
import {IdentifyModal} from "@/widgets/IdentifyModal.tsx";
import {Spinner} from "@/components/Spinner.tsx";

export const Identificated = () => {
    const {data: player, isSuccess, error} = useQuery({
        queryFn: async () => (await api.get('/user/userInfo')).data as PlayerInfo,
        queryKey: ['player'],
        retry: 0,
    })
    const setPlayer = usePlayer(state => state.setPlayer)

    useEffect(() => {
        if (isSuccess)
            setPlayer(player)
    }, [isSuccess, player, setPlayer])

    if (isSuccess)
        return <Outlet/>

    if (error) {
        console.log('user data error', error)
        if (error && error instanceof AxiosError && error.response?.status === 401)
            return <div className={'text-white bg-stone-800 w-full min-h-screen pt-[50px] flex place-items-center'}>
                <IdentifyModal onClose={() => {
                }} open={true}/>
            </div>
        else
            return (
                <div className={'text-white bg-stone-800 w-full min-h-screen pt-[50px] flex place-items-center'}>
                    Ошибка :(
                </div>
            )
    }

    return (
        <div className={'text-white bg-stone-800 w-full min-h-screen pt-[50px] flex place-items-center'}>
            <div className={'mx-auto max-w-[600px] flex flex-col items-center gap-6'}>
                <Spinner/>
                <h1 className={'text-4xl'}>
                    Проверка авторизации...
                </h1>
            </div>
        </div>
    )
}