import tw from "twin.macro";

export const Button = tw.button`font-bold border-4 border-solid border-stone-900 px-[70px] py-4 rounded-3xl text-xl transition-colors`

export const RedButton = tw(Button)`text-white bg-red-500 hover:bg-red-600 disabled:bg-red-300`
export const YellowButton = tw(Button)`text-black bg-amber-300 hover:bg-amber-400 disabled:bg-amber-200`