import React, { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import "./css/App.css";
import Welcome from "./Welcome"; // 👈 importa tu componente


export default function App({ onLogin = () => {} }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isLogged, setIsLogged] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (!localStorage.getItem("appName")) {
      localStorage.setItem("appName", "PRUEBAS");
    }
  }, []);

  const handleLoginSuccess = (user) => {
    setIsLogged(true);
    setUsername(user.username); // guardamos el nombre
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      const response = await fetch("/api/security/loginPrueba", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) throw new Error("CREDENCIALES_INVALIDAS");

      const data = await response.json();

      if (data.token && data.user) {
        const { token, user } = data;

        const rolesPorEmpresa = user.rolesPorEmpresa || {};
        if (!rolesPorEmpresa["PRUEBAS"]) {
          throw new Error("USUARIO_SIN_ACCESO");
        }

        localStorage.setItem("access_token", token);
        localStorage.setItem("username", user.username);
        localStorage.setItem("userEmail", user.email);
        localStorage.setItem(
          "rolesPorEmpresa",
          JSON.stringify(rolesPorEmpresa)
        );

        const appName = localStorage.getItem("appName");
        if (appName && rolesPorEmpresa[appName.toUpperCase()]) {
          localStorage.setItem(
            "rolesAppActual",
            JSON.stringify(rolesPorEmpresa[appName.toUpperCase()])
          );
        }

        onLogin({
          username: user.username,
          email: user.email,
          roles: rolesPorEmpresa,
        });

        // 👇 aquí llamas al éxito
        handleLoginSuccess(user);
      } else {
        throw new Error("RESPUESTA_INESPERADA");
      }
    } catch (err) {
      console.error("Error al iniciar sesión:", err);

      if (err.message === "CREDENCIALES_INVALIDAS") {
        setError("Credenciales incorrectas, por favor verificar.");
      } else if (err.message === "USUARIO_SIN_ACCESO") {
        setError("Este usuario no tiene acceso a esta aplicación, verifica.");
      } else {
        setError("Ocurrió un error inesperado, intenta nuevamente.");
      }

      setPassword("");
    } finally {
      setIsLoading(false);
    }
  };

  // 👇 Renderizado condicional
  if (isLogged) {
    return (
      <Welcome
        username={username}
        onLogout={() => {
          setIsLogged(false);
          setUsername("");
          setPassword("");
          localStorage.removeItem("access_token");
          localStorage.removeItem("rolesAppActual");
        }}
      />
    );
  }

  return (
    <div className="login-page">
      <header className="login-header">
        <div className="brand">FILL COMMERCE</div>
      </header>

      <main className="login-shell">
        <section className="left-pane">
          <div className="form-wrap">
            <h1 className="title">¡Hola!</h1>
            <p className="subtitle">Bienvenido nuevamente</p>

            <form className="form" onSubmit={handleSubmit} noValidate>
              <label className="field">
                <span className="field-label">Usuario</span>
                <input
                  name="username"
                  type="text"
                  placeholder="tu.usuario"
                  className="input"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  autoComplete="username"
                />
              </label>

              <label className="field">
                <div className="field-row">
                  <span className="field-label">Contraseña</span>
                  <Link className="link-inline" to="/olvidar-contrasenia">
                    ¿Olvidaste tu contraseña?
                  </Link>
                </div>
                <input
                  name="password"
                  type="password"
                  placeholder="••••••••"
                  className="input"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  autoComplete="current-password"
                />
              </label>

              <button
                className="btn btn-dark"
                type="submit"
                disabled={isLoading}
              >
                {isLoading ? "Validando..." : "Iniciar sesión"}
              </button>

              {error && <p className="feedback err">{error}</p>}
            </form>

            <div className="cta-row">
              <span>¿Aún no tienes cuenta?</span>
              <Link className="btn btn-link" to="/registrar">
                Crear cuenta
              </Link>
            </div>
          </div>
        </section>

        <section className="right-pane">
          <div className="glow" />
          <div className="panel-content">
            <h2 className="panel-title">
              Descubre los tesoros ocultos del mundo, paso a paso.
            </h2>
            <p className="panel-text">
              Explora, aprende y conéctate. Tu aventura comienza aquí.
            </p>
          </div>
        </section>
      </main>
    </div>
  );
}
