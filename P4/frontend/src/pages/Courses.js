import React, { useState, useEffect } from 'react';

function Courses() {
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');
    fetch('http://course-service:8081/courses', {
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
    .then(response => response.json())
    .then(data => setCourses(data))
    .catch(err => alert('Failed to load courses: ' + err));
  }, []);

  return (
    <div>
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
