(function () {
    function show(elId, msg, isError) {
        var el = document.getElementById(elId);
        if (!el) return;
        if (!msg) {
            el.classList.add("staff-alert--hidden");
            el.textContent = "";
            return;
        }
        el.textContent = msg;
        el.classList.remove("staff-alert--hidden");
        if (isError) {
            el.classList.toggle("staff-alert--error", true);
            el.classList.toggle("staff-alert--success", false);
        } else {
            el.classList.toggle("staff-alert--error", false);
            el.classList.toggle("staff-alert--success", true);
        }
    }

    function parseErr(r, text) {
        var j = null;
        try {
            j = text ? JSON.parse(text) : null;
        } catch (e) {}
        return (j && j.message) || "Erro " + r.status;
    }

    document.addEventListener("DOMContentLoaded", function () {
        var form = document.getElementById("formCadastro");
        if (!form) return;

        form.addEventListener("submit", function (ev) {
            ev.preventDefault();
            show("cadastroError", "");
            show("cadastroSuccess", "");

            var nomeCompleto = document.getElementById("nomeCompleto").value.trim();
            var email = document.getElementById("email").value.trim();
            var senha = document.getElementById("senha").value;
            var confirmar = document.getElementById("confirmarSenha").value;
            var btn = document.getElementById("btnCadastrar");

            if (!nomeCompleto || !email || !senha || !confirmar) {
                show("cadastroError", "Preencha todos os campos.", true);
                return;
            }
            if (senha !== confirmar) {
                show("cadastroError", "A senha e a confirmação não coincidem.", true);
                return;
            }
            if (senha.length < 8) {
                show("cadastroError", "A senha deve ter pelo menos 8 caracteres.", true);
                return;
            }

            btn.disabled = true;
            btn.textContent = "Cadastrando…";

            fetch("/api/v1/auth/cadastro", {
                method: "POST",
                headers: { "Content-Type": "application/json", Accept: "application/json" },
                body: JSON.stringify({
                    nomeCompleto: nomeCompleto,
                    email: email,
                    senha: senha,
                    confirmarSenha: confirmar
                })
            })
                .then(function (r) {
                    return r.text().then(function (text) {
                        if (r.status === 201) return;
                        throw new Error(parseErr(r, text));
                    });
                })
                .then(function () {
                    show("cadastroSuccess", "Conta criada. Redirecionando para o login…", false);
                    setTimeout(function () {
                        window.location.href = "/funcionario-login.html?cadastro=1";
                    }, 900);
                })
                .catch(function (err) {
                    show("cadastroError", err.message || "Não foi possível cadastrar.", true);
                })
                .finally(function () {
                    btn.disabled = false;
                    btn.textContent = "Cadastrar";
                });
        });
    });
})();
