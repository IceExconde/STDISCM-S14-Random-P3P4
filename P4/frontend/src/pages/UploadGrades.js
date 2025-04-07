import React, { useState, useEffect } from 'react';
import Navigation from '../components/Navigation';

function UploadGrades() {
  const [courses, setCourses] = useState([]);
  const [selectedCourse, setSelectedCourse] = useState('');
  const [studentId, setStudentId] = useState('');
  const [grade, setGrade] = useState('');
  const [message, setMessage] = useState('');
  const [students, setStudents] = useState([]);

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');
    // Fetch courses taught by the faculty
    fetch('http://course-service:8081/courses/enrolled', {
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
    .then(response => response.json())
    .then(data => setCourses(data))
    .catch(err => alert('Failed to load courses: ' + err));
  }, []);

  const handleCourseChange = (courseId) => {
    setSelectedCourse(courseId);
    
    if (!courseId) return;
    
    const jwt = localStorage.getItem('jwt');
    // Get students enrolled in this course (optional - if API supports it)
    fetch(`http://course-service:8081/courses/${courseId}/students`, {
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
    .then(response => response.json())
    .then(data => setStudents(data))
    .catch(err => {
      console.error('Failed to load students:', err);
      setStudents([]);
    });
  };

  const handleSubmitGrade = () => {
    if (!selectedCourse || !studentId || !grade) {
      setMessage('Please fill in all fields');
      return;
    }

    const jwt = localStorage.getItem('jwt');
    // Submit grade to grade service
    fetch('http://grade-service:8082/grades', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${jwt}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        courseId: selectedCourse,
        studentId,
        grade
      })
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Failed to submit grade');
      }
      return response.json();
    })
    .then(data => {
      setMessage('Grade successfully submitted');
      // Reset form
      setStudentId('');
      setGrade('');
    })
    .catch(err => setMessage(`Grade submission failed: ${err.message}`));
  };

  return (
    <div>
      <Navigation />
      <h2>Upload Grades</h2>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Your Courses</h3>
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
          <p>You are not teaching any courses.</p>
        )}
      </div>
      
      <div style={{ marginTop: '20px' }}>
        <h3>Submit Grade</h3>
        <div style={{ marginBottom: '10px' }}>
          <label>
            Course:
            <select 
              value={selectedCourse} 
              onChange={(e) => handleCourseChange(e.target.value)}
              style={{ marginLeft: '10px' }}
            >
              <option value="">Select a course</option>
              {courses.map(course => (
                <option key={course.id} value={course.id}>
                  {course.code} - {course.title} (Section: {course.section})
                </option>
              ))}
            </select>
          </label>
        </div>
        
        <div style={{ marginBottom: '10px' }}>
          <label>
            Student ID:
            <input 
              type="text" 
              value={studentId} 
              onChange={(e) => setStudentId(e.target.value)}
              placeholder="Enter Student ID"
              style={{ marginLeft: '10px' }}
            />
          </label>
        </div>
        
        <div style={{ marginBottom: '10px' }}>
          <label>
            Grade:
            <input 
              type="text" 
              value={grade} 
              onChange={(e) => setGrade(e.target.value)}
              placeholder="Enter Grade"
              style={{ marginLeft: '10px' }}
            />
          </label>
        </div>
        
        <button onClick={handleSubmitGrade}>Submit Grade</button>
        
        {message && (
          <p style={{ color: message.includes('failed') ? 'red' : 'green' }}>
            {message}
          </p>
        )}
      </div>
      
      {students.length > 0 && (
        <div style={{ marginTop: '20px' }}>
          <h3>Enrolled Students</h3>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid #ddd', backgroundColor: '#f2f2f2' }}>
                <th style={{ padding: '10px', textAlign: 'left' }}>ID</th>
                <th style={{ padding: '10px', textAlign: 'left' }}>Name</th>
                <th style={{ padding: '10px', textAlign: 'left' }}>Email</th>
              </tr>
            </thead>
            <tbody>
              {students.map(student => (
                <tr key={student.id} style={{ borderBottom: '1px solid #ddd' }}>
                  <td style={{ padding: '10px' }}>{student.id}</td>
                  <td style={{ padding: '10px' }}>{student.name}</td>
                  <td style={{ padding: '10px' }}>{student.email}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default UploadGrades; 