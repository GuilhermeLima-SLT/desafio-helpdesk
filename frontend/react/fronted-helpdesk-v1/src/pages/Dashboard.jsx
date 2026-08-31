import { useEffect, useState } from "react";
import { listarChamados } from "../api/ticketApi";

export default function Dashboard() {
  const [chamados, setChamados] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    listarChamados()
      .then((dados) => setChamados(dados))
      .catch(() => setErro("Não foi possível carregar o dashboard."))
      .finally(() => setCarregando(false));
  }, []);

  if (carregando) return <p>Carregando...</p>;
  if (erro) return <p className="erro">{erro}</p>;

  const total = chamados.length;
  const abertos = chamados.filter((c) => c.status === "OPEN").length;
  const emAtendimento = chamados.filter((c) => c.status === "IN_PROGRESS").length;
  const resolvidos = chamados.filter((c) => c.status === "RESOLVED").length;
  const criticos = chamados.filter((c) => c.priority === "CRITICAL").length;

  const cards = [
    { rotulo: "Total de chamados", valor: total },
    { rotulo: "Abertos", valor: abertos },
    { rotulo: "Em atendimento", valor: emAtendimento },
    { rotulo: "Resolvidos", valor: resolvidos },
    { rotulo: "Críticos", valor: criticos, destaque: true },
  ];

  return (
    <div>
      <h2>Dashboard</h2>
      <div className="cards">
        {cards.map((c) => (
          <div key={c.rotulo} className={c.destaque ? "card card-critico" : "card"}>
            <span className="card-valor">{c.valor}</span>
            <span className="card-rotulo">{c.rotulo}</span>
          </div>
        ))}
      </div>
    </div>
  );
}