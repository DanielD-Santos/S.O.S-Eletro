package com.soseletro.util;

public final class AgendamentoJdbcMessages {

    private AgendamentoJdbcMessages() {}

    public static final String ERRO_GRAVAR_AGENDAMENTO =
            "O MySQL recusou gravar o agendamento. No Workbench (base sos_eletro) execute DESCRIBE agendamento; "
                    + "tem de existir a coluna numero_protocolo. Se não existir, execute db/11_agendamento_numero_protocolo.sql "
                    + "(ou o script completo db/01_schema_sos_eletro.sql numa base nova), reinicie a API e tente de novo. "
                    + "Detalhe técnico: ";

    public static final String ERRO_ATUALIZAR_AGENDAMENTO =
            "O MySQL recusou gravar a alteração. Confira DESCRIBE agendamento; precisa da coluna numero_protocolo. "
                    + "Execute db/11_agendamento_numero_protocolo.sql se necessário. Detalhe: ";
}
