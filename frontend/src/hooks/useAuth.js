import { useState, useEffect, createContext, useContext } from "react";
import { jwtDecode } from "jwt-decode"; 

const AuthContext = createContext();

// CONFIGURACIÓN (Solo para el logout)
const KEYCLOAK_URL = "http://localhost:8091"; 
const REALM = "ecommerce";            
const CLIENT_ID = "ecommerce-client"; 

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [user, setUser] = useState(null);
  const [userRole, setUserRole] = useState(null);
  const [token, setToken] = useState(null);

  useEffect(() => {
    // Solo necesitamos recuperar la sesión al recargar
    const savedToken = localStorage.getItem("token");
    if (savedToken) {
      processToken(savedToken);
    }
  }, []);

  // --- FUNCIÓN PRINCIPAL: PROCESAR TOKEN ---
  const processToken = (accessToken) => {
    try {
      const decoded = jwtDecode(accessToken);
      
      // 1. Datos básicos
      setUser({
        name: decoded.name || decoded.preferred_username,
        email: decoded.email,
        id: decoded.sub
      });

      // 2. Roles
      const realmRoles = decoded.realm_access?.roles || [];
      const clientRoles = decoded.resource_access?.[CLIENT_ID]?.roles || [];
      const allRoles = [...realmRoles, ...clientRoles];

      // 3. Check Admin
      const hasAdminRole = allRoles.some(role => {
        const r = role.toLowerCase();
        return r === 'admin' || r === 'administradores' || r === 'administrador';
      });

      if (hasAdminRole) {
        setIsAdmin(true);
        setUserRole("admin");
      } else {
        setIsAdmin(false);
        setUserRole("customer");
      }

      setToken(accessToken);
      setIsAuthenticated(true);

    } catch (error) {
      console.error("Token inválido o expirado", error);
      logout(); // Si el token guardado está mal, limpiamos todo
    }
  };

  // --- NUEVA FUNCIÓN LOGIN (¡ESTO CAMBIÓ!) ---
  // Ahora recibe el token directamente desde LoginView.jsx
  const login = (accessToken) => {
    localStorage.setItem("token", accessToken);
    processToken(accessToken);
  };

  // --- LOGOUT ---
  const logout = () => {
    localStorage.removeItem("token");
    setIsAuthenticated(false);
    setIsAdmin(false);
    setUser(null);
    setUserRole(null);
    setToken(null);

    // Redirigir a Keycloak para cerrar sesión allá también
    const logoutRedirect = encodeURIComponent("http://localhost:3000");
    window.location.href = `${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/logout?post_logout_redirect_uri=${logoutRedirect}&client_id=${CLIENT_ID}`;
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, isAdmin, user, userRole, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);