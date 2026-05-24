CREATE DATABASE IF NOT EXISTS sos_eletro
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sos_eletro;

-- Clientes
CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL,
    telefone VARCHAR(30) NOT NULL,
    cpf VARCHAR(14) NULL,
    endereco VARCHAR(255) NULL,
    numero_protocolo VARCHAR(40) NULL COMMENT 'Número de protocolo de atendimento vinculado ao cliente',
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cliente_email (email),
    UNIQUE KEY uk_cliente_protocolo (numero_protocolo)
) ENGINE=InnoDB;

-- Protocolos de cadastro por cliente (antigo nome da tabela: equipamento).
CREATE TABLE IF NOT EXISTS protocolo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    marca VARCHAR(80) NOT NULL,
    modelo VARCHAR(120) NOT NULL,
    tipo VARCHAR(80) NOT NULL,
    numero_protocolo VARCHAR(80) NULL COMMENT 'Número de protocolo vinculado ao cadastro',
    cliente_id BIGINT NOT NULL,
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY fk_protocolo_cliente (cliente_id),
    CONSTRAINT fk_protocolo_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Técnicos
CREATE TABLE IF NOT EXISTS tecnico (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL,
    telefone VARCHAR(30) NOT NULL,
    especialidade VARCHAR(120) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tecnico_email (email)
) ENGINE=InnoDB;

-- Funcionários do portal (login JWT)
CREATE TABLE IF NOT EXISTS funcionario_portal (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome_completo VARCHAR(160) NOT NULL,
    email VARCHAR(180) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_funcionario_portal_email (email)
) ENGINE=InnoDB;

-- Agendamentos (sem UK em data+hora: concluídos/cancelados liberam vaga; conflito tratado na aplicação)
CREATE TABLE IF NOT EXISTS agendamento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero_protocolo VARCHAR(40) NOT NULL COMMENT 'Protocolo único do agendamento (exibido ao cliente)',
    nome_cliente VARCHAR(120) NOT NULL,
    telefone VARCHAR(30) NOT NULL,
    tipo_aparelho VARCHAR(40) NOT NULL,
    tipo_conserto VARCHAR(255) NOT NULL,
    data_atendimento DATE NOT NULL,
    horario TIME NOT NULL,
    observacoes TEXT NULL,
    status VARCHAR(40) NOT NULL DEFAULT 'AGENDADO',
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_agendamento_protocolo (numero_protocolo),
    KEY idx_agendamento_data_status (data_atendimento, status)
) ENGINE=InnoDB;

-- =============================================================================
-- Dados de exemplo: 6 clientes, 6 protocolos (cadastro) e 6 agendamentos com o MESMO
-- número de protocolo em cliente, protocolo e agendamento (PRT-2026-001 … 006).
-- =============================================================================

INSERT INTO cliente (nome, email, telefone, cpf, endereco, numero_protocolo, criado_em) VALUES
('Ana Paula Silva', 'ana.paula.silva@email.com', '(81) 98888-1001', NULL, 'Vitória de Santo Antão, PE', 'PRT-2026-001', '2026-05-10 09:00:00.000000'),
('Bruno Costa', 'bruno.costa@email.com', '(81) 98888-1002', NULL, 'Vitória de Santo Antão, PE', 'PRT-2026-002', '2026-05-10 09:15:00.000000'),
('Carla Dias', 'carla.dias@email.com', '(81) 98888-1003', NULL, 'Vitória de Santo Antão, PE', 'PRT-2026-003', '2026-05-10 09:30:00.000000'),
('Diego Oliveira', 'diego.oliveira@email.com', '(81) 98888-1004', NULL, 'Vitória de Santo Antão, PE', 'PRT-2026-004', '2026-05-10 09:45:00.000000'),
('Fernanda Lima', 'fernanda.lima@email.com', '(81) 98888-1005', NULL, 'Vitória de Santo Antão, PE', 'PRT-2026-005', '2026-05-10 10:00:00.000000'),
('Gustavo Mendes', 'gustavo.mendes@email.com', '(81) 98888-1006', NULL, 'Vitória de Santo Antão, PE', 'PRT-2026-006', '2026-05-10 10:15:00.000000');

INSERT INTO protocolo (marca, modelo, tipo, numero_protocolo, cliente_id, criado_em) VALUES
('Samsung', 'Galaxy A54', 'Smartphone', 'PRT-2026-001', 1, '2026-05-10 09:00:00.000000'),
('Dell', 'Inspiron 15', 'Notebook', 'PRT-2026-002', 2, '2026-05-10 09:15:00.000000'),
('HP', 'LaserJet Pro', 'Impressora', 'PRT-2026-003', 3, '2026-05-10 09:30:00.000000'),
('Apple', 'iPad 10', 'Tablet', 'PRT-2026-004', 4, '2026-05-10 09:45:00.000000'),
('Lenovo', 'IdeaCentre', 'Computador', 'PRT-2026-005', 5, '2026-05-10 10:00:00.000000'),
('Motorola', 'Edge 40', 'Smartphone', 'PRT-2026-006', 6, '2026-05-10 10:15:00.000000');

INSERT INTO agendamento (
    numero_protocolo, nome_cliente, telefone, tipo_aparelho, tipo_conserto,
    data_atendimento, horario, observacoes, status, criado_em
) VALUES
('PRT-2026-001', 'Ana Paula Silva', '(81) 98888-1001', 'CELULAR', 'Troca de tela / display', '2026-06-02', '09:00:00', NULL, 'AGENDADO', '2026-05-10 09:00:00.000000'),
('PRT-2026-002', 'Bruno Costa', '(81) 98888-1002', 'NOTEBOOK', 'Manutenção / limpeza / upgrade', '2026-06-02', '10:30:00', 'Upgrade de SSD', 'EM_ATENDIMENTO', '2026-05-10 09:15:00.000000'),
('PRT-2026-003', 'Carla Dias', '(81) 98888-1003', 'IMPRESSORA', 'Impressão / toner / papel', '2026-06-03', '08:00:00', NULL, 'CONCLUIDO', '2026-05-10 09:30:00.000000'),
('PRT-2026-004', 'Diego Oliveira', '(81) 98888-1004', 'TABLET', 'Troca de bateria', '2026-06-03', '14:30:00', NULL, 'AGENDADO', '2026-05-10 09:45:00.000000'),
('PRT-2026-005', 'Fernanda Lima', '(81) 98888-1005', 'COMPUTADOR', 'Formatação / software / vírus', '2026-06-04', '11:00:00', 'Cliente desistiu', 'CANCELADO', '2026-05-10 10:00:00.000000'),
('PRT-2026-006', 'Gustavo Mendes', '(81) 98888-1006', 'CELULAR', 'Reparo de placa / não liga', '2026-06-04', '16:30:00', NULL, 'AGENDADO', '2026-05-10 10:15:00.000000');
