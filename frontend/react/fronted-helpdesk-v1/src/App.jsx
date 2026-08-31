import { Routes, Route, NavLink } from "react-router-dom";
import Dashboard from "./pages/Dashboard.jsx";
import Tickets from "./pages/Tickets.jsx";
import TicketDetails from "./pages/TicketDetails.jsx";
import NovoChamado from "./pages/NovoChamado.jsx";
import "./App.css";

export default function App() {
  return (
    <div className="app">
      <header className="topbar">
        <h1>HelpDesk</h1>
        <nav>
          <NavLink to="/" end>Dashboard</NavLink>
          <NavLink to="/chamados">Chamados</NavLink>
        </nav>
      </header>

      <main className="content">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/chamados" element={<Tickets />} />
          <Route path="/chamados/:id" element={<TicketDetails />} />
          <Route path="/chamados/novo" element={<NovoChamado />} />
        </Routes>
      </main>
    </div>
  );
}