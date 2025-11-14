import forms from '@tailwindcss/forms'

/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['"Plus Jakarta Sans"', 'Inter', 'system-ui', 'sans-serif'],
      },
      colors: {
        brand: {
          50: '#f1f5ff',
          100: '#dfe8ff',
          200: '#bfd1ff',
          400: '#5c8dff',
          500: '#3f6ff2',
          600: '#2755d6',
          700: '#1c3ea7',
        },
      },
      boxShadow: {
        card: '0 25px 65px -20px rgba(15, 23, 42, 0.35)',
      },
      backgroundImage: {
        'auth-gradient': 'radial-gradient(circle at 10% 20%, rgba(63,111,242,0.35), transparent 55%), radial-gradient(circle at 90% 30%, rgba(15,118,110,0.25), transparent 50%), radial-gradient(circle at 50% 80%, rgba(14,116,144,0.3), transparent 45%)',
      },
    },
  },
  plugins: [forms],
}

