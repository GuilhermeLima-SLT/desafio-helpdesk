import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { buscarChamado } from "../api/ticketApi";
import { buscarUsuario } from "../api/userApi";

export default function TicketDetails() {
  const { id } = useParams();

  const [chamado, setChamado] = useState(null);
  const [cliente, setCliente] = useState(null);
  const [tecnico, setTecnico] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    setCarregando(true);
    buscarChamado(id)
      .then((c) => {
        setChamado(c);
        // resolve os nomes em paralelo; técnico pode não existir ainda
        const pCliente = c.customerId ? buscarUsuario(c.customerId) : Promise.resolve(null);
        const pTecnico = c.technicianId ? buscarUsuario(c.technicianId) : Promise.resolve(null);
        return Promise.all([pCliente, pTecnico]);
      })
      .then(([cli, tec]) => {
        setCliente(cli);
        setTecnico(tec);
      })
      .catch(() => setErro("Não foi possível carregar o chamado."))
      .finally(() => setCarregando(false));
  }, [id]);

  if (carregando) return <p>Carregando...</p>;
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
    </div>
  );
}

function formatarData(iso) {
  if (!iso) return "—";
  return new Date(iso).toLocaleString("pt-BR");
}