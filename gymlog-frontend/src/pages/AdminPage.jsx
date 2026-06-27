import { useState, useEffect } from 'react';
import { adminAPI } from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { Navigate } from 'react-router-dom';
import toast from 'react-hot-toast';

const AdminPage = () => {
  const { isAdmin } = useAuth();
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    if (!isAdmin) return;
    const fetchData = async () => {
      try {
        const [statsRes, usersRes] = await Promise.all([adminAPI.getStats(), adminAPI.getUsers(page)]);
        setStats(statsRes.data);
        setUsers(usersRes.data.content);
        setTotalPages(usersRes.data.totalPages);
      } catch {
        toast.error('Failed to load admin data');
      }
    };
    fetchData();
  }, [page, isAdmin]);

  if (!isAdmin) return <Navigate to="/" replace />;

  const handleToggle = async (id) => {
    try {
      await adminAPI.toggleUserStatus(id);
      toast.success('User status updated');
      // Trigger re-fetch by toggling a refresh counter
      setPage(p => p);
      // Force re-render by refetching
      const [statsRes, usersRes] = await Promise.all([adminAPI.getStats(), adminAPI.getUsers(page)]);
      setStats(statsRes.data);
      setUsers(usersRes.data.content);
      setTotalPages(usersRes.data.totalPages);
    } catch { toast.error('Failed'); }
  };

  return (
    <div className="space-y-6">
      <div><h1 className="page-title">Admin Panel</h1><p className="page-subtitle">Platform management and statistics</p></div>

      {/* Stats */}
      {stats && (
        <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
          <div className="card p-4 text-center"><p className="text-2xl font-bold text-primary-500">{stats.totalUsers}</p><p className="text-xs text-surface-500">Total Users</p></div>
          <div className="card p-4 text-center"><p className="text-2xl font-bold text-accent-500">{stats.activeUsers}</p><p className="text-xs text-surface-500">Active Users</p></div>
          <div className="card p-4 text-center"><p className="text-2xl font-bold text-blue-500">{stats.totalWorkouts}</p><p className="text-xs text-surface-500">Total Workouts</p></div>
          <div className="card p-4 text-center"><p className="text-2xl font-bold text-amber-500">{stats.totalExercises}</p><p className="text-xs text-surface-500">Exercises</p></div>
          <div className="card p-4 text-center"><p className="text-2xl font-bold text-red-500">{stats.totalNutritionLogs}</p><p className="text-xs text-surface-500">Nutrition Logs</p></div>
        </div>
      )}

      {/* Users table */}
      <div className="card overflow-hidden">
        <div className="p-4 border-b border-surface-200 dark:border-surface-700">
          <h3 className="text-lg font-semibold text-surface-900 dark:text-white">User Management</h3>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-surface-50 dark:bg-surface-800">
              <tr className="text-left text-surface-500">
                <th className="p-3 font-medium">Name</th><th className="p-3 font-medium">Email</th><th className="p-3 font-medium">Role</th>
                <th className="p-3 font-medium">Status</th><th className="p-3 font-medium">Joined</th><th className="p-3 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(u => (
                <tr key={u.id} className="border-t border-surface-200 dark:border-surface-700">
                  <td className="p-3 text-surface-900 dark:text-white font-medium">{u.firstName} {u.lastName}</td>
                  <td className="p-3">{u.email}</td>
                  <td className="p-3"><span className={u.role === 'ADMIN' ? 'badge-warning' : 'badge-info'}>{u.role}</span></td>
                  <td className="p-3"><span className={u.active ? 'badge-success' : 'badge-danger'}>{u.active ? 'Active' : 'Inactive'}</span></td>
                  <td className="p-3 text-surface-500">{u.createdAt?.split('T')[0]}</td>
                  <td className="p-3">
                    <button onClick={() => handleToggle(u.id)} className={`text-xs font-medium px-3 py-1 rounded-lg transition-colors ${u.active ? 'text-red-500 hover:bg-red-50 dark:hover:bg-red-500/10' : 'text-accent-500 hover:bg-accent-50 dark:hover:bg-accent-500/10'}`}>
                      {u.active ? 'Deactivate' : 'Activate'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AdminPage;
