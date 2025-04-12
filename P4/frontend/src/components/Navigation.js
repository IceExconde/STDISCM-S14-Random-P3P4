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
            Available Courses
          </button>
          <button 
            onClick={() => navigate('/enroll')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            Enroll Courses
          </button>
          <button 
            onClick={() => navigate('/enrolled-courses')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            My Enrolled Courses
          </button>
          <button 
            onClick={() => navigate('/grades')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            My Grades
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
            View Courses
          </button>
          <button 
            onClick={() => navigate('/create-course')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            Create Course
          </button>
          <button 
            onClick={() => navigate('/drop-courses')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            Drop Courses
          </button>
          <button
            onClick={() => navigate('/edit-courses')}
            style={{ margin: '0 5px', padding: '8px 15px' }}
          >
            Edit Courses
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