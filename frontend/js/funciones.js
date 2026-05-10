// ============================================================
//  AVIVA — funciones.js
//  Funciones compartidas entre todas las páginas
// ============================================================

// ─── BASE DE DATOS LOCAL (solo para demo sin backend) ────────
// Ya no se usa para médicos/pagos/departamentos
// Se mantiene solo para notificaciones locales

const DB = {
    get notificaciones() {
        const d = localStorage.getItem('aviva_notificaciones');
        if (d) return JSON.parse(d);
        const inicial = [
            { id:1, leida:false, tipo:'urgente', icono:'fa-circle-exclamation', colorBg:'#fee2e2', colorIcon:'#dc2626', titulo:'Cita urgente sin confirmar', mensaje:'Ana García tiene una cita sin confirmar.', tiempo:'Hace 5 min' },
            { id:2, leida:false, tipo:'info',    icono:'fa-calendar-check',     colorBg:'#cffafe', colorIcon:'#0891b2', titulo:'Nueva cita agendada',       mensaje:'Roberto Chávez registró una cita para mañana.',      tiempo:'Hace 20 min' },
            { id:3, leida:false, tipo:'pago',    icono:'fa-money-bill-wave',    colorBg:'#d1fae5', colorIcon:'#059669', titulo:'Pago pendiente',             mensaje:'Carlos Mendoza tiene un pago pendiente.',            tiempo:'Hace 1 hora' },
        ];
        localStorage.setItem('aviva_notificaciones', JSON.stringify(inicial));
        return inicial;
    },
    setNotificaciones(arr) { localStorage.setItem('aviva_notificaciones', JSON.stringify(arr)); }
};

// ─── UTILIDADES ───────────────────────────────────────────────

function escapeHtml(str) {
    if (!str && str !== 0) return '';
    return String(str)
        .replace(/&/g,'&amp;').replace(/</g,'&lt;')
        .replace(/>/g,'&gt;').replace(/"/g,'&quot;')
        .replace(/'/g,'&#39;');
}

function getIniciales(nombre) {
    return (nombre || '').split(' ').map(w => w[0] || '').join('').substring(0,2).toUpperCase();
}

// ─── TEMA ─────────────────────────────────────────────────────

function aplicarTema(tema) {
    document.body.setAttribute('data-tema', tema);
    const btn = document.getElementById('boton-modo-oscuro');
    if (btn) {
        btn.innerHTML = tema === 'oscuro'
            ? '<i class="fa-solid fa-sun text-warning"></i>'
            : '<i class="fa-solid fa-moon"></i>';
    }
}

function initTema() {
    const btn      = document.getElementById('boton-modo-oscuro');
    const guardado = localStorage.getItem('temaAviva') || 'claro';
    aplicarTema(guardado);
    if (btn) {
        btn.addEventListener('click', () => {
            const actual = document.body.getAttribute('data-tema');
            const nuevo  = actual === 'claro' ? 'oscuro' : 'claro';
            aplicarTema(nuevo);
            localStorage.setItem('temaAviva', nuevo);
        });
    }
}

// ─── HAMBURGUESA ──────────────────────────────────────────────

function initHamburgesa() {
    const btn     = document.getElementById('boton-hamburguesa');
    const sidebar = document.getElementById('sidebar-wrapper');
    if (!btn || !sidebar) return;

    btn.addEventListener('click', e => {
        e.stopPropagation();
        sidebar.classList.toggle('menu-abierto');
    });

    // Botón X para cerrar sidebar en móvil
    sidebar.addEventListener('click', e => {
        const btnCerrar = e.target.closest('#btn-cerrar-sidebar');
        if (btnCerrar) sidebar.classList.remove('menu-abierto');
    });

    document.addEventListener('click', e => {
        if (window.innerWidth < 992 && !sidebar.contains(e.target) && e.target !== btn) {
            sidebar.classList.remove('menu-abierto');
        }
    });
}

// ─── NAVEGACIÓN ───────────────────────────────────────────────

function initNavegacion() {
    const pagina = window.location.pathname.split('/').pop() || 'index.html';
    const mapaPagina = {
        'index.html':           'panel-principal',
        'citas.html':           'panel-citas',
        'pacientes.html':       'panel-pacientes',
        'horarioMedicos.html':  'panel-horario-medicos',
        'pagos.html':           'panel-pagos',
        'notificaciones.html':  'panel-notificaciones',
    };
    const panelActual = mapaPagina[pagina] || 'panel-principal';
    document.querySelectorAll('#sidebar-wrapper .list-group-item').forEach(item => {
        item.classList.remove('nav-activo');
        item.classList.add('nav-link-custom');
        if (item.getAttribute('data-panel') === panelActual) {
            item.classList.add('nav-activo');
            item.classList.remove('nav-link-custom');
        }
    });

    document.querySelectorAll('[data-panel]').forEach(el => {
        el.addEventListener('click', e => {
            e.preventDefault();
            const mapa = {
                'panel-principal':      'index.html',
                'panel-citas':          'citas.html',
                'panel-pacientes':      'pacientes.html',
                'panel-horario-medicos':'horarioMedicos.html',
                'panel-pagos':          'pagos.html',
                'panel-notificaciones': 'notificaciones.html',
            };
            const destino = mapa[el.getAttribute('data-panel')];
            if (destino) window.location.href = destino;
        });
    });
}

// ─── NOTIFICACIONES QUICK ─────────────────────────────────────

function initNotificacionesQuick() {
    const btnNotif     = document.getElementById('boton-notif-nav');
    const panel        = document.getElementById('notif-quick-panel');
    const notifDot     = document.getElementById('notif-dot');
    const badgeSidebar = document.getElementById('notif-count-sidebar');

    function actualizarBadges() {
        const noLeidas = DB.notificaciones.filter(n => !n.leida).length;
        if (notifDot)     notifDot.style.display     = noLeidas > 0 ? 'block' : 'none';
        if (badgeSidebar) {
            badgeSidebar.textContent  = noLeidas;
            badgeSidebar.style.display = noLeidas > 0 ? 'inline-flex' : 'none';
        }
    }

    function renderQuick() {
        const lista    = document.getElementById('notif-quick-list');
        if (!lista) return;
        const noLeidas = DB.notificaciones.filter(n => !n.leida).slice(0,4);
        if (noLeidas.length === 0) {
            lista.innerHTML = `<div class="notif-empty py-3"><i class="fa-solid fa-bell-slash d-block mb-2" style="font-size:1.8rem;opacity:0.3"></i><small>Sin notificaciones nuevas</small></div>`;
        } else {
            lista.innerHTML = noLeidas.map(n => `
                <div class="notif-item-quick no-leida" onclick="marcarLeidaQuick(${n.id})">
                    <div class="notif-icono" style="background:${n.colorBg};color:${n.colorIcon};width:38px;height:38px;min-width:38px;border-radius:50%;display:flex;align-items:center;justify-content:center;">
                        <i class="fa-solid ${n.icono}" style="font-size:0.9rem"></i>
                    </div>
                    <div class="flex-grow-1">
                        <p class="mb-0 fw-semibold" style="font-size:0.82rem">${escapeHtml(n.titulo)}</p>
                        <p class="mb-0 text-muted" style="font-size:0.77rem">${escapeHtml(n.mensaje)}</p>
                        <small class="text-muted" style="font-size:0.72rem">${n.tiempo}</small>
                    </div>
                    <div class="notif-punto ms-1 flex-shrink-0"></div>
                </div>`).join('');
        }
    }

    window.marcarLeidaQuick = function(id) {
        const arr = DB.notificaciones;
        const n   = arr.find(x => x.id === id);
        if (n) { n.leida = true; DB.setNotificaciones(arr); }
        renderQuick();
        actualizarBadges();
    };

    if (btnNotif && panel) {
        btnNotif.addEventListener('click', e => {
            e.stopPropagation();
            const oculto = panel.classList.contains('d-none');
            panel.classList.toggle('d-none', !oculto);
            if (oculto) renderQuick();
        });
    }

    const btnMarcarTodas = document.getElementById('marcar-todas-leidas');
    if (btnMarcarTodas) {
        btnMarcarTodas.addEventListener('click', () => {
            const arr = DB.notificaciones;
            arr.forEach(n => n.leida = true);
            DB.setNotificaciones(arr);
            renderQuick();
            actualizarBadges();
        });
    }

    document.addEventListener('click', e => {
        if (panel && !panel.contains(e.target) && btnNotif && !btnNotif.contains(e.target)) {
            panel.classList.add('d-none');
        }
    });

    actualizarBadges();
}

// ─── MODAL ÉXITO ──────────────────────────────────────────────

function mostrarModalExito(titulo, mensaje) {
    const t = document.getElementById('exito-titulo');
    const m = document.getElementById('exito-mensaje');
    if (t) t.textContent = titulo;
    if (m) m.innerHTML   = mensaje;
    const el = document.getElementById('modalExito');
    if (el) new bootstrap.Modal(el).show();
}

function agregarNotificacion(titulo, mensaje, tipo = 'info') {
    const iconoMap    = { info:'fa-calendar-plus', pago:'fa-money-bill-wave', urgente:'fa-circle-exclamation', sistema:'fa-gear' };
    const colorBgMap  = { info:'#cffafe', pago:'#d1fae5', urgente:'#fee2e2', sistema:'#f1f5f9' };
    const colorIconMap = { info:'#0891b2', pago:'#059669', urgente:'#dc2626', sistema:'#64748b' };
    const arr = DB.notificaciones;
    arr.unshift({
        id: Date.now(), leida: false, tipo,
        icono:     iconoMap[tipo]     || 'fa-bell',
        colorBg:   colorBgMap[tipo]   || '#cffafe',
        colorIcon: colorIconMap[tipo] || '#0891b2',
        titulo, mensaje, tiempo: 'Ahora'
    });
    DB.setNotificaciones(arr);
}

// ─── SIDEBAR Y NAVBAR ─────────────────────────────────────────

function renderSidebar() {
    const el = document.getElementById('sidebar-wrapper');
    if (!el) return;
    const noLeidas = DB.notificaciones.filter(n => !n.leida).length;
    el.innerHTML = `
        <div class="sidebar-heading d-flex align-items-center justify-content-between px-3">
            <span><i class="fa-solid fa-notes-medical texto-primario-personalizado me-2"></i> Aviva</span>
            <button class="btn btn-sm d-lg-none text-muted p-0" id="btn-cerrar-sidebar" style="font-size:1.1rem;">
                <i class="fa-solid fa-xmark"></i>
            </button>
        </div>
        <div class="list-group list-group-flush px-3 pt-3 gap-2">
            <a href="#" class="list-group-item list-group-item-action nav-link-custom rounded-pill" data-panel="panel-principal">
                <i class="fa-solid fa-border-all me-3"></i> Panel Principal
            </a>
            <a href="#" class="list-group-item list-group-item-action nav-link-custom rounded-pill" data-panel="panel-citas">
                <i class="fa-regular fa-calendar-check me-3"></i> Citas
            </a>
            <a href="#" class="list-group-item list-group-item-action nav-link-custom rounded-pill" data-panel="panel-pacientes">
                <i class="fa-solid fa-bed-pulse me-3"></i> Pacientes
            </a>
            <a href="#" class="list-group-item list-group-item-action nav-link-custom rounded-pill" data-panel="panel-horario-medicos">
                <i class="fa-solid fa-stethoscope me-3"></i> Horario Médicos
            </a>
            <a href="#" class="list-group-item list-group-item-action nav-link-custom rounded-pill" data-panel="panel-pagos">
                <i class="fa-solid fa-money-bill-wave me-3"></i> Pagos
            </a>
            <a class="list-group-item list-group-item-action nav-link-custom rounded-pill opacity-50 pe-none">
                <i class="fa-solid fa-bell me-3"></i> Notificaciones
            </a>
        </div>
        <div class="px-3 pt-2 pb-3 mt-auto">
            <a href="#" class="list-group-item list-group-item-action rounded-pill text-danger fw-semibold" onclick="logout()">
                <i class="fa-solid fa-right-from-bracket me-3"></i> Cerrar Sesión
            </a>
        </div>`;
}

function renderNavbar() {
    const el = document.getElementById('navbar-wrapper');
    if (!el) return;
    el.innerHTML = `
        <nav class="navbar navbar-light py-3 px-4 navbar-superior">
            <div class="d-flex align-items-center w-100 gap-3">
                <button class="btn btn-white rounded-2 d-lg-none boton-hamburgesa shadow-sm" id="boton-hamburguesa">
                    <i class="fa-solid fa-bars"></i>
                </button>
                <div class="busqueda-wrapper flex-grow-1 position-relative">
                    <div class="input-group barra-busqueda rounded-pill overflow-hidden">
                        <span class="input-group-text bg-white border-0 text-muted ps-4">
                            <i class="fa-solid fa-search"></i>
                        </span>
                        <input type="text" id="input-busqueda" class="form-control border-0 bg-white shadow-none"
                               placeholder="Buscar..." autocomplete="off"/>
                    </div>
                </div>
                <div class="d-flex align-items-center gap-2 ms-auto">
                    <button class="btn btn-white rounded-circle shadow-sm boton-icono" id="boton-modo-oscuro">
                        <i class="fa-solid fa-moon"></i>
                    </button>
                    <button class="btn btn-white rounded-circle shadow-sm boton-icono position-relative" id="boton-notif-nav">
                        <i class="fa-regular fa-bell"></i>
                        <span class="notif-dot position-absolute top-0 start-100 translate-middle p-1 bg-danger border border-light rounded-circle" id="notif-dot"></span>
                    </button>
                    <div class="notif-quick-panel d-none" id="notif-quick-panel">
                        <div class="notif-panel-header">
                            <span class="fw-bold">Notificaciones</span>
                            <button class="btn btn-sm text-primary p-0" id="marcar-todas-leidas">Marcar todas</button>
                        </div>
                        <div id="notif-quick-list"></div>
                        <div class="notif-panel-footer">
                            <a href="notificaciones.html" class="text-primary text-decoration-none small">Ver todas</a>
                        </div>
                    </div>
                    <div class="dropdown ms-2">
                        <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle usuario-dropdown" data-bs-toggle="dropdown">
                            <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-2 shadow-sm"
                                 style="width:40px;height:40px;font-weight:bold;" id="navbar-iniciales">
                                ${getIniciales(localStorage.getItem('username') || 'U')}
                            </div>
                            <strong class="fw-semibold d-none d-lg-block" id="navbar-username">
                                ${localStorage.getItem('username') || 'Usuario'}
                            </strong>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                            <li><a class="dropdown-item" href="#"><i class="fa-solid fa-user me-2 text-muted"></i>Mi Perfil</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="#" onclick="logout()">
                                <i class="fa-solid fa-right-from-bracket me-2"></i>Cerrar Sesión
                            </a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </nav>`;
}

function renderModalExito() {
    if (document.getElementById('modalExito')) return;
    const div = document.createElement('div');
    div.innerHTML = `
        <div class="modal fade" id="modalExito" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-sm">
                <div class="modal-content border-0 rounded-4 shadow">
                    <div class="modal-body p-4 text-center">
                        <div class="icono-exito-anim mb-3"><div class="circulo-exito"><i class="fa-solid fa-check"></i></div></div>
                        <h5 class="fw-bold" id="exito-titulo">¡Listo!</h5>
                        <p class="text-muted small mb-4" id="exito-mensaje">Operación realizada correctamente.</p>
                        <button class="btn btn-primary rounded-pill px-4 fw-bold text-white" data-bs-dismiss="modal">Aceptar</button>
                    </div>
                </div>
            </div>
        </div>`;
    document.body.appendChild(div.firstElementChild);
}

// ─── LOGOUT ───────────────────────────────────────────────────

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('username');
    window.location.href = '/frontend/views/login/login.html';
}

// ─── INIT GLOBAL ──────────────────────────────────────────────

function initGlobal() {
    renderSidebar();
    renderNavbar();
    renderModalExito();
    initTema();
    initHamburgesa();
    initNavegacion();
    initNotificacionesQuick();
}

document.addEventListener('DOMContentLoaded', initGlobal);