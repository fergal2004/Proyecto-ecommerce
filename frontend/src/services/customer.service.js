import api from '../api/axiosConfig';

const CUSTOMER_URI = '/customer/api/v1/customers';

export const CustomerService = {
  
  // 1. Obtener por ID
  getById: async (id) => {
    const response = await api.get(`${CUSTOMER_URI}/${id}`);
    return response.data;
  },

  // 2. Crear Cliente (¡ESTA FALTABA!)
  create: async (data) => {
    const response = await api.post(CUSTOMER_URI, data);
    return response.data;
  },

  // 3. Actualizar Cliente
  update: async (id, data) => {
    const response = await api.put(`${CUSTOMER_URI}/${id}`, data);
    return response.data;
  },

  // 4. Buscar por Email (Vital para el "Login" automático en carrito)
  getByEmail: async (email) => {
    try {
      // Intenta llamar al endpoint de búsqueda
      // NOTA: Asegúrate de tener @GetMapping("/search") en tu Backend Java
      const response = await api.get(`${CUSTOMER_URI}/search`, {
        params: { email } 
      });
      return response.data;
    } catch (error) {
      // Si el backend devuelve 404 (No encontrado), retornamos null
      // Esto le dice al CartView que debe abrir el formulario de registro
      if (error.response && error.response.status === 404) {
        return null;
      }
      throw error;
    }
  },

  // Simulación: ID hardcodeado (Útil solo para pruebas rápidas)
  getCurrentCustomerId: () => 1 
};