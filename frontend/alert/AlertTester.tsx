import {RedButton, YellowButton} from "@/components/Button.tsx";
import {nanoid} from "nanoid";
import {useAlertStore} from "@/alert/useAlert.tsx";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import tw from 'twin.macro'

export const AlertTester = () => {
    const push = useAlertStore(state => state.push)
    return (
        <div tw={'fixed z-10 top-0 right-0 w-[300px] flex flex-col gap-4'}>
            <YellowButton onClick={() => push({
                type: "info",
                id: nanoid(),
                header: "Info message",
                message: "abodabwda",
            })}>
                Info
            </YellowButton>
            <RedButton onClick={() => push({
                type: "error",
                id: nanoid(),
                header: "Error message",
                message: "abodabwda",
            })}>
                Error
            </RedButton>
            <YellowButton onClick={() => push({
                type: "success",
                id: nanoid(),
                header: "Success message",
                message: "abodabwda",
            })}>
                Success
            </YellowButton>
            <YellowButton onClick={() => push({
                type: "debug",
                id: nanoid(),
                header: "Debug message",
                message: "abodabwda",
            })}>
                Debug
            </YellowButton>
        </div>
    )
}