import React from 'react';
import { Box, Typography, Button, Container } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const Hero = () => {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        bgcolor: '#1e293b', // Fondo oscuro moderno
        color: 'white',
        py: 8,
        mb: 4,
        textAlign: 'center',
        backgroundImage: 'linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url(https://images.unsplash.com/photo-1472851294608-4155f2118c03?ixlib=rb-1.2.1&auto=format&fit=crop&w=1920&q=80)',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
      }}
    >
      <Container maxWidth="md">
        <Typography variant="h2" fontWeight="800" gutterBottom>
          Tecnología al Mejor Precio
        </Typography>
        <Typography variant="h5" sx={{ mb: 4, opacity: 0.9 }}>
          Descubre las últimas novedades en laptops, componentes y accesorios.
        </Typography>
        <Button 
          variant="contained" 
          size="large" 
          color="warning"
          sx={{ fontSize: '1.2rem', px: 4, borderRadius: '50px' }}
          onClick={() => navigate('/products')}
        >
          Ver Ofertas
        </Button>
      </Container>
    </Box>
  );
};

export default Hero;