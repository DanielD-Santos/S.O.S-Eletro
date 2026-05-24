-- Base legada: renomeou-se `equipamento` para `protocolo` mas a coluna continuou `numero_serie`.
-- O JPA espera `numero_protocolo`. Execute só se o erro do MySQL mencionar coluna inexistente em `protocolo`.
--
-- Se aparecer "Unknown column 'numero_serie'", pode ignorar este script (coluna já está correta).

USE sos_eletro;

ALTER TABLE protocolo
    CHANGE COLUMN numero_serie numero_protocolo VARCHAR(80) NULL COMMENT 'Número de protocolo vinculado ao cadastro';
