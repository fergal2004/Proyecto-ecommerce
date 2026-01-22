import React, { useState } from 'react';
import {
  Dialog, DialogTitle, DialogContent, DialogActions, TextField, Button, Grid, Typography
} from '@mui/material';

const CustomerFormDialog = ({ open, onClose, onSubmit }) => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    address: '',
    taxId: ''
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = () => {
    // Validaciones
    if (!formData.name || !formData.email || !formData.phone || !formData.address || !formData.taxId) {
      alert("Por favor completa todos los campos");
      return;
    }
    onSubmit(formData);
  };

  const handleClose = () => {
    // Limpiar formulario al cerrar
    setFormData({ name: '', email: '', phone: '', address: '', taxId: '' });
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>Datos para tu pedido</DialogTitle>
      <DialogContent dividers>
        <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
          Ingresa tus datos para procesar tu compra y enviar tu pedido.
        </Typography>

        <Grid container spacing={2}>
          <Grid item xs={12}>
            <TextField
              name="name"
              label="Nombre Completo"
              fullWidth
              required
              value={formData.name}
              onChange={handleChange}
              placeholder="Ej: Juan Pérez"
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name="email"
              label="Correo Electrónico"
              type="email"
              fullWidth
              required
              value={formData.email}
              onChange={handleChange}
              placeholder="Ej: juan@email.com"
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name="taxId"
              label="Cédula / RUC"
              fullWidth
              required
              value={formData.taxId}
              onChange={handleChange}
              placeholder="Ej: 1712345678"
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name="phone"
              label="Teléfono Celular"
              fullWidth
              required
              value={formData.phone}
              onChange={handleChange}
              placeholder="Ej: 0991234567"
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name="address"
              label="Dirección de Envío"
              fullWidth
              required
              multiline
              rows={2}
              value={formData.address}
              onChange={handleChange}
              placeholder="Calle principal, secundaria y número de casa"
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="error">Cancelar</Button>
        <Button onClick={handleSubmit} variant="contained" color="primary">
          Continuar con la Compra
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CustomerFormDialog;
