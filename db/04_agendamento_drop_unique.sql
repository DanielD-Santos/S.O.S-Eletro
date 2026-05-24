-- Rode apenas se a tabela ainda tiver o índice único antigo (erro 1091 pode ser ignorado).
USE sos_eletro;
ALTER TABLE agendamento DROP INDEX uk_agendamento_data_horario;
