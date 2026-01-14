import React, { useState, useEffect } from 'react';
import { CustomerService } from '../../services/customer.service';
import { TextField, Button, Typography, Container } from '@mui/material';

const CustomerProfile = () => {
  const [customer, setCustomer] = useState({ firstName: '', lastName: '', customerCode: '' });
  const [loading, setLoading] = useState(true);
  
  const customerId = CustomerService.getCurrentCustomerId();

  useEffect(() => {
    CustomerService.getById(customerId)
      .then(res => setCustomer(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [customerId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    await CustomerService.update(customerId, customer);
    alert('Datos actualizados');
  };

  if (loading) return <p>Cargando...</p>;

  return (
    <Container maxWidth="sm" style={{ marginTop: '20px' }}>
      <Typography variant="h4" gutterBottom>Mi Perfil</Typography>
      <form onSubmit={handleSubmit}>
        <TextField 
          label="Código Cliente" 
          value={customer.customerCode || ''} 
          disabled 
          fullWidth 
          margin="normal" 
        />
        <TextField 
          label="Nombre" 
          value={customer.firstName || ''} 
          onChange={(e) => setCustomer({...customer, firstName: e.target.value})} 
          fullWidth 
          margin="normal" 
        />
        <TextField 
          label="Apellido" 
          value={customer.lastName || ''} 
          onChange={(e) => setCustomer({...customer, lastName: e.target.value})} 
          fullWidth 
          margin="normal" 
        />
        <Button type="submit" variant="contained" color="primary" fullWidth style={{ marginTop: '20px' }}>
          Guardar Cambios
        </Button>
      </form>
    </Container>
  );
};

export default CustomerProfile;
