import React from 'react';
import { useNavigate } from 'react-router-dom';

function Navigation() {
  const navigate = useNavigate();
  const userRole = localStorage.getItem('userRole');

  const handleLogout = () => {
    localStorage.removeItem('jwt');
    localStorage.removeItem('userRole');
    navigate('/');
  };

  // Only render navigation buttons if user is logged in
  if (!localStorage.getItem('jwt')) {
    return null;
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '20px' }}>
      {/* Navigation buttons for STUDENT role */}
      {userRole === 'STUDENT' && (
        <>
          <button 
            onClick={() => navigate('/courses')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            Courses
          </button>
          <button 
            onClick={() => navigate('/grades')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            Grades
          </button>
          <button 
            onClick={() => navigate('/enroll')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            Enroll
          </button>
        </>
      )}

      {/* Navigation buttons for FACULTY role */}
      {userRole === 'FACULTY' && (
        <>
          <button 
            onClick={() => navigate('/courses')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            Courses
          </button>
          <button 
            onClick={() => navigate('/upload-grades')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            Upload Grades
          </button>
        </>
      )}

      {/* Logout button always visible if logged in */}
      <button 
        onClick={handleLogout}
        style={{ margin: '0 5px', padding: '8px 15px', backgroundColor: '#ff6b6b' }}
      >
        Logout
      </button>
    </div>
  );
}

export default Navigation; 