import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  HiOutlineHome, HiOutlineClipboardList, HiOutlineLightningBolt,
  HiOutlineChartBar, HiOutlineScale, HiOutlineFlag, HiOutlineUser,
  HiOutlineCog, HiOutlineLogout, HiOutlineX
} from 'react-icons/hi';
import { GiMeal } from 'react-icons/gi';

const navItems = [
  { path: '/', label: 'Dashboard', icon: HiOutlineHome },
  { path: '/workouts', label: 'Workouts', icon: HiOutlineClipboardList },
  { path: '/exercises', label: 'Exercises', icon: HiOutlineLightningBolt },
  { path: '/nutrition', label: 'Nutrition', icon: GiMeal },
  { path: '/body-progress', label: 'Body Progress', icon: HiOutlineScale },
  { path: '/goals', label: 'Goals', icon: HiOutlineFlag },
  { path: '/analytics', label: 'Analytics', icon: HiOutlineChartBar },
];

const Sidebar = ({ isOpen, onClose }) => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <aside className={`
      fixed inset-y-0 left-0 z-40 w-64 bg-white dark:bg-surface-900
      border-r border-surface-200 dark:border-surface-800
      transform transition-transform duration-300 ease-in-out
      lg:translate-x-0 ${isOpen ? 'translate-x-0' : '-translate-x-full'}
    `}>
      <div className="flex flex-col h-full">
        {/* Logo */}
        <div className="flex items-center justify-between p-5 border-b border-surface-200 dark:border-surface-800">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center shadow-lg shadow-primary-500/20">
              <span className="text-white font-bold text-lg">G</span>
            </div>
            <span className="text-xl font-bold text-surface-900 dark:text-white tracking-tight">GymLog</span>
          </div>
          <button onClick={onClose} className="lg:hidden p-1.5 rounded-lg text-surface-500 hover:text-surface-900 dark:hover:text-white hover:bg-surface-100 dark:hover:bg-surface-800 transition-all">
            <HiOutlineX className="w-5 h-5" />
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          {navItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.path === '/'}
              onClick={onClose}
              className={({ isActive }) => `
                relative flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 group
                ${isActive
                  ? 'bg-primary-50 dark:bg-primary-500/10 text-primary-600 dark:text-primary-400 shadow-sm'
                  : 'text-surface-600 dark:text-surface-400 hover:bg-surface-100 dark:hover:bg-surface-800 hover:text-surface-900 dark:hover:text-white'
                }
              `}
            >
              {({ isActive }) => (
                <>
                  {isActive && (
                    <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-5 bg-primary-500 rounded-r-full" />
                  )}
                  <item.icon className={`w-5 h-5 flex-shrink-0 transition-transform duration-200 group-hover:scale-110 ${isActive ? '' : ''}`} />
                  {item.label}
                </>
              )}
            </NavLink>
          ))}

          {isAdmin && (
            <NavLink
              to="/admin"
              onClick={onClose}
              className={({ isActive }) => `
                relative flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 group mt-2
                ${isActive
                  ? 'bg-amber-50 dark:bg-amber-500/10 text-amber-600 dark:text-amber-400 shadow-sm'
                  : 'text-surface-600 dark:text-surface-400 hover:bg-surface-100 dark:hover:bg-surface-800'
                }
              `}
            >
              {({ isActive }) => (
                <>
                  {isActive && (
                    <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-5 bg-amber-500 rounded-r-full" />
                  )}
                  <HiOutlineCog className="w-5 h-5 transition-transform duration-200 group-hover:rotate-90" />
                  Admin Panel
                </>
              )}
            </NavLink>
          )}
        </nav>

        {/* User section */}
        <div className="border-t border-surface-200 dark:border-surface-800 p-3 space-y-1">
          <NavLink
            to="/profile"
            onClick={onClose}
            className={({ isActive }) => `
              flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 group
              ${isActive
                ? 'bg-primary-50 dark:bg-primary-500/10 text-primary-600 dark:text-primary-400'
                : 'text-surface-600 dark:text-surface-400 hover:bg-surface-100 dark:hover:bg-surface-800'
              }
            `}
          >
            <div className="w-8 h-8 rounded-full bg-gradient-to-br from-primary-400 to-accent-400 flex items-center justify-center shadow-sm group-hover:shadow-md transition-shadow">
              <span className="text-white text-xs font-bold">
                {user?.firstName?.[0]}{user?.lastName?.[0]}
              </span>
            </div>
            <div className="flex-1 min-w-0">
              <p className="truncate font-medium">{user?.firstName} {user?.lastName}</p>
              <p className="truncate text-xs text-surface-400">{user?.email}</p>
            </div>
          </NavLink>
          <button
            onClick={handleLogout}
            className="flex items-center gap-3 w-full px-3 py-2.5 rounded-xl text-sm font-medium text-red-500 hover:bg-red-50 dark:hover:bg-red-500/10 transition-all duration-200 group"
          >
            <HiOutlineLogout className="w-5 h-5 transition-transform duration-200 group-hover:-translate-x-0.5" />
            Logout
          </button>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
