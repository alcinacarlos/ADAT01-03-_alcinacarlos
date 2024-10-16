import service.CsvHandler
import service.XmlHandler
import java.nio.file.Path

fun main(){
    val empleadosCsv = Path.of("src", "main", "resources", "empleados.csv")
    val empleadosXml = Path.of("src", "main", "resources", "empleados.xml")

    val csvHandler = CsvHandler(empleadosCsv)
    val xmlHandler = XmlHandler(empleadosXml)

    val empleados = csvHandler.csVtoEmpleados()
    xmlHandler.empleadosToXML(empleados)

    xmlHandler.modifySalary(3, 1234.00)

    val empleadosModified = xmlHandler.readXml()
    empleadosModified.forEach { println(it) }

}