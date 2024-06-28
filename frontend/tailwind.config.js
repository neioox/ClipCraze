/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [],
  purge: ['./public/index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {

      fontFamily:{
          'rounded':["M PLUS Rounded 1c"]

      },
      backgroundImage: {
        'gradient-to-r': 'linear-gradient(to right, #a855f7, #4f46e5)',
      },
    },
  },
  plugins: [
    function ({ addUtilities }) {
      addUtilities({
        '.text-gradient': {
          'background-image': 'linear-gradient(to right, #a855f7, #4f46e5)',
          '-webkit-background-clip': 'text',
          '-webkit-text-fill-color': 'transparent',
        },
      });
    },
  ],
};
