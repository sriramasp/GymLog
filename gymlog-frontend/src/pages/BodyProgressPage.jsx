import { useState, useEffect } from 'react';
import { bodyMeasurementAPI } from '../api/axios';
import { HiOutlinePlus, HiOutlineTrash } from 'react-icons/hi';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import toast from 'react-hot-toast';

const BodyProgressPage = () => {
  const [measurements, setMeasurements] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ weight: '', bodyFatPct: '', chest: '', waist: '', hips: '', biceps: '', thighs: '', neck: '', notes: '', measurementDate: new Date().toISOString().split('T')[0] });
  const [loading, setLoading] = useState(false);

  const fetchData = async () => {
    try { const res = await bodyMeasurementAPI.getAll(); setMeasurements(res.data); } catch {}
  };

  useEffect(() => { fetchData(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const payload = {};
      Object.entries(form).forEach(([k, v]) => { if (v !== '' && v !== null) payload[k] = k === 'notes' || k === 'measurementDate' ? v : Number(v); });
      await bodyMeasurementAPI.create(payload);
      toast.success('Measurement recorded! 📏');
      setShowForm(false);
      setForm({ weight: '', bodyFatPct: '', chest: '', waist: '', hips: '', biceps: '', thighs: '', neck: '', notes: '', measurementDate: new Date().toISOString().split('T')[0] });
      fetchData();
    } catch { toast.error('Failed to save'); }
    finally { setLoading(false); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this measurement?')) return;
    try {
      await bodyMeasurementAPI.delete(id);
      toast.success('Measurement deleted');
      fetchData();
    } catch { toast.error('Failed to delete'); }
  };

  const chartData = measurements.map(m => ({ date: m.measurementDate, weight: m.weight, bodyFat: m.bodyFatPct }));

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div><h1 className="page-title">Body Progress</h1><p className="page-subtitle">Track your body measurements over time</p></div>
        <button onClick={() => setShowForm(!showForm)} className="btn-primary flex items-center gap-2"><HiOutlinePlus className="w-5 h-5" /> Add Measurement</button>
      </div>

      {/* Weight chart */}
      {chartData.length > 1 && (
        <div className="card p-6 animate-fade-in">
          <h3 className="text-lg font-semibold text-surface-900 dark:text-white mb-4">Weight Trend</h3>
          <ResponsiveContainer width="100%" height={250}>
            <LineChart data={chartData}>
              <defs>
                <linearGradient id="weightGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#6366f1" stopOpacity={0.8} />
                  <stop offset="100%" stopColor="#6366f1" stopOpacity={0.2} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} domain={['auto', 'auto']} />
              <Tooltip />
              <Line type="monotone" dataKey="weight" stroke="#6366f1" strokeWidth={2.5} dot={{ fill: '#6366f1', r: 4, strokeWidth: 2, stroke: '#fff' }} activeDot={{ r: 6, strokeWidth: 2 }} name="Weight (kg)" />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}

      {/* Add form */}
      {showForm && (
        <form onSubmit={handleSubmit} className="card p-6 space-y-4 animate-slide-up">
          <h3 className="font-semibold text-surface-900 dark:text-white">New Measurement</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div><label className="label">Date *</label><input type="date" value={form.measurementDate} onChange={e => setForm({...form, measurementDate: e.target.value})} className="input" /></div>
            <div><label className="label">Weight (kg)</label><input type="number" step="0.1" value={form.weight} onChange={e => setForm({...form, weight: e.target.value})} className="input" /></div>
            <div><label className="label">Body Fat %</label><input type="number" step="0.1" value={form.bodyFatPct} onChange={e => setForm({...form, bodyFatPct: e.target.value})} className="input" /></div>
            <div><label className="label">Chest (cm)</label><input type="number" step="0.1" value={form.chest} onChange={e => setForm({...form, chest: e.target.value})} className="input" /></div>
            <div><label className="label">Waist (cm)</label><input type="number" step="0.1" value={form.waist} onChange={e => setForm({...form, waist: e.target.value})} className="input" /></div>
            <div><label className="label">Hips (cm)</label><input type="number" step="0.1" value={form.hips} onChange={e => setForm({...form, hips: e.target.value})} className="input" /></div>
            <div><label className="label">Biceps (cm)</label><input type="number" step="0.1" value={form.biceps} onChange={e => setForm({...form, biceps: e.target.value})} className="input" /></div>
            <div><label className="label">Thighs (cm)</label><input type="number" step="0.1" value={form.thighs} onChange={e => setForm({...form, thighs: e.target.value})} className="input" /></div>
          </div>
          <div><label className="label">Notes</label><textarea value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} className="input" rows={2} /></div>
          <div className="flex gap-3">
            <button type="submit" disabled={loading} className="btn-primary">{loading ? 'Saving...' : 'Save'}</button>
            <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">Cancel</button>
          </div>
        </form>
      )}

      {/* History table */}
      {measurements.length === 0 ? (
        <div className="card p-12 text-center animate-fade-in">
          <p className="text-4xl mb-3">📏</p>
          <p className="text-lg font-medium text-surface-900 dark:text-white mb-1">No measurements yet</p>
          <p className="text-surface-400 text-sm">Start tracking your body measurements to see your progress!</p>
        </div>
      ) : (
        <div className="card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-surface-50 dark:bg-surface-800">
                <tr className="text-left text-surface-500">
                  <th className="p-3 font-medium">Date</th><th className="p-3 font-medium">Weight</th><th className="p-3 font-medium">Body Fat</th>
                  <th className="p-3 font-medium">Chest</th><th className="p-3 font-medium">Waist</th><th className="p-3 font-medium">Biceps</th>
                  <th className="p-3 font-medium w-16"></th>
                </tr>
              </thead>
              <tbody>
                {measurements.slice().reverse().map(m => (
                  <tr key={m.id} className="border-t border-surface-200 dark:border-surface-700 hover:bg-surface-50 dark:hover:bg-surface-800/50 transition-colors">
                    <td className="p-3 text-surface-900 dark:text-white font-medium">{m.measurementDate}</td>
                    <td className="p-3">{m.weight ? `${m.weight} kg` : '-'}</td>
                    <td className="p-3">{m.bodyFatPct ? `${m.bodyFatPct}%` : '-'}</td>
                    <td className="p-3">{m.chest ? `${m.chest} cm` : '-'}</td>
                    <td className="p-3">{m.waist ? `${m.waist} cm` : '-'}</td>
                    <td className="p-3">{m.biceps ? `${m.biceps} cm` : '-'}</td>
                    <td className="p-3">
                      <button onClick={() => handleDelete(m.id)} className="p-1.5 rounded-lg hover:bg-red-50 dark:hover:bg-red-500/10 text-surface-400 hover:text-red-500 transition-all" title="Delete measurement">
                        <HiOutlineTrash className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default BodyProgressPage;
