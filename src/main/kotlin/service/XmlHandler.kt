package service

import entity.Empleado
import java.nio.file.Files
import java.nio.charset.StandardCharsets
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.nio.file.Path

class XmlHandler(private val filePath: Path) {
    fun empleadosToXML(empleados: List<Empleado>){
        val docFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docFactory.newDocument()

        val rootElement: Element = doc.createElement("empleados")
        doc.appendChild(rootElement)

        empleados.forEach { empleado ->
            val empleadoElement = doc.createElement("empleado")
            empleadoElement.setAttribute("id", empleado.id.toString())

            val apellidoElement = doc.createElement("apellido")
            apellidoElement.appendChild(doc.createTextNode(empleado.apellido))
            empleadoElement.appendChild(apellidoElement)

            val departamentoElement = doc.createElement("departamento")
            departamentoElement.appendChild(doc.createTextNode(empleado.departamento))
            empleadoElement.appendChild(departamentoElement)

            val salarioElement = doc.createElement("salario")
            salarioElement.appendChild(doc.createTextNode(empleado.salario.toString()))
            empleadoElement.appendChild(salarioElement)

            rootElement.appendChild(empleadoElement)
        }

        try {
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")

            val source = DOMSource(doc)

            try {
                val result = StreamResult(Files.newBufferedWriter(filePath, StandardCharsets.UTF_8))
                transformer.transform(source, result)

            } catch (e: Exception) {
                println("Error al escribir el archivo XML: ${e.message}")
                e.printStackTrace()
            }

        } catch (e: Exception) {
            println("Error al transformar el documento XML: ${e.message}")
            e.printStackTrace()
        }
    }

}