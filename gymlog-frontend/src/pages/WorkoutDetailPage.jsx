import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { workoutAPI } from '../api/axios';
import toast from 'react-hot-toast';

const WorkoutDetailPage = () => {
  const { id } = useParams();
  const [workout, setWorkout] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    workoutAPI.getById(id).then(res => setWorkout(res.data)).catch(() => toast.error('Workout not found')).finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="card p-8 animate-pulse"><div className="h-6 bg-surface-200 dark:bg-surface-700 rounded w-1/3 mb-4" /><div className="h-4 bg-surface-200 dark:bg-surface-700 rounded w-2/3" /></div>;
  if (!workout) return <p className="text-center text-surface-500">Workout not found</p>;

  const statusColors = { COMPLETED: 'badge-success', IN_PROGRESS: 'badge-warning', PLANNED: 'badge-info' };

  return (
    <div className="space-y-6 max-w-3xl">
      <div className="flex items-center justify-between">
        <div>
          <Link to="/workouts" className="text-sm text-primary-500 hover:text-primary-600 mb-1 inline-block">← Back to Workouts</Link>
          <h1 className="page-title">{workout.name}</h1>
          <div className="flex items-center gap-3 mt-1">
            <span className="text-surface-500">{workout.workoutDate}</span>
            <span className={statusColors[workout.status]}>{workout.status}</span>
            {workout.durationMinutes && <span className="text-surface-500">{workout.durationMinutes} min</span>}
          </div>
        </div>
      </div>

      {/* Summary */}
      <div className="grid grid-cols-3 gap-4">
        <div className="card p-4 text-center">
          <p className="text-2xl font-bold text-primary-500">{workout.totalExercises}</p>
          <p className="text-sm text-surface-500">Exercises</p>
        </div>
        <div className="card p-4 text-center">
          <p className="text-2xl font-bold text-accent-500">{workout.totalSets}</p>
          <p className="text-sm text-surface-500">Total Sets</p>
        </div>
        <div className="card p-4 text-center">
          <p className="text-2xl font-bold text-orange-500">{Math.round(workout.totalVolume).toLocaleString()}</p>
          <p className="text-sm text-surface-500">Volume (kg)</p>
        </div>
      </div>

      {workout.notes && (
        <div className="card p-4">
          <h3 className="text-sm font-medium text-surface-500 mb-1">Notes</h3>
          <p className="text-surface-900 dark:text-white">{workout.notes}</p>
        </div>
      )}

      {/* Exercises */}
      <div className="space-y-4">
        {workout.exercises?.map((ex) => (
          <div key={ex.id} className="card p-5">
            <div className="flex items-center justify-between mb-3">
              <div>
                <h3 className="font-semibold text-surface-900 dark:text-white">{ex.exerciseName}</h3>
                <p className="text-xs text-surface-400">{ex.muscleGroup}</p>
              </div>
              <span className="badge-info">{ex.sets?.length} sets</span>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-surface-500 border-b border-surface-200 dark:border-surface-700">
                    <th className="text-left py-2 font-medium">Set</th>
                    <th className="text-left py-2 font-medium">Weight (kg)</th>
                    <th className="text-left py-2 font-medium">Reps</th>
                    <th className="text-left py-2 font-medium">Volume</th>
                  </tr>
                </thead>
                <tbody>
                  {ex.sets?.map((s) => (
                    <tr key={s.id} className="border-b border-surface-100 dark:border-surface-800 last:border-0">
                      <td className="py-2 text-surface-900 dark:text-white">{s.setNumber}</td>
                      <td className="py-2 text-surface-900 dark:text-white">{s.weight || '-'}</td>
                      <td className="py-2 text-surface-900 dark:text-white">{s.reps || '-'}</td>
                      <td className="py-2 text-surface-600 dark:text-surface-400">{s.weight && s.reps ? (s.weight * s.reps) : '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default WorkoutDetailPage;
