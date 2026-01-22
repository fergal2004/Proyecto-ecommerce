import { useState, useEffect } from 'react';
import { ProductService } from '../services/product.service'; // <--- Importación corregida con { }

export const useProducts = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      // Traemos todos los productos (sin filtrar por stock si es posible)
      const response = await ProductService.getAll(); 
      setProducts(response.data);
      setError(null);
    } catch (err) {
      console.error("Error cargando productos:", err);
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  return { products, loading, error, setProducts, refresh: fetchProducts };
};