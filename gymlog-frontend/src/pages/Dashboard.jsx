import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { dashboardAPI } from '../api/axios';
import { HiOutlineLightningBolt, HiOutlineFire, HiOutlineTrendingUp, HiOutlineFlag, HiOutlineArrowRight } from 'react-icons/hi';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import toast from 'react-hot-toast';

const StatCard = ({ icon: Icon, label, value, sub, gradient, delay = 0 }) => (
  <div className="stat-card group animate-slide-up" style={{ animationDelay: `${delay}ms` }}>
    <div className="flex items-center gap-4">
      <div className={`w-12 h-12 rounded-xl flex items-center justify-center shadow-lg transition-transform duration-300 group-hover:scale-110 ${gradient}`}>
        <Icon className="w-6 h-6 text-white" />
      </div>
      <div>
        <p className="text-2xl font-bold text-surface-900 dark:text-white">{value}</p>
        <p className="text-sm text-surface-500 dark:text-surface-400">{label}</p>
      </div>
    </div>
    {sub && <p className="text-xs text-surface-400 mt-1 pl-16">{sub}</p>}
  </div>
);

const CircularProgress = ({ percentage, label, color = '#6366f1' }) => {
  const [animatedPct, setAnimatedPct] = useState(0);
  const radius = 45;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (Math.min(animatedPct, 100) / 100) * circumference;

  useEffect(() => {
    const timer = setTimeout(() => setAnimatedPct(percentage), 200);
    return () => clearTimeout(timer);
  }, [percentage]);

  return (
    <div className="flex flex-col items-center">
      <div className="relative w-28 h-28">
        <svg className="w-28 h-28 -rotate-90" viewBox="0 0 100 100">
          <circle cx="50" cy="50" r={radius} fill="none" stroke="currentColor"
            className="text-surface-200 dark:text-surface-700" strokeWidth="8" />
          <circle cx="50" cy="50" r={radius} fill="none" strokeWidth="8" strokeLinecap="round"
            stroke={color}
            strokeDasharray={circumference} strokeDashoffset={offset}
            style={{ transition: 'stroke-dashoffset 1.2s cubic-bezier(0.4, 0, 0.2, 1)' }} />
        </svg>
        <div className="absolute inset-0 flex items-center justify-center">
          <span className="text-lg font-bold text-surface-900 dark:text-white">{Math.round(percentage)}%</span>
        </div>
      </div>
      <p className="text-sm text-surface-500 dark:text-surface-400 mt-2">{label}</p>
    </div>
  );
};

const Dashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const res = await dashboardAPI.get();
        setData(res.data);
      } catch (err) {
        toast.error('Failed to load dashboard');
      } finally {
        setLoading(false);
      }
    };
    fetchDashboard();
  }, []);

  if (loading) return (
    <div className="space-y-6">
      <div>
        <div className="skeleton h-8 w-40 mb-2" />
        <div className="skeleton h-4 w-64" />
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {[...Array(4)].map((_, i) => (
          <div key={i} className="card p-5">
            <div className="flex items-center gap-4">
              <div className="skeleton w-12 h-12 rounded-xl" />
              <div className="flex-1">
                <div className="skeleton h-6 w-16 mb-2" />
                <div className="skeleton h-4 w-24" />
              </div>
            </div>
          </div>
        ))}
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <div className="card p-6"><div className="skeleton h-48 rounded-xl" /></div>
        <div className="card p-6"><div className="skeleton h-48 rounded-xl" /></div>
      </div>
    </div>
  );

  if (!data) return <p className="text-center text-surface-500">No data available</p>;

  const weeklyData = [
    { name: 'Workouts', value: data.weeklyWorkouts },
    { name: 'Volume (k)', value: Math.round(data.weeklyVolume / 1000) },
    { name: 'Avg Cal', value: Math.round(data.weeklyCaloriesAvg) },
  ];

  return (
    <div className="space-y-6">
      <div className="animate-fade-in">
        <h1 className="page-title">Dashboard</h1>
        <p className="page-subtitle">Your fitness overview at a glance</p>
      </div>

      {/* Stats row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard icon={HiOutlineLightningBolt} label="Today's Workouts" value={data.todayWorkouts}
          sub={`${data.todayExercises} exercises · ${data.todaySets} sets`}
          gradient="bg-gradient-to-br from-primary-500 to-primary-700 shadow-primary-500/30" delay={0} />
        <StatCard icon={HiOutlineFire} label="Workout Streak" value={`${data.workoutStreak} days`}
          sub="Keep it going!" gradient="bg-gradient-to-br from-orange-500 to-red-500 shadow-orange-500/30" delay={50} />
        <StatCard icon={HiOutlineTrendingUp} label="Weekly Workouts" value={data.weeklyWorkouts}
          sub={`${Math.round(data.weeklyVolume).toLocaleString()} kg volume`}
          gradient="bg-gradient-to-br from-accent-500 to-accent-700 shadow-accent-500/30" delay={100} />
        <StatCard icon={HiOutlineFlag} label="Active Goals" value={data.activeGoals}
          sub={`${data.completedGoals} completed`}
          gradient="bg-gradient-to-br from-purple-500 to-pink-500 shadow-purple-500/30" delay={150} />
      </div>

      {/* Calories + Weekly chart row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {/* Calories card */}
        <div className="card p-6 animate-slide-up" style={{ animationDelay: '200ms' }}>
          <h3 className="text-lg font-semibold text-surface-900 dark:text-white mb-4">Today's Nutrition</h3>
          <div className="flex items-center justify-around">
            <CircularProgress percentage={data.caloriePercentage} label="Calories" color="#6366f1" />
            <div className="space-y-3">
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-blue-500" />
                <span className="text-sm text-surface-600 dark:text-surface-400">
                  Protein: <strong className="text-surface-900 dark:text-white">{Math.round(data.todayProtein)}g</strong>
                </span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-amber-500" />
                <span className="text-sm text-surface-600 dark:text-surface-400">
                  Carbs: <strong className="text-surface-900 dark:text-white">{Math.round(data.todayCarbs)}g</strong>
                </span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-red-500" />
                <span className="text-sm text-surface-600 dark:text-surface-400">
                  Fat: <strong className="text-surface-900 dark:text-white">{Math.round(data.todayFat)}g</strong>
                </span>
              </div>
              <p className="text-xs text-surface-400 mt-2">
                {Math.round(data.todayCalories)} / {data.calorieTarget} kcal
              </p>
            </div>
          </div>
        </div>

        {/* Weekly stats chart */}
        <div className="card p-6 animate-slide-up" style={{ animationDelay: '250ms' }}>
          <h3 className="text-lg font-semibold text-surface-900 dark:text-white mb-4">Weekly Summary</h3>
          <ResponsiveContainer width="100%" height={180}>
            <BarChart data={weeklyData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" tick={{ fontSize: 12 }} />
              <YAxis tick={{ fontSize: 12 }} />
              <Tooltip />
              <Bar dataKey="value" fill="url(#barGradient)" radius={[8, 8, 0, 0]} />
              <defs>
                <linearGradient id="barGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#6366f1" />
                  <stop offset="100%" stopColor="#4f46e5" />
                </linearGradient>
              </defs>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Goals + Recent PRs */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {/* Goals */}
        <div className="card p-6 animate-slide-up" style={{ animationDelay: '300ms' }}>
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-surface-900 dark:text-white">Active Goals</h3>
            <Link to="/goals" className="text-sm text-primary-500 hover:text-primary-600 font-medium flex items-center gap-1 transition-colors">
              View all <HiOutlineArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
          {data.recentGoals?.length > 0 ? (
            <div className="space-y-3">
              {data.recentGoals.map((goal) => (
                <div key={goal.id} className="flex items-center gap-3">
                  <div className="flex-1">
                    <p className="text-sm font-medium text-surface-900 dark:text-white">{goal.title}</p>
                    <div className="mt-1.5 w-full bg-surface-200 dark:bg-surface-700 rounded-full h-2 overflow-hidden">
                      <div className="bg-gradient-to-r from-primary-500 to-accent-500 h-2 rounded-full transition-all duration-700"
                        style={{ width: `${Math.min(goal.progressPercentage, 100)}%` }} />
                    </div>
                  </div>
                  <span className="text-sm font-bold text-primary-500">{Math.round(goal.progressPercentage)}%</span>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-6">
              <p className="text-3xl mb-2">🎯</p>
              <p className="text-sm text-surface-400">No active goals. Create one to start tracking!</p>
            </div>
          )}
        </div>

        {/* Recent PRs */}
        <div className="card p-6 animate-slide-up" style={{ animationDelay: '350ms' }}>
          <h3 className="text-lg font-semibold text-surface-900 dark:text-white mb-4">Recent PRs 🏆</h3>
          {data.recentPRs?.length > 0 ? (
            <div className="space-y-3">
              {data.recentPRs.map((pr) => (
                <div key={pr.id} className="flex items-center justify-between py-2 border-b border-surface-100 dark:border-surface-700 last:border-0">
                  <span className="text-sm font-medium text-surface-900 dark:text-white">{pr.exerciseName}</span>
                  <span className="badge-success">{pr.maxWeight} kg</span>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-6">
              <p className="text-3xl mb-2">🏋️</p>
              <p className="text-sm text-surface-400">No personal records yet. Start logging workouts!</p>
            </div>
          )}
        </div>
      </div>

      {/* Last week workouts */}
      <div className="card p-6 animate-slide-up">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-lg font-semibold text-surface-900 dark:text-white">Last Week's Workout Splits</h3>
            <p className="text-sm text-surface-500 dark:text-surface-400">Tap a split to see exercise variations, sets, reps and weights.</p>
          </div>
          <Link to="/workouts" className="text-sm text-primary-500 hover:text-primary-600 font-medium transition-colors">View all workouts</Link>
        </div>

        {data.recentWorkouts?.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {data.recentWorkouts.map((w) => (
              <Link to={`/workouts/${w.id}`} key={w.id} className="card p-4 hover:shadow-lg transition-all border border-surface-200 dark:border-surface-700">
                <div className="flex items-center justify-between mb-3">
                  <div>
                    <p className="text-sm text-surface-500">{new Date(w.workoutDate).toLocaleDateString()}</p>
                    <h4 className="font-semibold text-surface-900 dark:text-white">{w.name}</h4>
                  </div>
                  <span className="badge-info">{w.status}</span>
                </div>
                <p className="text-sm text-surface-500 mb-3">{w.exercises?.slice(0, 3).map((ex) => ex.exerciseName).join(' · ')}</p>
                <div className="flex items-center gap-3 text-sm text-surface-600 dark:text-surface-400">
                  <span>{w.totalExercises} exercises</span>
                  <span>{w.totalSets} sets</span>
                  <span>{Math.round(w.totalVolume)} kg</span>
                </div>
              </Link>
            ))}
          </div>
        ) : (
          <div className="text-center py-10 text-surface-500">No workouts tracked in the last week. Log one in the Workouts section to see your splits here.</div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
