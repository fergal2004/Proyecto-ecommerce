import React from "react";
import "./css/Welcome.css";

export default function Welcome({ username, onLogout }) {
  return (
    <div className="welcome-container">
      <div className="welcome-card">
        <h1 className="welcome-title">¡Hola Mundo!</h1>
        <p className="welcome-subtitle">
          Bienvenido <strong>{username}</strong>
        </p>

        <button className="btn-logout" onClick={onLogout}>
          Cerrar sesión
        </button>
      </div>
    </div>
  );
}
