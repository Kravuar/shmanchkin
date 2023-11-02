import {Link} from "react-router-dom";
import tw from "twin.macro";
import styled from "@emotion/styled";
import {css} from "@emotion/react";

export const Main = () => {
    return (
        <div className={'text-white bg-stone-800 w-full min-h-screen pt-[50px] flex place-items-center'}>
            <div className={'mx-auto w-[558px] flex flex-col items-center gap-6'}>
                <MainMenuLink to={'/games'}>
                    Список игр
                </MainMenuLink>
                <MainMenuLink className={''} to={'/create-game'}>
                    Создать игру
                </MainMenuLink>
            </div>
        </div>
    )
}

const MainMenuLink = styled(Link)(() => ([
    tw`text-5xl font-bold`,
    css`&:hover {
      text-shadow: 0px 6px 10px rgba(255, 255, 255, 0.7)
    }`
]))