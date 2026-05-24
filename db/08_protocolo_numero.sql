-- Migração opcional: bases já criadas com o schema antigo (sem número de protocolo).
-- Execute manualmente após backup. Ajuste se seus nomes de colunas/tabelas forem diferentes.

USE sos_eletro;

-- Cliente: protocolo de atendimento (opcional, único quando preenchido)
ALTER TABLE cliente
    ADD COLUMN numero_protocolo VARCHAR(40) NULL AFTER endereco;

ALTER TABLE cliente
    ADD UNIQUE KEY uk_cliente_protocolo (numero_protocolo);

-- Cadastro por cliente (tabela equipamento em bases antigas): renomear número de série → número de protocolo.
-- Depois execute db/09_rename_equipamento_to_protocolo.sql para renomear a tabela para protocolo.
ALTER TABLE equipamento
    CHANGE COLUMN numero_serie numero_protocolo VARCHAR(80) NULL COMMENT 'Número de protocolo vinculado ao cadastro';

-- Agendamento: número de protocolo único (preencha registros existentes antes de endurecer NOT NULL)
ALTER TABLE agendamento
    ADD COLUMN numero_protocolo VARCHAR(40) NULL AFTER id;

-- Gerar protocolos para linhas antigas sem valor (exemplo: PRT-LEG-<id>)
UPDATE agendamento
SET numero_protocolo = CONCAT('PRT-LEG-', LPAD(id, 6, '0'))
WHERE numero_protocolo IS NULL OR numero_protocolo = '';

ALTER TABLE agendamento
    MODIFY COLUMN numero_protocolo VARCHAR(40) NOT NULL;

ALTER TABLE agendamento
    ADD UNIQUE KEY uk_agendamento_protocolo (numero_protocolo);
