import {useAlertStore} from "@/alert/useAlert.tsx";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import tw from 'twin.macro'
import styled from "@emotion/styled";

export const AlertProvider = (
    // {children}: {children: ReactNode | ReactNode[]}
) => {
    const alerts = useAlertStore(state => state.alerts)

    return (
        <div tw={'fixed z-10 bottom-2 right-2 flex flex-col gap-2 w-[400px]'}>
            {
                alerts.map(alert => (
                    <Alert type={alert.type} key={alert.id}>
                        <Title>
                            {alert.header}
                        </Title>
                        <p>
                            {alert.message}
                        </p>
                    </Alert>
                ))
            }
        </div>
    )
}

const Alert = styled.article(({type}: {type: "success" | "error" | "info" | "debug"}) => [
    tw`w-full px-4 py-1 rounded-3xl border-4`,
    {
        success: tw`bg-green-300 border-green-500 text-green-950`,
        error: tw`bg-red-300 border-red-500 text-red-950`,
        debug: tw`bg-slate-300 border-slate-500 text-slate-950`,
        info: tw`bg-blue-300 border-blue-500 text-blue-950`
    }[type]
])

const Title = tw.h1`text-2xl`