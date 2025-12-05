
/** @type {import('next').NextConfig} */
const nextConfig = {
  reactCompiler: true,
 /** env: {
    NEXT_PUBLIC_API_BASE_URL: process.env.NEXT_PUBLIC_API_BASE_URL,
  },**/
  async redirects() {
    return [
      {
        source: '/dashboard',
        destination: '/admin/dashboard',
        permanent: true,
      },
    ]
  },

};
export default nextConfig;
