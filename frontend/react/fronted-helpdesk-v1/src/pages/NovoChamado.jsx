import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { criarChamado } from "../api/ticketApi";
import { listarUsuarios } from "../api/userApi";
import { PRIORIDADES, CATEGORIAS } from "../constants";

export default function NovoChamado() {
  const navigate = useNavigate();

  const [clientes, setClientes] = useState([]);
  const [form, setForm] = useState({
    title: "", description: "", priority: "", category: "", customerId: "",
  });
  const [erro, setErro] = useState(null);
  const [salvando, setSalvando] = useState(false);

  // Busca os usuarios e mantem so os CLIENT para o dropdown por enquanto...  
  useEffect(() => {
    listarUsuarios()
      .then((usuarios) => setClientes(usuarios.filter((u) => u.role === "CLIENT")))
      .catch(() => setErro("Não foi possível carregar os clientes."));
  }, []);

  function alterar(campo, valor) {
    setForm((atual) => ({ ...atual, [campo]: valor }));
  }

  function enviar(e) {
    e.preventDefault();
    setErro(null);
    setSalvando(true);
    criarChamado(form)
      .then(() => navigate("/chamados"))
      .catch((err) => {
        setErro(mensagemDeErro(err));
        setSalvando(false);
      });
  }

  return (
    <div>
      <h2>Novo chamado</h2>
      {erro && <p className="erro">{erro}</p>}

      <form className="formulario" onSubmit={enviar}>
        <label>Título
          <input required value={form.title}
                 onChange={(e) => alterar("title", e.target.value)}
                 onInvalid={(e) => e.target.setCustomValidity("!! Titulo é um campo obrigatório !!")}
                 onInput={(e) => e.target.setCustomValidity("")} />
        </label>

        <label>Descrição
          <textarea required value={form.description}
                    onChange={(e) => alterar("description", e.target.value)} 
                    onInvalid={(e) => e.target.setCustomValidity("!! Descrição é um campo obrigatório !!")}
                    onInput={(e) => e.target.setCustomValidity("")}/>
        </label>

        <label>Categoria
          <select required value={form.category}
                onChange={(e) => alterar("category", e.target.value)}
                onInvalid={(e) => e.target.setCustomValidity("!! Categoria é um campo obrigatório !!")}
                onInput={(e) => e.target.setCustomValidity("")}>
            <option value="">Selecione...</option>
            {CATEGORIAS.map((c) => <option key={c} value={c}>{c}</option>)}
          </select>
        </label>

        <label>Prioridade
          <select required value={form.priority}
                onChange={(e) => alterar("priority", e.target.value)}
                onInvalid={(e) => e.target.setCustomValidity("!! Prioridade é um campo obrigatório !!")}
                onInput={(e) => e.target.setCustomValidity("")}>
            <option value="">Selecione...</option>
            {PRIORIDADES.map((p) => <option key={p} value={p}>{p}</option>)}
          </select>
        </label>

        <label>Cliente
          <select required value={form.customerId}
                onChange={(e) => alterar("customerId", e.target.value)}
                onInvalid={(e) => e.target.setCustomValidity("!! Solicitante é um campo obrigatório !!")}
                onInput={(e) => e.target.setCustomValidity("")}>
            <option value="">Selecione...</option>
            {clientes.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </label>

        <div className="acoes">
          <button type="submit" disabled={salvando}>
            {salvando ? "Salvando..." : "Criar chamado"}
          </button>
          <button type="button" onClick={() => navigate("/chamados")}>Cancelar</button>
        </div>
      </form>
    </div>
  );
}

// O backend pode responder o erro em formatos diferentes, o ProblemDetail Traduz qualquer um em uma mensagem.
function mensagemDeErro(err) {
  const data = err.response?.data;
  if (typeof data === "string") return data;
  if (data?.detail) return data.detail;
  if (data && typeof data === "object") return Object.values(data).join(" | ");
  return "Não foi possível criar o chamado.";
}