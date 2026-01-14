import api from '../api/axiosConfig';

// IMPORTANTE: Debe tener el prefijo /product para que el Gateway lo reconozca
const PRODUCT_URI = '/product/api/v1/products';

export const ProductService = {
  // Obtener todos los productos (acepta filtro de disponibles)
  getAll: (available) => api.get(PRODUCT_URI, { params: { available } }),
  
  // Obtener producto por ID
  getById: (id) => api.get(`${PRODUCT_URI}/${id}`),
  
  // Crear producto (opcional)
  create: (data) => api.post(PRODUCT_URI, data),
};