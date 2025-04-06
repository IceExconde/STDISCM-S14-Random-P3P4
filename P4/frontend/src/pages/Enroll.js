import React, { useState, useEffect } from 'react';

function Enroll() {
  const [courses, setCourses] = useState([]);
  const [courseId, setCourseId] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');
    fetch('http://course-service:8081/courses', {
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
    .then(response => response.json())
    .then(data => setCourses(data))
    .catch(err => alert('Failed to load courses: ' + err));
  }, []);

  const handleEnroll = () => {
    if (!courseId) {
      setMessage('Please enter a course ID');
      return;
    }

    const jwt = localStorage.getItem('jwt');
    fetch(`http://course-service:8081/courses/${courseId}/enroll`, {
      method: 'POST',
      headers: { 
        'Authorization': `Bearer ${jwt}`,
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Enrollment failed');
      }
      return response.json();
    })
    .then(data => {
      setMessage(`Successfully enrolled in course: ${courseId}`);
      fetch('http://course-service:8081/courses', {
        headers: { 'Authorization': `Bearer ${jwt}` }
      })
      .then(response => response.json())
      .then(data => setCourses(data))
      .catch(err => alert('Failed to refresh courses: ' + err));
    })
    .catch(err => setMessage(`Enrollment failed: ${err.message}`));
  };

  return (
    <div>
      <h2>Enroll in Courses</h2>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Available Courses</h3>
        {courses.length > 0 ? (
          <ul>
            {courses.map(course => (
              <li key={course.id}>
                ID: {course.id} - {course.code} {course.title} 
                <br />
                <small>{course.description}</small>
              </li>
            ))}
          </ul>
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