import React from 'react';
import { useNavigate } from 'react-router-dom';

function ErrorPage() {
  const navigate = useNavigate();

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      minHeight: '100vh',
      padding: '20px',
      textAlign: 'center',
      backgroundColor: '#f8f9fa'
    }}>
      <h1 style={{ fontSize: '4rem', color: '#dc3545', marginBottom: '20px' }}>404</h1>
      <h2 style={{ fontSize: '2rem', marginBottom: '20px' }}>Page Not Found</h2>
      <p style={{ fontSize: '1.2rem', marginBottom: '30px', color: '#6c757d' }}>
        The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.
      </p>
      <button
        onClick={() => navigate(-1)}
        style={{
          padding: '12px 24px',
          fontSize: '1.1rem',
          backgroundColor: '#007bff',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
          transition: 'background-color 0.3s'
        }}
        onMouseOver={(e) => e.target.style.backgroundColor = '#0056b3'}
        onMouseOut={(e) => e.target.style.backgroundColor = '#007bff'}
      >
        Go Back
      </button>
    </div>
  );
}

export default ErrorPage; 