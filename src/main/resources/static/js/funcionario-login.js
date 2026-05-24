(function () {
    var TOKEN_KEY = "sos_eletro_jwt";

    function showError(msg) {
        var el = document.getElementById("loginError");
        if (!el) return;
        if (!msg) {
            el.classList.add("staff-alert--hidden");
            el.textContent = "";
            return;
        }
        el.textContent = msg;
        el.classList.remove("staff-alert--hidden");
    }

    document.addEventListener("DOMContentLoaded", function () {
        if (sessionStorage.getItem(TOKEN_KEY)) {
            window.location.href = "/funcionario-painel.html";
            return;
        }

        try {
            var qs = new URLSearchParams(window.location.search);
            if (qs.get("cadastro") === "1") {
                var h = document.getElementById("loginHint");
                if (h) {
                    h.textContent = "Cadastro concluído. Entre com seu e-mail e senha.";
                    h.classList.remove("staff-alert--hidden");
                }
            }
        } catch (e) {}

        var form = document.getElementById("formLogin");
        if (!form) return;

        form.addEventListener("submit", function (ev) {
            ev.preventDefault();
            showError("");

            var email = document.getElementById("email").value.trim();
            var password = document.getElementById("password").value;
            var btn = document.getElementById("btnLogin");

            if (!email || !password) {
                showError("Preencha e-mail e senha.");
                return;
            }

            btn.disabled = true;
            btn.textContent = "Entrando…";

            fetch("/api/v1/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json", Accept: "application/json" },
                body: JSON.stringify({ email: email, password: password })
            })
                .then(function (r) {
                    return r.text().then(function (text) {
                        var j = null;
                        try {
                            j = text ? JSON.parse(text) : null;
                        } catch (e) {}
                        if (!r.ok) {
                            throw new Error(
                                (j && j.message) ||
                                    (r.status === 401
                                        ? "E-mail ou senha inválidos."
                                        : "Falha no login.")
                            );
                        }
                        return j;
                    });
                })
                .then(function (data) {
                    if (data && data.token) {
                        sessionStorage.setItem(TOKEN_KEY, data.token);
                        window.location.href = "/funcionario-painel.html";
                    } else {
                        throw new Error("Resposta inválida do servidor.");
                    }
                })
                .catch(function (err) {
                    showError(err.message || "E-mail ou senha inválidos.");
                })
                .finally(function () {
                    btn.disabled = false;
                    btn.textContent = "Entrar";
                });
        });
    });
})();
