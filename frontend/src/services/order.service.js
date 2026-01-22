import api from '../api/axiosConfig';

// Ajusta esta URI si tu Gateway tiene otro prefijo, pero suele ser /order/api/v1/orders
const ORDER_URI = '/order/api/v1/orders';

export const OrderService = {
  // Crear una nueva orden (Checkout)
  create: (orderData) => api.post(ORDER_URI, orderData),

  // Obtener historial de órdenes de un cliente
  getByCustomer: (customerId) => api.get(`${ORDER_URI}/customer/${customerId}`),
  
  // Obtener una orden específica por ID (Opcional)
  getById: (id) => api.get(`${ORDER_URI}/${id}`),
};