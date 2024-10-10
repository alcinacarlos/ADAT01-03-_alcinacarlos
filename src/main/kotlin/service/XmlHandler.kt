package service

import entity.Empleado
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.nio.file.Path

class XmlHandler(private val filePath: Path) {
    fun empleadosToXML(empleados: List<Empleado>) {
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

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")

        val source = DOMSource(doc)

        try {
            val result = StreamResult(filePath.toFile())
            transformer.transform(source, result)

        } catch (e: Exception) {
            println("Error trying to write XML file:  ${e.message}")
        }
    }

    fun modifySalary(idEmpleado: Int, newSalary: Double): Boolean {
        var doc: Document? = null
        try {
            val docFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            doc = docFactory.parse(filePath.toFile())
        } catch (e: Exception) {
            println("Error while trying to read document: ${e.message}")
            return false
        }

        try {
            val empleados = doc!!.getElementsByTagName("empleado")
            var empleadoEncontrado = false

            for (i in 0 until empleados.length) {
                val empleadoE = empleados.item(i)

                if (empleadoE.nodeType == Node.ELEMENT_NODE) {
                    val empleado = empleadoE as Element
                    val id = empleado.getAttribute("id").toIntOrNull()

                    if (id != null && id == idEmpleado) {
                        empleadoEncontrado = true
                        val salarioElement = empleado.getElementsByTagName("salario").item(0)
                        salarioElement.textContent = newSalary.toString()
                        break
                    }
                }
            }
            if (!empleadoEncontrado) return false

        } catch (e: Exception) {
            println("Error while trying to modify Empleado: ${e.message}")
            return false
        }

        try {
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()

            val source = DOMSource(doc)
            val result = StreamResult(filePath.toFile())

            transformer.transform(source, result)

        } catch (e: Exception) {
            println("Error trying to save modified salary: ${e.message}")
            return false
        }
        return true
    }

    fun readXml(): List<Empleado> {
        val empleados = mutableListOf<Empleado>()
        var doc: Document? = null
        try {
            val docFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            doc = docFactory.parse(filePath.toFile())
        } catch (e: Exception) {
            println("Error while trying to read document: ${e.message}")
        }
        val root = doc!!.documentElement
        root.normalize()

        val listaNodos = root.getElementsByTagName("empleado")
        for (i in 0..<listaNodos.length) {
            val nodo = listaNodos.item(i)
            if (nodo.nodeType == Node.ELEMENT_NODE) {
                val empleado = nodo as Element
                val id = empleado.getAttribute("id")
                val apellido = empleado.getElementsByTagName("apellido").item(0).textContent
                val departamento = empleado.getElementsByTagName("departamento").item(0).textContent
                val salario = empleado.getElementsByTagName("salario").item(0).textContent

                empleados.add(
                    Empleado(
                        id.toIntOrNull() ?:0 ,
                        apellido,
                        departamento,
                        salario.toDoubleOrNull() ?: 0.0
                    )
                )
            }
        }
        return empleados
    }
}