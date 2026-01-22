import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth'; 
import { useCart } from '../context/CartContext';
import { Link, useNavigate } from 'react-router-dom';
import { 
  AppBar, Toolbar, Button, Badge, Box, Typography, 
  Menu, MenuItem, IconButton 
} from '@mui/material';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import AccountCircle from '@mui/icons-material/AccountCircle';

const Navbar = () => {
  const { isAuthenticated, userRole, logout, user } = useAuth();
  const { totalItems } = useCart(); // Usamos totalItems si tu contexto lo tiene, o cart.length
  const navigate = useNavigate();
  
  // Estado para el menú desplegable del perfil
  const [anchorEl, setAnchorEl] = useState(null);

  const handleMenu = (event) => setAnchorEl(event.currentTarget);
  const handleClose = () => setAnchorEl(null);

  const handleLogout = () => {
    logout();
    handleClose();
    // Al salir, podemos ir al Home o al Login, tú decides. 
    // Por ahora lo mando al login para que se note que cerró sesión.
    navigate('/login'); 
  };

  return (
    <AppBar position="sticky" sx={{ bgcolor: '#1e293b' }}>
      <Toolbar>
        {/* LOGO */}
        <Typography 
          variant="h6" 
          component={Link} 
          to="/" 
          sx={{ flexGrow: 1, textDecoration: 'none', color: 'white', fontWeight: 'bold' }}
        >
          TecnoStore 🚀
        </Typography>

        {/* ENLACES PÚBLICOS */}
        <Button color="inherit" component={Link} to="/products">Catálogo</Button>

        {/* CARRITO (Siempre visible) */}
        <IconButton color="inherit" component={Link} to="/cart" sx={{ mr: 2 }}>
          <Badge badgeContent={totalItems} color="secondary">
            <ShoppingCartIcon />
          </Badge>
        </IconButton>

        {/* LÓGICA DE LOGIN / USUARIO */}
        {isAuthenticated ? (
          <Box>
            <Button
              color="inherit"
              onClick={handleMenu}
              startIcon={<AccountCircle />}
              sx={{ textTransform: 'none' }}
            >
              {user?.name || "Mi Cuenta"}
            </Button>
            
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleClose}
            >
              <MenuItem component={Link} to="/profile" onClick={handleClose}>Mi Perfil</MenuItem>
              <MenuItem component={Link} to="/orders" onClick={handleClose}>Mis Compras</MenuItem>
              
              {/* Opcional: Panel Admin si tienes roles */}
              {userRole === 'admin' && (
                <MenuItem component={Link} to="/admin" onClick={handleClose} sx={{ color: 'orange' }}>
                  Panel Admin
                </MenuItem>
              )}
              
              <MenuItem onClick={handleLogout}>Cerrar Sesión</MenuItem>
            </Menu>
          </Box>
        ) : (
          /* --- CORRECCIÓN CLAVE AQUÍ --- */
          /* Ya NO usamos onClick={handleLogin} */
          /* Usamos Link to="/login" para ir a tu formulario bonito */
          <Button 
            variant="contained" 
            color="warning" 
            component={Link} 
            to="/login"
          >
            Iniciar Sesión
          </Button>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;