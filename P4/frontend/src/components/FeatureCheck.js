// src/components/ServiceCheck.js
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function FeatureCheck({ children, serviceUrl }) {
  const [isChecking, setIsChecking] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const checkService = async () => {
      try {
        const response = await fetch(serviceUrl, {
          method: 'GET',
          headers: {
            'Accept': 'application/json'
          }
        });

        if (!response.ok) {
          throw new Error(`Service returned ${response.status}`);
        }

        const data = await response.json();
        if (data.status !== 'UP') {
          throw new Error('Service is not healthy');
        }

        setIsChecking(false);
      } catch (error) {
        console.error('Service health check failed:', error);
        navigate('/error/service-unavailable', { 
          state: { 
            message: `The service is currently unavailable. Please try again later.`,
            service: serviceUrl.split('/')[3] // Extract service name from URL
          }
        });
      }
    };

    checkService();
  }, [serviceUrl, navigate]);

  if (isChecking) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh' 
      }}>
        <div>Checking service availability...</div>
      </div>
    );
  }

  return children;
}

export default FeatureCheck;