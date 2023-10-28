import React from 'react'
import ReactDOM from 'react-dom/client'
import {ReactQueryDevtools} from "@tanstack/react-query-devtools";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import './index.css'

import {Games} from "@/routes/Games.tsx";
import {CreateGame} from "@/routes/CreateGame.tsx";
import {Game} from "@/routes/Game.tsx";


const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 1000 * 60 * 2,
            refetchOnWindowFocus: false,
            refetchOnMount: true,
        }
    }
})

const router = createBrowserRouter([
    {
        path: "/games",
        element: <Games/>,
    },
    {
        path: "/create-game",
        element: <CreateGame/>
    },
    {
        path: "/games/:lobbyName/:username",
        element: <Game/>
    }
])

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router}/>
            <ReactQueryDevtools initialIsOpen={false}/>
        </QueryClientProvider>
    </React.StrictMode>,
)
