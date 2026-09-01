-- Usuarios inicialmente definidos para sistemas subir dados mockados para usuarios utilizaveis no docker compose up
INSERT INTO users (name, email, role) VALUES
    ('Guilherme Client', 'guilherme.cliente@solutis.com.br','CLIENT'),
    ('Manuel Client', 'manuel.cliente@solutis.com.br','CLIENT'),
    ('João Técnico', 'joao.technician@solutis.com.br','TECHNICIAN'),
    ('Marcia Técnica', 'marcia.technician@solutis.com.br','TECHNICIAN'),
    ('Paulo Administrador', 'paulo.admin@solutis.com.br','ADMIN')