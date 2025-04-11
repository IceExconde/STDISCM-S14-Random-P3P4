// src/components/ServiceCheck.js
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function FeatureCheck({ children, serviceUrl }) {
  const [serviceAvailable, setServiceAvailable] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const checkService = async () => {
      try {
        const response = await fetch(serviceUrl, {
          method: 'GET', // HEAD might not be properly configured
          mode: 'cors', 
          headers: {
            'Content-Type': 'application/json'
          }
        });
        if (!response.ok) {
          setServiceAvailable(false);
        }
      } catch (error) {
        console.error('Service check failed:', error);
        setServiceAvailable(false);
      }
    };

    checkService();
  }, [serviceUrl]);

  if (!serviceAvailable) {
    navigate('/error', { 
      state: { 
        message: 'Login service is currently unavailable',
        service: 'Authentication Service'
      }
    });
    return null;
  }

  return children;
}

export default FeatureCheck;