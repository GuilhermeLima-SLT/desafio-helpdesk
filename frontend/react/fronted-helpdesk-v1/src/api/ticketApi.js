import http from "./http";

export function listarChamados() {
  return http.get("/api/tickets").then((res) => res.data);
}

export function criarChamado(dados) {
  return http.post("/api/tickets", dados).then((res) => res.data);
}

export function buscarChamado(id) {
  return http.get(`/api/tickets/${id}`).then((res) => res.data);
}