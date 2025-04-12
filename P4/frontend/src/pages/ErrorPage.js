import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

function ErrorPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { state } = location;
  const isLoggedIn = localStorage.getItem('jwt');
  const userRole = localStorage.getItem('userRole');

  const handleLogout = () => {
    localStorage.removeItem('jwt');
    localStorage.removeItem('userRole');
    navigate('/');
  };

  const errorMessage = state?.message || 'The feature you are trying to access is temporarily unavailable.';

  const buttonStyle = {
    padding: '12px 24px',
    margin: '0 10px',
    fontSize: '1.1rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    transition: 'background-color 0.3s'
  };

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      minHeight: '100vh',
      padding: '20px',
      textAlign: 'center',
      backgroundColor: '#f8f9fa'
    }}>
      <h1 style={{ fontSize: '4rem', color: '#dc3545', marginBottom: '20px' }}>
        {state?.feature ? 'Feature Unavailable' : '404'}
      </h1>
      <h2 style={{ fontSize: '2rem', marginBottom: '20px' }}>
        {state?.feature ? 'Feature Error' : 'Page Not Found'}
      </h2>
      <p style={{ fontSize: '1.2rem', marginBottom: '30px', color: '#6c757d' }}>
        {errorMessage}
      </p>
      
      <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', gap: '10px' }}>
        {!isLoggedIn ? (
          <>
            <button
              onClick={() => navigate('/')}
              style={buttonStyle}
            >
              Login
            </button>
            <button
              onClick={() => navigate('/register')}
              style={buttonStyle}
            >
              Register
            </button>
          </>
        ) : (
          <>
            {userRole === 'STUDENT' && (
              <>
                <button
                  onClick={() => navigate('/courses')}
                  style={buttonStyle}
                >
                  Available Courses
                </button>
                <button
                  onClick={() => navigate('/enrolled-courses')}
                  style={buttonStyle}
                >
                  My Enrolled Courses
                </button>
                <button
                  onClick={() => navigate('/grades')}
                  style={buttonStyle}
                >
                  My Grades
                </button>
                <button
                  onClick={() => navigate('/enroll')}
                  style={buttonStyle}
                >
                  Enroll in a Course
                </button>
                <button
                  onClick={handleLogout}
                  style={buttonStyle}
                >
                  Logout
                </button>
              </>
            )}
            {userRole === 'FACULTY' && (
              <>
                <button
                  onClick={() => navigate('/courses')}
                  style={buttonStyle}
                >
                  View All Courses
                </button>
                <button
                  onClick={() => navigate('/create-course')}
                  style={buttonStyle}
                >
                  Create Course
                </button>
                <button
                  onClick={() => navigate('/edit-courses')}
                  style={buttonStyle}
                >
                  Edit Courses
                </button>
                <button
                  onClick={() => navigate('/drop-courses')}
                  style={buttonStyle}
                >
                  Drop Courses
                </button>
                <button
                  onClick={() => navigate('/upload-grades')}
                  style={buttonStyle}
                >
                  Upload Grades
                </button>
                <button
                  onClick={handleLogout}
                  style={buttonStyle}
                >
                  Logout
                </button>
              </>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default ErrorPage;