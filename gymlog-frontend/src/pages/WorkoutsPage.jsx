import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { workoutAPI } from '../api/axios';
import { HiOutlinePlus, HiOutlineTrash, HiOutlineEye, HiOutlineChevronLeft, HiOutlineChevronRight } from 'react-icons/hi';
import toast from 'react-hot-toast';

const WorkoutsPage = () => {
  const [workouts, setWorkouts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchWorkouts = async (p = 0) => {
    try {
      const res = await workoutAPI.getHistory(p, 10);
      setWorkouts(res.data.content);
      setTotalPages(res.data.totalPages);
      setPage(res.data.page);
    } catch { toast.error('Failed to load workouts'); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchWorkouts(); }, []);

  const handleDelete = async (id) => {
    if (!confirm('Delete this workout?')) return;
    try {
      await workoutAPI.delete(id);
      toast.success('Workout deleted');
      fetchWorkouts(page);
    } catch { toast.error('Failed to delete'); }
  };

  const statusColors = { COMPLETED: 'badge-success', IN_PROGRESS: 'badge-warning', PLANNED: 'badge-info' };
  const statusIcons = { COMPLETED: '✅', IN_PROGRESS: '🔄', PLANNED: '📋' };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="page-title">Workouts</h1>
          <p className="page-subtitle">Track and manage your workout sessions</p>
        </div>
        <Link to="/workouts/new" className="btn-primary flex items-center gap-2">
          <HiOutlinePlus className="w-5 h-5" /> New Workout
        </Link>
      </div>

      {loading ? (
        <div className="space-y-3">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="card p-4 flex items-center gap-4">
              <div className="skeleton w-12 h-12 rounded-xl" />
              <div className="flex-1">
                <div className="skeleton h-5 w-40 mb-2" />
                <div className="skeleton h-3 w-64" />
              </div>
            </div>
          ))}
        </div>
      ) : workouts.length === 0 ? (
        <div className="card p-12 text-center animate-fade-in">
          <div className="text-6xl mb-4">🏋️‍♂️</div>
          <h3 className="text-xl font-semibold text-surface-900 dark:text-white mb-2">No workouts yet</h3>
          <p className="text-surface-400 mb-6 max-w-md mx-auto">Start your fitness journey by logging your first workout. Track your exercises, sets, reps, and watch your progress grow!</p>
          <Link to="/workouts/new" className="btn-primary inline-flex items-center gap-2">
            <HiOutlinePlus className="w-5 h-5" /> Create Your First Workout
          </Link>
        </div>
      ) : (
        <div className="space-y-3">
          {workouts.map((w, i) => (
            <div key={w.id} className="card p-4 flex items-center justify-between hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200 animate-slide-up" style={{ animationDelay: `${i * 40}ms` }}>
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 rounded-xl bg-primary-100 dark:bg-primary-500/10 flex items-center justify-center">
                  <div className="text-center">
                    <span className="text-primary-600 dark:text-primary-400 font-bold text-sm block leading-none">
                      {new Date(w.workoutDate).getDate()}
                    </span>
                    <span className="text-primary-400 dark:text-primary-500 text-[10px] uppercase">
                      {new Date(w.workoutDate).toLocaleString('default', { month: 'short' })}
                    </span>
                  </div>
                </div>
                <div>
                  <h3 className="font-semibold text-surface-900 dark:text-white">{w.name}</h3>
                  <p className="text-sm text-surface-500">
                    {w.totalExercises} exercises · {w.totalSets} sets · {Math.round(w.totalVolume).toLocaleString()} kg
                    {w.durationMinutes && ` · ${w.durationMinutes} min`}
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <span className={statusColors[w.status] || 'badge-info'}>{w.status}</span>
                <Link to={`/workouts/${w.id}`} className="p-2 rounded-lg hover:bg-surface-100 dark:hover:bg-surface-700 text-surface-500 hover:text-primary-500 transition-all" title="View details">
                  <HiOutlineEye className="w-5 h-5" />
                </Link>
                <button onClick={() => handleDelete(w.id)} className="p-2 rounded-lg hover:bg-red-50 dark:hover:bg-red-500/10 text-surface-400 hover:text-red-500 transition-all" title="Delete workout">
                  <HiOutlineTrash className="w-5 h-5" />
                </button>
              </div>
            </div>
          ))}

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center items-center gap-2 pt-4">
              <button
                onClick={() => fetchWorkouts(Math.max(0, page - 1))}
                disabled={page === 0}
                className="p-2 rounded-xl text-surface-500 hover:bg-surface-100 dark:hover:bg-surface-800 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
              >
                <HiOutlineChevronLeft className="w-5 h-5" />
              </button>
              {[...Array(totalPages)].map((_, i) => (
                <button key={i} onClick={() => fetchWorkouts(i)}
                  className={`w-10 h-10 rounded-xl text-sm font-medium transition-all ${
                    page === i ? 'bg-primary-500 text-white shadow-lg shadow-primary-500/25' : 'bg-surface-100 dark:bg-surface-800 text-surface-600 dark:text-surface-400 hover:bg-surface-200 dark:hover:bg-surface-700'
                  }`}>
                  {i + 1}
                </button>
              ))}
              <button
                onClick={() => fetchWorkouts(Math.min(totalPages - 1, page + 1))}
                disabled={page === totalPages - 1}
                className="p-2 rounded-xl text-surface-500 hover:bg-surface-100 dark:hover:bg-surface-800 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
              >
                <HiOutlineChevronRight className="w-5 h-5" />
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default WorkoutsPage;
