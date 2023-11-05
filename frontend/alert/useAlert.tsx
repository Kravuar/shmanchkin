import {create} from "zustand";
import {nanoid} from "nanoid";

type Alert = {
    id: string
    type: "success" | "error" | "info" | "debug"
    header: string
    message: string
}

type AlertState = {
    alerts: Alert[]
    push: (alert: Alert) => void
    pop: () => void
    remove: (id: string) => void
}

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const fish = [
    {
        id: nanoid(),
        type: "success",
        header: "Initial",
        message: "success alert"
    },
    {
        id: nanoid(),
        type: "error",
        header: "Initial",
        message: "error alert"
    },
    {
        id: nanoid(),
        type: "debug",
        header: "Initial",
        message: "debug alert"
    },
    {
        id: nanoid(),
        type: "info",
        header: "Initial",
        message: "info alert"
    }
] as Alert[]

export const useAlertStore = create<AlertState>()(set => ({
    alerts: [],
    // to end
    push: (alert) => set(state => {
        setTimeout(() => {
            state.remove(alert.id)
        }, 2000)
        return {alerts: [...state.alerts, alert]}
    }),
    // from start
    pop: () => set(state => ({
        alerts: state.alerts.slice(1)
    })),
    remove: (id) => set(state => ({
        alerts: state.alerts.filter(alert => alert.id !== id)
    }))
}))

