import React, { useState, useEffect } from 'react';

function Courses() {
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');
    fetch('http://course-service:8082/courses', {
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
        <ul>
          {courses.map(course => (
            <li key={course.id}>{course.name}</li>
          ))}
        </ul>
      ) : (
        <p>No courses available.</p>
      )}
    </div>
  );
}

export default Courses;
