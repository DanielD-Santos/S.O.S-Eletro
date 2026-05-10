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
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cliente_email (email)
) ENGINE=InnoDB;

-- Equipamentos (vinculados ao cliente)
CREATE TABLE IF NOT EXISTS equipamento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    marca VARCHAR(80) NOT NULL,
    modelo VARCHAR(120) NOT NULL,
    tipo VARCHAR(80) NOT NULL,
    numero_serie VARCHAR(80) NULL,
    cliente_id BIGINT NOT NULL,
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY fk_equipamento_cliente (cliente_id),
    CONSTRAINT fk_equipamento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id)
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
    KEY idx_agendamento_data_status (data_atendimento, status)
) ENGINE=InnoDB;
