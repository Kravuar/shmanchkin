import {Dialog, Transition} from "@headlessui/react";
import {useForm} from "react-hook-form";
import {Fragment} from "react";
import {Input} from "@/components/Input.tsx";
import {YellowButton} from "@/components/Button.tsx";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {api} from "@/api.ts";
import {ErrorText} from "@/components/Typography.tsx";
import tw from 'twin.macro'

type FormValues = {
    username: string
}

//  ComponentProps<typeof Dialog>
export const IdentifyModal = ({onClose, open}: { onClose: () => void, open: boolean }) => {
    const client = useQueryClient()
    const authMutation = useMutation({
        mutationFn: (data: FormValues) => api.post('/auth/signIn', data),
        onSuccess: () => {
            onClose()
        },
        onSettled: () => {
            client.invalidateQueries({
                queryKey: ['player']
            })
        }
    })
    const {register, handleSubmit} = useForm<FormValues>()
    const onSubmit = (data: FormValues) => {
        authMutation.mutate(data)
    }
    return (
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        <Transition appear show={open} as={Fragment}>
            <Dialog as="div" className="relative z-10" onClose={onClose}>
                {/*eslint-disable-next-line @typescript-eslint/ban-ts-comment*/}
                {/*@ts-ignore*/}
                <Transition.Child as={Fragment}
                                  enter="ease-out duration-300"
                                  enterFrom="opacity-0"
                                  enterTo="opacity-100"
                                  leave="ease-in duration-200"
                                  leaveFrom="opacity-100"
                                  leaveTo="opacity-0"
                >
                    <div className="fixed inset-0 bg-black/25"/>
                </Transition.Child>

                <div className="fixed inset-0 overflow-y-auto">
                    <div className="flex min-h-full items-center justify-center p-4 text-center">
                        {/*eslint-disable-next-line @typescript-eslint/ban-ts-comment*/}
                        {/*@ts-ignore*/}
                        <Transition.Child as={Fragment}
                                          enter="ease-out duration-300"
                                          enterFrom="opacity-0 scale-95"
                                          enterTo="opacity-100 scale-100"
                                          leave="ease-in duration-200"
                                          leaveFrom="opacity-100 scale-100"
                                          leaveTo="opacity-0 scale-95"
                        >
                            <Dialog.Panel
                                className="w-full max-w-md transform overflow-hidden rounded-2xl bg-stone-800 p-6 text-left align-middle shadow-xl transition-all">
                                <Dialog.Title as="h3" tw="text-2xl font-medium leading-6 text-white">
                                    Введите свой никнейм
                                </Dialog.Title>
                                <Dialog.Description tw={'mt-2 text-white'}>
                                    Он нужен, чтобы вас индентифицировать в игре
                                </Dialog.Description>

                                <form tw={'mt-4 flex flex-col items-center gap-4'} onSubmit={handleSubmit(onSubmit)}>
                                    <Input type={'text'} placeholder={'Никнейм'} {...register('username')}/>
                                    <YellowButton type={'submit'}>
                                        Подтвердить
                                    </YellowButton>
                                    <ErrorText>
                                        {authMutation.error?.message}
                                    </ErrorText>
                                </form>
                            </Dialog.Panel>
                        </Transition.Child>
                    </div>
                </div>
            </Dialog>
        </Transition>
    )
}




