import {useMutation, useQueryClient} from "@tanstack/react-query";
import {api} from "@/api.ts";
import {useForm} from "react-hook-form";
import {Link} from "react-router-dom";
import {RedButton, YellowButton} from "@/components/Button.tsx";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import tw from "twin.macro";

type FormValues = {
    lobbyName: string
}

// TODO: добавить перенаправление в созданное лобби
//  при успехе и вывод ошибки при неудаче
export const CreateGame = () => {
    const {register, handleSubmit} = useForm<FormValues>()
    const client = useQueryClient()
    const createGameMutation = useMutation({
        mutationFn: (data: FormValues) => api.post("/games/create", data),
        onSettled: () => {
            client.invalidateQueries({queryKey: ["games"]})
        }
    })
    return (
        <div tw={'text-white bg-stone-800 w-full min-h-screen pt-[50px] flex place-items-center'}>
            <form onSubmit={handleSubmit(data => {
                createGameMutation.mutate(data)
            })} tw={'mx-auto w-[558px] flex flex-col items-center gap-6'}>
                <h1 tw={'text-4xl font-bold'}>
                    Создание лобби
                </h1>
                <input type={'text'} placeholder={'Название лобби'}
                       tw={'w-full text-center px-6 py-6 border-4 bg-stone-700 border-solid border-stone-900 rounded-3xl placeholder:tracking-[8.64px] text-2xl'}
                       {...register('lobbyName')}
                />
                <div tw={'w-full flex justify-between'}>
                    <RedLink to={'/'} role={'button'}>
                        Отмена
                    </RedLink>
                    <YellowButton type={'submit'}>
                        Создать
                    </YellowButton>
                </div>
            </form>
        </div>
    )
}

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
const RedLink = RedButton.withComponent(Link)