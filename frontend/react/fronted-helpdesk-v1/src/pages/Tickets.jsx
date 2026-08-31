import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { listarChamados } from "../api/ticketApi";
import { STATUS, PRIORIDADES, CATEGORIAS } from "../constants";

export default function Tickets() {
  const [chamados, setChamados] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const [busca, setBusca] = useState("");
  const [filtroStatus, setFiltroStatus] = useState("");
  const [filtroPrioridade, setFiltroPrioridade] = useState("");
  const [filtroCategoria, setFiltroCategoria] = useState("");

  useEffect(() => {
    listarChamados()
      .then((dados) => setChamados(dados))
      .catch(() => setErro("Não foi possível carregar os chamados."))
      .finally(() => setCarregando(false));
  }, []);

  
  const chamadosFiltrados = chamados.filter((c) => {
    const texto = busca.trim().toLowerCase();
    const casaBusca =
      texto === "" ||
      c.title.toLowerCase().includes(texto) ||
      (c.description ?? "").toLowerCase().includes(texto);
    const casaStatus = filtroStatus === "" || c.status === filtroStatus;
    const casaPrioridade = filtroPrioridade === "" || c.priority === filtroPrioridade;
    const casaCategoria = filtroCategoria === "" || c.category === filtroCategoria;
    return casaBusca && casaStatus && casaPrioridade && casaCategoria;
  });

  if (carregando) return <p>Carregando...</p>;
  if (erro) return <p className="erro">{erro}</p>;

  return (
    <div>
      <h2>Chamados</h2>

      <Link to="/chamados/novo" className="btn">+ Novo chamado</Link>

      <div className="filtros">
        <input
          type="text"
          placeholder="Pesquisar por título ou descrição..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />
        <select value={filtroStatus} onChange={(e) => setFiltroStatus(e.target.value)}>
          <option value="">Status (todos)</option>
          {STATUS.map((s) => <option key={s} value={s}>{s}</option>)}
        </select>
        <select value={filtroPrioridade} onChange={(e) => setFiltroPrioridade(e.target.value)}>
          <option value="">Prioridade (todas)</option>
          {PRIORIDADES.map((p) => <option key={p} value={p}>{p}</option>)}
        </select>
        <select value={filtroCategoria} onChange={(e) => setFiltroCategoria(e.target.value)}>
          <option value="">Categoria (todas)</option>
          {CATEGORIAS.map((c) => <option key={c} value={c}>{c}</option>)}
        </select>
      </div>

      {chamadosFiltrados.length === 0 ? (
        <p>Nenhum chamado encontrado.</p>
      ) : (
        <table className="tabela">
          <thead>
            <tr>
              <th>Título</th><th>Status</th><th>Prioridade</th><th>Categoria</th><th></th>
            </tr>
          </thead>
          <tbody>
            {chamadosFiltrados.map((c) => (
              <tr key={c.id}>
                <td>{c.title}</td>
                <td>{c.status}</td>
                <td>{c.priority}</td>
                <td>{c.category}</td>
                <td><Link to={`/chamados/${c.id}`}>Detalhes</Link></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}