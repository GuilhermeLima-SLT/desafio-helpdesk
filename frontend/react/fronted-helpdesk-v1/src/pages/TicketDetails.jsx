import { useParams } from "react-router-dom";

export default function TicketDetails() {
  const { id } = useParams();          // lê o ':id' da URL
  return <h2>Detalhes do chamado {id}</h2>;
}