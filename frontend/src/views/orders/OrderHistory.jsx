import React, { useEffect, useState } from 'react';
import { OrderService } from '../../services/order.service';
import { 
  Container, Typography, Paper, Table, TableBody, TableCell, 
  TableContainer, TableHead, TableRow, Chip, Button, Box 
} from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { useNavigate } from 'react-router-dom';

const OrderHistory = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        // NOTA: Aquí asumimos el ID 1 o lo sacamos del Token/LocalStorage
        // Idealmente: const userId = auth.user.id;
        const customerId = 1; 
        const response = await OrderService.getByCustomer(customerId);
        setOrders(response.data);
      } catch (error) {
        console.error("Error cargando órdenes", error);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  if (loading) return <Typography sx={{ mt: 5, textAlign: 'center' }}>Cargando tus compras...</Typography>;

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#333' }}>
        Mis Compras
      </Typography>

      {orders.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography>Aún no has realizado ninguna compra.</Typography>
          <Button variant="contained" sx={{ mt: 2 }} onClick={() => navigate('/products')}>
            Ir a comprar
          </Button>
        </Paper>
      ) : (
        <TableContainer component={Paper} elevation={2}>
          <Table>
            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
              <TableRow>
                <TableCell><strong>N° Orden</strong></TableCell>
                <TableCell><strong>Fecha</strong></TableCell>
                <TableCell><strong>Estado</strong></TableCell>
                <TableCell align="right"><strong>Total</strong></TableCell>
                <TableCell align="center"><strong>Acciones</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {orders.map((order) => (
                <TableRow key={order.id} hover>
                  <TableCell>#{order.id}</TableCell>
                  <TableCell>{new Date().toLocaleDateString()} {/* Ajustar si tu back trae fecha */}</TableCell>
                  <TableCell>
                    <Chip 
                      label={order.status || "COMPLETADO"} 
                      color="success" 
                      size="small" 
                      variant="outlined" 
                    />
                  </TableCell>
                  <TableCell align="right">
                    <Typography fontWeight="bold" color="primary">
                       ${order.totalAmount || "0.00"}
                    </Typography>
                  </TableCell>
                  <TableCell align="center">
                    <Button 
                      size="small" 
                      startIcon={<VisibilityIcon />}
                      onClick={() => alert(`Detalles de orden #${order.id} (Próximamente)`)}
                    >
                      Ver Detalle
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Container>
  );
};

export default OrderHistory;