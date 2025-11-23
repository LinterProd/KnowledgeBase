export default function Home() {
    return (
        <div className="space-y-8">
            <h1 className="text-4xl font-bold text-center text-white">
                Документооборот
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-gray-800 p-6 rounded-lg border border-gray-700">
                    <h2 className="text-2xl font-semibold mb-4 text-white">Система готова</h2>
                    <p className="text-gray-300">
                        Все основные стили работают корректно
                    </p>
                </div>

                <div className="bg-blue-900 p-6 rounded-lg border border-blue-700">
                    <h2 className="text-2xl font-semibold mb-4 text-white">Функционал</h2>
                    <p className="text-blue-200">
                        Можно начинать разработку
                    </p>
                </div>
            </div>

            <div className="bg-gray-800 p-6 rounded-lg mt-8">
                <h3 className="text-xl font-medium text-white mb-4">Статус:</h3>
                <p className="text-green-400">✅ Tailwind настроен</p>
                <p className="text-green-400">✅ Тёмная тема активна</p>
                <p className="text-green-400">✅ Готово к разработке</p>
            </div>
        </div>
    );
}