import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './pages/Login';
import Courses from './pages/Courses';
import Grades from './pages/Grades';
import Enroll from './pages/Enroll';
import UploadGrades from './pages/UploadGrades';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/courses" element={<Courses />} />
        <Route path="/grades" element={<Grades />} />
        <Route path="/enroll" element={<Enroll />} />
        <Route path="/upload-grades" element={<UploadGrades />} />
      </Routes>
    </Router>
  );
}

export default App;
