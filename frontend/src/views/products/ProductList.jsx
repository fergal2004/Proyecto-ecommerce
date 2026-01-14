import React from 'react';
import { useProducts } from '../../hooks/useProducts';
import { Link } from 'react-router-dom'; // Importante
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardActions from '@mui/material/CardActions'; // Nuevo
import Button from '@mui/material/Button'; // Nuevo
import Typography from '@mui/material/Typography';

const ProductList = () => {
  const { products, loading, error } = useProducts();

  if (loading) return <p>Cargando catálogo...</p>;
  if (error) return <p>Error al cargar productos.</p>;

  return (
    <div style={{ padding: '20px' }}>
      <Typography variant="h4" gutterBottom>Catálogo de Productos</Typography>
      <Grid container spacing={3}>
        {products.map((product) => (
          <Grid item xs={12} sm={6} md={4} key={product.id}>
            <Card>
              <CardContent>
                <Typography variant="h6">{product.name}</Typography>
                <Typography color="textSecondary">SKU: {product.sku}</Typography>
                <Typography variant="h5" color="primary">${product.price}</Typography>
                <Typography variant="body2" color={product.stock > 0 ? "success.main" : "error.main"}>
                   {product.stock > 0 ? `Stock: ${product.stock}` : "Agotado"}
                </Typography>
              </CardContent>
              {/* Nueva sección de acciones */}
              <CardActions>
                <Button size="small" component={Link} to={`/products/${product.id}`}>
                  Ver Detalle Técnico
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
    </div>
  );
};

export default ProductList;