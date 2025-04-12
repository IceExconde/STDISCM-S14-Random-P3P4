import React, { useState, useEffect } from 'react';
import Navigation from '../components/Navigation';

function FacultyDropCourses() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    const fetchCourses = async () => {
      try {

        const professorId = localStorage.getItem('professorId');
        if (!professorId) throw new Error('Professor ID not found.');

        const response = await fetch(`http://localhost:8090/professors/${professorId}/courses`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwt')}`
          }
        });

        if (!response.ok) {
          throw new Error('Failed to fetch courses');
        }

        const data = await response.json();
        setCourses(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchCourses();
  }, []);

  const handleDropCourse = async (courseId) => {
    if (!window.confirm('Are you sure you want to drop this course?')) {
      return;
    }

    try {
      const profId = localStorage.getItem('professorId');
      if (!profId) throw new Error('Professor ID not found.');

      const response = await fetch(`http://localhost:8090/professors/${profId}/drop-course/${courseId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwt')}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to drop course');
      }

      setCourses(courses.filter(course => course.id !== courseId));
      setSuccessMessage('Course dropped successfully');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading) {
    return <div style={{ padding: '20px', textAlign: 'center' }}>Loading...</div>;
  }

  if (error) {
    return <div style={{ padding: '20px', color: 'red' }}>{error}</div>;
  }

  return (
    <div style={{ padding: '20px' }}>
      <Navigation />
      <h2>My Teaching Courses</h2>
      {successMessage && (
        <div style={{ color: 'green', marginBottom: '10px' }}>{successMessage}</div>
      )}
      
      {courses.length === 0 ? (
        <p>You are not teaching any courses.</p>
      ) : (
        <div style={{ display: 'grid', gap: '20px' }}>
          {courses.map(course => (
            <div
              key={course.id}
              style={{
                border: '1px solid #ddd',
                borderRadius: '8px',
                padding: '15px',
                backgroundColor: '#fff',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
              }}
            >
              <div>
                <h3>{course.course}</h3>
                <p><strong>Section:</strong> {course.section}</p>
                <p><strong>Schedule:</strong> {course.days} {course.time}</p>
                <p><strong>Room:</strong> {course.room}</p>
              </div>
              <button
                onClick={() => handleDropCourse(course.id)}
                style={{
                  padding: '8px 16px',
                  backgroundColor: '#dc3545',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer'
                }}
              >
                Drop Course
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default FacultyDropCourses; 