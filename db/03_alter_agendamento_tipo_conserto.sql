-- Opcional: se já criou `agendamento` com tipo_conserto VARCHAR(120), alargue a coluna.
USE sos_eletro;
ALTER TABLE agendamento MODIFY COLUMN tipo_conserto VARCHAR(255) NOT NULL;
