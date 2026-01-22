import React, { useState } from 'react';
import { useAuth } from '../../hooks/useAuth'; 
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { 
  Container, TextField, Button, Paper, Typography, Box, Alert, CircularProgress 
} from '@mui/material';
import LockOpenIcon from '@mui/icons-material/LockOpen';

const LoginView = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth(); // Tu función del contexto para guardar el token
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // 1. Llamamos a TU backend fusionado
      // NOTA LA RUTA: Ahora es /customer/api/v1/customers/login porque lo metimos en CustomerController
      const response = await axios.post('http://localhost:8080/customer/api/v1/customers/login', {
        email: email,
        password: password
      });

      // 2. Si todo sale bien, obtenemos el token
      // Buscamos 'access_token' directamente o dentro de 'token_response' por si acaso
      const data = response.data;
      const accessToken = data.access_token || data.token_response?.access_token;
      
      if (accessToken) {
        // 3. Guardamos sesión y redirigimos
        login(accessToken); // Esto guarda en localStorage y actualiza el estado
        navigate('/'); // Redirige al Home
      } else {
        setError("El servidor respondió, pero no envió el token.");
      }

    } catch (err) {
      console.error(err);
      // Intentamos mostrar el mensaje de error que manda el backend, si existe
      if (err.response && err.response.data) {
        setError(typeof err.response.data === 'string' ? err.response.data : "Credenciales incorrectas");
      } else {
        setError("Error de conexión o credenciales incorrectas.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="xs" sx={{ mt: 8, mb: 4 }}>
      <Paper elevation={3} sx={{ p: 4, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        
        {/* ICONO Y TÍTULO */}
        <Box sx={{ m: 1, bgcolor: 'primary.main', borderRadius: '50%', p: 1 }}>
          <LockOpenIcon sx={{ color: 'white' }} />
        </Box>
        <Typography component="h1" variant="h5" fontWeight="bold">
          Acceso Administrativo
        </Typography>
        <Typography variant="body2" color="textSecondary" sx={{ mb: 3 }}>
          Solo personal autorizado
        </Typography>

        {/* ALERTA DE ERROR */}
        {error && <Alert severity="error" sx={{ width: '100%', mb: 2 }}>{error}</Alert>}

        {/* FORMULARIO */}
        <Box component="form" onSubmit={handleLogin} sx={{ mt: 1, width: '100%' }}>
          <TextField
            margin="normal"
            required
            fullWidth
            label="Correo Electrónico"
            autoFocus
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            label="Contraseña"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          
          <Button
            type="submit"
            fullWidth
            variant="contained"
            size="large"
            disabled={loading}
            sx={{ mt: 3, mb: 2, py: 1.5 }}
          >
            {loading ? <CircularProgress size={24} color="inherit"/> : "INGRESAR"}
          </Button>
          
          <Box textAlign="center" mt={2}>
            <Typography variant="caption" color="textSecondary">
              Para comprar no necesitas cuenta. Simplemente agrega productos al carrito.
            </Typography>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
};

export default LoginView;