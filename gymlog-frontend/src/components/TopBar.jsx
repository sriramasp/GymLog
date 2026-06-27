import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { HiOutlineMenu, HiOutlineSun, HiOutlineMoon } from 'react-icons/hi';

const getGreeting = () => {
  const hour = new Date().getHours();
  if (hour < 12) return 'Good morning';
  if (hour < 17) return 'Good afternoon';
  return 'Good evening';
};

const TopBar = ({ onMenuClick }) => {
  const { user } = useAuth();
  const { darkMode, toggleTheme } = useTheme();

  return (
    <header className="sticky top-0 z-20 bg-white/80 dark:bg-surface-900/80 backdrop-blur-xl border-b border-surface-200 dark:border-surface-800">
      <div className="flex items-center justify-between px-4 md:px-6 h-16">
        {/* Mobile menu button */}
        <button
          onClick={onMenuClick}
          className="lg:hidden p-2 rounded-xl text-surface-500 hover:bg-surface-100 dark:hover:bg-surface-800 transition-all"
        >
          <HiOutlineMenu className="w-5 h-5" />
        </button>

        {/* Greeting */}
        <div className="hidden lg:block">
          <p className="text-sm font-medium text-surface-900 dark:text-white">
            {getGreeting()}, <span className="text-primary-500">{user?.firstName || 'there'}</span> 👋
          </p>
        </div>

        {/* Right actions */}
        <div className="flex items-center gap-2">
          {/* Theme toggle */}
          <button
            onClick={toggleTheme}
            className="relative p-2.5 rounded-xl text-surface-500 hover:bg-surface-100 dark:hover:bg-surface-800 hover:text-surface-900 dark:hover:text-white transition-all overflow-hidden"
            title={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}
          >
            <div className="relative w-5 h-5">
              <HiOutlineSun className={`w-5 h-5 absolute inset-0 transition-all duration-300 ${darkMode ? 'rotate-0 scale-100 opacity-100' : 'rotate-90 scale-0 opacity-0'}`} />
              <HiOutlineMoon className={`w-5 h-5 absolute inset-0 transition-all duration-300 ${darkMode ? '-rotate-90 scale-0 opacity-0' : 'rotate-0 scale-100 opacity-100'}`} />
            </div>
          </button>
        </div>
      </div>
    </header>
  );
};

export default TopBar;
