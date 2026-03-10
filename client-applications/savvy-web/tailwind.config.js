/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts,scss}"],
  darkMode: ["class", { className: "dark-theme" }],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: "#5b7a9d",
          light: "#7ea3c4",
          dark: "#3d5a7c",
        },
        secondary: "#8b8ec2",
        accent: "#d4a55a",
        danger: "#c96b6b",
        success: "#6aaa8e",
        surface: {
          app: "var(--bg-app)",
          card: "var(--bg-surface)",
          glass: "var(--bg-glass)",
        },
        txt: {
          main: "var(--text-main)",
          secondary: "var(--text-secondary)",
          light: "var(--text-light)",
        },
        border: {
          glass: "var(--border-glass)",
        },
      },
      borderRadius: {
        xl: "16px",
        "2xl": "24px",
      },
      backdropBlur: {
        glass: "12px",
      },
      boxShadow: {
        glass: "var(--shadow-glass)",
        glow: "0 0 20px var(--primary-glow)",
      },
      fontFamily: {
        sans: ["Inter", "system-ui", "-apple-system", "sans-serif"],
      },
    },
  },
  plugins: [],
};
