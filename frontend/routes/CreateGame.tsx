import {useMutation, useQueryClient} from "@tanstack/react-query";
import {api} from "@/api.ts";
import {useForm} from "react-hook-form";
import {Link, useNavigate} from "react-router-dom";
import {RedButton, YellowButton} from "@/components/Button.tsx";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import tw from "twin.macro";
import {AxiosError} from "axios";
import {Input} from "@/components/Input.tsx";
import {useState} from "react";
import {IdentifyModal} from "@/widgets/IdentifyModal.tsx";
import {useAlertStore} from "@/alert/useAlert.tsx";
import {nanoid} from "nanoid";

type FormValues = {
    lobbyName: string
}

// TODO: добавить перенаправление в созданное лобби
//  при успехе и вывод ошибки при неудаче
export const CreateGame = () => {
    const pushAlert = useAlertStore(state => state.push)
    const navigate = useNavigate()
    const [authModalOpen, setAuthModalOpen] = useState(false)

    const {register, handleSubmit} = useForm<FormValues>()
    const client = useQueryClient()
    const createGameMutation = useMutation({
        mutationFn: (data: FormValues) => api.post("/games/create", data),
        onSettled: () => {
            client.invalidateQueries({queryKey: ["games"]})
        },
        onError: error => {
            if (error instanceof AxiosError) {
                pushAlert({
                    id: nanoid(),
                    type: "error",
                    header: "Ошибка " + error.response?.status,
                    message: error.response?.data ?? "Неизвестная ошибка"
                })
                if (error.response?.status === 401) {
                    setAuthModalOpen(true)
                }
            }
        },
        onSuccess: (_, {lobbyName}) => {
            pushAlert({
                id: nanoid(),
                type: "success",
                header: "Успех",
                message: "Игры успешно создана"
            })
            navigate(`/games/${lobbyName}`)
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
                <Input type={'text'} placeholder={'Название лобби'}
                       {...register('lobbyName')}
                />
                <div tw={'w-full flex justify-between'}>
                    {/* @ts-ignore */}
                    <RedButton as={Link} to={'/'} role={'button'}>
                        Отмена
                    </RedButton>
                    <YellowButton type={'submit'}>
                        Создать
                    </YellowButton>
                </div>
                <IdentifyModal open={authModalOpen} onClose={() => setAuthModalOpen(false)}/>
            </form>
        </div>
    )
}