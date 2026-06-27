import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { HiOutlineMail, HiOutlineLockClosed, HiOutlineEye, HiOutlineEyeOff } from 'react-icons/hi';
import toast from 'react-hot-toast';

const FloatingShape = ({ className }) => (
  <div className={`absolute rounded-full opacity-20 dark:opacity-10 ${className}`} />
);

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const { darkMode, toggleTheme } = useTheme();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password) return toast.error('Please fill in all fields');
    setLoading(true);
    try {
      await login(email, password);
      toast.success('Welcome back! 💪');
      navigate('/');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-surface-50 via-primary-50/30 to-accent-50/20 dark:from-surface-950 dark:via-surface-900 dark:to-surface-950 p-4 overflow-hidden relative">
      {/* Animated floating background shapes */}
      <FloatingShape className="w-72 h-72 bg-primary-400 dark:bg-primary-600 -top-20 -left-20 blur-3xl animate-float" />
      <FloatingShape className="w-96 h-96 bg-accent-400 dark:bg-accent-600 -bottom-32 -right-32 blur-3xl animate-float-delayed" />
      <FloatingShape className="w-48 h-48 bg-purple-400 dark:bg-purple-600 top-1/3 right-1/4 blur-2xl animate-float-slow" />

      <div className="w-full max-w-md animate-slide-up relative z-10">
        {/* Logo */}
        <div className="text-center mb-8">
          <div className="relative inline-block">
            <div className="w-16 h-16 rounded-2xl bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center mx-auto mb-4 shadow-xl shadow-primary-500/30 glow-primary">
              <span className="text-white font-bold text-2xl">G</span>
            </div>
          </div>
          <h1 className="text-3xl font-bold text-surface-900 dark:text-white">Welcome Back</h1>
          <p className="text-surface-500 dark:text-surface-400 mt-1">Sign in to your GymLog account</p>
        </div>

        {/* Form card */}
        <div className="card-glass p-8">
          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="label">Email</label>
              <div className="relative">
                <HiOutlineMail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-surface-400" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="input pl-10"
                  placeholder="you@example.com"
                  id="login-email"
                />
              </div>
            </div>

            <div>
              <label className="label">Password</label>
              <div className="relative">
                <HiOutlineLockClosed className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-surface-400" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="input pl-10 pr-10"
                  placeholder="••••••••"
                  id="login-password"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-surface-400 hover:text-surface-600 transition-colors"
                >
                  {showPassword ? <HiOutlineEyeOff className="w-5 h-5" /> : <HiOutlineEye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn-primary w-full flex items-center justify-center gap-2 py-3"
              id="login-submit"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                'Sign In'
              )}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-surface-500 dark:text-surface-400">
              Don't have an account?{' '}
              <Link to="/register" className="text-primary-500 hover:text-primary-600 font-semibold transition-colors">
                Sign up
              </Link>
            </p>
          </div>

          {/* Demo credentials */}
          <div className="mt-4 p-3 rounded-xl bg-primary-50 dark:bg-primary-500/10 border border-primary-200 dark:border-primary-500/20">
            <p className="text-xs text-primary-700 dark:text-primary-300 font-semibold mb-1">Demo Credentials:</p>
            <p className="text-xs text-primary-600 dark:text-primary-400">User: demo@gymlog.com / Demo@123</p>
            <p className="text-xs text-primary-600 dark:text-primary-400">Admin: admin@gymlog.com / Admin@123</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
