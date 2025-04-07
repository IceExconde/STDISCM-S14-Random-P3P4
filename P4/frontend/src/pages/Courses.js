import React, { useState, useEffect } from 'react';
import Navigation from '../components/Navigation';

function Courses() {
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');
    fetch('http://localhost:8081/view-courses', {
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      console.log('Courses response:', data);
      if (data && data.data) {
        setCourses(data.data);
      } else {
        setCourses([]);
      }
    })
    .catch(err => {
      console.error('Failed to load courses:', err);
      alert('Failed to load courses: ' + err);
    });
  }, []);

  return (
    <div>
      <Navigation />
      <h2>Available Courses</h2>
      {courses.length > 0 ? (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid #ddd', backgroundColor: '#f2f2f2' }}>
              <th style={{ padding: '10px', textAlign: 'left' }}>ID</th>
              <th style={{ padding: '10px', textAlign: 'left' }}>Course</th>
              <th style={{ padding: '10px', textAlign: 'left' }}>Section</th>
              <th style={{ padding: '10px', textAlign: 'left' }}>Days</th>
              <th style={{ padding: '10px', textAlign: 'left' }}>Time</th>
              <th style={{ padding: '10px', textAlign: 'left' }}>Room</th>
            </tr>
          </thead>
          <tbody>
            {courses.map(course => (
              <tr key={course.id} style={{ borderBottom: '1px solid #ddd' }}>
                <td style={{ padding: '10px' }}>{course.id}</td>
                <td style={{ padding: '10px' }}>{course.course}</td>
                <td style={{ padding: '10px' }}>{course.section}</td>
                <td style={{ padding: '10px' }}>{course.days}</td>
                <td style={{ padding: '10px' }}>{course.time}</td>
                <td style={{ padding: '10px' }}>{course.room}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No courses available.</p>
      )}
    </div>
  );
}

export default Courses;
