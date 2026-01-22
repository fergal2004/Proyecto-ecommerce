import api from '../api/axiosConfig';

const PRODUCT_URI = '/product/api/v1/products';

// CORRECCIÓN: Usamos 'export const' para evitar errores de importación
export const ProductService = {
  getAll: (available) => api.get(PRODUCT_URI, { params: { available } }),
  getById: (id) => api.get(`${PRODUCT_URI}/${id}`),
  create: (data) => api.post(PRODUCT_URI, data),
  update: (id, data) => api.put(`${PRODUCT_URI}/${id}`, data),
  delete: (id) => api.delete(`${PRODUCT_URI}/${id}`),
};