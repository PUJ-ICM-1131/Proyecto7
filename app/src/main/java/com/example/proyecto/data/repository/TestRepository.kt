package com.example.proyecto.data.repository

import com.example.proyecto.data.model.InscripcionTest
import com.example.proyecto.data.model.Resena
import com.example.proyecto.data.model.TestModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface TestRepository {
    val todosLosTests: StateFlow<List<TestModel>>
    val inscripcionesEnCurso: StateFlow<List<InscripcionTest>>
    val historialParticipaciones: StateFlow<List<InscripcionTest>>

    fun obtenerTestPorId(id: String): TestModel?
    fun inscribirATest(testId: String)
    fun agregarEvidencia(inscripcionId: String, uriString: String)
    fun agregarResenaATest(testId: String, resena: Resena)
}

class MockTestRepository : TestRepository {

    private val muestraTests = listOf(
        TestModel(
            id = "t1",
            nombre = "Prueba de Auriculares Bluetooth Noise-Cancelling",
            descripcion = "Evaluación de confort, aislamiento acústico y calidad de audio durante 3 días de uso cotidiano.",
            recompensa = "$150.000 COP + Auriculares de obsequio",
            criteriosAplicacion = "Personas de 18 a 35 años, estrato 2 a 5. Uso habitual de transporte público.",
            direccion = "Calle 85 #11-53, Zona T, Bogotá",
            tipoProducto = "Electrónica / Audio",
            latitud = 4.6698,
            longitud = -74.0532,
            cumpleRequisitos = true,
            resenas = listOf(
                Resena("r1", "Carlos M.", "Muy buena experiencia, el aislamiento de ruido superó expectativas.", 4.8f),
                Resena("r2", "Laura G.", "Auriculares muy cómodos y el pago fue puntual.", 5.0f)
            )
        ),
        TestModel(
            id = "t2",
            nombre = "Degustación y Empaque de Snack Orgánico de Frutos Secos",
            descripcion = "Degustar nuevo sabor de barra proteica y dar retroalimentación sobre la usabilidad del empaque ecológico.",
            recompensa = "$80.000 COP en bono de supermercado",
            criteriosAplicacion = "Mayores de 18 años sin alergias a frutos secos ni maní.",
            direccion = "Carrera 15 #93-60, Chico, Bogotá",
            tipoProducto = "Alimentos / Snacks",
            latitud = 4.6782,
            longitud = -74.0481,
            cumpleRequisitos = true,
            resenas = listOf(
                Resena("r3", "Andrea P.", "Delicioso sabor y empaque muy innovador.", 4.5f)
            )
        ),
        TestModel(
            id = "t3",
            nombre = "Test de Usabilidad de App Móvil Bancaria",
            descripcion = "Realizar transacciones simuladas en la app para medir tiempos de respuesta y claridad visual de la interfaz.",
            recompensa = "$120.000 COP abonados a tu cuenta",
            criteriosAplicacion = "Estrato 3 en adelante, contar con Smartphone Android 12+",
            direccion = "Calle 72 #7-30, Financial District, Bogotá",
            tipoProducto = "Software / FinTech",
            latitud = 4.6561,
            longitud = -74.0560,
            cumpleRequisitos = true,
            resenas = listOf(
                Resena("r4", "Felipe H.", "Prueba rápida de 45 minutos. Muy profesional.", 4.9f)
            )
        ),
        TestModel(
            id = "t4",
            nombre = "Prueba de Silla Ergonómica de Oficina Gamer",
            descripcion = "Silla de alta gama para prueba intensiva durante 1 semana laboral en estudio de diseño.",
            recompensa = "$300.000 COP",
            criteriosAplicacion = "Personas de 25 a 50 años, estrato 4 a 6. Trabajo remoto comprobable.",
            direccion = "Carrera 9 #116-20, Usaquén, Bogotá",
            tipoProducto = "Mobiliario",
            latitud = 4.6983,
            longitud = -74.0305,
            cumpleRequisitos = false,
            resenas = listOf(
                Resena("r5", "Daniela K.", "Excelente soporte lumbar.", 5.0f)
            )
        ),
        TestModel(
            id = "t5",
            nombre = "Evaluación de Smartwatch Deportivo GPS",
            descripcion = "Monitorear carreras de 5K usando el GPS y sensor cardíaco del dispositivo prototipo.",
            recompensa = "$250.000 COP",
            criteriosAplicacion = "Atletas o corredores frecuentes mayores de 21 años, estrato 3 a 6.",
            direccion = "Parque Simon Bolivar, Entrada Calle 63, Bogotá",
            tipoProducto = "Wearables / Deporte",
            latitud = 4.6582,
            longitud = -74.0938,
            cumpleRequisitos = false,
            resenas = emptyList()
        )
    )

    private val _todosLosTests = MutableStateFlow(muestraTests)
    override val todosLosTests: StateFlow<List<TestModel>> = _todosLosTests.asStateFlow()

    private val _inscripcionesEnCurso = MutableStateFlow<List<InscripcionTest>>(
        listOf(
            InscripcionTest(
                id = "ins_1",
                test = muestraTests[0],
                estadoRecompensa = "En curso",
                evidenciasUris = emptyList(),
                completado = false
            )
        )
    )
    override val inscripcionesEnCurso: StateFlow<List<InscripcionTest>> = _inscripcionesEnCurso.asStateFlow()

    private val _historialParticipaciones = MutableStateFlow<List<InscripcionTest>>(
        listOf(
            InscripcionTest(
                id = "ins_h1",
                test = muestraTests[2],
                estadoRecompensa = "Entregada",
                evidenciasUris = listOf("https://example.com/evidencia1.jpg"),
                completado = true
            )
        )
    )
    override val historialParticipaciones: StateFlow<List<InscripcionTest>> = _historialParticipaciones.asStateFlow()

    override fun obtenerTestPorId(id: String): TestModel? {
        return _todosLosTests.value.find { it.id == id }
    }

    override fun inscribirATest(testId: String) {
        val test = obtenerTestPorId(testId) ?: return
        val existe = _inscripcionesEnCurso.value.any { it.test.id == testId }
        if (!existe) {
            val nuevaInscripcion = InscripcionTest(
                id = "ins_${System.currentTimeMillis()}",
                test = test,
                estadoRecompensa = "En curso",
                evidenciasUris = emptyList(),
                completado = false
            )
            _inscripcionesEnCurso.value = _inscripcionesEnCurso.value + nuevaInscripcion
        }
    }

    override fun agregarEvidencia(inscripcionId: String, uriString: String) {
        _inscripcionesEnCurso.value = _inscripcionesEnCurso.value.map { ins ->
            if (ins.id == inscripcionId) {
                ins.copy(evidenciasUris = ins.evidenciasUris + uriString)
            } else {
                ins
            }
        }
    }

    override fun agregarResenaATest(testId: String, resena: Resena) {
        _todosLosTests.value = _todosLosTests.value.map { test ->
            if (test.id == testId) {
                test.copy(resenas = test.resenas + resena)
            } else {
                test
            }
        }
    }
}
