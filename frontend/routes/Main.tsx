import {Link} from "react-router-dom";

export const Main = () => {
    return (
        <div className={'text-white bg-stone-800 w-full min-h-screen pt-[50px] flex place-items-center'}>
            <div className={'mx-auto w-[558px] flex flex-col items-center gap-6'}>
                <Link to={'/games'}>
                    Games
                </Link>
                <Link to={'/create-game'}>
                </Link>
            </div>
        </div>
    )
}