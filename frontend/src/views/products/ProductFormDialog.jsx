import React, { useState } from 'react';
import { 
  Dialog, DialogTitle, DialogContent, DialogActions, 
  TextField, Button, Grid, InputAdornment, Box, Typography 
} from '@mui/material';
import LabelIcon from '@mui/icons-material/Label';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import InventoryIcon from '@mui/icons-material/Inventory';
import DescriptionIcon from '@mui/icons-material/Description';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';

const ProductFormDialog = ({ open, onClose, onSave }) => {
  const [formData, setFormData] = useState({
    name: '',
    price: '',
    stock: '',
    description: '' 
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = () => {
    if (!formData.name || Number(formData.price) <= 0) {
      alert("Por favor completa un nombre válido y un precio mayor a 0.");
      return;
    }

    const autoSku = 'PROD-' + Math.floor(Math.random() * 100000);

    const payload = {
      name: formData.name,
      sku: autoSku,
      description: formData.description,
      price: parseFloat(formData.price),
      stock: parseInt(formData.stock) || 0,

      category: "ELECTRONICS"
    };

    onSave(payload);
    setFormData({ name: '', price: '', stock: '', description: '' }); 
  };

  return (
    <Dialog 
      open={open} 
      onClose={onClose} 
      maxWidth="xs"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 3, 
          boxShadow: '0px 10px 40px rgba(0,0,0,0.2)',
          overflow: 'hidden' // Asegura que el degradado respete los bordes redondos
        }
      }}
    >
      {/* CORRECCIÓN DEL ERROR DE HTML:
         Agregamos component="div" para evitar meter un h5 dentro de un h2.
         Usamos Flexbox para centrar todo el contenido.
      */}
      <DialogTitle 
        component="div" 
        sx={{ 
          background: 'linear-gradient(180deg, #1565c0 0%, #42a5f5 100%)',
          color: 'white',
          padding: 3,
          display: 'flex',           // <--- Activa Flexbox
          flexDirection: 'column',   // <--- Organiza verticalmente
          alignItems: 'center',      // <--- Centra horizontalmente
          justifyContent: 'center',  // <--- Centra verticalmente
          textAlign: 'center'        // <--- Asegura que el texto multilinea se centre
        }}
      >
        <Typography variant="h5" fontWeight="bold" component="div">
          Nuevo Producto
        </Typography>
        <Typography variant="body2" sx={{ opacity: 0.9, mt: 0.5 }} component="div">
          Ingresa los datos a continuación
        </Typography>
      </DialogTitle>

      <DialogContent sx={{ mt: 2, px: 3, pb: 2 }}>
        <Box component="form" noValidate autoComplete="off" mt={1}>
          <Grid container spacing={3}>
            
            {/* 1. NOMBRE */}
            <Grid item xs={12}>
              <TextField
                label="Nombre del Producto"
                name="name"
                fullWidth
                required
                placeholder="Ej: Auriculares Sony"
                variant="outlined"
                value={formData.name}
                onChange={handleChange}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <LabelIcon color="primary" />
                    </InputAdornment>
                  ),
                }}
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2, bgcolor: '#f9f9f9' } }}
              />
            </Grid>

            {/* 2. STOCK */}
            <Grid item xs={12}>
              <TextField
                label="Stock Disponible"
                name="stock"
                type="number"
                fullWidth
                required
                placeholder="Cantidad en bodega"
                variant="outlined"
                value={formData.stock}
                onChange={handleChange}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <InventoryIcon color="warning" />
                    </InputAdornment>
                  ),
                }}
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2, bgcolor: '#f9f9f9' } }}
              />
            </Grid>

            {/* 3. PRECIO */}
            <Grid item xs={12}>
              <TextField
                label="Precio Unitario ($)"
                name="price"
                type="number"
                fullWidth
                required
                placeholder="0.00"
                variant="outlined"
                value={formData.price}
                onChange={handleChange}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <AttachMoneyIcon color="success" />
                    </InputAdornment>
                  ),
                }}
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2, bgcolor: '#f9f9f9' } }}
              />
            </Grid>

            {/* 4. DESCRIPCIÓN */}
            <Grid item xs={12}>
              <TextField
                label="Descripción del Producto"
                name="description"
                fullWidth
                multiline
                rows={4}
                placeholder="Escribe los detalles aquí..."
                variant="outlined"
                value={formData.description}
                onChange={handleChange}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start" sx={{ alignSelf: 'flex-start', mt: 1 }}>
                      <DescriptionIcon color="action" />
                    </InputAdornment>
                  ),
                }}
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2, bgcolor: '#f9f9f9' } }}
              />
            </Grid>
          </Grid>
        </Box>
      </DialogContent>

      <DialogActions sx={{ p: 3, display: 'flex', flexDirection: 'column', gap: 1 }}>
        <Button 
          onClick={handleSubmit} 
          variant="contained" 
          color="primary"
          fullWidth
          startIcon={<CheckCircleIcon />}
          sx={{ 
            borderRadius: 2, 
            py: 1.5,
            fontSize: '1rem',
            fontWeight: 'bold',
            boxShadow: '0 4px 12px rgba(21, 101, 192, 0.4)'
          }}
        >
          GUARDAR PRODUCTO
        </Button>
        
        <Button 
          onClick={onClose} 
          variant="text" 
          color="inherit" 
          fullWidth
          sx={{ borderRadius: 2, color: '#666' }}
        >
          CANCELAR
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ProductFormDialog;