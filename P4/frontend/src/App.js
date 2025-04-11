import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import Login from './pages/Login';
import Courses from './pages/Courses';
import Grades from './pages/Grades';
import Enroll from './pages/Enroll';
import UploadGrades from './pages/UploadGrades';
import ErrorPage from './pages/ErrorPage';
import EditCourses from './pages/EditCourses';
import CreateCourse from './pages/CreateCourse';
import CreateAccount from './pages/CreateAccount';
import ProtectedRoute from './components/ProtectedRoute';
import FeatureCheck from './components/FeatureCheck';

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
        <Route 
          path="/register" 
          element={
            <FeatureCheck serviceUrl="http://localhost:8085/api/auth/health">
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
              <Courses />
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
        <Route
          path="/edit-courses"
          element={
            <ProtectedRoute allowedRoles={['FACULTY']}>
              <EditCourses />
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