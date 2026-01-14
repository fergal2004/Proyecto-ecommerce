import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ProductService } from '../../services/product.service';
import { Container, Typography, Button, Paper, Alert, Box, CircularProgress } from '@mui/material';

const ProductDetail = () => {
  const { id } = useParams(); // Obtiene el ID de la URL
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        // Llamamos al servicio
        const response = await ProductService.getById(id);
        // Guardamos SOLO los datos (response.data)
        setProduct(response.data);
      } catch (err) {
        console.error("Error cargando producto:", err);
        setError("No se pudo cargar la información del producto.");
      } finally {
        setLoading(false);
      }
    };
    
    if (id) {
      fetchProduct();
    }
  }, [id]);

  // 1. Estado de Carga
  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
        <CircularProgress />
        <Typography style={{ marginLeft: '10px' }}>Buscando producto...</Typography>
      </Box>
    );
  }

  // 2. Estado de Error
  if (error) {
    return (
      <Container style={{ marginTop: '20px' }}>
        <Alert severity="error">{error}</Alert>
        <Button component={Link} to="/products" style={{ marginTop: '10px' }}>
          Volver al catálogo
        </Button>
      </Container>
    );
  }

  // 3. Estado Sin Datos (por si acaso)
  if (!product) return <p>Producto no encontrado.</p>;

  // 4. Vista Exitosa
  return (
    <Container maxWidth="md" style={{ marginTop: '20px' }}>
      <Paper elevation={3} style={{ padding: '30px' }}>
        <Typography variant="h4" component="h1" gutterBottom>
          {product.name}
        </Typography>
        
        <Typography variant="subtitle1" color="textSecondary" gutterBottom>
          SKU: {product.sku}
        </Typography>
        
        <Box my={3}>
          <Typography variant="body1">
            {product.description || "Sin descripción técnica disponible para este producto."}
          </Typography>
        </Box>

        <Typography variant="h5" color="primary" gutterBottom>
          Precio: ${product.price}
        </Typography>

        <Typography 
          variant="h6" 
          color={product.stock > 0 ? "success.main" : "error.main"} 
          gutterBottom
        >
          {product.stock > 0 ? `Disponible: ${product.stock} unidades` : "Agotado"}
        </Typography>
        
        <Box mt={4}>
          <Button variant="contained" color="primary" style={{ marginRight: '10px' }}>
            Añadir al Carrito
          </Button>
          <Button variant="outlined" component={Link} to="/products">
            Volver
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default ProductDetail;