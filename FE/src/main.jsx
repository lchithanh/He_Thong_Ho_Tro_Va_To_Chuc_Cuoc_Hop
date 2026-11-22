import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import LoiMoiKhachNgoai from './pages/thamdu/LoiMoiKhachNgoai.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    {window.location.pathname === '/loi-moi-khach' ? <LoiMoiKhachNgoai /> : <App />}
  </StrictMode>,
)
