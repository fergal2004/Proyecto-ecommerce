import React from 'react';
import CircularProgress from '@mui/material/CircularProgress';
import Box from '@mui/material/Box';

const Loading = () => (
  <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
    <CircularProgress />
    <span style={{ marginLeft: '10px' }}>Cargando sistema...</span>
  </Box>
);

export default Loading;