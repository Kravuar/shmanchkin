import {Link} from "react-router-dom";
import tw from "twin.macro";
import styled from "@emotion/styled";
import {css} from "@emotion/react";
import video from "@/assets/video_lowBitRate_AV1_60fps.mp4";

export const Main = () => {
    return (
        <div tw={'w-full min-h-screen relative'}>
            <div className={'absolute h-screen w-full'}>
                <video src={video} playsInline autoPlay muted loop className={'object-cover h-full w-full'}/>
            </div>
            <div tw={'absolute text-white flex place-items-center h-screen w-full'}>
                <div tw={'mx-auto w-[558px] flex flex-col items-center gap-6'}>
                    <MainMenuLink to={'/games'}>
                        Список игр
                    </MainMenuLink>
                    <MainMenuLink className={''} to={'/create-game'}>
                        Создать игру
                    </MainMenuLink>
                </div>
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