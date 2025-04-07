import React, { useEffect } from 'react';
import { Navigate } from 'react-router-dom';

function ProtectedRoute({ children, allowedRoles }) {
  const jwt = localStorage.getItem('jwt');
  const userRole = localStorage.getItem('userRole');

  // If not logged in, redirect to login
  if (!jwt) {
    return <Navigate to="/" replace />;
  }

  // If role restriction is specified and user's role is not allowed
  if (allowedRoles && !allowedRoles.includes(userRole)) {
    if (userRole === 'FACULTY') {
      return <Navigate to="/upload-grades" replace />;
    } else {
      return <Navigate to="/courses" replace />;
    }
  }

  // Otherwise, render the protected component
  return children;
}

export default ProtectedRoute; 