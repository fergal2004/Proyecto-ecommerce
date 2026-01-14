import api from '../api/axiosConfig';

const CUSTOMER_URI = '/customer/api/v1/customers';

export const CustomerService = {
  getById: (id) => api.get(`${CUSTOMER_URI}/${id}`),
  update: (id, data) => api.put(`${CUSTOMER_URI}/${id}`, data),
  // Simulación: Obtener ID del usuario logueado (hardcodeado a 1 por ahora)
  getCurrentCustomerId: () => 1 
};
