import React, { useState, useEffect } from 'react';
import Navigation from '../components/Navigation';

function Enroll() {
  const [courses, setCourses] = useState([]);
  const [courseId, setCourseId] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');
    fetch('http://localhost:8081/view-courses', {
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
    .then(response => response.json())
    .then(data => {
      console.log('Courses for enrollment:', data);
      if (data && data.courses) {
        setCourses(data.courses);
      } else if (Array.isArray(data)) {
        setCourses(data);
      } else {
        setCourses([]);
      }
    })
    .catch(err => {
      console.error('Failed to load courses:', err);
      alert('Failed to load courses: ' + err);
    });
  }, []);

  const handleEnroll = () => {
    if (!courseId) {
      setMessage('Please enter a course ID');
      return;
    }
  
    const jwt = localStorage.getItem('jwt');
    const decoded = JSON.parse(atob(jwt.split('.')[1])); // Decoding JWT
    console.log(decoded); // Check the contents of the JWT (sub, role, etc.)

    if (jwt) {
      const decoded = JSON.parse(atob(jwt.split('.')[1]));
      console.log('Decoded JWT:', decoded);
    }

    const studentId = localStorage.getItem('studentId');
    
    fetch('http://localhost:8082/api/enroll', {  
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${jwt}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        studentId,
        courseId
      })
    })
    .then(response => {
      if (!response.ok) {
        console.error(`Failed to enroll: ${response.status} ${response.statusText}`);
        console.error('Response Headers:', [...response.headers]);
        
        setMessage('Enrollment failed: You are not authorized to enroll in this course.');
        throw new Error(`Enrollment failed with status: ${response.status}`);
      }
      return response.json(); 
    })
    .then(data => {
      console.log('Enrollment response:', data);
      setMessage(data.message || `Successfully enrolled in course: ${courseId}`);
  
      return fetch('http://localhost:8081/view-courses', {
        headers: { 'Authorization': `Bearer ${jwt}` }
      });
    })
    .then(response => response.json())
    .then(data => {
      if (data && data.data) {
        setCourses(data.data);
      } else if (Array.isArray(data)) {
        setCourses(data);
      } else {
        setCourses([]);
      }
    })
    .catch(err => {
      console.error('Enrollment error:', err);
      setMessage(`Enrollment failed: ${err.message}`);
    });
  };
  

  return (
    <div>
      <Navigation />
      <h2>Enroll in Courses</h2>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Available Courses</h3>
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
                <th style={{ padding: '10px', textAlign: 'left' }}>Count</th>
              </tr>
            </thead>
            <tbody>
              {courses.map(course => (
                <tr key={course.classNbr} style={{ borderBottom: '1px solid #ddd' }}>
                  <td style={{ padding: '10px' }}>{course.classNbr}</td>
                  <td style={{ padding: '10px' }}>{course.course}</td>
                  <td style={{ padding: '10px' }}>{course.section}</td>
                  <td style={{ padding: '10px' }}>{course.days}</td>
                  <td style={{ padding: '10px' }}>{course.time}</td>
                  <td style={{ padding: '10px' }}>{course.room}</td>
                  <td style={{ padding: '10px' }}>{course.count}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No courses available.</p>
        )}
      </div>
      
      <div style={{ marginTop: '20px' }}>
        <h3>Enroll in a Course</h3>
        <input
          type="text"
          value={courseId}
          onChange={(e) => setCourseId(e.target.value)}
          placeholder="Enter Course ID"
        />
        <button onClick={handleEnroll}>Enroll</button>
        
        {message && (
          <p style={{ color: message.includes('failed') ? 'red' : 'green' }}>
            {message}
          </p>
        )}
      </div>
    </div>
  );
}

export default Enroll; 