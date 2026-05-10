-- Remove a tabela de ordens de serviço (dados antigos são apagados).
USE sos_eletro;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS ordem_servico;
SET FOREIGN_KEY_CHECKS = 1;
