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
        const studentId = localStorage.getItem('studentId');
        
        console.log('Making request to:', 'http://localhost:8086/api/view-enrolled');
        console.log('With auth header:', `Bearer ${jwt}`);

        const response = await fetch('http://localhost:8086/api/view-enrolled', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${jwt}`,
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          body: JSON.stringify({
            studentId
          })
        });

        if (!response.ok) {
          throw new Error('Failed to fetch enrolled courses');
        }

        const data = await response.json();
        console.log('Fetched data:', data);
        setEnrolledCourses(data.courseList);
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
              key={course.classNbr}
              style={{
                border: '1px solid #ddd',
                borderRadius: '8px',
                padding: '15px',
                backgroundColor: '#fff'
              }}
            >
              <h3>{course.course}</h3>
              <p><strong>Course Code:</strong> {course.classNbr}</p>
              <p><strong>Section:</strong> {course.section}</p>
              <p><strong>Room:</strong> {course.room}</p>
              <p><strong>Days:</strong> {course.days}</p>
              <p><strong>Time:</strong> {course.time}</p>
              {course.grade && <p><strong>Grade:</strong> {course.grade}</p>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default ViewEnrolledCourses; 