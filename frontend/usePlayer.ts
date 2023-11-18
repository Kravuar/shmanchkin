import {create} from "zustand";
import {PlayerInfo} from "@/types/domain.tsx";


type PlayerState = {
    player: PlayerInfo | null
    setPlayer: (player: PlayerInfo) => void
}

export const usePlayer = create<PlayerState>()(set => ({
    player: null,
    setPlayer: (player: PlayerInfo) => set({player})
}))