import React, { useState, useEffect } from 'react';
import { useProducts } from '../../hooks/useProducts';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../hooks/useAuth'; 
import { ProductService } from '../../services/product.service';
import { Link } from 'react-router-dom';
import { 
  Grid, Card, CardContent, CardActions, Button, Typography, 
  TextField, Switch, FormControlLabel, Box, Alert, Container, IconButton 
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete'; 
import AddCircleIcon from '@mui/icons-material/AddCircle';
import Hero from '../../components/Hero';
import ProductFormDialog from './ProductFormDialog'; // <--- Ahora sí lo encontrará

const ProductList = () => {
  const { products, loading, error } = useProducts(); 
  const { addToCart } = useCart();
  const { isAdmin } = useAuth();
  
  const [isAdminMode, setIsAdminMode] = useState(false);
  const [editStock, setEditStock] = useState({});
  const [openDialog, setOpenDialog] = useState(false);

  useEffect(() => {
    if (isAdmin) setIsAdminMode(true);
  }, [isAdmin]);

  // --- CREAR ---
  const handleCreateProduct = async (newProduct) => {
    try {
      await ProductService.create(newProduct);
      alert("Producto creado exitosamente");
      setOpenDialog(false);
      window.location.reload();
    } catch (err) {
      console.error(err);
      alert("Error al crear. Verifica que el código/SKU no esté repetido.");
    }
  };

  // --- ACTUALIZAR ---
  const handleUpdateStock = async (product) => {
    const newStock = editStock[product.id];
    if (newStock === undefined || newStock === "") return;
    try {
      const updatedProduct = { ...product, stock: parseInt(newStock) };
      await ProductService.update(product.id, updatedProduct);
      alert(`Stock actualizado`);
      window.location.reload();
    } catch (err) { console.error(err); alert("Error al actualizar."); }
  };

  // --- ELIMINAR ---
  const handleDelete = async (id) => {
    if (window.confirm("¿Estás seguro de eliminar este producto?")) {
      try {
        await ProductService.delete(id);
        alert("Producto eliminado");
        window.location.reload();
      } catch (err) {
        console.error(err);
        alert("No se pudo eliminar (verifica si tiene órdenes asociadas).");
      }
    }
  };

  if (loading) return <p style={{ textAlign: 'center', marginTop: 20 }}>Cargando...</p>;
  if (error) return <Alert severity="error">Error de conexión.</Alert>;

  return (
    <>
      <Hero />
      <Container maxWidth="lg" sx={{ mb: 8 }}>
        
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
          <Typography variant="h4" fontWeight="bold">Nuestros Productos</Typography>
          
          <Box display="flex" gap={2}>
            {isAdmin && (
              <>
                <FormControlLabel
                  control={<Switch checked={isAdminMode} onChange={() => setIsAdminMode(!isAdminMode)} color="warning" />}
                  label="Modo Admin"
                />
                <Button 
                  variant="contained" 
                  color="success" 
                  startIcon={<AddCircleIcon />}
                  onClick={() => setOpenDialog(true)}
                >
                  Nuevo Producto
                </Button>
              </>
            )}
          </Box>
        </Box>

        <Grid container spacing={4}>
          {products.map((product) => (
            <Grid item xs={12} sm={6} md={4} key={product.id}>
              <Card 
                elevation={isAdminMode ? 4 : 2} 
                sx={{ border: isAdminMode ? '2px solid #ed6c02' : 'none', height: '100%', display: 'flex', flexDirection: 'column' }}
              >
                {isAdminMode && (
                  <Box display="flex" justifyContent="flex-end" p={1}>
                    <IconButton size="small" color="error" onClick={() => handleDelete(product.id)}>
                      <DeleteIcon />
                    </IconButton>
                  </Box>
                )}

                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography variant="h6" fontWeight="bold">{product.name}</Typography>
                  <Typography variant="body2" color="textSecondary">SKU: {product.sku}</Typography>
                  <Typography variant="h5" color="primary" fontWeight="bold" mt={1}>${product.price}</Typography>
                  
                  {!isAdminMode ? (
                    <Typography variant="body2" color={product.stock > 0 ? "success.main" : "error.main"} mt={2} fontWeight="bold">
                       {product.stock > 0 ? `Disponible: ${product.stock}` : "Agotado"}
                    </Typography>
                  ) : (
                    <Box mt={2} p={1} bgcolor="#fff3e0" borderRadius={1}>
                      <Typography variant="caption" color="warning.main">Gestión Stock:</Typography>
                      <Box display="flex" gap={1} mt={1}>
                        <TextField 
                          type="number" size="small" defaultValue={product.stock}
                          onChange={(e) => setEditStock({...editStock, [product.id]: e.target.value})}
                          sx={{ bgcolor: 'white' }}
                        />
                        <Button variant="contained" color="warning" size="small" onClick={() => handleUpdateStock(product)}>
                          Ok
                        </Button>
                      </Box>
                    </Box>
                  )}
                </CardContent>
                
                <CardActions sx={{ p: 2 }}>
                  <Button size="small" component={Link} to={`/products/${product.id}`}>Detalles</Button>
                  {!isAdminMode && (
                    <Button 
                      variant="contained" color="primary" sx={{ ml: 'auto', borderRadius: 20 }}
                      disabled={product.stock <= 0} onClick={() => addToCart(product)}
                    >
                      Agregar
                    </Button>
                  )}
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>

        <ProductFormDialog 
          open={openDialog} 
          onClose={() => setOpenDialog(false)} 
          onSave={handleCreateProduct} 
        />
      </Container>
    </>
  );
};

export default ProductList;