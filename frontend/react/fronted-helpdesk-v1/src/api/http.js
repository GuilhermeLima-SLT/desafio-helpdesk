import axios from "axios";

// Uma única fonte de verdade para a URL do backend apontando pro Gateway.
// localhost:8004
const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8004",
});

export default http;