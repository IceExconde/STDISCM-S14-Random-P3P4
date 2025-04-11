import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import Login from './pages/Login';
import Courses from './pages/Courses';
import Grades from './pages/Grades';
import Enroll from './pages/Enroll';
import UploadGrades from './pages/UploadGrades';
import ErrorPage from './pages/ErrorPage';
import ProtectedRoute from './components/ProtectedRoute';
import FeatureCheck from './components/FeatureCheck';
import CreateCourse from './pages/CreateCourse';
import ViewEnrolledCourses from './pages/ViewEnrolledCourses';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route 
          path="/login" 
          element={
            <FeatureCheck serviceUrl="http://localhost:8080/api/auth/health">
              <Login />
            </FeatureCheck>
          } 
        />
        <Route path="/error" element={<ErrorPage />} />
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
        <Route
          path="/enrolled-courses"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <ViewEnrolledCourses />
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
        <Route
          path="/create-course"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <CreateCourse />
            </ProtectedRoute>
          }
        />

        {/* Error Routes */}
        <Route path="/error/feature-unavailable" element={
          <ErrorPage state={{
            feature: true,
            message: 'This feature is currently unavailable. Please try again later.'
          }} />
        } />
        <Route path="/error/unauthorized" element={
          <ErrorPage state={{
            feature: true,
            message: 'You do not have permission to access this feature.'
          }} />
        } />
        <Route path="/error/not-found" element={
          <ErrorPage state={{
            message: 'The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.'
          }} />
        } />
        
        {/* Catch-all route */}
        <Route path="*" element={<Navigate to="/error/not-found" replace />} />
      </Routes>
    </Router>
  );
}

export default App;