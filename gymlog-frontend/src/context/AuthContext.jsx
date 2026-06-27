import { createContext, useContext, useState, useEffect } from 'react';
import { authAPI, userAPI } from '../api/axios';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('gymlog_token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const savedToken = localStorage.getItem('gymlog_token');
      const savedUser = localStorage.getItem('gymlog_user');

      if (savedToken && savedUser) {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
        try {
          const res = await userAPI.getProfile();
          const profile = res.data;
          setUser(profile);
          localStorage.setItem('gymlog_user', JSON.stringify(profile));
        } catch {
          logout();
        }
      }
      setLoading(false);
    };
    initAuth();
  }, []);

  const login = async (email, password) => {
    const res = await authAPI.login({ email, password });
    const { token: jwt, ...userData } = res.data;
    setToken(jwt);
    setUser(userData);
    localStorage.setItem('gymlog_token', jwt);
    localStorage.setItem('gymlog_user', JSON.stringify(userData));
    return userData;
  };

  const register = async (data) => {
    const res = await authAPI.register(data);
    const { token: jwt, ...userData } = res.data;
    setToken(jwt);
    setUser(userData);
    localStorage.setItem('gymlog_token', jwt);
    localStorage.setItem('gymlog_user', JSON.stringify(userData));
    return userData;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('gymlog_token');
    localStorage.removeItem('gymlog_user');
  };

  const updateUser = (updatedUser) => {
    setUser(updatedUser);
    localStorage.setItem('gymlog_user', JSON.stringify(updatedUser));
  };

  const isAdmin = user?.role === 'ADMIN';

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, updateUser, isAdmin }}>
      {children}
    </AuthContext.Provider>
  );
};
