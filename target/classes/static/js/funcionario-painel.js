(function () {
    var TOKEN_KEY = "sos_eletro_jwt";
    var API = "";

    var TIPO_APARELHO = [
        { v: "CELULAR", l: "Celular" },
        { v: "NOTEBOOK", l: "Notebook" },
        { v: "IMPRESSORA", l: "Impressora" },
        { v: "COMPUTADOR", l: "Computador" },
        { v: "TABLET", l: "Tablet" }
    ];

    function token() {
        return sessionStorage.getItem(TOKEN_KEY);
    }

    function authHeaders() {
        var t = token();
        return {
            Accept: "application/json",
            "Content-Type": "application/json",
            Authorization: t ? "Bearer " + t : ""
        };
    }

    function redirectLogin() {
        sessionStorage.removeItem(TOKEN_KEY);
        window.location.href = "/funcionario-login.html";
    }

    function showMsg(ok, text) {
        var s = document.getElementById("msgSuccess");
        var e = document.getElementById("msgError");
        if (s) {
            s.classList.toggle("staff-alert--hidden", !ok || !text);
            s.textContent = ok ? text : "";
        }
        if (e) {
            e.classList.toggle("staff-alert--hidden", ok || !text);
            e.textContent = ok ? "" : text;
        }
    }

    function clearMsgs() {
        showMsg(true, "");
        showMsg(false, "");
    }

    function parseJsonResponse(r) {
        return r.text().then(function (text) {
            var j = null;
            try {
                j = text ? JSON.parse(text) : null;
            } catch (ignore) {}
            if (!r.ok) {
                var msg = (j && j.message) || "Erro " + r.status;
                throw new Error(msg);
            }
            return j;
        });
    }

    function authFetch(url, opts) {
        opts = opts || {};
        opts.headers = Object.assign({}, authHeaders(), opts.headers || {});
        return fetch(API + url, opts).then(function (r) {
            if (r.status === 401) {
                redirectLogin();
                throw new Error("Sessão expirada.");
            }
            return r;
        });
    }

    function labelTipo(v) {
        var f = TIPO_APARELHO.find(function (x) {
            return x.v === v;
        });
        return f ? f.l : v;
    }

    function labelStatus(s) {
        var m = {
            AGENDADO: "Agendado",
            EM_ATENDIMENTO: "Em atendimento",
            CONCLUIDO: "Concluído",
            CANCELADO: "Cancelado"
        };
        return m[s] || s;
    }

    function badgeClass(s) {
        if (s === "CONCLUIDO") return "staff-badge staff-badge--ok";
        if (s === "EM_ATENDIMENTO") return "staff-badge staff-badge--em";
        if (s === "CANCELADO") return "staff-badge staff-badge--cancel";
        return "staff-badge staff-badge--agendado";
    }

    function formatData(d) {
        if (!d) return "";
        if (typeof d === "string") return d;
        if (Array.isArray(d) && d.length >= 3) {
            return d[0] + "-" + String(d[1]).padStart(2, "0") + "-" + String(d[2]).padStart(2, "0");
        }
        return String(d);
    }

    function formatHora(h) {
        if (!h) return "";
        if (typeof h === "string") return h.length >= 5 ? h.substring(0, 5) : h;
        return String(h);
    }

    function escapeHtml(s) {
        if (!s) return "";
        return String(s)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;");
    }

    function truncate(s, n) {
        s = s || "";
        return s.length <= n ? s : s.substring(0, n) + "…";
    }

    var dadosCache = [];

    function carregar() {
        clearMsgs();
        var st = document.getElementById("filterStatus").value;
        var q = document.getElementById("filterBusca").value.trim();
        var qs = [];
        if (st) qs.push("status=" + encodeURIComponent(st));
        if (q) qs.push("q=" + encodeURIComponent(q));
        var url = "/api/v1/admin/agendamentos" + (qs.length ? "?" + qs.join("&") : "");

        var tbody = document.getElementById("tblBody");
        tbody.innerHTML = '<tr><td colspan="9" class="staff-empty">Carregando…</td></tr>';

        authFetch(url, { method: "GET" })
            .then(parseJsonResponse)
            .then(function (list) {
                dadosCache = Array.isArray(list) ? list : [];
                renderTabela(dadosCache);
            })
            .catch(function (err) {
                tbody.innerHTML =
                    '<tr><td colspan="9" class="staff-empty">' +
                    (err.message || "Erro ao carregar.") +
                    "</td></tr>";
            });
    }

    function renderTabela(list) {
        var tbody = document.getElementById("tblBody");
        if (!list.length) {
            tbody.innerHTML = '<tr><td colspan="9" class="staff-empty">Nenhum agendamento encontrado.</td></tr>';
            return;
        }
        tbody.innerHTML = list
            .map(function (a) {
                return (
                    "<tr data-id=\"" +
                    a.id +
                    "\">" +
                    "<td>" +
                    a.id +
                    "</td>" +
                    "<td>" +
                    escapeHtml(a.nomeCliente) +
                    "</td>" +
                    "<td>" +
                    escapeHtml(a.telefone) +
                    "</td>" +
                    "<td>" +
                    escapeHtml(labelTipo(a.tipoAparelho)) +
                    "</td>" +
                    "<td>" +
                    escapeHtml(truncate(a.tipoConserto, 28)) +
                    "</td>" +
                    "<td>" +
                    formatData(a.dataAtendimento) +
                    "</td>" +
                    "<td>" +
                    formatHora(a.horario) +
                    "</td>" +
                    "<td><span class=\"" +
                    badgeClass(a.status) +
                    "\">" +
                    labelStatus(a.status) +
                    "</span></td>" +
                    "<td class=\"staff-actions\">" +
                    '<button type="button" data-acao="ver" data-id="' +
                    a.id +
                    '">Detalhes</button> ' +
                    '<button type="button" data-acao="editar" data-id="' +
                    a.id +
                    '">Editar</button> ' +
                    (a.status !== "CONCLUIDO"
                        ? '<button type="button" data-acao="concluir" data-id="' + a.id + '">Concluir</button> '
                        : "") +
                    '<button type="button" class="staff-btn-danger" data-acao="excluir" data-id="' +
                    a.id +
                    '">Excluir</button>' +
                    "</td>" +
                    "</tr>"
                );
            })
            .join("");
    }

    function findLocal(id) {
        return dadosCache.find(function (x) {
            return x.id === id;
        });
    }

    function abrirDetalhe(id) {
        var a = findLocal(id);
        if (!a) return;
        var html = "";
        html += "<dl>";
        html += "<dt>ID</dt><dd>" + a.id + "</dd>";
        html += "<dt>Cliente</dt><dd>" + escapeHtml(a.nomeCliente) + "</dd>";
        html += "<dt>Telefone / WhatsApp</dt><dd>" + escapeHtml(a.telefone) + "</dd>";
        html += "<dt>Tipo de aparelho</dt><dd>" + escapeHtml(labelTipo(a.tipoAparelho)) + "</dd>";
        html += "<dt>Tipo de conserto</dt><dd>" + escapeHtml(a.tipoConserto) + "</dd>";
        html += "<dt>Data</dt><dd>" + formatData(a.dataAtendimento) + "</dd>";
        html += "<dt>Horário</dt><dd>" + formatHora(a.horario) + "</dd>";
        html += "<dt>Status</dt><dd>" + labelStatus(a.status) + "</dd>";
        html += "<dt>Observações</dt><dd>" + (a.observacoes ? escapeHtml(a.observacoes) : "—") + "</dd>";
        html += "<dt>Criado em</dt><dd>" + (a.criadoEm || "—") + "</dd>";
        html += "</dl>";
        document.getElementById("detalheConteudo").innerHTML = html;
        document.getElementById("dlgDetalhe").showModal();
    }

    function fillSelectAparelho(sel, selected) {
        sel.innerHTML = TIPO_APARELHO.map(function (o) {
            return (
                '<option value="' +
                o.v +
                '"' +
                (o.v === selected ? " selected" : "") +
                ">" +
                o.l +
                "</option>"
            );
        }).join("");
    }

    function abrirEditar(id) {
        var a = findLocal(id);
        if (!a) return;
        document.getElementById("editId").value = a.id;
        document.getElementById("editNome").value = a.nomeCliente;
        document.getElementById("editTel").value = a.telefone;
        fillSelectAparelho(document.getElementById("editAparelho"), a.tipoAparelho);
        document.getElementById("editServico").value = a.tipoConserto;
        document.getElementById("editData").value = formatData(a.dataAtendimento);
        document.getElementById("editHora").value = formatHora(a.horario);
        document.getElementById("editObs").value = a.observacoes || "";
        document.getElementById("editStatus").value = a.status;
        document.getElementById("dlgEditar").showModal();
    }

    document.addEventListener("DOMContentLoaded", function () {
        if (!token()) {
            redirectLogin();
            return;
        }

        var claims = (function decodeJwtPayload(t) {
            try {
                var p = t.split(".")[1];
                if (!p) return null;
                var s = p.replace(/-/g, "+").replace(/_/g, "/");
                while (s.length % 4) s += "=";
                return JSON.parse(atob(s));
            } catch (e) {
                return null;
            }
        })(token());
        if (claims && claims.sub) {
            document.getElementById("staffUserEmail").textContent = claims.sub;
        }

        document.getElementById("btnLogout").addEventListener("click", function () {
            redirectLogin();
        });

        document.getElementById("btnFiltrar").addEventListener("click", carregar);

        document.getElementById("tblBody").addEventListener("click", function (ev) {
            var btn = ev.target.closest("button[data-acao]");
            if (!btn) return;
            var id = parseInt(btn.getAttribute("data-id"), 10);
            var acao = btn.getAttribute("data-acao");
            if (acao === "ver") abrirDetalhe(id);
            if (acao === "editar") abrirEditar(id);
            if (acao === "concluir") {
                if (!confirm("Marcar este agendamento como concluído?")) return;
                authFetch("/api/v1/admin/agendamentos/" + id + "/concluir", { method: "PATCH" })
                    .then(parseJsonResponse)
                    .then(function () {
                        showMsg(true, "Agendamento marcado como concluído.");
                        carregar();
                    })
                    .catch(function (err) {
                        showMsg(false, err.message);
                    });
            }
            if (acao === "excluir") {
                if (!confirm("Excluir este agendamento permanentemente? Esta ação não pode ser desfeita.")) return;
                authFetch("/api/v1/admin/agendamentos/" + id, { method: "DELETE" })
                    .then(function (r) {
                        if (r.status === 204) return null;
                        return parseJsonResponse(r);
                    })
                    .then(function () {
                        showMsg(true, "Agendamento excluído.");
                        carregar();
                    })
                    .catch(function (err) {
                        showMsg(false, err.message);
                    });
            }
        });

        document.getElementById("btnCancelarEdit").addEventListener("click", function () {
            document.getElementById("dlgEditar").close();
        });

        document.getElementById("formEditar").addEventListener("submit", function (ev) {
            ev.preventDefault();
            var id = document.getElementById("editId").value;
            var body = {
                nomeCliente: document.getElementById("editNome").value.trim(),
                telefone: document.getElementById("editTel").value.trim(),
                tipoAparelho: document.getElementById("editAparelho").value,
                tipoConserto: document.getElementById("editServico").value.trim(),
                dataAtendimento: document.getElementById("editData").value,
                horario: document.getElementById("editHora").value.trim(),
                observacoes: document.getElementById("editObs").value.trim() || null,
                status: document.getElementById("editStatus").value
            };
            authFetch("/api/v1/admin/agendamentos/" + id, {
                method: "PUT",
                body: JSON.stringify(body)
            })
                .then(parseJsonResponse)
                .then(function () {
                    document.getElementById("dlgEditar").close();
                    showMsg(true, "Agendamento atualizado.");
                    carregar();
                })
                .catch(function (err) {
                    showMsg(false, err.message);
                });
        });

        carregar();
    });
})();
