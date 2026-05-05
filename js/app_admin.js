// ===============================
// 🔍 BUSCADOR PACIENTES
// ===============================
const buscarPaciente = document.getElementById("buscarPaciente");

if (buscarPaciente) {
    buscarPaciente.addEventListener("keyup", function () {
        let filtro = buscarPaciente.value.toLowerCase();

        document.querySelectorAll("#tablaPacientes tbody tr")
            .forEach(fila => {
                fila.style.display = fila.innerText.toLowerCase().includes(filtro) ? "" : "none";
            });
    });
}

// ===============================
// 🔍 BUSCADOR DOCTORES
// ===============================
const buscarDoctor = document.getElementById("buscarDoctor");

if (buscarDoctor) {
    buscarDoctor.addEventListener("keyup", function () {
        let filtro = buscarDoctor.value.toLowerCase();

        document.querySelectorAll("#tablaDoctores tbody tr")
            .forEach(fila => {
                fila.style.display = fila.innerText.toLowerCase().includes(filtro) ? "" : "none";
            });
    });
}

// ===============================
// 🔍 BUSCADOR ESPECIALIDADES
// ===============================
const buscarEspecialidad = document.getElementById("buscarEspecialidad");

if (buscarEspecialidad) {
    buscarEspecialidad.addEventListener("keyup", function () {
        let filtro = buscarEspecialidad.value.toLowerCase();

        document.querySelectorAll("#tablaEspecialidades tbody tr")
            .forEach(fila => {
                fila.style.display = fila.innerText.toLowerCase().includes(filtro) ? "" : "none";
            });
    });
}

// ===============================
// 🔍 BUSCADOR PAGOS
// ===============================
const buscarPago = document.getElementById("buscarPago");

if (buscarPago) {
    buscarPago.addEventListener("keyup", function () {
        let filtro = buscarPago.value.toLowerCase();

        document.querySelectorAll("#tablaPagos tbody tr")
            .forEach(fila => {
                fila.style.display = fila.innerText.toLowerCase().includes(filtro) ? "" : "none";
            });
    });
}

// ===============================
// 👨‍⚕️ VER DOCTOR
// ===============================
function verDoctor(nombre, especialidad, horario) {
    alert(
        "👨‍⚕️ " + nombre +
        "\nEspecialidad: " + especialidad +
        "\nHorario: " + horario
    );
}

// ===============================
// ✏️ EDITAR DOCTOR
// ===============================
function editarDoctor(event, nombre) {
    event.stopPropagation();
    alert("✏️ Editando: " + nombre);
}

// ===============================
// 👁️ VER BOLETA
// ===============================
function verBoleta(id, paciente, monto, estado) {
    alert(
        "🧾 BOLETA\n\n" +
        "ID: " + id +
        "\nPaciente: " + paciente +
        "\nMonto: " + monto +
        "\nEstado: " + estado
    );
}

// ===============================
// ✏️ EDITAR PAGO
// ===============================
function editarPago(id) {
    alert("✏️ Editar pago: " + id);
}

// ===============================
// 💳 NUEVA BOLETA
// ===============================
function nuevaBoleta() {
    alert("💳 Registrar nueva boleta");
}

// ===============================
// 📊 GRÁFICOS se elimino (by Jos 08-04-26)
// ===============================

// ===============================
// 🎯 FILTRO POR BOTONES POR ESPECIALIDAD (by Jos 08-04-26)
// ===============================
function filtrarPorEspecialidad(valor) {
    const buscador = document.getElementById("buscarDoctor");
    if (buscador) {
        buscador.value = valor; 
        
        
        const evento = new Event('keyup');
        buscador.dispatchEvent(evento);
    }
}

// PROTEGER ADMIN
if (window.location.pathname.includes("/admin/")) {
    let logeado = localStorage.getItem("login");

    if (logeado !== "true") {
        window.location.href = "../login/login.html";
    }
}

function logout(){
    localStorage.removeItem("login");
    window.location.href = "../login/login.html";
}