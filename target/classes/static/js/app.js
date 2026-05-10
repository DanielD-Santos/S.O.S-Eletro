(function () {
    // Brasil: +55 81 99698779 (sem símbolos, só dígitos)
    var WHATSAPP_E164 = "558199698779";
    var WHATSAPP_MENSAGEM = "preciso de ajuda para arrumar meu aparelho";

    function applyWhatsAppLinks() {
        var text = encodeURIComponent(WHATSAPP_MENSAGEM);
        var base = "https://wa.me/" + WHATSAPP_E164;

        var hero = document.getElementById("whatsappHero");
        var cta = document.getElementById("whatsappCta");
        if (hero) hero.href = base + "?text=" + text;
        if (cta) cta.href = base + "?text=" + text;
    }

    function checkApi() {
        var el = document.getElementById("apiStatus");
        if (!el) return;

        fetch("/api/v1/health", { method: "GET" })
            .then(function (r) {
                if (!r.ok) throw new Error("offline");
                return r.json();
            })
            .then(function (data) {
                el.textContent = "API online — " + (data.service || "S.O.S Eletro");
                el.classList.add("is-online");
                el.classList.remove("is-offline");
            })
            .catch(function () {
                el.textContent = "API offline (inicie o Spring Boot na porta 8080)";
                el.classList.add("is-offline");
                el.classList.remove("is-online");
            });
    }

    function setupNav() {
        var toggle = document.getElementById("navToggle");
        var nav = document.getElementById("nav");
        if (!toggle || !nav) return;

        toggle.addEventListener("click", function () {
            nav.classList.toggle("is-open");
            var open = nav.classList.contains("is-open");
            toggle.setAttribute("aria-label", open ? "Fechar menu" : "Abrir menu");
        });

        nav.querySelectorAll("a").forEach(function (a) {
            a.addEventListener("click", function () {
                nav.classList.remove("is-open");
                toggle.setAttribute("aria-label", "Abrir menu");
            });
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        applyWhatsAppLinks();
        setupNav();
        checkApi();
    });
})();
