import React, { useState, useEffect } from 'react';
import Navigation from '../components/Navigation';

function Courses() {
  const [courses, setCourses] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        setLoading(true);
        const jwt = localStorage.getItem('jwt');
        
        // Log the request details
        console.log('Making request to:', 'http://localhost:8081/view-courses');
        console.log('With auth header:', `Bearer ${jwt}`);
        
        const response = await fetch('http://localhost:8081/view-courses', {
          headers: { 
            'Authorization': `Bearer ${jwt}`,
            'Accept': 'application/json'
          }
        });
        
        // Log the response details
        console.log('Response status:', response.status);
        console.log('Response headers:', Object.fromEntries([...response.headers.entries()]));
        
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
        const text = await response.text();
        console.log('Response text:', text);
        
        let data;
        try {
          data = JSON.parse(text);
          console.log('Parsed data:', data);
        } catch (e) {
          console.error('JSON parse error:', e);
          setError('Invalid response format from server');
          return;
        }
        
        if (data && data.data) {
          setCourses(data.data);
        } else if (Array.isArray(data)) {
          setCourses(data);
        } else if (data && typeof data === 'object') {
          // Try some common response patterns
          const possibleArrays = ['courses', 'items', 'results', 'list'];
          for (const key of possibleArrays) {
            if (Array.isArray(data[key])) {
              setCourses(data[key]);
              break;
            }
          }
        } else {
          setCourses([]);
        }
        setError('');
      } catch (err) {
        console.error('Failed to load courses:', err);
        setError(`Failed to load courses: ${err.message}`);
        setCourses([]);
      } finally {
        setLoading(false);
      }
    };

    fetchCourses();
  }, []);

  return (
    <div>
      <Navigation />
      <h2>Available Courses</h2>
      
      {loading && <p>Loading courses...</p>}
      
      {error && (
        <div style={{ color: 'red', margin: '10px 0', padding: '10px', backgroundColor: '#ffeeee', borderRadius: '4px' }}>
          {error}
        </div>
      )}
      
      {!loading && !error && courses.length === 0 && (
        <p>No courses available.</p>
      )}
      
      {courses.length > 0 && (
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
            {courses.map((course, index) => (
              <tr key={course.id || index} style={{ borderBottom: '1px solid #ddd' }}>
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
      )}
    </div>
  );
}

export default Courses;
