import http from "./http";

export function listarUsuarios() {
  return http.get("/api/users").then((res) => res.data);
}

export function buscarUsuario(id) {
  return http.get(`/api/users/${id}`).then((res) => res.data);
}