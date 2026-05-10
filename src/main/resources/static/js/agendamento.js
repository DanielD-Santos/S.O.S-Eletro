(function () {
    var API = "";

    function defaultSlots() {
        var out = [];
        for (var minutes = 8 * 60; minutes <= 17 * 60 + 30; minutes += 30) {
            var h = Math.floor(minutes / 60);
            var m = minutes % 60;
            out.push((h < 10 ? "0" : "") + h + ":" + (m === 0 ? "00" : "30"));
        }
        return out;
    }

    function todayISODateLocal() {
        var d = new Date();
        return d.getFullYear() + "-" + String(d.getMonth() + 1).padStart(2, "0") + "-" + String(d.getDate()).padStart(2, "0");
    }

    function isPastSlot(dateStr, timeStr) {
        if (dateStr !== todayISODateLocal()) return false;
        var parts = timeStr.split(":");
        var h = parseInt(parts[0], 10);
        var mi = parseInt(parts[1], 10);
        var now = new Date();
        var slot = new Date(now.getFullYear(), now.getMonth(), now.getDate(), h, mi, 0);
        return slot.getTime() <= now.getTime();
    }

    function clearFieldErrors() {
        ["errNome", "errTelefone", "errAparelho", "errConserto", "errData", "errHorario"].forEach(function (id) {
            var el = document.getElementById(id);
            if (el) el.textContent = "";
        });
    }

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

    function setApiHint(text, variant) {
        var el = document.getElementById("apiHint");
        if (!el) return;
        el.textContent = text;
        el.classList.remove("is-ok", "is-warn");
        if (variant === "ok") el.classList.add("is-ok");
        if (variant === "warn") el.classList.add("is-warn");
    }

    function fetchJson(url) {
        return fetch(API + url, { headers: { Accept: "application/json" } }).then(function (r) {
            if (!r.ok) return r.json().then(function (j) { throw j; });
            return r.json();
        });
    }

    var slotsPermitidosCache = null;

    function loadSlotsPermitidos() {
        if (slotsPermitidosCache) return Promise.resolve(slotsPermitidosCache);
        return fetchJson("/api/v1/agendamentos/slots-permitidos")
            .then(function (list) {
                slotsPermitidosCache = Array.isArray(list) && list.length ? list : defaultSlots();
                return slotsPermitidosCache;
            })
            .catch(function () {
                slotsPermitidosCache = defaultSlots();
                return slotsPermitidosCache;
            });
    }

    function loadOcupados(dataStr) {
        if (!dataStr) return Promise.resolve([]);
        return fetchJson("/api/v1/agendamentos/horarios-ocupados?data=" + encodeURIComponent(dataStr))
            .then(function (body) {
                return body.horariosOcupados || [];
            })
            .catch(function () {
                return [];
            });
    }

    function populateHorarios(dataInput) {
        var sel = document.getElementById("horario");
        if (!sel) return;
        var dataStr = dataInput.value;
        sel.innerHTML = "";
        sel.disabled = true;

        if (!dataStr) {
            sel.appendChild(new Option("Primeiro escolha a data", "", false, false));
            return;
        }

        sel.appendChild(new Option("Carregando horários…", "", true, false));
        sel.disabled = true;

        Promise.all([loadSlotsPermitidos(), loadOcupados(dataStr)]).then(function (pair) {
            var todos = pair[0];
            var ocupados = pair[1];
            var setOcup = {};
            ocupados.forEach(function (h) {
                setOcup[normalizeH(h)] = true;
            });

            sel.innerHTML = "";
            var frag = document.createDocumentFragment();
            frag.appendChild(new Option("Selecione o horário", "", true, false));
            var disponiveis = 0;
            todos.forEach(function (h) {
                var key = normalizeH(h);
                if (setOcup[key]) return;
                if (isPastSlot(dataStr, key)) return;
                frag.appendChild(new Option(formatLabel(h), key, false, false));
                disponiveis++;
            });
            sel.appendChild(frag);
            sel.disabled = disponiveis === 0;
            if (disponiveis === 0) {
                sel.innerHTML = "";
                sel.appendChild(new Option("Nenhum horário livre nesta data", "", true, false));
            }
        });
    }

    function normalizeH(h) {
        if (!h) return "";
        var parts = String(h).split(":");
        var hh = parseInt(parts[0], 10);
        var mm = parseInt(parts[1] || "0", 10);
        return (hh < 10 ? "0" : "") + hh + ":" + (mm < 10 ? "0" + mm : mm);
    }

    function formatLabel(h) {
        return normalizeH(h);
    }

    function digitsOnly(s) {
        return String(s || "").replace(/\D/g, "");
    }

    function validate() {
        clearFieldErrors();
        var ok = true;
        var nome = document.getElementById("nomeCliente").value.trim();
        if (nome.length < 3) {
            document.getElementById("errNome").textContent = "Informe um nome com pelo menos 3 caracteres.";
            ok = false;
        }
        var tel = digitsOnly(document.getElementById("telefone").value);
        if (tel.length < 10) {
            document.getElementById("errTelefone").textContent = "Informe um telefone válido (DDD + número).";
            ok = false;
        }
        if (!document.getElementById("tipoAparelho").value) {
            document.getElementById("errAparelho").textContent = "Selecione o tipo de aparelho.";
            ok = false;
        }
        if (!document.getElementById("tipoConserto").value) {
            document.getElementById("errConserto").textContent = "Selecione o tipo de serviço.";
            ok = false;
        }
        var dataVal = document.getElementById("dataAtendimento").value;
        if (!dataVal) {
            document.getElementById("errData").textContent = "Escolha a data.";
            ok = false;
        } else if (dataVal < todayISODateLocal()) {
            document.getElementById("errData").textContent = "Data não pode ser no passado.";
            ok = false;
        }
        var horaVal = document.getElementById("horario").value;
        if (!horaVal) {
            document.getElementById("errHorario").textContent = "Escolha um horário disponível.";
            ok = false;
        }
        return ok;
    }

    function parseApiError(body) {
        if (!body || typeof body !== "object") return "Não foi possível concluir o agendamento.";
        if (body.message) return body.message;
        return "Erro ao enviar. Tente novamente.";
    }

    document.addEventListener("DOMContentLoaded", function () {
        var dataInput = document.getElementById("dataAtendimento");
        if (dataInput) {
            dataInput.min = todayISODateLocal();
            dataInput.addEventListener("change", function () {
                showFeedback(null, "");
                populateHorarios(dataInput);
            });
        }

        fetch(API + "/api/v1/health")
            .then(function (r) {
                if (!r.ok) throw new Error();
                return r.json();
            })
            .then(function () {
                setApiHint("API conectada: horários ocupados são consultados no MySQL em tempo real.", "ok");
            })
            .catch(function () {
                setApiHint("API offline: o formulário funciona, mas não há verificação de conflitos no servidor até você iniciar o Spring Boot.", "warn");
            });

        var form = document.getElementById("formAgendamento");
        if (form) {
            form.addEventListener("submit", function (ev) {
                ev.preventDefault();
                showFeedback(null, "");
                if (!validate()) return;

                var btn = document.getElementById("btnSubmit");
                if (btn) {
                    btn.disabled = true;
                    btn.textContent = "Enviando…";
                }

                var payload = {
                    nomeCliente: document.getElementById("nomeCliente").value.trim(),
                    telefone: document.getElementById("telefone").value.trim(),
                    tipoAparelho: document.getElementById("tipoAparelho").value,
                    tipoConserto: document.getElementById("tipoConserto").value,
                    dataAtendimento: document.getElementById("dataAtendimento").value,
                    horario: document.getElementById("horario").value,
                    observacoes: document.getElementById("observacoes").value.trim() || null
                };

                function parseHttpResponse(r) {
                    return r.text().then(function (text) {
                        var j = null;
                        if (text) {
                            try {
                                j = JSON.parse(text);
                            } catch (ignore) {}
                        }
                        if (!r.ok) {
                            if (j && typeof j === "object" && j.message) {
                                throw j;
                            }
                            throw {
                                message:
                                    (j && j.message) ||
                                    "Erro " +
                                        r.status +
                                        (text ? ". Resposta: " + text.substring(0, 280) : "")
                            };
                        }
                        return j || {};
                    });
                }

                function formatDataResposta(d) {
                    if (!d) return "";
                    if (typeof d === "string") return d;
                    if (Array.isArray(d) && d.length >= 3) {
                        return d[0] + "-" + String(d[1]).padStart(2, "0") + "-" + String(d[2]).padStart(2, "0");
                    }
                    return String(d);
                }

                fetch(API + "/api/v1/agendamentos", {
                    method: "POST",
                    headers: { "Content-Type": "application/json", Accept: "application/json" },
                    body: JSON.stringify(payload)
                })
                    .then(parseHttpResponse)
                    .then(function (res) {
                        showFeedback(
                            "success",
                            "Agendamento confirmado! Protocolo nº " +
                                res.id +
                                " — " +
                                formatDataResposta(res.dataAtendimento) +
                                " às " +
                                (res.horario || "") +
                                "."
                        );
                        form.reset();
                        if (dataInput) {
                            dataInput.min = todayISODateLocal();
                            populateHorarios(dataInput);
                        }
                    })
                    .catch(function (err) {
                        showFeedback("error", parseApiError(err));
                    })
                    .finally(function () {
                        if (btn) {
                            btn.disabled = false;
                            btn.textContent = "Confirmar agendamento";
                        }
                    });
            });
        }
    });
})();
