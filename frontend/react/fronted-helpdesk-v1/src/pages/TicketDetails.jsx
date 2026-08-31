import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { buscarChamado, atualizarChamado, atribuirTecnico } from "../api/ticketApi";
import { buscarUsuario, listarUsuarios } from "../api/userApi";
import { STATUS, PRIORIDADES } from "../constants";

export default function TicketDetails() {
  const { id } = useParams();

  const [chamado, setChamado] = useState(null);
  const [cliente, setCliente] = useState(null);
  const [tecnico, setTecnico] = useState(null);
  const [tecnicos, setTecnicos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const [novoStatus, setNovoStatus] = useState("");
  const [novaPrioridade, setNovaPrioridade] = useState("");
  const [tecnicoSelecionado, setTecnicoSelecionado] = useState("");
  const [acaoErro, setAcaoErro] = useState(null);

  // Carrega o chamado + nomes, e sincroniza os selects editáveis com os valores atuais.
  function carregar() {
    setCarregando(true);
    return buscarChamado(id)
      .then((c) => {
        setChamado(c);
        setNovoStatus(c.status);
        setNovaPrioridade(c.priority);
        const pCliente = c.customerId ? buscarUsuario(c.customerId) : Promise.resolve(null);
        const pTecnico = c.technicianId ? buscarUsuario(c.technicianId) : Promise.resolve(null);
        return Promise.all([pCliente, pTecnico]);
      })
      .then(([cli, tec]) => { setCliente(cli); setTecnico(tec); })
      .catch(() => setErro("Não foi possível carregar o chamado."))
      .finally(() => setCarregando(false));
  }

  useEffect(() => {
    carregar();
    listarUsuarios()
      .then((us) => setTecnicos(us.filter((u) => u.role === "TECHNICIAN")))
      .catch(() => {});
  }, [id]);

  function salvarAlteracoes() {
    setAcaoErro(null);
    // O PUT exige o corpo completo (validação do backend) trocando só status e prioridade.
    const corpo = {
      title: chamado.title,
      description: chamado.description,
      category: chamado.category,
      customerId: chamado.customerId,
      priority: novaPrioridade,
      status: novoStatus,
    };
    atualizarChamado(id, corpo)
      .then(() => carregar())
      .catch((err) => setAcaoErro(mensagemDeErro(err)));
  }

  function atribuir() {
    setAcaoErro(null);
    if (!tecnicoSelecionado) return;
    atribuirTecnico(id, tecnicoSelecionado)
      .then(() => carregar())
      .catch((err) => setAcaoErro(mensagemDeErro(err)));
  }

  if (carregando && !chamado) return <p>Carregando...</p>;
  if (erro) return <p className="erro">{erro}</p>;
  if (!chamado) return null;

  return (
    <div>
      <Link to="/chamados">&larr; Voltar</Link>
      <h2>{chamado.title}</h2>

      <dl className="detalhes">
        <dt>Descrição</dt><dd>{chamado.description}</dd>
        <dt>Categoria</dt><dd>{chamado.category}</dd>
        <dt>Prioridade</dt><dd>{chamado.priority}</dd>
        <dt>Status</dt><dd>{chamado.status}</dd>
        <dt>Cliente</dt><dd>{cliente ? cliente.name : "—"}</dd>
        <dt>Técnico</dt><dd>{tecnico ? tecnico.name : "Não atribuído"}</dd>
        <dt>Criado em</dt><dd>{formatarData(chamado.createdAt)}</dd>
        <dt>Atualizado em</dt><dd>{formatarData(chamado.updatedAt)}</dd>
      </dl>

      {acaoErro && <p className="erro">{acaoErro}</p>}

      <div className="acoes-painel">
        <div className="acao">
          <h3>Alterar status / prioridade</h3>
          <label>Status
            <select value={novoStatus} onChange={(e) => setNovoStatus(e.target.value)}>
              {STATUS.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>
          </label>
          <label>Prioridade
            <select value={novaPrioridade} onChange={(e) => setNovaPrioridade(e.target.value)}>
              {PRIORIDADES.map((p) => <option key={p} value={p}>{p}</option>)}
            </select>
          </label>
          <button onClick={salvarAlteracoes}>Salvar alterações</button>
        </div>

        <div className="acao">
          <h3>Atribuir técnico</h3>
          <label>Técnico
            <select value={tecnicoSelecionado} onChange={(e) => setTecnicoSelecionado(e.target.value)}>
              <option value="">Selecione...</option>
              {tecnicos.map((t) => <option key={t.id} value={t.id}>{t.name}</option>)}
            </select>
          </label>
          <button onClick={atribuir}>Atribuir</button>
        </div>
      </div>
    </div>
  );
}

function formatarData(iso) {
  if (!iso) return "—";
  return new Date(iso).toLocaleString("pt-BR");
}

function mensagemDeErro(err) {
  const data = err.response?.data;
  if (typeof data === "string") return data;
  if (data?.detail) return data.detail;
  if (data && typeof data === "object") return Object.values(data).join(" | ");
  return "Não foi possível concluir a ação.";
}