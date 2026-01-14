import React from 'react';
import { Link } from "react-router-dom";
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';

const Navbar = () => {
  const handleLogout = () => {
    localStorage.removeItem("access_token");
    window.location.href = "/login";
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" style={{ flexGrow: 1 }}>
          Ecommerce
        </Typography>
        <Button color="inherit" component={Link} to="/products">Productos</Button>
        <Button color="inherit" component={Link} to="/profile">Perfil</Button>
        <Button color="inherit" onClick={handleLogout}>Salir</Button>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;