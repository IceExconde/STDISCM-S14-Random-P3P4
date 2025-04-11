import React, { useState, useEffect } from 'react';
import Navigation from '../components/Navigation';

function Grades() {
  const [grades, setGrades] = useState([]);

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');
    const studentId = localStorage.getItem('studentId');
    fetch('http://localhost:8083/api/view-grades', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${jwt}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ studentId })
    })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      console.log('Grades response:', data);
      if (data && data.data) {
        setGrades(data.data);
      } else if (Array.isArray(data)) {
        setGrades(data);
      } else {
        setGrades([]);
      }
    })
    .catch(err => {
      console.error('Failed to load grades:', err);
      alert('Failed to load grades: ' + err);
    });
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
