import React from 'react'
import ReactDOM from 'react-dom/client'
import {ReactQueryDevtools} from "@tanstack/react-query-devtools";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import './index.css'

import {Games} from "@/routes/Games.tsx";
import {CreateGame} from "@/routes/CreateGame.tsx";
import {Game} from "@/routes/Game.tsx";
import {Test} from "@/routes/Test.tsx";
import {Main} from "@/routes/Main.tsx";
import {AlertProvider} from "@/alert/AlertProvider.tsx";
import {Identificated} from "@/routes/Identificated.tsx";


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
        path: '/',
        element: <Identificated/>,
        children: [
            {
                path: '/',
                element: <Main/>,
            },
            {
                path: "/games",
                element: <Games/>,
            },
            {
                path: "/create-game",
                element: <CreateGame/>
            },
            {
                path: "/games/:lobbyName",
                element: <Game/>
            },
            {
                path: "/test",
                element: <Test/>
            }
        ]
    }
])

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            {/*<AlertTester/>*/}
            <AlertProvider/>
            <RouterProvider router={router}/>
            <ReactQueryDevtools buttonPosition={'bottom-left'} initialIsOpen={false}/>
        </QueryClientProvider>
    </React.StrictMode>,
)
