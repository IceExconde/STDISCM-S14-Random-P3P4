import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function FeatureCheck({ children, serviceUrl }) {
  const [serviceAvailable, setServiceAvailable] = useState(null); 
  const navigate = useNavigate();

  useEffect(() => {
    const checkService = async () => {
      try {
        const response = await fetch(serviceUrl, {
          method: 'GET',
          headers: { 'Accept': 'application/json' },
        });

        if (!response.ok) {
          setServiceAvailable(false);
        } else {
          setServiceAvailable(true);
        }
      } catch (error) {
        setServiceAvailable(false);
      }
    };

    checkService();
  }, [serviceUrl]);

  if (serviceAvailable === null) return null;

  if (serviceAvailable === false) {
    navigate('/error/service-unavailable', {
      state: {
        feature: true,
        message: 'The service is currently unavailable. Please try again later.',
      },
      replace: true,
    });
    return null;
  }

  return children;
}

export default FeatureCheck;
