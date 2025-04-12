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
                
                if (data && data.courses) {
                    const coursesWithIds = data.courses.map((course, index) => ({
                        ...course,
                        uniqueId: course.classNbr || `temp-id-${index}`
                    }));
                    setCourses(coursesWithIds);
                } else if (Array.isArray(data)) {
                    const coursesWithIds = data.map((course, index) => ({
                        ...course, 
                        uniqueId: course.classNbr || `temp-id-${index}`
                    }));
                    setCourses(coursesWithIds);
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

    const getStringId = (id) => {
        return id ? String(id) : '';
    };

    const getUniqueId = (course, index) => {
        return course.uniqueId || course.classNbr || `course-${index}`;
    };

    const handleEditClick = (course) => {
        const courseId = getStringId(course.uniqueId || course.classNbr);
        console.log("Editing course with ID:", courseId);
        setEditingId(null);
        setEditFormDataMap({});
        setTimeout(() => {
            setEditingId(courseId);
            
            const courseFormData = {
                course: course.course || '',
                section: course.section || '',
                days: course.days || '',
                time: course.time || '',
                room: course.room || '',
                facultyId: course.facultyId || ''
            };
            
            setEditFormDataMap({
                [courseId]: courseFormData
            });
            
            console.log("Set editing ID to:", courseId);
            console.log("Updated form data map:", { [courseId]: courseFormData });
        }, 50);
    };

    const handleCancelClick = () => {
        console.log("Canceling edit, clearing editingId");
        setEditingId(null);
    };

    const handleEditFormChange = (e, courseId) => {
        const { name, value } = e.target;
        const stringId = getStringId(courseId);
        
        console.log(`Updating form data for course ${stringId}, field: ${name}, value: ${value}`);
        
        setEditFormDataMap(prevMap => ({
            ...prevMap,
            [stringId]: {
                ...prevMap[stringId],
                [name]: value
            }
        }));
    };

    const handleSaveClick = async (course) => {
        try {
            const jwt = localStorage.getItem('jwt');
            const courseId = course.id || course.courseId || course.classNbr;
            const uniqueId = getStringId(course.uniqueId || course.classNbr);
            
            if (!courseId) {
                throw new Error('Course ID not found');
            }
            
            const courseFormData = editFormDataMap[uniqueId];
            
            if (!courseFormData) {
                throw new Error(`No form data found for course with ID: ${uniqueId}`);
            }
            
            const payload = {
                classNbr: course.classNbr,
                course: courseFormData.course,
                section: courseFormData.section,
                days: courseFormData.days,
                time: courseFormData.time,
                room: courseFormData.room,
                facultyId: courseFormData.facultyId || course.facultyId
            };
            
            console.log("Saving course with ID:", courseId);
            console.log("API endpoint:", `http://localhost:8088/api/edit-courses/${courseId}`);
            console.log("Request payload:", JSON.stringify(payload, null, 2));

            const response = await fetch(`http://localhost:8088/api/edit-courses/${courseId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${jwt}`,
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(payload)
            });
            
            console.log("Response status:", response.status);
            
            const responseText = await response.text();
            console.log("Response body:", responseText);
            
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}, Response: ${responseText}`);
            }

            let updatedCourse;
            try {
                updatedCourse = JSON.parse(responseText);
            } catch (e) {
                console.log("Response was not JSON, using text response:", responseText);
                updatedCourse = { message: responseText };
            }
            
            setCourses(prevCourses => 
                prevCourses.map(c => {
                    const isSameCourse = 
                        (c.uniqueId && c.uniqueId === course.uniqueId) || 
                        (c.classNbr && course.classNbr && String(c.classNbr) === String(course.classNbr));
                    
                    if (isSameCourse) {
                        console.log("Updating course:", c.course, "to:", courseFormData.course);
                        return {
                            ...c,
                            course: courseFormData.course,
                            section: courseFormData.section,
                            days: courseFormData.days,
                            time: courseFormData.time,
                            room: courseFormData.room
                        };
                    }
                    return c;
                })
            );
            
            setEditingId(null);
            setError('');
            setMessage('Course updated successfully');
            
            setTimeout(() => {
                setMessage('');
            }, 3000);
        } catch (err) {
            console.error('Failed to update course:', err);
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
                {courses.map((course, index) => {
                    const uniqueId = getUniqueId(course, index);
                    const isEditing = editingId === getStringId(uniqueId);
                    
                    console.log(`Rendering course ${uniqueId}, editingId: ${editingId}, isEditing: ${isEditing}`);
                    
                    return (
                        <tr key={uniqueId} style={{ borderBottom: '1px solid #eee' }}>
                            {isEditing ? (
                                <>
                                    <td style={{ padding: '12px' }}>
                                    <input
                                        type="text"
                                        name="course"
                                        value={editFormDataMap[getStringId(uniqueId)]?.course || ''}
                                        onChange={(e) => handleEditFormChange(e, uniqueId)}
                                        style={{ width: '100%', padding: '8px' }}
                                    />
                                    </td>
                                    <td style={{ padding: '12px' }}>
                                    <input
                                        type="text"
                                        name="section"
                                        value={editFormDataMap[getStringId(uniqueId)]?.section || ''}
                                        onChange={(e) => handleEditFormChange(e, uniqueId)}
                                        style={{ width: '100%', padding: '8px' }}
                                    />
                                    </td>
                                    <td style={{ padding: '12px' }}>
                                    <div style={{ display: 'flex', gap: '8px' }}>
                                        <input
                                        type="text"
                                        name="days"
                                        value={editFormDataMap[getStringId(uniqueId)]?.days || ''}
                                        onChange={(e) => handleEditFormChange(e, uniqueId)}
                                        style={{ width: '60px', padding: '8px' }}
                                        />
                                        <input
                                        type="text"
                                        name="time"
                                        value={editFormDataMap[getStringId(uniqueId)]?.time || ''}
                                        onChange={(e) => handleEditFormChange(e, uniqueId)}
                                        style={{ width: '120px', padding: '8px' }}
                                        />
                                    </div>
                                    </td>
                                    <td style={{ padding: '12px' }}>
                                    <input
                                        type="text"
                                        name="room"
                                        value={editFormDataMap[getStringId(uniqueId)]?.room || ''}
                                        onChange={(e) => handleEditFormChange(e, uniqueId)}
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
                    );
                })}
            </tbody>
            </table>
        </div>
        )}
    </div>
    );
}

export default EditCourses;