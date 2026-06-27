import { useState, useEffect } from 'react';
import { nutritionAPI } from '../api/axios';
import { HiOutlinePlus, HiOutlineTrash } from 'react-icons/hi';
import toast from 'react-hot-toast';

const NutritionPage = () => {
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [logs, setLogs] = useState([]);
  const [summary, setSummary] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ foodName: '', calories: '', protein: '', carbs: '', fat: '', mealType: 'LUNCH', servingSize: '', servingUnit: 'g' });
  const [loading, setLoading] = useState(false);

  const fetchData = async () => {
    try {
      const [logsRes, sumRes] = await Promise.all([nutritionAPI.getByDate(date), nutritionAPI.getDailySummary(date)]);
      setLogs(logsRes.data);
      setSummary(sumRes.data);
    } catch { toast.error('Failed to load nutrition data'); }
  };

  useEffect(() => { fetchData(); }, [date]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.foodName || !form.calories) return toast.error('Food name and calories are required');
    setLoading(true);
    try {
      await nutritionAPI.create({ ...form, calories: Number(form.calories), protein: Number(form.protein) || 0, carbs: Number(form.carbs) || 0, fat: Number(form.fat) || 0, servingSize: Number(form.servingSize) || null, logDate: date });
      toast.success('Food logged!');
      setShowForm(false);
      setForm({ foodName: '', calories: '', protein: '', carbs: '', fat: '', mealType: 'LUNCH', servingSize: '', servingUnit: 'g' });
      fetchData();
    } catch { toast.error('Failed to log food'); }
    finally { setLoading(false); }
  };

  const handleDelete = async (id) => {
    try { await nutritionAPI.delete(id); toast.success('Deleted'); fetchData(); } catch { toast.error('Failed'); }
  };

  const mealTypeColors = { BREAKFAST: 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400', LUNCH: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400', DINNER: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400', SNACK: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400' };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="page-title">Nutrition</h1>
          <p className="page-subtitle">Track your daily food intake</p>
        </div>
        <button onClick={() => setShowForm(!showForm)} className="btn-primary flex items-center gap-2">
          <HiOutlinePlus className="w-5 h-5" /> Log Food
        </button>
      </div>

      {/* Date picker */}
      <input type="date" value={date} onChange={e => setDate(e.target.value)} className="input max-w-xs" />

      {/* Daily summary */}
      {summary && (
        <div className="grid grid-cols-2 md:grid-cols-5 gap-3">
          <div className="card p-4 text-center"><p className="text-xl font-bold text-primary-500">{Math.round(summary.totalCalories)}</p><p className="text-xs text-surface-500">Calories</p></div>
          <div className="card p-4 text-center"><p className="text-xl font-bold text-blue-500">{Math.round(summary.totalProtein)}g</p><p className="text-xs text-surface-500">Protein</p></div>
          <div className="card p-4 text-center"><p className="text-xl font-bold text-amber-500">{Math.round(summary.totalCarbs)}g</p><p className="text-xs text-surface-500">Carbs</p></div>
          <div className="card p-4 text-center"><p className="text-xl font-bold text-red-500">{Math.round(summary.totalFat)}g</p><p className="text-xs text-surface-500">Fat</p></div>
          <div className="card p-4 text-center"><p className="text-xl font-bold text-accent-500">{summary.caloriePercentage}%</p><p className="text-xs text-surface-500">of {summary.calorieTarget} target</p></div>
        </div>
      )}

      {/* Add food form */}
      {showForm && (
        <form onSubmit={handleSubmit} className="card p-6 space-y-4 animate-slide-up">
          <h3 className="font-semibold text-surface-900 dark:text-white">Add Food Entry</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div><label className="label">Food Name *</label><input value={form.foodName} onChange={e => setForm({...form, foodName: e.target.value})} className="input" placeholder="e.g. Chicken Breast" /></div>
            <div><label className="label">Meal Type</label>
              <select value={form.mealType} onChange={e => setForm({...form, mealType: e.target.value})} className="input">
                <option value="BREAKFAST">Breakfast</option><option value="LUNCH">Lunch</option><option value="DINNER">Dinner</option><option value="SNACK">Snack</option>
              </select>
            </div>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div><label className="label">Calories *</label><input type="number" value={form.calories} onChange={e => setForm({...form, calories: e.target.value})} className="input" placeholder="250" /></div>
            <div><label className="label">Protein (g)</label><input type="number" value={form.protein} onChange={e => setForm({...form, protein: e.target.value})} className="input" placeholder="30" /></div>
            <div><label className="label">Carbs (g)</label><input type="number" value={form.carbs} onChange={e => setForm({...form, carbs: e.target.value})} className="input" placeholder="0" /></div>
            <div><label className="label">Fat (g)</label><input type="number" value={form.fat} onChange={e => setForm({...form, fat: e.target.value})} className="input" placeholder="5" /></div>
          </div>
          <div className="flex gap-3">
            <button type="submit" disabled={loading} className="btn-primary">{loading ? 'Saving...' : 'Add Entry'}</button>
            <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">Cancel</button>
          </div>
        </form>
      )}

      {/* Food log list */}
      <div className="space-y-2">
        {logs.length === 0 ? (
          <div className="card p-8 text-center text-surface-400">No food logged for this date</div>
        ) : logs.map(log => (
          <div key={log.id} className="card p-4 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <span className={`badge ${mealTypeColors[log.mealType] || ''}`}>{log.mealType}</span>
              <div>
                <p className="font-medium text-surface-900 dark:text-white">{log.foodName}</p>
                <p className="text-xs text-surface-500">{log.protein}g P · {log.carbs}g C · {log.fat}g F</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <span className="font-bold text-primary-500">{Math.round(log.calories)} kcal</span>
              <button onClick={() => handleDelete(log.id)} className="p-1.5 rounded-lg hover:bg-red-50 dark:hover:bg-red-500/10 text-red-500"><HiOutlineTrash className="w-4 h-4" /></button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default NutritionPage;
