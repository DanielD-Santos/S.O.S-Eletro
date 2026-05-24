-- Execute no MySQL Workbench se o banco sos_eletro já existir sem esta tabela.
-- Modelo alinhado ao JPA (inclui numero_protocolo — obrigatório na API atual).
USE sos_eletro;

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
