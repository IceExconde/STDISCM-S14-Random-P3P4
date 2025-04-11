import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

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

      const data = await response.json();
      alert('Course created successfully!');
      navigate('/courses');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
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
        facultyId: localStorage.getItem('professorId') || ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCourseData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        try {
            const jwt = localStorage.getItem('jwt');
            
            // Basic validation
            if (!courseData.course || !courseData.section || !courseData.days || 
                !courseData.time || !courseData.room || !courseData.facultyId) {
                throw new Error('All fields are required');
            }

            const response = await fetch('http://localhost:8087/api/create-course', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    course: courseData.course,
                    section: courseData.section,
                    days: courseData.days,
                    time: courseData.time,
                    room: courseData.room,
                    facultyId: courseData.facultyId
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to create course');
            }

            setSuccess('Course created successfully!');
            setCourseData({
                course: '',
                section: '',
                days: '',
                time: '',
                room: '',
                facultyId: localStorage.getItem('professorId') || ''
            });
            
            // Redirect after 2 seconds
            setTimeout(() => {
                navigate('/edit-courses');
            }, 2000);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <Navigation />
            <div style={{ maxWidth: '600px', margin: '0 auto', padding: '20px' }}>
                <h2>Create New Course</h2>
                
                {error && (
                    <div style={{ 
                        color: 'red', 
                        margin: '10px 0', 
                        padding: '10px', 
                        backgroundColor: '#ffeeee', 
                        borderRadius: '4px' 
                    }}>
                        {error}
                    </div>
                )}
                
                {success && (
                    <div style={{ 
                        color: 'green', 
                        margin: '10px 0', 
                        padding: '10px', 
                        backgroundColor: '#eeffee', 
                        borderRadius: '4px' 
                    }}>
                        {success}
                    </div>
                )}
                
                <form onSubmit={handleSubmit}>
                    <div style={{ marginBottom: '15px' }}>
                        <label style={{ display: 'block', marginBottom: '5px' }}>Course Name:</label>
                        <input
                            type="text"
                            name="course"
                            value={courseData.course}
                            onChange={handleInputChange}
                            placeholder="e.g., CCPROG1"
                            required
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                    </div>
                    
                    <div style={{ marginBottom: '15px' }}>
                        <label style={{ display: 'block', marginBottom: '5px' }}>Section:</label>
                        <input
                            type="text"
                            name="section"
                            value={courseData.section}
                            onChange={handleInputChange}
                            placeholder="e.g., S11"
                            required
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                    </div>
                    
                    <div style={{ marginBottom: '15px' }}>
                        <label style={{ display: 'block', marginBottom: '5px' }}>Days:</label>
                        <input
                            type="text"
                            name="days"
                            value={courseData.days}
                            onChange={handleInputChange}
                            placeholder="e.g., MH or TF or WS"
                            required
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                    </div>
                    
                    <div style={{ marginBottom: '15px' }}>
                        <label style={{ display: 'block', marginBottom: '5px' }}>Time:</label>
                        <input
                            type="text"
                            name="time"
                            value={courseData.time}
                            onChange={handleInputChange}
                            placeholder="e.g., 9:15-10:45"
                            required
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                    </div>
                    
                    <div style={{ marginBottom: '15px' }}>
                        <label style={{ display: 'block', marginBottom: '5px' }}>Room:</label>
                        <input
                            type="text"
                            name="room"
                            value={courseData.room}
                            onChange={handleInputChange}
                            placeholder="e.g., AG1103, G204"
                            required
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                    </div>
                    
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '20px' }}>
                        <button 
                            type="button" 
                            onClick={() => navigate('/edit-courses')}
                            style={{
                                padding: '10px 15px',
                                backgroundColor: '#f44336',
                                color: 'white',
                                border: 'none',
                                borderRadius: '4px',
                                cursor: 'pointer'
                            }}
                        >
                            Cancel
                        </button>
                        <button 
                            type="submit"
                            disabled={loading}
                            style={{
                                padding: '10px 15px',
                                backgroundColor: loading ? '#cccccc' : '#4CAF50',
                                color: 'white',
                                border: 'none',
                                borderRadius: '4px',
                                cursor: loading ? 'not-allowed' : 'pointer'
                            }}
                        >
                            {loading ? 'Creating Course...' : 'Create Course'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateCourse;