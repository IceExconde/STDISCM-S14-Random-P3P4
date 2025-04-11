import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function LandingPage() {
  const navigate = useNavigate();
  const [checkingService, setCheckingService] = useState(false);

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

  const handleRegisterClick = async () => {
    setCheckingService(true);
    try {
      const response = await fetch('http://localhost:8085/api/auth/health');
      if (!response.ok) {
        navigate('/error', { 
          state: { 
            message: 'Registration service is currently unavailable',
            feature: 'Authentication Service'
          }
        });
        return;
      }
      navigate('/register');
    } catch (error) {
      navigate('/error', { 
        state: { 
          message: 'Cannot connect to registration service',
          feature: 'Authentication Service'
        }
      });
    } finally {
      setCheckingService(false);
    }
  };

  return (
    <div className="landing-container">
      <div className="landing-content">
        <h1>Welcome to Our P4 - Distributed Fault Tolerance</h1>
        <p>By: STDISCM S14 Exconde, Gomez, Maristela, Rejano</p>
        
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
            {checkingService ? 'Checking service...' : 'Create Account'}
          </button>
        </div>
      </div>
    </div>
  );
}

export default LandingPage;