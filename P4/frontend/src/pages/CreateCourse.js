import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navigation from '../components/Navigation';

function CreateCourse() {
  const navigate = useNavigate();
  const [courseData, setCourseData] = useState({
    course: '',
    section: '',
    days: '',
    time: '',
    room: '',
    facultyId: ''
  });
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCourseData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await fetch('http://localhost:8087/api/create-course', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(courseData)
      });

      if (!response.ok) {
        throw new Error('Failed to create course');
      }

      //const data = await response.json();
      alert('Course created successfully!');
      navigate('/courses');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
      <Navigation />
      <h2>Create New Course</h2>
      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}
      
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
        <div>
          <label>Course:</label>
          <input
            type="text"
            name="course"
            value={courseData.course}
            onChange={handleChange}
            required
            placeholder="e.g., CS101"
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <div>
          <label>Section:</label>
          <input
            type="text"
            name="section"
            value={courseData.section}
            onChange={handleChange}
            required
            placeholder="e.g., A01"
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <div>
          <label>Days:</label>
          <input
            type="text"
            name="days"
            value={courseData.days}
            onChange={handleChange}
            required
            placeholder="e.g., MWF or TTh"
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <div>
          <label>Time:</label>
          <input
            type="text"
            name="time"
            value={courseData.time}
            onChange={handleChange}
            required
            placeholder="e.g., 9:00 AM - 10:30 AM"
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <div>
          <label>Room:</label>
          <input
            type="text"
            name="room"
            value={courseData.room}
            onChange={handleChange}
            required
            placeholder="e.g., Room 101"
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <div>
          <label>Faculty ID:</label>
          <input
            type="text"
            name="facultyId"
            value={courseData.facultyId}
            onChange={handleChange}
            required
            placeholder="Enter faculty ID"
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <button
          type="submit"
          style={{
            padding: '10px 20px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            marginTop: '10px'
          }}
        >
          Create Course
        </button>
      </form>
    </div>
  );
}

export default CreateCourse; 