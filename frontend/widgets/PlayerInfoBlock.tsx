import {DefaultError, UndefinedInitialDataOptions, useQuery} from "@tanstack/react-query";
import {api} from "@/api.ts";
import {PlayerInfo} from "@/types/domain.tsx";
import {useEffect, useState} from "react";
import {AxiosError} from "axios";
import {IdentifyModal} from "@/widgets/IdentifyModal.tsx";
import {QueryKey} from "@tanstack/query-core";

const usePlayer = (options?: UndefinedInitialDataOptions<unknown, DefaultError, unknown, QueryKey>) => useQuery({
    queryFn: async () => (await api.get('/user/userInfo')).data as PlayerInfo,
    queryKey: ['player'],
    retry: 0,
    ...options
})

export const PlayerInfoBlock = () => {
    const [open, setOpen] = useState(false)
    const {data: player, error} = usePlayer()
    useEffect(() => {
        console.log('error from effect', error)
        if (error && error instanceof AxiosError && error.response?.status === 401)
            setOpen(true)
    }, [error])
    console.log(player)
    return (
        <IdentifyModal onClose={() => setOpen(false)} open={open}/>
    )
}