import React, { useState, useEffect } from 'react';

import Navigation from '../components/Navigation';

function EditCourses() {
    const [courses, setCourses] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);
    const [editingId, setEditingId] = useState(null);
    const [message, setMessage] = useState('');
    const [editFormDataMap, setEditFormDataMap] = useState({});

    useEffect(() => {
    const fetchProfessorCourses = async () => {
        try {
        setLoading(true);
        const jwt = localStorage.getItem('jwt');
        const professorId = localStorage.getItem('professorId');
        
        if (!professorId) {
            throw new Error('Professor ID not found in local storage');
        }

        const response = await fetch(`http://localhost:8088/api/edit-courses/professor/${professorId}`, {
            headers: { 
            'Authorization': `Bearer ${jwt}`,
            'Accept': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
        const data = await response.json();
        
        // Handle different response formats
        if (data && data.courses) {
            setCourses(data.courses);
        } else if (Array.isArray(data)) {
            setCourses(data);
        } else {
            setCourses([]);
        }
        setError('');
        } catch (err) {
        console.error('Failed to load professor courses:', err);
        setError(`Failed to load courses.`);
        setCourses([]);
        } finally {
        setLoading(false);
        }
    };

    fetchProfessorCourses();
    }, []);

    const handleEditClick = (course) => {
    setEditingId(course.classNbr);
    
    const courseFormData = {
        course: course.course,
        section: course.section,
        days: course.days,
        time: course.time,
        room: course.room,
        facultyId: course.facultyId || ''
    };
    
    setEditFormDataMap({
        ...editFormDataMap,
        [course.classNbr]: courseFormData
    });
    };

    const handleCancelClick = () => {
    setEditingId(null);
    };

    const handleEditFormChange = (e, classNbr) => {
    const { name, value } = e.target;
    
    setEditFormDataMap({
        ...editFormDataMap,
        [classNbr]: {
            ...editFormDataMap[classNbr],
            [name]: value
        }
    });
    };

    const handleSaveClick = async (course) => {
    try {
        const jwt = localStorage.getItem('jwt');
        const courseId = course.id || course.courseId || course.classNbr;
        
        if (!courseId) {
            throw new Error('Course ID not found');
        }
        
        const courseFormData = editFormDataMap[course.classNbr];
        
        console.log("Saving course with ID:", courseId);
        console.log("Updated data:", courseFormData);

        const response = await fetch(`http://localhost:8088/api/edit-courses/${courseId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${jwt}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ...courseFormData,
            id: courseId,
            classNbr: course.classNbr,
            courseId: courseId
        })
        });

        if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const updatedCourse = await response.json();
        
        setCourses(courses.map(c => 
        c.classNbr === course.classNbr ? {
            ...c,
            course: courseFormData.course,
            section: courseFormData.section,
            days: courseFormData.days,
            time: courseFormData.time,
            room: courseFormData.room
        } : c
        ));
        
        setEditingId(null);
        setError('');
        setMessage('Course updated successfully');
        
        setTimeout(() => {
            setMessage('');
        }, 3000);
    } catch (err) {
        console.error('Failed to update course:', err);
        setError(`Failed to update course.`);
    }
    };

    return (
    <div>
        <Navigation />
        <h2>Edit My Courses</h2>
        {message && (
        <div style={{ color: 'green', margin: '10px 0', padding: '10px', backgroundColor: '#e6ffed', borderRadius: '4px' }}>
            {message}
        </div>
        )}
        
        {loading && <p>Loading courses...</p>}
        
        {error && (
        <div style={{ color: 'red', margin: '10px 0', padding: '10px', backgroundColor: '#ffeeee', borderRadius: '4px' }}>
            {error}
        </div>
        )}
        
        {!loading && !error && courses.length === 0 && (
        <p>No courses assigned to you.</p>
        )}
        
        {courses.length > 0 && (
        <div style={{ marginTop: '20px', padding: '20px', borderRadius: '8px', backgroundColor: '#f9f9f9' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
                <tr style={{ borderBottom: '1px solid #ddd', backgroundColor: '#f2f2f2' }}>
                <th style={{ padding: '12px', textAlign: 'left' }}>Course Code</th>
                <th style={{ padding: '12px', textAlign: 'left' }}>Section</th>
                <th style={{ padding: '12px', textAlign: 'left' }}>Schedule</th>
                <th style={{ padding: '12px', textAlign: 'left' }}>Room</th>
                <th style={{ padding: '12px', textAlign: 'left' }}>Actions</th>
                </tr>
            </thead>
            <tbody>
                {courses.map((course) => (
                <tr key={course.classNbr} style={{ borderBottom: '1px solid #eee' }}>
                    {editingId === course.classNbr ? (
                    <>
                        <td style={{ padding: '12px' }}>
                        <input
                            type="text"
                            name="course"
                            value={editFormDataMap[course.classNbr]?.course || ''}
                            onChange={(e) => handleEditFormChange(e, course.classNbr)}
                            style={{ width: '100%', padding: '8px' }}
                        />
                        </td>
                        <td style={{ padding: '12px' }}>
                        <input
                            type="text"
                            name="section"
                            value={editFormDataMap[course.classNbr]?.section || ''}
                            onChange={(e) => handleEditFormChange(e, course.classNbr)}
                            style={{ width: '100%', padding: '8px' }}
                        />
                        </td>
                        <td style={{ padding: '12px' }}>
                        <div style={{ display: 'flex', gap: '8px' }}>
                            <input
                            type="text"
                            name="days"
                            value={editFormDataMap[course.classNbr]?.days || ''}
                            onChange={(e) => handleEditFormChange(e, course.classNbr)}
                            style={{ width: '60px', padding: '8px' }}
                            />
                            <input
                            type="text"
                            name="time"
                            value={editFormDataMap[course.classNbr]?.time || ''}
                            onChange={(e) => handleEditFormChange(e, course.classNbr)}
                            style={{ width: '120px', padding: '8px' }}
                            />
                        </div>
                        </td>
                        <td style={{ padding: '12px' }}>
                        <input
                            type="text"
                            name="room"
                            value={editFormDataMap[course.classNbr]?.room || ''}
                            onChange={(e) => handleEditFormChange(e, course.classNbr)}
                            style={{ width: '100%', padding: '8px' }}
                        />
                        </td>
                        <td style={{ padding: '12px' }}>
                        <button 
                            onClick={() => handleSaveClick(course)}
                            style={{ 
                            marginRight: '8px',
                            padding: '6px 12px',
                            backgroundColor: '#4CAF50',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px'
                            }}
                        >
                            Save
                        </button>
                        <button 
                            onClick={handleCancelClick}
                            style={{
                            padding: '6px 12px',
                            backgroundColor: '#f44336',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px'
                            }}
                        >
                            Cancel
                        </button>
                        </td>
                    </>
                    ) : (
                    <>
                        <td style={{ padding: '12px' }}>{course.course}</td>
                        <td style={{ padding: '12px' }}>{course.section}</td>
                        <td style={{ padding: '12px' }}>
                        {course.days} {course.time}
                        </td>
                        <td style={{ padding: '12px' }}>{course.room}</td>
                        <td style={{ padding: '12px' }}>
                        <button 
                            onClick={() => handleEditClick(course)}
                            style={{
                            padding: '6px 12px',
                            backgroundColor: '#2196F3',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px'
                            }}
                        >
                            Edit
                        </button>
                        </td>
                    </>
                    )}
                </tr>
                ))}
            </tbody>
            </table>
        </div>
        )}
    </div>
    );
}

export default EditCourses;