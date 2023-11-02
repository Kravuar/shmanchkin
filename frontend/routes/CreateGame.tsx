import {useMutation, useQueryClient} from "@tanstack/react-query";
import {api} from "@/api.ts";
import {useForm} from "react-hook-form";
import {Link} from "react-router-dom";

type FormValues = {
    lobbyName: string
    ownerName: string
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
        <div className={'text-white bg-stone-800 w-full min-h-screen pt-[50px] flex place-items-center'}>
            <form onSubmit={handleSubmit(data => {
                createGameMutation.mutate(data)
            })} className={'mx-auto w-[558px] flex flex-col items-center gap-6'}>
                <h1 className={'text-4xl font-bold'}>
                    Создание лобби
                </h1>
                <input type={'text'} placeholder={'Название лобби'}
                       className={'w-full text-center px-6 py-6 border-4 bg-stone-700 border-solid border-stone-900 rounded-3xl placeholder:tracking-[8.64px] text-2xl'}
                       {...register('lobbyName')}
                />
                <input type={'text'} placeholder={'Ваш никнейм'}
                       className={'w-full text-center px-6 py-6 border-4 bg-stone-700 border-solid border-stone-900 rounded-3xl placeholder:tracking-[8.64px] text-2xl'}
                       {...register('ownerName')}
                />
                <div className={'w-full flex justify-between'}>
                    <Link to={'/'} role={'button'} className={'font-bold border-4 border-solid border-stone-900 text-white bg-red-500 px-[70px] py-6 rounded-3xl text-xl'}>
                        Отмена
                    </Link>
                    <button type={'submit'} className={'font-bold border-4 border-solid border-stone-900 text-black bg-amber-300 px-[70px] py-6 rounded-3xl text-xl'}>
                        Создать
                    </button>
                </div>
            </form>
        </div>
    )
}