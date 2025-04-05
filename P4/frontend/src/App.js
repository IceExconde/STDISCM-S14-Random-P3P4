import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './pages/Login';
import Courses from './pages/Courses';
import Grades from './pages/Grades';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/courses" component={Courses} />
        <Route path="/grades" component={Grades} />
      </Routes>
    </Router>
  );
}

export default App;
