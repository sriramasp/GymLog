import { useState, useEffect } from 'react';
import { goalAPI } from '../api/axios';
import { HiOutlinePlus, HiOutlineTrash, HiOutlineCheck, HiOutlineBan } from 'react-icons/hi';
import toast from 'react-hot-toast';

const GoalsPage = () => {
  const [goals, setGoals] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ goalType: 'WEIGHT', title: '', description: '', targetValue: '', currentValue: '', unit: 'kg', startDate: new Date().toISOString().split('T')[0], targetDate: '' });
  const [loading, setLoading] = useState(false);
  const [progressEdit, setProgressEdit] = useState(null); // { goalId, value }

  const fetchGoals = async () => {
    try { const res = await goalAPI.getAll(0, 50); setGoals(res.data.content); } catch {}
  };
  useEffect(() => { fetchGoals(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.title || !form.targetValue) return toast.error('Title and target are required');
    setLoading(true);
    try {
      await goalAPI.create({ ...form, targetValue: Number(form.targetValue), currentValue: Number(form.currentValue) || 0 });
      toast.success('Goal created! 🎯');
      setShowForm(false);
      setForm({ goalType: 'WEIGHT', title: '', description: '', targetValue: '', currentValue: '', unit: 'kg', startDate: new Date().toISOString().split('T')[0], targetDate: '' });
      fetchGoals();
    } catch { toast.error('Failed to create goal'); }
    finally { setLoading(false); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this goal?')) return;
    try { await goalAPI.delete(id); toast.success('Goal deleted'); fetchGoals(); } catch { toast.error('Failed'); }
  };

  const handleUpdateProgress = async (goalId) => {
    if (progressEdit?.goalId !== goalId || !progressEdit?.value) return;
    try {
      await goalAPI.updateProgress(goalId, Number(progressEdit.value));
      toast.success('Progress updated! 💪');
      setProgressEdit(null);
      fetchGoals();
    } catch { toast.error('Failed to update progress'); }
  };

  const handleStatusChange = async (goal, newStatus) => {
    try {
      await goalAPI.update(goal.id, { ...goal, status: newStatus });
      toast.success(`Goal marked as ${newStatus.toLowerCase()}`);
      fetchGoals();
    } catch { toast.error('Failed to update status'); }
  };

  const typeIcons = { WEIGHT: '⚖️', WORKOUT_FREQUENCY: '🏋️', NUTRITION: '🥗', STRENGTH: '💪' };
  const statusColors = { ACTIVE: 'badge-success', COMPLETED: 'badge-info', ABANDONED: 'badge-danger' };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div><h1 className="page-title">Goals</h1><p className="page-subtitle">Set and track your fitness goals</p></div>
        <button onClick={() => setShowForm(!showForm)} className="btn-primary flex items-center gap-2"><HiOutlinePlus className="w-5 h-5" /> New Goal</button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="card p-6 space-y-4 animate-slide-up">
          <h3 className="font-semibold text-surface-900 dark:text-white">Create Goal</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div><label className="label">Goal Type</label>
              <select value={form.goalType} onChange={e => setForm({...form, goalType: e.target.value})} className="input">
                <option value="WEIGHT">Weight</option><option value="WORKOUT_FREQUENCY">Workout Frequency</option>
                <option value="NUTRITION">Nutrition</option><option value="STRENGTH">Strength</option>
              </select>
            </div>
            <div><label className="label">Title *</label><input value={form.title} onChange={e => setForm({...form, title: e.target.value})} className="input" placeholder="e.g. Lose 5kg" /></div>
          </div>
          <div><label className="label">Description</label><textarea value={form.description} onChange={e => setForm({...form, description: e.target.value})} className="input" rows={2} /></div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div><label className="label">Target *</label><input type="number" value={form.targetValue} onChange={e => setForm({...form, targetValue: e.target.value})} className="input" /></div>
            <div><label className="label">Current</label><input type="number" value={form.currentValue} onChange={e => setForm({...form, currentValue: e.target.value})} className="input" /></div>
            <div><label className="label">Unit</label><input value={form.unit} onChange={e => setForm({...form, unit: e.target.value})} className="input" placeholder="kg" /></div>
            <div><label className="label">Target Date</label><input type="date" value={form.targetDate} onChange={e => setForm({...form, targetDate: e.target.value})} className="input" /></div>
          </div>
          <div className="flex gap-3">
            <button type="submit" disabled={loading} className="btn-primary">{loading ? 'Saving...' : 'Create Goal'}</button>
            <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">Cancel</button>
          </div>
        </form>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {goals.length === 0 ? (
          <div className="col-span-2 card p-12 text-center">
            <p className="text-4xl mb-3">🎯</p>
            <p className="text-lg font-medium text-surface-900 dark:text-white mb-1">No goals yet</p>
            <p className="text-surface-400 text-sm">Create one to start tracking your fitness journey!</p>
          </div>
        ) : goals.map(goal => (
          <div key={goal.id} className="card p-5 space-y-3 hover:shadow-lg transition-shadow">
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-2">
                <span className="text-2xl">{typeIcons[goal.goalType]}</span>
                <div>
                  <h3 className="font-semibold text-surface-900 dark:text-white">{goal.title}</h3>
                  <p className="text-xs text-surface-500">{goal.goalType.replace('_', ' ')}</p>
                </div>
              </div>
              <div className="flex items-center gap-1.5">
                <span className={statusColors[goal.status]}>{goal.status}</span>
                {goal.status === 'ACTIVE' && (
                  <>
                    <button onClick={() => handleStatusChange(goal, 'COMPLETED')} className="p-1 rounded hover:bg-accent-50 dark:hover:bg-accent-500/10 text-accent-500 transition-colors" title="Mark completed">
                      <HiOutlineCheck className="w-4 h-4" />
                    </button>
                    <button onClick={() => handleStatusChange(goal, 'ABANDONED')} className="p-1 rounded hover:bg-amber-50 dark:hover:bg-amber-500/10 text-amber-500 transition-colors" title="Abandon goal">
                      <HiOutlineBan className="w-4 h-4" />
                    </button>
                  </>
                )}
                <button onClick={() => handleDelete(goal.id)} className="p-1 rounded hover:bg-red-50 dark:hover:bg-red-500/10 text-red-500 transition-colors" title="Delete goal">
                  <HiOutlineTrash className="w-4 h-4" />
                </button>
              </div>
            </div>
            {goal.description && <p className="text-sm text-surface-500">{goal.description}</p>}
            <div>
              <div className="flex justify-between text-sm mb-1">
                <span className="text-surface-500">{goal.currentValue} / {goal.targetValue} {goal.unit}</span>
                <span className="font-bold text-primary-500">{Math.round(goal.progressPercentage)}%</span>
              </div>
              <div className="w-full bg-surface-200 dark:bg-surface-700 rounded-full h-2.5 overflow-hidden">
                <div className="bg-gradient-to-r from-primary-500 to-accent-500 h-2.5 rounded-full transition-all duration-700" style={{ width: `${Math.min(goal.progressPercentage, 100)}%` }} />
              </div>
            </div>

            {/* Update progress inline */}
            {goal.status === 'ACTIVE' && (
              <div className="flex items-center gap-2 pt-1">
                {progressEdit?.goalId === goal.id ? (
                  <>
                    <input
                      type="number"
                      step="any"
                      value={progressEdit.value}
                      onChange={(e) => setProgressEdit({ ...progressEdit, value: e.target.value })}
                      className="input py-1.5 text-sm flex-1"
                      placeholder={`Current: ${goal.currentValue}`}
                      autoFocus
                    />
                    <button onClick={() => handleUpdateProgress(goal.id)} className="btn-primary py-1.5 px-3 text-xs">Save</button>
                    <button onClick={() => setProgressEdit(null)} className="btn-secondary py-1.5 px-3 text-xs">Cancel</button>
                  </>
                ) : (
                  <button
                    onClick={() => setProgressEdit({ goalId: goal.id, value: goal.currentValue })}
                    className="text-xs text-primary-500 hover:text-primary-600 font-medium transition-colors"
                  >
                    📝 Update Progress
                  </button>
                )}
              </div>
            )}

            {goal.targetDate && <p className="text-xs text-surface-400">Target: {goal.targetDate}</p>}
          </div>
        ))}
      </div>
    </div>
  );
};

export default GoalsPage;
