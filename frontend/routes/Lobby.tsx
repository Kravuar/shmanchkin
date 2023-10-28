import chest from 'assets/chest.png'
import {ArrowLeftIcon, ArrowPathIcon} from "@heroicons/react/24/outline";

export const Lobby = () => {
    return (
        <div className={'text-white bg-stone-800 w-full min-h-screen pt-[106px] pb-[80px]'}>

            <div style={{
                filter: "drop-shadow(0px 0px 110px #1C1917)"
            }}
                 className={'mx-auto max-w-[909px] rounded-[36px] bg-stone-700 text-[20px] pt-8 text-center'}>
                <div className={'flex ps-12'}>
                    <button>
                        <ArrowLeftIcon className={'w-8 h-8 stroke-[3px]'}/>
                    </button>
                </div>
                <div className={'max-h-[700px] overflow-y-auto'}>
                    <table className={'w-full'}>
                        <thead>
                        <tr className={'top-0 sticky bg-stone-700 font-bold h-[80px] shadow-2xl'}>
                            <th className={'w-[400px]'}>
                                Название сервера
                            </th>
                            <th className={'w-[120px]'}>
                                Игроки
                            </th>
                            <th className={'w-[382px]'}>
                                Создатель
                            </th>
                        </tr>
                        </thead>
                        <tbody className={'divide-y-4 divide-stone-900'}>
                        {
                            Array.from({length: 14}, (_, k) => (
                                <tr key={k} className={'h-[72px] divide-x-4 divide-stone-900 '}>
                                    <td className={''}>
                                        Такое вот название у сервера!?
                                    </td>
                                    <td className={''}>
                                        4/4
                                    </td>
                                    <td className={''}>
                                        hetEro_phobE
                                    </td>
                                </tr>
                            ))
                        }
                        </tbody>
                    </table>
                </div>
                <div style={{
                    boxShadow: "0px 0px 250px 0px rgba(28, 25, 23, 0.60)"
                }} className={'pt-8 pb-[36px] pe-[78px] flex justify-end gap-7 items-center rounded-b-[36px]'}>
                    <button className={'bg-red-400 w-[52px] h-[52px] rounded-full flex items-center justify-center'}>
                        <ArrowPathIcon className={'w-6 h-6'}/>
                    </button>
                    <button style={{
                        backgroundImage: `url(${chest})`
                    }} className={'h-20 w-[90px] bg-contain bg-no-repeat'}>

                    </button>
                </div>
            </div>
        </div>
    )
}