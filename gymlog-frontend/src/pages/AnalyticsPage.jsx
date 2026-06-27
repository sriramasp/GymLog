import { useState, useEffect } from 'react';
import { analyticsAPI, exerciseAPI } from '../api/axios';
import { LineChart, Line, BarChart, Bar, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const AnalyticsPage = () => {
  const [weightData, setWeightData] = useState([]);
  const [volumeData, setVolumeData] = useState([]);
  const [calorieData, setCalorieData] = useState([]);
  const [strengthData, setStrengthData] = useState([]);
  const [exercises, setExercises] = useState([]);
  const [selectedExercise, setSelectedExercise] = useState('');
  const [days, setDays] = useState(30);

  // Weight data and exercises don't depend on the days filter
  useEffect(() => {
    analyticsAPI.getWeightProgression().then(r => setWeightData(r.data)).catch(() => {});
    exerciseAPI.getAll(0, 100).then(r => setExercises(r.data.content)).catch(() => {});
  }, []);

  // Volume and calorie trends depend on the days filter
  useEffect(() => {
    analyticsAPI.getVolumeTrends(days).then(r => setVolumeData(r.data)).catch(() => {});
    analyticsAPI.getCalorieTrends(days).then(r => setCalorieData(r.data)).catch(() => {});
  }, [days]);

  useEffect(() => {
    if (selectedExercise) {
      analyticsAPI.getStrengthProgression(selectedExercise).then(r => setStrengthData(r.data)).catch(() => {});
    }
  }, [selectedExercise]);

  const ChartCard = ({ title, children, empty }) => (
    <div className="card p-6">
      <h3 className="text-lg font-semibold text-surface-900 dark:text-white mb-4">{title}</h3>
      {empty ? <p className="text-surface-400 text-center py-8">Not enough data yet. Keep logging!</p> : children}
    </div>
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div><h1 className="page-title">Analytics</h1><p className="page-subtitle">Visualize your fitness progress</p></div>
        <select value={days} onChange={e => setDays(Number(e.target.value))} className="input w-auto">
          <option value={7}>Last 7 days</option><option value={30}>Last 30 days</option><option value={90}>Last 90 days</option>
        </select>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <ChartCard title="📊 Weight Progression" empty={weightData.length < 2}>
          <ResponsiveContainer width="100%" height={250}>
            <LineChart data={weightData}>
              <CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="date" tick={{ fontSize: 11 }} /><YAxis tick={{ fontSize: 11 }} domain={['auto', 'auto']} />
              <Tooltip /><Line type="monotone" dataKey="value" stroke="#6366f1" strokeWidth={2} dot={{ fill: '#6366f1', r: 4 }} name="Weight (kg)" />
            </LineChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="📈 Workout Volume Trends" empty={volumeData.length < 2}>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={volumeData}>
              <CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="date" tick={{ fontSize: 11 }} /><YAxis tick={{ fontSize: 11 }} />
              <Tooltip /><Bar dataKey="value" fill="#10b981" radius={[4, 4, 0, 0]} name="Volume (kg×reps)" />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="🔥 Calorie Intake Trends" empty={calorieData.length < 2}>
          <ResponsiveContainer width="100%" height={250}>
            <AreaChart data={calorieData}>
              <CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="date" tick={{ fontSize: 11 }} /><YAxis tick={{ fontSize: 11 }} />
              <Tooltip /><Area type="monotone" dataKey="value" stroke="#f59e0b" fill="#f59e0b" fillOpacity={0.15} strokeWidth={2} name="Calories" />
            </AreaChart>
          </ResponsiveContainer>
        </ChartCard>

        <div className="card p-6">
          <h3 className="text-lg font-semibold text-surface-900 dark:text-white mb-4">💪 Strength Progression</h3>
          <select value={selectedExercise} onChange={e => setSelectedExercise(e.target.value)} className="input mb-4">
            <option value="">Select an exercise</option>
            {exercises.map(ex => <option key={ex.id} value={ex.id}>{ex.name}</option>)}
          </select>
          {strengthData.length >= 2 ? (
            <ResponsiveContainer width="100%" height={220}>
              <LineChart data={strengthData}>
                <CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="date" tick={{ fontSize: 11 }} /><YAxis tick={{ fontSize: 11 }} />
                <Tooltip /><Line type="monotone" dataKey="value" stroke="#ec4899" strokeWidth={2} dot={{ fill: '#ec4899', r: 4 }} name="Max Weight (kg)" />
              </LineChart>
            </ResponsiveContainer>
          ) : <p className="text-surface-400 text-center py-8">{selectedExercise ? 'Not enough data' : 'Select an exercise above'}</p>}
        </div>
      </div>
    </div>
  );
};

export default AnalyticsPage;
