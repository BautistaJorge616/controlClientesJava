package web;

import datos.ClienteDaoJDBC;
import dominio.Cliente;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ServletControlador")
public class ServletControlador extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");

        if (accion != null) {

            switch (accion) {
                case "editar":
                    this.editarCliente(request, response);
                    break;
                case "eliminar":
                    this.eliminarCliente(request, response);
                    break;
                default:
                    this.accionDefault(request, response);
            }
        } else {
            this.accionDefault(request, response);
        }
    }
    
    private void eliminarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        
        Cliente cliente = new Cliente(idCliente);
        ClienteDaoJDBC c = new ClienteDaoJDBC();
        
        int registrosEliminados = c.eliminar(cliente);
        System.out.println("registrosEliminados = " + registrosEliminados);
        this.accionDefault(request, response);
        
        
    }
    
    private void editarCliente(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        
        Cliente cliente = new ClienteDaoJDBC().encontrar(new Cliente(idCliente));
        
        //Ponemos el objeto que nos retorno a un alcance "request"
        request.setAttribute("cliente", cliente);
        
        String jspEditar = "/WEB-INF/paginas/cliente/editarCliente.jsp";
        
        //Redireccionamos al JSP para editar el objeto
        request.getRequestDispatcher(jspEditar).forward(request, response);
        
    }

    //Accion por default 
    private void accionDefault(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recuperamos los clientes
        List<Cliente> clientes = new ClienteDaoJDBC().listar();
        System.out.println("clientes = " + clientes);

        //Los ponemos al alcance de "session"
        HttpSession sesion = request.getSession();
        sesion.setAttribute("clientes", clientes);
        sesion.setAttribute("totalClientes", clientes.size());
        sesion.setAttribute("saldoTotal", this.calcularSaldo(clientes));

        //Le mandamos la informacion al JSP
        //request.getRequestDispatcher("clientes.jsp").forward(request, response);
        response.sendRedirect("clientes.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion != null) {

            switch (accion) {
                case "insertar":
                    this.insertarCliente(request, response);
                    break;
                case "modificar":
                    this.modificarCliente(request, response);
                    break;
                default:
                    this.accionDefault(request, response);
            }
        } else {
            this.accionDefault(request, response);
        }

    }
    
    //Método para modificar un cliente
    private void modificarCliente(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        //Recuperar los valores del formulario editarCliente
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        double saldo = 0;
        String saldoString = request.getParameter("saldo");

        if (saldoString != null && !"".equals(saldoString)) {
            saldo = Double.parseDouble(saldoString);
        }

        //Creamos el objeto de cliente (modelo)
        Cliente cliente = new Cliente(idCliente, nombre, apellido, email, telefono, saldo);

        //Modificamos el objeto en la base de datos
        int registrosModificados = new ClienteDaoJDBC().actualizar(cliente);
        System.out.println("registrosModificados = " + registrosModificados);

        //Redirigimos hacia la accion por defecto
        this.accionDefault(request, response);
    }

    //Método para insertar
    private void insertarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recuperar los valores del formulario agregarClientes
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        double saldo = 0;
        String saldoString = request.getParameter("saldo");

        if (saldoString != null && !"".equals(saldoString)) {
            saldo = Double.parseDouble(saldoString);
        }

        //Creamos el objeto de cliente (modelo)
        Cliente cliente = new Cliente(nombre, apellido, email, telefono, saldo);

        //Insertamos el nuevo objeto en la base de datos
        int registrosModificados = new ClienteDaoJDBC().insertar(cliente);
        System.out.println("registrosModificados = " + registrosModificados);

        //Redirigimos hacia la accion por defecto
        this.accionDefault(request, response);

    }

    //Calcular saldo total
    private double calcularSaldo(List<Cliente> clientes) {
        double totalSaldo = 0;

        for (Cliente cliente : clientes) {
            totalSaldo += cliente.getSaldo();
        }

        return totalSaldo;
    }
}
