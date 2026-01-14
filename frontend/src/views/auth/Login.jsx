import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Card from '@mui/material/Card';
import Typography from '@mui/material/Typography';

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch("http://localhost:8080/api/security/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) throw new Error("Error en login");
      
      const data = await response.json();
      
      // --- CAMBIO IMPORTANTE AQUÍ ---
      // Verificamos si existe la estructura que vimos en Postman
      const token = data.token_response?.access_token; 

      if (token) {
        // Guardamos el token real
        localStorage.setItem("access_token", token);
        // Redirigimos
        navigate("/products"); 
      } else {
        alert("El servidor respondió, pero no envió el token correctamente.");
        console.log("Respuesta recibida:", data); // Para depurar si falla
      }
      // -----------------------------

    } catch (err) {
      console.error(err);
      alert("Credenciales incorrectas o error de conexión");
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', marginTop: '50px' }}>
      <Card style={{ padding: '20px', width: '300px' }}>
        <Typography variant="h5" gutterBottom>Iniciar Sesión</Typography>
        <form onSubmit={handleSubmit}>
          <TextField 
            label="Usuario" 
            fullWidth 
            margin="normal"
            value={username} 
            onChange={(e) => setUsername(e.target.value)} 
          />
          <TextField 
            label="Contraseña" 
            type="password" 
            fullWidth 
            margin="normal"
            value={password} 
            onChange={(e) => setPassword(e.target.value)} 
          />
          <Button type="submit" variant="contained" color="primary" fullWidth style={{ marginTop: '10px' }}>
            Ingresar
          </Button>
        </form>
      </Card>
    </div>
  );
}