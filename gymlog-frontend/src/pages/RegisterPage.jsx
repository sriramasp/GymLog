import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { HiOutlineMail, HiOutlineLockClosed, HiOutlineUser } from 'react-icons/hi';
import toast from 'react-hot-toast';

const RegisterPage = () => {
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.firstName || !form.lastName || !form.email || !form.password) {
      return toast.error('Please fill in all fields');
    }
    if (form.password.length < 6) {
      return toast.error('Password must be at least 6 characters');
    }
    setLoading(true);
    try {
      await register(form);
      toast.success('Account created! Welcome to GymLog! 🎉');
      navigate('/');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-surface-50 via-accent-50/30 to-primary-50/20 dark:from-surface-950 dark:via-surface-900 dark:to-surface-950 p-4 overflow-hidden relative">
      {/* Animated floating background shapes */}
      <div className="absolute w-72 h-72 bg-accent-400 dark:bg-accent-600 -top-20 -right-20 rounded-full opacity-20 dark:opacity-10 blur-3xl animate-float" />
      <div className="absolute w-96 h-96 bg-primary-400 dark:bg-primary-600 -bottom-32 -left-32 rounded-full opacity-20 dark:opacity-10 blur-3xl animate-float-delayed" />

      <div className="w-full max-w-md animate-slide-up relative z-10">
        <div className="text-center mb-8">
          <div className="w-16 h-16 rounded-2xl bg-gradient-to-br from-accent-500 to-primary-600 flex items-center justify-center mx-auto mb-4 shadow-lg shadow-accent-500/30">
            <span className="text-white font-bold text-2xl">G</span>
          </div>
          <h1 className="text-3xl font-bold text-surface-900 dark:text-white">Create Account</h1>
          <p className="text-surface-500 dark:text-surface-400 mt-1">Start your fitness journey today</p>
        </div>

        <div className="card-glass p-8">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="label">First Name</label>
                <div className="relative">
                  <HiOutlineUser className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-surface-400" />
                  <input name="firstName" value={form.firstName} onChange={handleChange}
                    className="input pl-10" placeholder="John" id="register-firstname" />
                </div>
              </div>
              <div>
                <label className="label">Last Name</label>
                <input name="lastName" value={form.lastName} onChange={handleChange}
                  className="input" placeholder="Doe" id="register-lastname" />
              </div>
            </div>

            <div>
              <label className="label">Email</label>
              <div className="relative">
                <HiOutlineMail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-surface-400" />
                <input type="email" name="email" value={form.email} onChange={handleChange}
                  className="input pl-10" placeholder="you@example.com" id="register-email" />
              </div>
            </div>

            <div>
              <label className="label">Password</label>
              <div className="relative">
                <HiOutlineLockClosed className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-surface-400" />
                <input type="password" name="password" value={form.password} onChange={handleChange}
                  className="input pl-10" placeholder="Min. 6 characters" id="register-password" />
              </div>
            </div>

            <button type="submit" disabled={loading} className="btn-primary w-full py-3" id="register-submit">
              {loading ? <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin mx-auto" /> : 'Create Account'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-surface-500 dark:text-surface-400">
              Already have an account?{' '}
              <Link to="/login" className="text-primary-500 hover:text-primary-600 font-medium">Sign in</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
