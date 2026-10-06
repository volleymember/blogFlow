/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eef3fb',
          100: '#d5e0f4',
          200: '#b3c8ea',
          300: '#8aaade',
          400: '#6490cc',
          500: '#5a7ec2',
          600: '#4a6db2',
          700: '#3d5da2',
          800: '#334d85',
          900: '#2a3f6c',
          950: '#1a2844',
          light: '#5a7ec2',
          dark: '#7ba1d9',
        },
        surface: {
          50: '#f6f8fb',
          100: '#eef1f6',
          200: '#dfe5ec',
          300: '#c5ced9',
          400: '#8e9bb0',
          500: '#6b7a90',
          600: '#546478',
          700: '#3e4f63',
          800: '#2a3a4d',
          900: '#1f2d3d',
          950: '#131c2a',
        },
        background: {
          light: '#f6f8fb',
          dark: '#0f1318',
        },
        text: {
          light: '#1f2d3d',
          dark: '#dfe5ed',
        },
        card: {
          light: '#ffffff',
          dark: '#171d27',
        },
        border: {
          light: '#dfe5ec',
          dark: '#252e3e',
        },
        accent: {
          blue: '#5a7ec2',
          slate: '#6b7a90',
          warm: '#a68a64',
          rose: '#c0726e',
        },
      },
      fontFamily: {
        sans: ['"Plus Jakarta Sans"', '-apple-system', 'BlinkMacSystemFont', '"Segoe UI"', 'sans-serif'],
        display: ['"Outfit"', '"Plus Jakarta Sans"', 'sans-serif'],
        mono: ['"JetBrains Mono"', '"Fira Code"', 'Monaco', 'Consolas', 'monospace'],
      },
      fontSize: {
        'display-xl': ['3rem', { lineHeight: '1.2', letterSpacing: '0', fontWeight: '700' }],
        'display-lg': ['2.5rem', { lineHeight: '1.25', letterSpacing: '0', fontWeight: '700' }],
        'display': ['2rem', { lineHeight: '1.35', letterSpacing: '0', fontWeight: '650' }],
        'display-sm': ['1.6rem', { lineHeight: '1.4', letterSpacing: '0', fontWeight: '650' }],
      },
      borderRadius: {
        '4xl': '2rem',
        '5xl': '2.5rem',
      },
      boxShadow: {
        'glass': '0 8px 32px rgba(90, 126, 194, 0.04)',
        'glass-lg': '0 16px 48px rgba(90, 126, 194, 0.06)',
        'glass-dark': '0 8px 32px rgba(0, 0, 0, 0.3)',
        'glow': '0 0 20px rgba(90, 126, 194, 0.1)',
        'glow-lg': '0 0 40px rgba(90, 126, 194, 0.15)',
        'inner-glow': 'inset 0 1px 1px rgba(255, 255, 255, 0.1)',
        'card': '0 1px 3px rgba(90, 126, 194, 0.03), 0 4px 12px rgba(90, 126, 194, 0.02)',
        'card-hover': '0 4px 16px rgba(90, 126, 194, 0.06), 0 12px 32px rgba(90, 126, 194, 0.04)',
        'card-dark': '0 1px 3px rgba(0, 0, 0, 0.2), 0 4px 12px rgba(0, 0, 0, 0.15)',
        'elevated': '0 20px 60px -12px rgba(0, 0, 0, 0.12)',
      },
      animation: {
        'fade-in': 'fadeIn 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards',
        'fade-in-up': 'fadeInUp 0.7s cubic-bezier(0.16, 1, 0.3, 1) forwards',
        'fade-in-down': 'fadeInDown 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards',
        'slide-up': 'slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards',
        'slide-in-left': 'slideInLeft 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards',
        'slide-in-right': 'slideInRight 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards',
        'scale-in': 'scaleIn 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
        'float': 'float 6s ease-in-out infinite',
        'float-slow': 'float 8s ease-in-out infinite',
        'float-delayed': 'float 7s ease-in-out 2s infinite',
        'pulse-soft': 'pulseSoft 3s ease-in-out infinite',
        'spin-slow': 'spin 20s linear infinite',
        'gradient': 'gradientShift 8s ease infinite',
        'gradient-slow': 'gradientShift 12s ease infinite',
        'shimmer': 'shimmer 2s linear infinite',
        'typing': 'typing 3.5s steps(30, end), blink 0.75s step-end infinite',
        'blink': 'blink 1s step-end infinite',
        'bounce-gentle': 'bounceGentle 2s ease-in-out infinite',
        'wiggle': 'wiggle 2.5s ease-in-out infinite',
        'morph': 'morph 8s ease-in-out infinite',
        'aurora': 'aurora 15s ease-in-out infinite',
        'grid-flow': 'gridFlow 20s linear infinite',
        'border-spin': 'borderSpin 4s linear infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        fadeInUp: {
          '0%': { opacity: '0', transform: 'translateY(24px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        fadeInDown: {
          '0%': { opacity: '0', transform: 'translateY(-16px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        slideUp: {
          '0%': { transform: 'translateY(100%)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        slideInLeft: {
          '0%': { transform: 'translateX(-24px)', opacity: '0' },
          '100%': { transform: 'translateX(0)', opacity: '1' },
        },
        slideInRight: {
          '0%': { transform: 'translateX(24px)', opacity: '0' },
          '100%': { transform: 'translateX(0)', opacity: '1' },
        },
        scaleIn: {
          '0%': { transform: 'scale(0.9)', opacity: '0' },
          '100%': { transform: 'scale(1)', opacity: '1' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-12px)' },
        },
        pulseSoft: {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.7' },
        },
        gradientShift: {
          '0%, 100%': { backgroundPosition: '0% 50%' },
          '50%': { backgroundPosition: '100% 50%' },
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
        typing: {
          '0%': { width: '0' },
          '100%': { width: '100%' },
        },
        blink: {
          '0%, 100%': { borderColor: 'transparent' },
          '50%': { borderColor: 'currentColor' },
        },
        bounceGentle: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-6px)' },
        },
        wiggle: {
          '0%, 100%': { transform: 'rotate(0deg)' },
          '25%': { transform: 'rotate(-3deg)' },
          '75%': { transform: 'rotate(3deg)' },
        },
        morph: {
          '0%, 100%': { borderRadius: '60% 40% 30% 70% / 60% 30% 70% 40%' },
          '50%': { borderRadius: '30% 60% 70% 40% / 50% 60% 30% 60%' },
        },
        aurora: {
          '0%, 100%': { transform: 'translate(0, 0) rotate(0deg) scale(1)' },
          '25%': { transform: 'translate(30px, -50px) rotate(3deg) scale(1.05)' },
          '50%': { transform: 'translate(-20px, 20px) rotate(-2deg) scale(0.95)' },
          '75%': { transform: 'translate(20px, 40px) rotate(4deg) scale(1.02)' },
        },
        gridFlow: {
          '0%': { transform: 'translateY(0)' },
          '100%': { transform: 'translateY(-50%)' },
        },
        borderSpin: {
          '0%': { '--border-angle': '0deg' },
          '100%': { '--border-angle': '360deg' },
        },
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'gradient-conic': 'conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))',
        'mesh-gradient': 'radial-gradient(at 40% 20%, rgba(90, 126, 194, 0.04) 0px, transparent 50%), radial-gradient(at 80% 0%, rgba(120, 150, 190, 0.03) 0px, transparent 50%), radial-gradient(at 0% 50%, rgba(74, 109, 178, 0.02) 0px, transparent 50%)',
        'mesh-gradient-dark': 'radial-gradient(at 40% 20%, rgba(123, 161, 217, 0.02) 0px, transparent 50%), radial-gradient(at 80% 0%, rgba(120, 150, 190, 0.015) 0px, transparent 50%), radial-gradient(at 0% 50%, rgba(123, 161, 217, 0.01) 0px, transparent 50%)',
        'noise': "url(\"data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n' x='0' y='0'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.85' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='0.03'/%3E%3C/svg%3E\")",
      },
      spacing: {
        '18': '4.5rem',
        '88': '22rem',
        '128': '32rem',
      },
      transitionTimingFunction: {
        'bounce-in': 'cubic-bezier(0.68, -0.55, 0.265, 1.55)',
        'smooth': 'cubic-bezier(0.16, 1, 0.3, 1)',
      },
    },
  },
  plugins: [
    require('@tailwindcss/typography'),
  ],
}
