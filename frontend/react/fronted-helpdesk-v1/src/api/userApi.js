import http from "./http";

export function listarUsuarios() {
  return http.get("/api/users").then((res) => res.data);
}