import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ProductService } from '../../services/product.service'; // <--- Importación corregida con { }
import { useCart } from '../../context/CartContext';
import { Container, Typography, Button, Paper, Grid, Box, Chip } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

const ProductDetail = () => {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const { addToCart } = useCart();

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await ProductService.getById(id);
        setProduct(response.data);
      } catch (error) {
        console.error("Error al cargar producto", error);
      } finally {
        setLoading(false);
      }
    };
    fetchProduct();
  }, [id]);

  if (loading) return <p style={{ padding: 20 }}>Cargando detalle...</p>;
  if (!product) return <p style={{ padding: 20 }}>Producto no encontrado</p>;

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Button startIcon={<ArrowBackIcon />} component={Link} to="/products" sx={{ mb: 2 }}>
        Volver al Catálogo
      </Button>

      <Paper elevation={3} sx={{ p: 4 }}>
        <Grid container spacing={4}>
          <Grid item xs={12} md={6}>
            <Box 
              sx={{ 
                height: 300, 
                bgcolor: '#f5f5f5', 
                borderRadius: 2,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#aaa'
              }}
            >
              [Imagen del Producto]
            </Box>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="h4" fontWeight="bold" gutterBottom>{product.name}</Typography>
            <Chip 
              label={`SKU: ${product.sku}`} 
              size="small" 
              sx={{ mb: 2 }} 
            />
            
            <Typography variant="body1" paragraph>
              {product.description || "Sin descripción disponible para este producto."}
            </Typography>

            <Typography variant="h3" color="primary" fontWeight="bold" sx={{ my: 3 }}>
              ${product.price}
            </Typography>

            <Typography 
              variant="subtitle1" 
              color={product.stock > 0 ? "success.main" : "error.main"} 
              fontWeight="bold" 
              sx={{ mb: 3 }}
            >
              Estado: {product.stock > 0 ? `En Stock (${product.stock})` : "Agotado"}
            </Typography>

            <Button 
              variant="contained" 
              size="large" 
              fullWidth
              disabled={product.stock <= 0}
              onClick={() => addToCart(product)}
            >
              Agregar al Carrito
            </Button>
          </Grid>
        </Grid>
      </Paper>
    </Container>
  );
};

export default ProductDetail;