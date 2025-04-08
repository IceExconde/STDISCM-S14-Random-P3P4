import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './pages/Login';
import Courses from './pages/Courses';
import Grades from './pages/Grades';
import Enroll from './pages/Enroll';
import UploadGrades from './pages/UploadGrades';
import ProtectedRoute from './components/ProtectedRoute';
import ErrorPage from './pages/ErrorPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        
        {/* Student Routes */}
        <Route
          path="/courses"
          element={
            <ProtectedRoute allowedRoles={['STUDENT', 'FACULTY']}>
              <Courses />
            </ProtectedRoute>
          }
        />
        <Route
          path="/grades"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <Grades />
            </ProtectedRoute>
          }
        />
        <Route
          path="/enroll"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <Enroll />
            </ProtectedRoute>
          }
        />
        
        {/* Faculty Routes */}
        <Route
          path="/upload-grades"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <UploadGrades />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<ErrorPage />} />
      </Routes>
    </Router>
  );
}

export default App;