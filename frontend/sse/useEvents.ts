import {DependencyList, useEffect, useState} from "react";

export const useEvents = (url: string, handlers: Record<string, (e: MessageEvent<string>) => void>, deps: DependencyList = []) => {
    const [source, setSource] = useState<EventSource | undefined>(undefined)

    // при изменении url, закрываем соединение и создаем новое
    useEffect(() => {
        const source = new EventSource(url, {withCredentials: true})
        setSource(source)
        return () => {
            source.close()
            setSource(undefined)
        }
    }, [url])

    // при изменении массива зависимостей обработчиков, отписываем все старые обработчики, подписываем новые
    useEffect(() => {
        if (!source) return
        Object.entries(handlers).forEach(([evt, cb]) => {
            source.addEventListener(evt, cb)
        })
        return () => {
            if (!source) return
            Object.entries(handlers).forEach(([evt, cb]) => {
                source.removeEventListener(evt, cb)
            })
        }
    }, [source, ...deps])
}