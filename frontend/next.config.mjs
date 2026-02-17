/** @type {import('next').NextConfig} */
const nextConfig = {
    async rewrites() {
        return [
            {
                source: '/api/:path*',
                destination: 'http://localhost:8080/api/:path*',
            },
        ];
    },
    reactStrictMode: true,
    // Если используешь Tailwind или что-то ещё — добавь
};

export default nextConfig;