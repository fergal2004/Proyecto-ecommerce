import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { Container, TextField, Button, Paper, Typography, Box, Alert, CircularProgress } from '@mui/material';
import PersonAddIcon from '@mui/icons-material/PersonAdd';

const RegisterView = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    phone: '',
    address: '',
    taxId: ''
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // 1. Enviamos los datos al backend (incluyendo la password)
      await axios.post('http://localhost:8080/customer/api/v1/customers', formData);
      
      // 2. Si todo sale bien
      alert("¡Cuenta creada con éxito! Ahora inicia sesión.");
      navigate('/login');

    } catch (err) {
      console.error(err);
      if (err.response && err.response.data) {
          setError("Error: " + (typeof err.response.data === 'string' ? err.response.data : JSON.stringify(err.response.data)));
      } else {
          setError("Error al registrarse. Verifica la conexión.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="xs" sx={{ mt: 8, mb: 4 }}>
      <Paper elevation={3} sx={{ p: 4, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Box sx={{ m: 1, bgcolor: 'secondary.main', borderRadius: '50%', p: 1 }}>
          <PersonAddIcon sx={{ color: 'white' }} />
        </Box>
        <Typography component="h1" variant="h5" fontWeight="bold">Crear Cuenta</Typography>

        {error && <Alert severity="error" sx={{ width: '100%', mb: 2, mt: 2 }}>{error}</Alert>}

        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3, width: '100%' }}>
          <TextField name="name" label="Nombre Completo" required fullWidth margin="normal" onChange={handleChange} />
          <TextField name="email" label="Correo Electrónico" required fullWidth margin="normal" onChange={handleChange} />
          
          {/* CAMPO DE CONTRASEÑA AGREGADO */}
          <TextField name="password" label="Contraseña" type="password" required fullWidth margin="normal" onChange={handleChange} />

          <TextField name="taxId" label="Cédula / RUC" required fullWidth margin="normal" onChange={handleChange} />
          <TextField name="phone" label="Celular (09...)" required fullWidth margin="normal" onChange={handleChange} />
          <TextField name="address" label="Dirección" required fullWidth margin="normal" onChange={handleChange} />

          <Button type="submit" fullWidth variant="contained" color="secondary" size="large" disabled={loading} sx={{ mt: 3, mb: 2 }}>
            {loading ? <CircularProgress size={24} /> : "REGISTRARSE"}
          </Button>

          <Box textAlign="center">
            <Link to="/login" style={{ textDecoration: 'none', color: '#1976d2' }}>
              ¿Ya tienes cuenta? Inicia sesión
            </Link>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
};

export default RegisterView;