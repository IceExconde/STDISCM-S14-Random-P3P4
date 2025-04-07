import React, { useState, useEffect } from 'react';
import Navigation from '../components/Navigation';

function Grades() {
  const [grades, setGrades] = useState([]);

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');
    fetch('http://grades-service:8083/grades', {
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
    .then(response => response.json())
    .then(data => setGrades(data))
    .catch(err => alert('Failed to load grades: ' + err));
  }, []);

  return (
    <div>
      <Navigation />
      <h2>Your Grades</h2>
      {grades.length > 0 ? (
        <ul>
          {grades.map(grade => (
            <li key={grade.id}>{grade.courseName}: {grade.grade}</li>
          ))}
        </ul>
      ) : (
        <p>No grades available.</p>
      )}
    </div>
  );
}

export default Grades;
