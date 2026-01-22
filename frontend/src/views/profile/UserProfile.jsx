import React from 'react';
import { useAuth } from '../../hooks/useAuth';
import { Container, Typography, Paper, Grid, Avatar, Button, Box } from '@mui/material';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';

const UserProfile = () => {
  const { user } = useAuth(); // Obtenemos datos del token

  return (
    <Container maxWidth="md" sx={{ mt: 5 }}>
      <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
        <Box display="flex" alignItems="center" gap={3} mb={4}>
          <Avatar sx={{ width: 80, height: 80, bgcolor: '#1976d2' }}>
            <AccountCircleIcon sx={{ fontSize: 60 }} />
          </Avatar>
          <Box>
            <Typography variant="h4" fontWeight="bold">Mi Perfil</Typography>
            <Typography variant="body1" color="textSecondary">{user?.email || "Usuario Invitado"}</Typography>
          </Box>
        </Box>

        <Grid container spacing={3}>
          <Grid item xs={12} sm={6}>
            <Typography variant="subtitle2" color="primary">Nombre</Typography>
            <Typography variant="h6">{user?.name || user?.preferred_username || "No disponible"}</Typography>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Typography variant="subtitle2" color="primary">Rol</Typography>
            <Typography variant="h6" sx={{ textTransform: 'capitalize' }}>
              {user?.roles?.includes('admin') || user?.roles?.includes('administradores') ? "Administrador" : "Cliente"}
            </Typography>
          </Grid>
        </Grid>

        <Box mt={4}>
          <Button variant="outlined" color="primary">Editar Información (Próximamente)</Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default UserProfile;