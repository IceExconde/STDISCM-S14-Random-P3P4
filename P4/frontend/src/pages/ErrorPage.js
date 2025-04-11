import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

function ErrorPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { state } = location;

  const errorMessage = state?.message || 'The feature you are trying to access is temporarily unavailable.';

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
      <h1 style={{ fontSize: '4rem', color: '#dc3545', marginBottom: '20px' }}>
        {state?.feature ? 'Feature Unavailable' : '404'}
      </h1>
      <h2 style={{ fontSize: '2rem', marginBottom: '20px' }}>
        {state?.feature ? 'Feature Error' : 'Page Not Found'}
      </h2>
      <p style={{ fontSize: '1.2rem', marginBottom: '10px', color: '#6c757d' }}>
        {errorMessage}
      </p>
      <div>
        <button
          onClick={() => navigate(-1)}
          style={{
            padding: '12px 24px',
            marginRight: '10px',
            fontSize: '1.1rem',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            transition: 'background-color 0.3s'
          }}
        >
          Go Back
        </button>
      </div>
    </div>
  );
}

export default ErrorPage;