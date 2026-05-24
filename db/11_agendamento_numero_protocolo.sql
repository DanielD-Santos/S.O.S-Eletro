-- Migração: você criou `agendamento` com o db/02 antigo (sem numero_protocolo).
-- Antes: rode DESCRIBE agendamento; se já existir a coluna numero_protocolo, não execute este script.
--
-- Depois: reinicie a API.

USE sos_eletro;

ALTER TABLE agendamento
    ADD COLUMN numero_protocolo VARCHAR(40) NULL COMMENT 'Protocolo único do agendamento (exibido ao cliente)' AFTER id;

UPDATE agendamento
SET numero_protocolo = CONCAT('PRT-LEG-', LPAD(id, 6, '0'))
WHERE numero_protocolo IS NULL OR numero_protocolo = '';

ALTER TABLE agendamento
    MODIFY COLUMN numero_protocolo VARCHAR(40) NOT NULL COMMENT 'Protocolo único do agendamento (exibido ao cliente)';

ALTER TABLE agendamento
    ADD UNIQUE KEY uk_agendamento_protocolo (numero_protocolo);
