import React, { useState } from 'react';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  console.log('Rendering Login');

  const handleLogin = () => {
    // API call to backend Auth service to get JWT
    fetch('http://auth-service:8080/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => response.json())
    .then(data => {
      localStorage.setItem('jwt', data.token); // Save JWT in local storage
      window.location.href = '/courses'; // Redirect to courses
      // TODO: redirect to upload-grades if faculty
    })
    .catch(err => alert('Login failed: ' + err));
  };

  return (
    <div>
      <h2>Login</h2>
      <input 
        type="email" 
        value={email} 
        onChange={(e) => setEmail(e.target.value)} 
        placeholder="Email"
      />
      <input 
        type="password" 
        value={password} 
        onChange={(e) => setPassword(e.target.value)} 
        placeholder="Password"
      />
      <button onClick={handleLogin}>Login</button>
    </div>
  );
}

export default Login;
