package service

import entity.Empleado
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.notExists

class CsvHandler(private val filePath: Path) {
    fun csVtoEmpleados():List<Empleado>{
        val br = Files.newBufferedReader(filePath)
        if (filePath.notExists()) throw FileNotFoundException("No existe el fichero $filePath")
        val empleados = mutableListOf<Empleado>()
        br.use { bufferedReader ->
            var fistLine = true
            bufferedReader.forEachLine { line ->
                if (!fistLine){
                    val data = line.split(",")
                    val empleado = Empleado(
                        data[0].toIntOrNull() ?: 0,
                        data[1],
                        data[2],
                        data[3].toDoubleOrNull() ?: 0.0
                    )
                    empleados.add(empleado)
                }
                fistLine = false
            }
        }
        return empleados
    }
}