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
import CreateAccount from './pages/CreateAccount';
import ViewEnrolledCourses from './pages/ViewEnrolledCourses';
import FacultyDropCourses from './pages/FacultyDropCourses';
import EditCourses from './pages/EditCourses';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route 
          path="/login" 
          element={
            <FeatureCheck serviceUrl='http://localhost:8080/api/auth/health'>
              <Login />
            </FeatureCheck>
          } 
        />
        <Route 
          path="/register" 
          element={
            <FeatureCheck serviceUrl='http://localhost:8085/api/auth/health'>
              <CreateAccount />
            </FeatureCheck>
          } 
        />

        <Route path="/error" element={<ErrorPage />} />
        {/* Student Routes */}
        <Route
          path="/courses"
          element={
            <ProtectedRoute allowedRoles={['STUDENT', 'FACULTY']}>
              <FeatureCheck serviceUrl='http://localhost:8081/view-courses/health'>
                <Courses />
              </FeatureCheck>
            </ProtectedRoute>
          }
        />
        <Route
          path="/grades"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <FeatureCheck serviceUrl='http://localhost:8083/api/view-grades/health'>
                <Grades />
              </FeatureCheck>
            </ProtectedRoute>
          }
        />
        <Route
          path="/enroll"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <FeatureCheck serviceUrl='http://localhost:8082/api/enroll/health'>
                <Enroll />
              </FeatureCheck>
            </ProtectedRoute>
          }
        />
        <Route
          path="/enrolled-courses"
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <FeatureCheck serviceUrl='http://localhost:8086/api/view-enrolled/health'>
                <ViewEnrolledCourses />
              </FeatureCheck>
            </ProtectedRoute>
          }
        />
        
        {/* Faculty Routes */}
        <Route
          path="/upload-grades"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <FeatureCheck serviceUrl='http://localhost:8084/professors/health'>
                <UploadGrades />
              </FeatureCheck>
            </ProtectedRoute>
          }
        />
        <Route
          path="/create-course"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <FeatureCheck serviceUrl='http://localhost:8087/api/create-course/health'>
                <CreateCourse />
              </FeatureCheck>
            </ProtectedRoute>
          }
        />
        <Route
          path="/edit-courses"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <FeatureCheck serviceUrl='http://localhost:8088/api/edit-courses/health'>
                <EditCourses />
              </FeatureCheck>
            </ProtectedRoute>
          }
        />
        <Route
          path="/drop-courses"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <FeatureCheck serviceUrl='http://localhost:8090/professors/health'>
                <FacultyDropCourses />
              </FeatureCheck>
            </ProtectedRoute>
          }
        />

        {/* Error Routes */}
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
        <Route path="/error/service-unavailable" element={
          <ErrorPage state={{
            feature: true,
            message: 'The service is currently unavailable. Please try again later.'
          }} />
        } />
        
        {/* Catch-all route */}
        <Route path="*" element={<Navigate to="/error/not-found" replace />} />
      </Routes>
    </Router>
  );
}

export default App;