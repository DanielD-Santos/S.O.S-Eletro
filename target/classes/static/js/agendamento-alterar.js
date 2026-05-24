(function () {
    var API = "";

    function showFeedback(type, message) {
        var box = document.getElementById("formFeedback");
        if (!box) return;
        box.classList.remove("form-feedback--hidden", "form-feedback--success", "form-feedback--error");
        if (!message) {
            box.classList.add("form-feedback--hidden");
            box.textContent = "";
            return;
        }
        box.textContent = message;
        box.classList.add(type === "success" ? "form-feedback--success" : "form-feedback--error");
    }

    function clearErrors() {
        ["errProtocolo", "errTelCad", "errTipo", "errNome", "errTelNovo"].forEach(function (id) {
            var el = document.getElementById(id);
            if (el) el.textContent = "";
        });
    }

    function digitsOnly(s) {
        return String(s || "").replace(/\D/g, "");
    }

    function parseApiError(body) {
        if (!body || typeof body !== "object") return "Não foi possível salvar.";
        if (body.message) return body.message;
        return "Erro ao enviar. Tente novamente.";
    }

    function atualizarBlocos() {
        var tipo = (document.querySelector('input[name="tipoAlteracao"]:checked') || {}).value;
        var blocoNome = document.getElementById("blocoNome");
        var blocoTel = document.getElementById("blocoTelefone");
        var nomeInput = document.getElementById("nomeNovo");
        var telInput = document.getElementById("telefoneNovo");

        if (tipo === "nome") {
            blocoNome.hidden = false;
            blocoNome.classList.remove("form-group--hidden");
            blocoTelefone.hidden = true;
            blocoTelefone.classList.add("form-group--hidden");
            nomeInput.required = true;
            telInput.required = false;
            telInput.value = "";
        } else if (tipo === "telefone") {
            blocoNome.hidden = true;
            blocoNome.classList.add("form-group--hidden");
            blocoTelefone.hidden = false;
            blocoTelefone.classList.remove("form-group--hidden");
            nomeInput.required = false;
            telInput.required = true;
            nomeInput.value = "";
        } else {
            blocoNome.hidden = true;
            blocoTelefone.hidden = true;
            blocoNome.classList.add("form-group--hidden");
            blocoTelefone.classList.add("form-group--hidden");
            nomeInput.required = false;
            telInput.required = false;
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll('input[name="tipoAlteracao"]').forEach(function (r) {
            r.addEventListener("change", atualizarBlocos);
        });
        atualizarBlocos();

        var form = document.getElementById("formAlterar");
        if (!form) return;

        form.addEventListener("submit", function (ev) {
            ev.preventDefault();
            clearErrors();
            showFeedback(null, "");

            var protoRaw = document.getElementById("protocolo").value.trim();
            if (!protoRaw) {
                document.getElementById("errProtocolo").textContent = "Informe o número do protocolo.";
                return;
            }

            var telCad = document.getElementById("telefoneCadastrado").value.trim();
            if (digitsOnly(telCad).length < 10) {
                document.getElementById("errTelCad").textContent = "Informe o telefone cadastrado no agendamento (DDD + número).";
                return;
            }

            var tipo = (document.querySelector('input[name="tipoAlteracao"]:checked') || {}).value;
            if (!tipo) {
                document.getElementById("errTipo").textContent = "Escolha o que deseja alterar.";
                return;
            }

            var body = { telefoneCadastrado: telCad };

            if (tipo === "nome") {
                var nome = document.getElementById("nomeNovo").value.trim();
                if (nome.length < 3) {
                    document.getElementById("errNome").textContent = "Informe o novo nome com pelo menos 3 caracteres.";
                    return;
                }
                body.nomeCliente = nome;
            } else {
                var telNovo = document.getElementById("telefoneNovo").value.trim();
                if (digitsOnly(telNovo).length < 10) {
                    document.getElementById("errTelNovo").textContent = "Informe o novo telefone válido.";
                    return;
                }
                body.telefone = telNovo;
            }

            var btn = document.getElementById("btnSubmit");
            if (btn) {
                btn.disabled = true;
                btn.textContent = "Salvando…";
            }

            fetch(API + "/api/v1/agendamentos/" + encodeURIComponent(protoRaw) + "/contato", {
                method: "PATCH",
                headers: { "Content-Type": "application/json", Accept: "application/json" },
                body: JSON.stringify(body)
            })
                .then(function (r) {
                    return r.text().then(function (text) {
                        var j = null;
                        if (text) {
                            try {
                                j = JSON.parse(text);
                            } catch (e) {}
                        }
                        if (!r.ok) throw j || { message: "Erro " + r.status };
                        return j || {};
                    });
                })
                .then(function () {
                    showFeedback("success", "Dados atualizados com sucesso.");
                    form.reset();
                    atualizarBlocos();
                })
                .catch(function (err) {
                    showFeedback("error", parseApiError(err));
                })
                .finally(function () {
                    if (btn) {
                        btn.disabled = false;
                        btn.textContent = "Salvar alteração";
                    }
                });
        });
    });
})();
