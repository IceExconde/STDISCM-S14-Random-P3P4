import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function LandingPage() {
  const navigate = useNavigate();
  const [checkingService, setCheckingService] = useState(false);
  const [showRegister, setShowRegister] = useState(false);
  const [registerData, setRegisterData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    name: '',
    role: 'STUDENT'
  });

  const handleLoginClick = async () => {
    setCheckingService(true);
    try {
      const response = await fetch('http://localhost:8080/api/auth/health');
      if (!response.ok) {
        navigate('/error', { 
          state: { 
            message: 'Login service is currently unavailable',
            feature: 'Authentication Service'
          }
        });
        return;
      }
      navigate('/login');
    } catch (error) {
      navigate('/error', { 
        state: { 
          message: 'Cannot connect to login service',
          feature: 'Authentication Service'
        }
      });
    } finally {
      setCheckingService(false);
    }
  };

  const handleRegisterClick = () => {
    setShowRegister(true);
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    if (registerData.password !== registerData.confirmPassword) {
      alert("Passwords don't match!");
      return;
    }

    setCheckingService(true);
    try {
      const response = await fetch('http://localhost:8085/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: registerData.email,
          password: registerData.password,
          name: registerData.name,
          role: registerData.role
        }),
      });

      const data = await response.json();
      
      if (!response.ok) {
        throw new Error(data.message || 'Registration failed');
      }

      alert('Registration successful! Please login.');
      setShowRegister(false);
      navigate('/login');
    } catch (error) {
      alert(`Registration error: ${error.message}`);
    } finally {
      setCheckingService(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setRegisterData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  return (
    <div className="landing-container">
      <div className="landing-content">
        <h1>Welcome to Our P4 - Distributed Fault Tolerance</h1>
        <p>By: STDISCM S14 Exconde, Gomez, Inocencio, Rejano</p>
        
        {!showRegister ? (
          <div className="auth-buttons">
            <button 
              onClick={handleLoginClick}
              className="login-button"
              disabled={checkingService}
            >
              {checkingService ? 'Checking service...' : 'Login'}
            </button>
            <button 
              onClick={handleRegisterClick}
              className="register-button"
              disabled={checkingService}
            >
              Create Account
            </button>
          </div>
        ) : (
          <form onSubmit={handleRegisterSubmit} className="register-form">
            <h2>Create Account</h2>
            
            <div className="form-group">
              <label>Email:</label>
              <input
                type="email"
                name="email"
                value={registerData.email}
                onChange={handleInputChange}
                required
              />
            </div>
            
            <div className="form-group">
              <label>Name:</label>
              <input
                type="text"
                name="name"
                value={registerData.name}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-group">
              <label>Role:</label>
              <select
                name="role"
                value={registerData.role}
                onChange={handleInputChange}
                required
              >
                <option value="STUDENT">Student</option>
                <option value="FACULTY">Faculty</option>
              </select>
            </div>
            
            <div className="form-group">
              <label>Password:</label>
              <input
                type="password"
                name="password"
                value={registerData.password}
                onChange={handleInputChange}
                required
              />
            </div>
            
            <div className="form-group">
              <label>Confirm Password:</label>
              <input
                type="password"
                name="confirmPassword"
                value={registerData.confirmPassword}
                onChange={handleInputChange}
                required
              />
            </div>
            
            <div className="form-actions">
              <button 
                type="submit" 
                className="submit-button"
                disabled={checkingService}
              >
                {checkingService ? 'Registering...' : 'Register'}
              </button>
              <button 
                type="button" 
                onClick={() => setShowRegister(false)}
                className="cancel-button"
              >
                Back to Login
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}

export default LandingPage;