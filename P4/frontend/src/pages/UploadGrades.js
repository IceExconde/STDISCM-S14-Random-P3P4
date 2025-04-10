import React, { useState, useEffect } from 'react';
import Navigation from '../components/Navigation';

function UploadGrades() {
  const [courses, setCourses] = useState([]);
  const [selectedCourse, setSelectedCourse] = useState('');
  const [students, setStudents] = useState([]);
  const [selectedStudent, setSelectedStudent] = useState('');
  const [grade, setGrade] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const professorId = localStorage.getItem('professorId');
        if (!professorId) throw new Error('Professor ID not found.');

        const response = await fetch(`http://localhost:8084/professors/${professorId}/courses`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('jwt')}`,
          },
        });

        if (!response.ok) throw new Error(await response.text());

        const data = await response.json();
        setCourses(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchCourses();
  }, []);

  const fetchStudentsWithGrades = async (courseId) => {
    try {
      const jwt = localStorage.getItem('jwt');

      const [studentRes, gradeRes] = await Promise.all([
        fetch(`http://localhost:8084/professors/courses/${courseId}/students`, {
          headers: { Authorization: `Bearer ${jwt}` },
        }),
        fetch(`http://localhost:8084/grades/all`, {
          headers: { Authorization: `Bearer ${jwt}` },
        }),
      ]);

      if (!studentRes.ok || !gradeRes.ok) throw new Error('Error fetching data');

      const studentsData = await studentRes.json();
      const gradesData = await gradeRes.json();

      const courseGrades = gradesData.filter(g => g.courseId === courseId);
      const merged = studentsData.map(student => {
        const studentGrade = courseGrades.find(g => g.studentId === student.id);
        return {
          ...student,
          grade: studentGrade ? studentGrade.grade : null,
        };
      });

      setStudents(merged);
    } catch (err) {
      console.error('Failed to load students with grades:', err);
      setStudents([]);
    }
  };

  const handleCourseChange = (courseId) => {
    setSelectedCourse(courseId);
    setSelectedStudent('');
    setGrade('');
    if (courseId) fetchStudentsWithGrades(courseId);
  };

  const handleSubmitGrade = async () => {
    if (!selectedCourse || !selectedStudent || !grade) {
      setMessage('Please fill in all fields');
      return;
    }

    const jwt = localStorage.getItem('jwt');
    const profId = localStorage.getItem('professorId');

    try {
      const res = await fetch('http://localhost:8084/grades/add', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${jwt}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          courseId: selectedCourse,
          studentId: selectedStudent,
          profId: profId,
          grade: parseFloat(grade),
        }),
      });

      if (!res.ok) throw new Error('Grade submission failed.');

      setMessage('Grade successfully submitted');
      setGrade('');
      fetchStudentsWithGrades(selectedCourse); // Refresh grades
    } catch (err) {
      setMessage(`Error: ${err.message}`);
    }
  };

  if (loading) return <div>Loading courses...</div>;
  if (error) return <div style={{ color: 'red' }}>{error}</div>;

  return (
    <div>
      <Navigation />
      <h2>Upload Grades</h2>

      <div style={{ marginBottom: '20px' }}>
        <label>
          Select Course:
          <select value={selectedCourse} onChange={(e) => handleCourseChange(e.target.value)} style={{ marginLeft: '10px' }}>
            <option value="">-- Select a course --</option>
            {courses.map(course => (
              <option key={course.id} value={course.id}>
                {course.code || course.course} - {course.section}
              </option>
            ))}
          </select>
        </label>
      </div>

      {selectedCourse && (
        <>
          <div style={{ marginBottom: '10px' }}>
            <label>
              Select Student:
              <select value={selectedStudent} onChange={(e) => setSelectedStudent(e.target.value)} style={{ marginLeft: '10px' }}>
                <option value="">-- Select student --</option>
                {students.map(student => (
                  <option key={student.id} value={student.id}>
                    {student.name} ({student.id})
                  </option>
                ))}
              </select>
            </label>
          </div>

          <div style={{ marginBottom: '10px' }}>
            <label>
              Grade:
              <input
                type="text"
                value={grade}
                onChange={(e) => setGrade(e.target.value)}
                placeholder="Enter grade (e.g. 1.0)"
                style={{ marginLeft: '10px' }}
              />
            </label>
          </div>

          <button onClick={handleSubmitGrade}>Submit Grade</button>
          {message && <p style={{ color: message.includes('failed') ? 'red' : 'green' }}>{message}</p>}
        </>
      )}

{students.length > 0 && (
  <div style={{ marginTop: '40px', padding: '20px', borderRadius: '12px', backgroundColor: '#ffffff', boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)' }}>
    <h2 style={{ marginBottom: '20px', fontSize: '1.5rem', borderBottom: '2px solid #ddd', paddingBottom: '10px' }}>
      Enrolled Students
    </h2>
    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
      <thead>
        <tr style={{ backgroundColor: '#f9fafb', textAlign: 'left' }}>
          <th style={{ padding: '12px', borderBottom: '2px solid #e5e7eb' }}>ID</th>
          <th style={{ padding: '12px', borderBottom: '2px solid #e5e7eb' }}>Name</th>
          <th style={{ padding: '12px', borderBottom: '2px solid #e5e7eb' }}>Grade</th>
        </tr>
      </thead>
      <tbody>
        {students.map((student, index) => (
          <tr key={student.id} style={{ backgroundColor: index % 2 === 0 ? '#ffffff' : '#f3f4f6' }}>
            <td style={{ padding: '12px', borderBottom: '1px solid #e5e7eb' }}>{student.id}</td>
            <td style={{ padding: '12px', borderBottom: '1px solid #e5e7eb' }}>{student.name}</td>
            <td style={{ padding: '12px', borderBottom: '1px solid #e5e7eb' }}>{student.grade ?? 'N/A'}</td>
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
