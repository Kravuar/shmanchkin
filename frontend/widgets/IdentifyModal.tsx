import {Dialog, Transition} from "@headlessui/react";
import {useForm} from "react-hook-form";
import {Fragment, useRef} from "react";
import {Input} from "@/components/Input.tsx";
import {YellowButton} from "@/components/Button.tsx";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {api} from "@/api.ts";
import {ErrorText} from "@/components/Typography.tsx";
import tw from 'twin.macro'
import {css} from "@emotion/react";

type FormValues = {
    username: string
}

//  ComponentProps<typeof Dialog>
export const IdentifyModal = ({onClose, open}: { onClose: () => void, open: boolean }) => {
    const inputRef = useRef<HTMLFormElement>(null)
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
            <Dialog as="div" className="relative z-10" onClose={onClose} initialFocus={inputRef}>
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
                    <div css={[
                        css`background: rgba(28, 25, 23, 0.01);
                          backdrop-filter: blur(9px);`,
                        tw`fixed inset-0`
                    ]}/>
                </Transition.Child>

                <div tw="fixed inset-0 overflow-y-auto">
                    <div tw="flex min-h-full items-center justify-center p-4 text-center">
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
                                className={'w-[640px] transform overflow-hidden rounded-3xl bg-stone-900 p-10 text-left align-middle transition-all'}
                                style={{
                                    boxShadow: "0 0 92px 0 #0C0A09"
                                }}
                            >
                                <Dialog.Title as="h1" tw="text-3xl font-medium leading-6 text-white text-center">
                                    Придумайте себе никнейм
                                </Dialog.Title>
                                <form ref={inputRef} tw={'mt-8 w-full flex flex-col items-center gap-8'}
                                      onSubmit={handleSubmit(onSubmit)}>
                                    <Input type={'text'} placeholder={'Никнейм'} {...register('username')}/>
                                    <YellowButton tw={'w-full'} type={'submit'}>
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




