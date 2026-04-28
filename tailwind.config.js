// /** @type {import('tailwindcss').Config} */
// export default {
//   content: [
//     "./src/main/resources/templates/**/*.html","./src/main/resources/templates/**/*.css"],
//   theme: {
//     extend: {},
//   },

//   plugins: [import flowbitePlugin from 'flowbite/plugin'],
//   darkMode: 'selector', // selector se hum apne dark mode ko control kar sakte hain, jaise ki .dark class ke through
// };

import flowbitePlugin from 'flowbite/plugin';

/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./src/main/resources/templates/**/*.html",
    "./src/main/resources/static/**/*.js",
    "./node_modules/flowbite/**/*.js",
  ],
  theme: {
    extend: {
      // Custom Colors ki pehchan
      colors: {
        brand: {
          DEFAULT: "#1a56db",
          strong: "#1e429f",
          light: "#ebf5ff",
        },
        "fg-brand": "#1a56db", // Isse text-fg-brand chalne lagega
        "text-heading": "#111827", // Screenshot mein ye bhi hai
        "facebook-blue": "#1877F2",
        primary: "#4f46e5",
        "fg-muted": "#6b7280",
        "fg-default": "#111827",
        "bg-neutral-primary": "#ffffff",
        "bg-neutral-secondary": "#f9fafb",
        "bg-neutral-tertiary": "#f3f4f6",
        "bg-neutral-secondary-soft": "#f9fafb",
      },

      // 5. Custom Radius (rounded-base, rounded-large)
      borderRadius: {
        base: "0.375rem",
        large: "0.5rem",
        xl: "0.75rem",
      },
      // 6. Custom Spacing (Jo standard list mein nahi hain)
      spacing: {
        13: "3.25rem",
        15: "3.75rem",
        18: "4.5rem",
      },
      // Custom Fonts ki pehchan
      fontFamily: {
        poppins: ["Poppins", "sans-serif"],
        hindi: ["Noto Sans Devanagari", "sans-serif"],
      },

      boxShadow: {
        "super-heavy": "0 35px 60px -15px rgba(0, 0, 0, 0.3)",
      },
    },
  },
  plugins: [
    flowbitePlugin, // Yahan upar wala variable use ho raha hai
  ],
  darkMode: "selector",
};
