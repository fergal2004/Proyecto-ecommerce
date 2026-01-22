import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

// --- 1. IMPORTS DE CONTEXTO ---
import { AuthProvider } from './hooks/useAuth'; 
import { CartProvider } from './context/CartContext'; 

// --- 2. IMPORTS DE COMPONENTES GLOBALES ---
import Navbar from './components/Navbar';
import PrivateRoute from './components/PrivateRoute'; 

// --- 3. IMPORTS DE VISTAS (PÁGINAS) ---
import ProductList from './views/products/ProductList';
import ProductDetail from './views/products/ProductDetail';
import CartView from './views/orders/CartView';
import UserProfile from './views/profile/UserProfile'; 
import OrderHistory from './views/orders/OrderHistory'; 
// Login solo para administradores (Keycloak)
import LoginView from './views/auth/Login'; 

function App() {
  return (
    <AuthProvider>
      <CartProvider> 
        <Router>
          <Navbar /> 
          
          <Routes>
            {/* --- RUTAS PÚBLICAS --- */}
            <Route path="/" element={<ProductList />} />
            <Route path="/products" element={<ProductList />} />
            <Route path="/products/:id" element={<ProductDetail />} />
            <Route path="/cart" element={<CartView />} />
            
            {/* RUTA DE LOGIN (Solo Administradores) */}
            <Route path="/login" element={<LoginView />} />

            {/* --- RUTAS PRIVADAS (Requieren Token) --- */}
            <Route 
              path="/profile" 
              element={
                <PrivateRoute>
                  <UserProfile />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/orders" 
              element={
                <PrivateRoute>
                  <OrderHistory />
                </PrivateRoute>
              } 
            />
          </Routes>
        </Router>
      </CartProvider>
    </AuthProvider>
  );
}

export default App;