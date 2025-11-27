import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './routes/AppRouter.tsx'
import { AuthProvider } from './contexts/AuthContext.tsx'
import { Toaster } from 'react-hot-toast';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AuthProvider>
      <App />
      <Toaster
        position="bottom-right"
        toastOptions={{
          style: {
            backgroundColor: "#282841",
            color: "#f9fafb",
            padding: "16px",
            borderRadius: "8px",
            fontWeight: "bold",
            border: "1px solid #ffffff",
          },
          success: {
            style: { border: "2px solid #3BB143" },
          },
          error: {
            style: { border: "2px solid #CD5C5C" },
          },
        }}
      />

    </AuthProvider>
  </StrictMode>,
)
