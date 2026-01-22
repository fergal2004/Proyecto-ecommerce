import React, { useState } from 'react';
import { useCart } from '../../context/CartContext';
import { useNavigate, Link } from 'react-router-dom';
import {
  Container, Typography, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Button, Box, IconButton, CircularProgress
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';

// --- SERVICIOS ---
import { OrderService } from '../../services/order.service';
import { CustomerService } from '../../services/customer.service';

// --- COMPONENTES ---
import CustomerFormDialog from '../../components/CustomerFormDialog';

const CartView = () => {
  const { cart, removeFromCart, clearCart, total } = useCart();
  const navigate = useNavigate();

  // Estados para controlar el modal y la carga
  const [showProfileForm, setShowProfileForm] = useState(false);
  const [loading, setLoading] = useState(false);

  // --- LÓGICA PRINCIPAL: AL DAR CLIC EN PAGAR ---
  // Guest Checkout: Siempre abre el formulario para llenar datos
  const handleCheckoutClick = () => {
    setShowProfileForm(true);
  };

  // --- FUNCIÓN 1: GUARDAR CLIENTE GUEST (Viene del Modal) ---
  const handleCreateCustomer = async (formData) => {
    try {
      setLoading(true);

      // Preparamos el objeto tal como lo espera el DTO de Java
      const newCustomer = {
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        address: formData.address,
        taxId: formData.taxId,
        taxIdType: "CEDULA",
        customerType: "REGULAR"
      };

      console.log("Creando/obteniendo cliente GUEST:", newCustomer);
      const savedCustomer = await CustomerService.create(newCustomer);

      setShowProfileForm(false);
      console.log("Cliente listo, ID:", savedCustomer.id);

      // Crear la orden con el ID del cliente
      createOrder(savedCustomer.id);

    } catch (error) {
      console.error("Error creando cliente:", error);
      const msg = error.response?.data?.message || error.response?.data || "Error desconocido";
      alert(`Error al guardar tus datos: ${msg}`);
      setLoading(false);
    }
  };

  // --- FUNCIÓN 2: CREAR LA ORDEN (Paso Final) ---
  const createOrder = async (customerId) => {
    try {
      setLoading(true);
      const orderData = {
        customerId: customerId,
        items: cart.map(item => ({
          productId: item.id,
          quantity: item.quantity
        })),
        notes: "Pedido Web - Autogenerado"
      };

      console.log("Enviando orden al backend:", orderData);
      await OrderService.create(orderData);
      
      alert("¡Compra exitosa! 🚀 Tu pedido ha sido procesado.");
      clearCart();
      navigate('/orders'); // Redirigir al historial

    } catch (error) {
      console.error("Error al crear la orden:", error);
      alert("Hubo un error al procesar tu pedido. Inténtalo de nuevo.");
    } finally {
      setLoading(false);
    }
  };

  // --- RENDERIZADO: CARRITO VACÍO ---
  if (cart.length === 0) {
    return (
      <Container sx={{ mt: 5, textAlign: 'center' }}>
        <Typography variant="h5" gutterBottom>Tu carrito está vacío 😢</Typography>
        <Button variant="contained" component={Link} to="/products">Ir a comprar</Button>
      </Container>
    );
  }

  // --- RENDERIZADO: CARRITO CON PRODUCTOS ---
  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Tu Carrito de Compras
      </Typography>
      
      <TableContainer component={Paper} elevation={2}>
         <Table>
            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
              <TableRow>
                <TableCell>Producto</TableCell>
                <TableCell align="right">Precio</TableCell>
                <TableCell align="center">Cantidad</TableCell>
                <TableCell align="right">Subtotal</TableCell>
                <TableCell align="center">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
                {cart.map(item => (
                    <TableRow key={item.id}>
                        <TableCell>{item.name}</TableCell>
                        <TableCell align="right">${item.price}</TableCell>
                        <TableCell align="center">{item.quantity}</TableCell>
                        <TableCell align="right">${(item.price * item.quantity).toFixed(2)}</TableCell>
                        <TableCell align="center">
                          <IconButton color="error" onClick={() => removeFromCart(item.id)}>
                            <DeleteIcon />
                          </IconButton>
                        </TableCell>
                    </TableRow>
                ))}
            </TableBody>
         </Table>
      </TableContainer>

      <Box display="flex" justifyContent="flex-end" alignItems="center" mt={3} p={2}>
        <Typography variant="h5" fontWeight="bold" sx={{ mr: 3 }}>
          Total: ${total}
        </Typography>
        <Button 
          variant="contained" 
          color="primary" 
          size="large" 
          startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <ShoppingBagIcon />}
          onClick={handleCheckoutClick}
          disabled={loading}
        >
          {loading ? "Procesando..." : "PAGAR AHORA"}
        </Button>
      </Box>

      {/* MODAL EMERGENTE DE REGISTRO */}
      <CustomerFormDialog 
        open={showProfileForm} 
        onClose={() => setShowProfileForm(false)} 
        onSubmit={handleCreateCustomer} 
      />
    </Container>
  );
};

export default CartView;