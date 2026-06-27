import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { userAPI, authAPI } from '../api/axios';
import toast from 'react-hot-toast';

const ProfilePage = () => {
  const { user, updateUser } = useAuth();
  const [form, setForm] = useState({ firstName: '', lastName: '', height: '', currentWeight: '', gender: '', dailyCalorieTarget: '' });
  const [pwForm, setPwForm] = useState({ currentPassword: '', newPassword: '' });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      setForm({ firstName: user.firstName || '', lastName: user.lastName || '', height: user.height || '', currentWeight: user.currentWeight || '', gender: user.gender || '', dailyCalorieTarget: user.dailyCalorieTarget || 2000 });
    }
  }, [user]);

  const handleProfileUpdate = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const payload = { ...form, height: form.height ? Number(form.height) : null, currentWeight: form.currentWeight ? Number(form.currentWeight) : null, dailyCalorieTarget: form.dailyCalorieTarget ? Number(form.dailyCalorieTarget) : null };
      const res = await userAPI.updateProfile(payload);
      updateUser(res.data);
      toast.success('Profile updated!');
    } catch { toast.error('Update failed'); }
    finally { setLoading(false); }
  };

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    if (pwForm.newPassword.length < 6) return toast.error('Password must be at least 6 characters');
    try {
      await authAPI.changePassword(pwForm);
      toast.success('Password changed!');
      setPwForm({ currentPassword: '', newPassword: '' });
    } catch (err) { toast.error(err.response?.data?.message || 'Failed to change password'); }
  };

  return (
    <div className="space-y-6 max-w-2xl">
      <div><h1 className="page-title">Profile</h1><p className="page-subtitle">Manage your account settings</p></div>

      {/* Profile info */}
      <form onSubmit={handleProfileUpdate} className="card p-6 space-y-4">
        <h3 className="text-lg font-semibold text-surface-900 dark:text-white">Personal Information</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div><label className="label">First Name</label><input value={form.firstName} onChange={e => setForm({...form, firstName: e.target.value})} className="input" /></div>
          <div><label className="label">Last Name</label><input value={form.lastName} onChange={e => setForm({...form, lastName: e.target.value})} className="input" /></div>
          <div><label className="label">Height (cm)</label><input type="number" value={form.height} onChange={e => setForm({...form, height: e.target.value})} className="input" /></div>
          <div><label className="label">Weight (kg)</label><input type="number" value={form.currentWeight} onChange={e => setForm({...form, currentWeight: e.target.value})} className="input" /></div>
          <div><label className="label">Gender</label>
            <select value={form.gender} onChange={e => setForm({...form, gender: e.target.value})} className="input">
              <option value="">Select</option><option value="Male">Male</option><option value="Female">Female</option><option value="Other">Other</option>
            </select>
          </div>
          <div><label className="label">Daily Calorie Target</label><input type="number" value={form.dailyCalorieTarget} onChange={e => setForm({...form, dailyCalorieTarget: e.target.value})} className="input" /></div>
        </div>
        <button type="submit" disabled={loading} className="btn-primary">{loading ? 'Saving...' : 'Update Profile'}</button>
      </form>

      {/* Change password */}
      <form onSubmit={handlePasswordChange} className="card p-6 space-y-4">
        <h3 className="text-lg font-semibold text-surface-900 dark:text-white">Change Password</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div><label className="label">Current Password</label><input type="password" value={pwForm.currentPassword} onChange={e => setPwForm({...pwForm, currentPassword: e.target.value})} className="input" /></div>
          <div><label className="label">New Password</label><input type="password" value={pwForm.newPassword} onChange={e => setPwForm({...pwForm, newPassword: e.target.value})} className="input" placeholder="Min. 6 characters" /></div>
        </div>
        <button type="submit" className="btn-secondary">Change Password</button>
      </form>

      {/* Account info */}
      <div className="card p-6">
        <h3 className="text-lg font-semibold text-surface-900 dark:text-white mb-3">Account</h3>
        <div className="space-y-2 text-sm">
          <p className="text-surface-500">Email: <span className="text-surface-900 dark:text-white font-medium">{user?.email}</span></p>
          <p className="text-surface-500">Role: <span className="badge-info">{user?.role}</span></p>
          <p className="text-surface-500">Member since: <span className="text-surface-900 dark:text-white">{user?.createdAt?.split('T')[0]}</span></p>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
