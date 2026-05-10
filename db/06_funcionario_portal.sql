-- Migração: tabela de funcionários do portal (cadastro + login)
USE sos_eletro;

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
