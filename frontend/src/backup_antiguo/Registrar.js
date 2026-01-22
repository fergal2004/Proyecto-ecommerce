import React, { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Toast } from 'primereact/toast'; // Usamos el Toast de PrimeReact
import './css/Registrar.css'; // Incluye tus estilos personalizados

const Registro = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState({});
  const [success, setSuccess] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  const toast = useRef(null); // Refs para el Toast de PrimeReact

  // Función de validación del nombre de usuario
  const validateUsername = (username) => {
    return username.length >= 3;
  };

  // Función de validación del email
  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  // Función para mostrar/ocultar la contraseña
  const togglePasswordVisibility = () => {
    setShowPassword((prevState) => !prevState);
  };

  // Función para alternar la visibilidad de la contraseña
  const toggleConfirmPasswordVisibility = () => {
    setShowConfirmPassword((prevState) => !prevState);
  };

  // Función de validación de los campos
  const validateFields = () => {
    const errors = {};

    if (!username) {
      errors.username = 'El campo de usuario es obligatorio.';
    } else if (!validateUsername(username)) {
      errors.username = 'El usuario debe tener al menos 3 caracteres.';
    }

    if (!email) {
      errors.email = 'El campo de email es obligatorio.';
    } else if (!validateEmail(email)) {
      errors.email = 'El email ingresado no es válido.';
    }

    if (!firstName) {
      errors.firstName = 'El campo de nombre es obligatorio.';
    }

    if (!lastName) {
      errors.lastName = 'El campo de apellido es obligatorio.';
    }

    if (!password) {
      errors.password = 'El campo de contraseña es obligatorio.';
    } else if (password.length < 6) {           
      errors.password = 'La contraseña debe tener al menos 6 caracteres.';
    }

    if (!confirmPassword) {
      errors.confirmPassword = 'Debe confirmar su contraseña.';
    } else if (password !== confirmPassword) {
      errors.confirmPassword = 'Las contraseñas no coinciden.';
    }

    setError(errors);
    return Object.keys(errors).length === 0;
  };

  // Función para manejar el registro
  const handleRegister = () => {
    setError({});
    setSuccess('');
    setIsSubmitting(true);

    if (!validateFields()) {
      setIsSubmitting(false);
      return;
    }

    const requestBody = {
      username,
      email,
      firstName,
      lastName,
      password,
      confirmPassword,
    };

    fetch('/api/security/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody),
    })
      .then(response => {
        if (!response.ok) {
          return response.text().then(text => {
            throw new Error(text || 'Error al registrar el usuario');
          });
        }
        return response.json().catch(() => ({}));
      })
      .then(data => {
        setUsername('');
        setEmail('');
        setFirstName('');
        setLastName('');
        setPassword('');
        setConfirmPassword('');

        setSuccess('Usuario registrado exitosamente');
        setError({});
        toast.current.show({ severity: 'success', summary: 'Registrado', detail: 'Usted se ha registrado correctamente', life: 6000 });
        setTimeout(() => {
          setIsSubmitting(false);
          navigate('/'); // Redirige a la página de inicio (login)
        }, 3000);
      })
      .catch(error => {
        const errorMessage = error.message.includes('usuario ya existe') ? 'El usuario ya existe' : 'Error al registrar el usuario';
        toast.current.show({ severity: 'error', summary: 'Error', detail: errorMessage, life: 6000 });
        setIsSubmitting(false);
      });
  };

  // Función para manejar el regreso a la página anterior
  const handleGoBack = () => {
    navigate(-1);
  };

  return (
    <div className="register-container">
      <Toast ref={toast} position="top-center" />
      <div className="register-card">
        <h2 className="register-title">Registro</h2>
        <div className="grid grid-cols-2 gap-4">
          <div className="p-field">
            <label htmlFor="username">Usuario</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className={`p-inputtext ${error.username ? 'border-red-600' : 'border-gray-300'}`}
              disabled={isSubmitting}
            />
            {error.username && <p className="p-error">{error.username}</p>}
          </div>
          <div className="p-field">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className={`p-inputtext ${error.email ? 'border-red-600' : 'border-gray-300'}`}
              disabled={isSubmitting}
            />
            {error.email && <p className="p-error">{error.email}</p>}
          </div>
          <div className="p-field">
            <label htmlFor="firstName">Nombre</label>
            <input
              id="firstName"
              type="text"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              className={`p-inputtext ${error.firstName ? 'border-red-600' : 'border-gray-300'}`}
              disabled={isSubmitting}
            />
            {error.firstName && <p className="p-error">{error.firstName}</p>}
          </div>
          <div className="p-field">
            <label htmlFor="lastName">Apellido</label>
            <input
              id="lastName"
              type="text"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              className={`p-inputtext ${error.lastName ? 'border-red-600' : 'border-gray-300'}`}
              disabled={isSubmitting}
            />
            {error.lastName && <p className="p-error">{error.lastName}</p>}
          </div>
          <div className="p-field">
            <label htmlFor="password">Contraseña</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className={`p-inputtext ${error.password ? 'border-red-600' : 'border-gray-300'}`}
              disabled={isSubmitting}
            />
            {error.password && <p className="p-error">{error.password}</p>}
          </div>
          <div className="p-field">
            <label htmlFor="confirmPassword">Confirmar Contraseña</label>
            <input
              id="confirmPassword"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className={`p-inputtext ${error.confirmPassword ? 'border-red-600' : 'border-gray-300'}`}
              disabled={isSubmitting}
            />
            {error.confirmPassword && <p className="p-error">{error.confirmPassword}</p>}
          </div>
        </div>
        <div className="flex mt-4">
          <button onClick={handleGoBack} class="p-button p-button-danger">Regresar</button>
          <button onClick={handleRegister} className="p-button p-button-primary">
            {isSubmitting ? 'Registrando...' : 'Registrar'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default Registro;
