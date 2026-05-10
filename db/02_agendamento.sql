-- Execute no MySQL Workbench se o banco sos_eletro já existir sem esta tabela.
USE sos_eletro;

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
