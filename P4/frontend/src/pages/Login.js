import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      setError('');
      setLoading(true);
      
      console.log('Attempting login to:', 'http://localhost:8080/api/auth/login');
      
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      
      console.log('Response status:', response.status);
      console.log('Response headers:', Object.fromEntries([...response.headers.entries()]));
      
      if (!response.ok) {
        const errorText = await response.text();
        console.error('Login failed with status:', response.status, errorText);
        throw new Error(`Login failed with status: ${response.status}. ${errorText}`);
      }
      
      const text = await response.text();
      console.log('Response text:', text);
      
      let data;
      try {
        data = JSON.parse(text);
        console.log('Parsed login response:', data);
      } catch (e) {
        console.error('JSON parse error:', e);
        throw new Error('Invalid response format from server');
      }
      
      if (!data.token) {
        throw new Error('No token received from server');
      }
      
      localStorage.setItem('jwt', data.token); // Save JWT in local storage

      // // Save student ID from response if it's available
      // if (data.user && data.user._id) {
      //   localStorage.setItem('studentId', data.user._id); // Save studentId in local storage
      // }
      
      try {
        const payload = data.token.split('.')[1];
        const decodedPayload = JSON.parse(atob(payload));
        console.log('Decoded JWT payload:', decodedPayload);
        const role = decodedPayload.role;
        const user_id = decodedPayload.sub; // Assuming 'sub' contains the student ID
        
        localStorage.setItem('userRole', role); // Save role in local storage

        if (role === 'STUDENT') {
          localStorage.setItem('studentId', user_id); // Save studentId in local storage
        }
        
        // Redirect based on role
        if (role === 'FACULTY') {
          localStorage.setItem('professorId', user_id); // Save studentId in local storage
          window.location.href = '/upload-grades';
        } else {
          window.location.href = '/courses';
        }
      } catch (e) {
        console.error('Error decoding JWT:', e);
        throw new Error('Invalid token format');
      }
    } catch (err) {
      console.error('Login error:', err);
      setError(err.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '0 auto', padding: '20px' }}>
      <h2>Login</h2>
      
      {error && (
        <div style={{ 
          color: 'red', 
          margin: '10px 0', 
          padding: '10px', 
          backgroundColor: '#ffeeee', 
          borderRadius: '4px' 
        }}>
          {error}
        </div>
      )}
      
      <div style={{ marginBottom: '15px' }}>
        <input 
          type="email" 
          value={email} 
          onChange={(e) => setEmail(e.target.value)} 
          placeholder="Email"
          style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
        />
      </div>
      
      <div style={{ marginBottom: '15px' }}>
        <input 
          type="password" 
          value={password} 
          onChange={(e) => setPassword(e.target.value)} 
          placeholder="Password"
          style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
        />
      </div>
      
      <button 
        onClick={handleLogin} 
        disabled={loading}
        style={{
          padding: '10px 15px',
          backgroundColor: loading ? '#cccccc' : '#4CAF50',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: loading ? 'not-allowed' : 'pointer'
        }}
      >
        {loading ? 'Logging in...' : 'Login'}
      </button>
      
      <div style={{ marginTop: '20px', fontSize: '14px', color: '#555' }}>
        <p>For testing purposes:</p>
        <p>Student: student@example.com / password</p>
        <p>Faculty: faculty@example.com / password</p>
      </div>
      <button onClick={() => navigate('/')} style={{ marginTop: '1rem' }}>
        Back to Home
      </button>
    </div>
  );
}

export default Login;
