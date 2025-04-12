import React, { useState, useEffect } from 'react';
import Navigation from '../components/Navigation';

function ViewEnrolledCourses() {
  const [enrolledCourses, setEnrolledCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchEnrolledCourses = async () => {
      try {
        const jwt = localStorage.getItem('jwt');
        
        console.log('Making request to:', 'http://localhost:8086/api/view-enrolled');
        console.log('With auth header:', `Bearer ${jwt}`);

        const response = await fetch('http://localhost:8086/api/view-enrolled', {
          headers: {
            'Authorization': `Bearer ${jwt}`,
            'Accept': 'application/json'
          }
        });

        if (!response.ok) {
          throw new Error('Failed to fetch enrolled courses');
        }

        const data = await response.json();
        setEnrolledCourses(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchEnrolledCourses();
  }, []);

  if (loading) {
    return <div style={{ padding: '20px', textAlign: 'center' }}>Loading...</div>;
  }

  if (error) {
    return <div style={{ padding: '20px', color: 'red' }}>{error}</div>;
  }

  return (
    <div style={{ padding: '20px' }}>
      <Navigation />
      <h2>My Enrolled Courses</h2>
      
      {enrolledCourses.length === 0 ? (
        <p>You are not enrolled in any courses.</p>
      ) : (
        <div style={{ display: 'grid', gap: '20px' }}>
          {enrolledCourses.map(course => (
            <div
              key={course.id}
              style={{
                border: '1px solid #ddd',
                borderRadius: '8px',
                padding: '15px',
                backgroundColor: '#fff'
              }}
            >
              <h3>{course.courseName}</h3>
              <p><strong>Course Code:</strong> {course.courseCode}</p>
              <p><strong>Credits:</strong> {course.credits}</p>
              <p><strong>Instructor:</strong> {course.instructor}</p>
              <p><strong>Status:</strong> {course.status}</p>
              {course.grade && <p><strong>Grade:</strong> {course.grade}</p>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default ViewEnrolledCourses; 