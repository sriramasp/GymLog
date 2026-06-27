import { useState, useEffect } from 'react';
import { exerciseAPI } from '../api/axios';
import { HiOutlineSearch } from 'react-icons/hi';

const ExercisesPage = () => {
  const [exercises, setExercises] = useState([]);
  const [categories, setCategories] = useState([]);
  const [search, setSearch] = useState('');
  const [selectedCat, setSelectedCat] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([exerciseAPI.getAll(0, 100), exerciseAPI.getCategories()])
      .then(([exRes, catRes]) => { setExercises(exRes.data.content); setCategories(catRes.data); })
      .finally(() => setLoading(false));
  }, []);

  const handleSearch = async (q) => {
    setSearch(q);
    setSelectedCat(null);
    if (q.length > 1) {
      const res = await exerciseAPI.search(q);
      setExercises(res.data.content);
    } else if (q.length === 0) {
      const res = await exerciseAPI.getAll(0, 100);
      setExercises(res.data.content);
    }
  };

  const handleCategory = async (catId) => {
    setSelectedCat(catId);
    setSearch('');
    if (catId) {
      const res = await exerciseAPI.getByCategory(catId, 0);
      setExercises(res.data.content);
    } else {
      const res = await exerciseAPI.getAll(0, 100);
      setExercises(res.data.content);
    }
  };

  const difficultyColors = { BEGINNER: 'badge-success', INTERMEDIATE: 'badge-warning', ADVANCED: 'badge-danger' };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="page-title">Exercise Library</h1>
        <p className="page-subtitle">Browse {exercises.length} exercises across {categories.length} categories</p>
      </div>

      {/* Search and filter */}
      <div className="flex flex-col md:flex-row gap-4">
        <div className="relative flex-1">
          <HiOutlineSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-surface-400 w-5 h-5" />
          <input value={search} onChange={e => handleSearch(e.target.value)} className="input pl-10" placeholder="Search exercises, muscles, equipment..." />
        </div>
      </div>

      {/* Category pills */}
      <div className="flex flex-wrap gap-2">
        <button onClick={() => handleCategory(null)}
          className={`px-4 py-2 rounded-xl text-sm font-medium transition-all ${!selectedCat ? 'bg-primary-500 text-white shadow-lg shadow-primary-500/25' : 'bg-surface-100 dark:bg-surface-800 text-surface-600 dark:text-surface-400 hover:bg-surface-200'}`}>
          All
        </button>
        {categories.map(cat => (
          <button key={cat.id} onClick={() => handleCategory(cat.id)}
            className={`px-4 py-2 rounded-xl text-sm font-medium transition-all ${selectedCat === cat.id ? 'bg-primary-500 text-white shadow-lg shadow-primary-500/25' : 'bg-surface-100 dark:bg-surface-800 text-surface-600 dark:text-surface-400 hover:bg-surface-200'}`}>
            {cat.icon} {cat.name}
          </button>
        ))}
      </div>

      {/* Exercise grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {[...Array(6)].map((_, i) => <div key={i} className="card p-5 h-32 animate-pulse" />)}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {exercises.map(ex => (
            <div key={ex.id} className="card p-5 hover:shadow-lg transition-all hover:-translate-y-0.5 cursor-default">
              <div className="flex items-start justify-between mb-2">
                <h3 className="font-semibold text-surface-900 dark:text-white">{ex.name}</h3>
                <span className={difficultyColors[ex.difficulty] || 'badge-info'}>{ex.difficulty}</span>
              </div>
              <p className="text-sm text-surface-500 mb-3 line-clamp-2">{ex.description}</p>
              <div className="flex items-center gap-2 flex-wrap">
                {ex.muscleGroup && <span className="text-xs px-2 py-1 rounded-lg bg-primary-50 dark:bg-primary-500/10 text-primary-600 dark:text-primary-400">{ex.muscleGroup}</span>}
                {ex.equipment && <span className="text-xs px-2 py-1 rounded-lg bg-surface-100 dark:bg-surface-700 text-surface-600 dark:text-surface-400">{ex.equipment}</span>}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ExercisesPage;
