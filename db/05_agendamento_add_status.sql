-- Rode se a coluna `status` ainda não existir (erro 1060 = coluna já existe, pode ignorar).
USE sos_eletro;
ALTER TABLE agendamento ADD COLUMN status VARCHAR(40) NOT NULL DEFAULT 'AGENDADO' AFTER observacoes;
ALTER TABLE agendamento ADD INDEX idx_agendamento_data_status (data_atendimento, status);
