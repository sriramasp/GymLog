import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { workoutAPI, exerciseAPI } from '../api/axios';
import { HiOutlinePlus, HiOutlineTrash, HiOutlineSearch } from 'react-icons/hi';
import toast from 'react-hot-toast';

const NewWorkoutPage = () => {
  const [form, setForm] = useState({ name: '', notes: '', workoutDate: new Date().toISOString().split('T')[0], durationMinutes: '', status: 'COMPLETED' });
  const [exercises, setExercises] = useState([]);
  const [allExercises, setAllExercises] = useState([]);
  const [search, setSearch] = useState('');
  const [showPicker, setShowPicker] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    exerciseAPI.getAll(0, 100).then(res => setAllExercises(res.data.content)).catch(() => {});
  }, []);

  const filteredExercises = allExercises.filter(e =>
    e.name.toLowerCase().includes(search.toLowerCase()) ||
    e.muscleGroup?.toLowerCase().includes(search.toLowerCase())
  );

  const addExercise = (ex) => {
    setExercises([...exercises, { exerciseId: ex.id, exerciseName: ex.name, muscleGroup: ex.muscleGroup, sets: [{ reps: 10, weight: 0 }] }]);
    setShowPicker(false);
    setSearch('');
  };

  const removeExercise = (idx) => setExercises(exercises.filter((_, i) => i !== idx));

  const addSet = (exIdx) => {
    const updated = [...exercises];
    const lastSet = updated[exIdx].sets[updated[exIdx].sets.length - 1];
    updated[exIdx].sets.push({ reps: lastSet?.reps || 10, weight: lastSet?.weight || 0 });
    setExercises(updated);
  };

  const removeSet = (exIdx, setIdx) => {
    const updated = [...exercises];
    updated[exIdx].sets = updated[exIdx].sets.filter((_, i) => i !== setIdx);
    setExercises(updated);
  };

  const updateSet = (exIdx, setIdx, field, value) => {
    const updated = [...exercises];
    updated[exIdx].sets[setIdx][field] = Number(value);
    setExercises(updated);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name) return toast.error('Workout name is required');
    if (exercises.length === 0) return toast.error('Add at least one exercise');
    setLoading(true);
    try {
      const payload = {
        ...form,
        durationMinutes: form.durationMinutes ? Number(form.durationMinutes) : null,
        exercises: exercises.map((ex, i) => ({
          exerciseId: ex.exerciseId,
          orderIndex: i,
          sets: ex.sets.map((s, j) => ({ setNumber: j + 1, reps: s.reps, weight: s.weight }))
        }))
      };
      await workoutAPI.create(payload);
      toast.success('Workout saved! 💪');
      navigate('/workouts');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save workout');
    } finally { setLoading(false); }
  };

  return (
    <div className="space-y-6 max-w-3xl">
      <div>
        <h1 className="page-title">Log Workout</h1>
        <p className="page-subtitle">Record your training session</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Workout info */}
        <div className="card p-6 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="label">Workout Name *</label>
              <input value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="input" placeholder="e.g. Push Day" />
            </div>
            <div>
              <label className="label">Date *</label>
              <input type="date" value={form.workoutDate} onChange={e => setForm({...form, workoutDate: e.target.value})} className="input" />
            </div>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="label">Duration (minutes)</label>
              <input type="number" value={form.durationMinutes} onChange={e => setForm({...form, durationMinutes: e.target.value})} className="input" placeholder="60" />
            </div>
            <div>
              <label className="label">Status</label>
              <select value={form.status} onChange={e => setForm({...form, status: e.target.value})} className="input">
                <option value="COMPLETED">Completed</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="PLANNED">Planned</option>
              </select>
            </div>
          </div>
          <div>
            <label className="label">Notes</label>
            <textarea value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} className="input" rows={2} placeholder="How did it go?" />
          </div>
        </div>

        {/* Exercises */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-surface-900 dark:text-white">Exercises ({exercises.length})</h2>
            <button type="button" onClick={() => setShowPicker(true)} className="btn-secondary flex items-center gap-2">
              <HiOutlinePlus className="w-4 h-4" /> Add Exercise
            </button>
          </div>

          {/* Exercise picker modal */}
          {showPicker && (
            <div className="card p-4 border-2 border-primary-500 space-y-3">
              <div className="relative">
                <HiOutlineSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-surface-400 w-5 h-5" />
                <input value={search} onChange={e => setSearch(e.target.value)} className="input pl-10" placeholder="Search exercises..." autoFocus />
              </div>
              <div className="max-h-48 overflow-y-auto space-y-1">
                {filteredExercises.slice(0, 20).map(ex => (
                  <button key={ex.id} type="button" onClick={() => addExercise(ex)}
                    className="w-full text-left px-3 py-2 rounded-lg hover:bg-primary-50 dark:hover:bg-primary-500/10 text-sm transition-colors">
                    <span className="font-medium text-surface-900 dark:text-white">{ex.name}</span>
                    <span className="text-surface-400 ml-2">{ex.muscleGroup}</span>
                  </button>
                ))}
              </div>
              <button type="button" onClick={() => { setShowPicker(false); setSearch(''); }} className="text-sm text-surface-500 hover:text-surface-700">Cancel</button>
            </div>
          )}

          {/* Exercise list */}
          {exercises.map((ex, exIdx) => (
            <div key={exIdx} className="card p-4 space-y-3 animate-slide-up">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-semibold text-surface-900 dark:text-white">{ex.exerciseName}</h3>
                  <p className="text-xs text-surface-400">{ex.muscleGroup}</p>
                </div>
                <button type="button" onClick={() => removeExercise(exIdx)} className="p-1.5 rounded-lg hover:bg-red-50 dark:hover:bg-red-500/10 text-red-500">
                  <HiOutlineTrash className="w-4 h-4" />
                </button>
              </div>

              {/* Sets table */}
              <div className="space-y-2">
                <div className="grid grid-cols-12 gap-2 text-xs font-medium text-surface-500 px-1">
                  <span className="col-span-2">Set</span>
                  <span className="col-span-4">Weight (kg)</span>
                  <span className="col-span-4">Reps</span>
                  <span className="col-span-2"></span>
                </div>
                {ex.sets.map((set, setIdx) => (
                  <div key={setIdx} className="grid grid-cols-12 gap-2 items-center">
                    <span className="col-span-2 text-sm font-medium text-surface-500">{setIdx + 1}</span>
                    <input type="number" value={set.weight} onChange={e => updateSet(exIdx, setIdx, 'weight', e.target.value)}
                      className="col-span-4 input py-1.5 text-sm" />
                    <input type="number" value={set.reps} onChange={e => updateSet(exIdx, setIdx, 'reps', e.target.value)}
                      className="col-span-4 input py-1.5 text-sm" />
                    <button type="button" onClick={() => removeSet(exIdx, setIdx)} className="col-span-2 text-red-400 hover:text-red-600">✕</button>
                  </div>
                ))}
              </div>
              <button type="button" onClick={() => addSet(exIdx)}
                className="text-sm text-primary-500 hover:text-primary-600 font-medium">+ Add Set</button>
            </div>
          ))}
        </div>

        <button type="submit" disabled={loading} className="btn-primary w-full py-3 text-lg">
          {loading ? 'Saving...' : 'Save Workout'}
        </button>
      </form>
    </div>
  );
};

export default NewWorkoutPage;
