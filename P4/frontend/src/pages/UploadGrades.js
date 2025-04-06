import React, { useState, useEffect } from 'react';

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
      <h2>Upload Grades</h2>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Your Courses</h3>
        {courses.length > 0 ? (
          <ul>
            {courses.map(course => (
              <li key={course.id}>
                ID: {course.id} - {course.code} {course.title}
              </li>
            ))}
          </ul>
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
                  {course.code} - {course.title}
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
          <ul>
            {students.map(student => (
              <li key={student.id}>
                ID: {student.id} - {student.name} ({student.email})
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default UploadGrades; 