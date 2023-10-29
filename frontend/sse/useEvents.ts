import {DependencyList, useEffect} from "react";

export const useEvents = (url: string, listeners: Record<string, (e: MessageEvent<string>) => void>, deps: DependencyList = []) => {
    useEffect(() => {
        const source = new EventSource(url)
        Object.entries(listeners).forEach(([evt, cb]) => {
            source.addEventListener(evt, cb)
        })
        return () => {
            source.close()
            // maybe detach all listeners...
        }
    }, [url, ...deps])
}