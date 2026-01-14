import React, { Suspense, lazy } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

// Importación de Componentes (Arquitectura Limpia)
import Navbar from "./components/Navbar";
import PrivateRoute from "./components/PrivateRoute";
import Loading from "./components/Loading";

// Lazy Loading (Optimización)
const Login = lazy(() => import("./views/auth/Login"));
const ProductList = lazy(() => import("./views/products/ProductList"));
const ProductDetail = lazy(() => import("./views/products/ProductDetail")); // Nueva ruta
const CustomerProfile = lazy(() => import("./views/customers/CustomerProfile"));

export default function App() {
  return (
    <Router>
      {/* El Navbar se mostrará siempre, podrías condicionarlo si quieres ocultarlo en login */}
      <Navbar />

      <Suspense fallback={<Loading />}>
        <Routes>
          <Route path="/login" element={<Login />} />
          
          <Route path="/products" element={
            <PrivateRoute>
              <ProductList />
            </PrivateRoute>
          } />
          
          {/* Nueva Ruta de Detalle */}
          <Route path="/products/:id" element={
            <PrivateRoute>
              <ProductDetail />
            </PrivateRoute>
          } />

          <Route path="/profile" element={
            <PrivateRoute>
              <CustomerProfile />
            </PrivateRoute>
          } />

          <Route path="*" element={<Navigate to="/products" />} />
        </Routes>
      </Suspense>
    </Router>
  );
}