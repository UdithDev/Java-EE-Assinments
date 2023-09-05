import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet(urlPatterns = {"/pages/customer"})
public class customerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_system", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("select * from customer");
            ResultSet rst = pstm.executeQuery();
            resp.addHeader("Content-Type", "application/json");
            JsonArrayBuilder allCustomers = Json.createArrayBuilder();
            while (rst.next()) {
                String id = rst.getString(1);
                String name = rst.getString(2);
                String address = rst.getString(3);
                String salary = rst.getString(4);
                JsonObjectBuilder customerObject = Json.createObjectBuilder();

                customerObject.add("id", id);
                customerObject.add("name", name);
                customerObject.add("address", address);
                customerObject.add("salary", salary);

                allCustomers.add(customerObject.build());
            }

            PrintWriter writer = resp.getWriter();

            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("status", 200);
            response.add("message", "Done");
            response.add("data", allCustomers.build());

            writer.print(response.build());


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);


        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusID = req.getParameter("cusID");
        String cusName = req.getParameter("cusName");
        String cusAddress = req.getParameter("cusAddress");
        String cusSalary = req.getParameter("cusSalary");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_system", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("insert into Customer values(?,?,?,?)");
            pstm.setObject(1, cusID);
            pstm.setObject(2, cusName);
            pstm.setObject(3, cusAddress);
            pstm.setObject(4, cusSalary);
            resp.addHeader("Content-Type", "application/json");

            if (pstm.executeUpdate() > 0) {
                JsonObjectBuilder response = Json.createObjectBuilder();
                response.add("state", "OK");
                response.add("message", "Successfully Added!!!!");
                response.add("data", "");
                resp.getWriter().print(response.build());
            }
        } catch (SQLException throwables) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status", 400);
            objectBuilder.add("message", "ERROR");
            objectBuilder.add("data", throwables.getLocalizedMessage());
            resp.getWriter().print(objectBuilder.build());

            resp.setStatus(HttpServletResponse.SC_OK);

            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_OK);

            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status", 400);
            objectBuilder.add("message", "ERROR");
            objectBuilder.add("data", e.getLocalizedMessage());
            resp.getWriter().print(objectBuilder.build());
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("delete");
        String customerID = req.getParameter("cusID");
        System.out.println(customerID);
        PrintWriter writer = resp.getWriter();
        resp.addHeader("Content-Type", "application/json");
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_system", "root", "1234");
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE  FROM customer WHERE cusId=?");
            preparedStatement.setObject(1, customerID);

            if (preparedStatement.executeUpdate() > 0) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("data", "");
                objectBuilder.add("message", "Successfully Deleted");
                objectBuilder.add("status", 200);
                writer.print(objectBuilder.build());
            } else {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("data", "");
                objectBuilder.add("message", "Wrong ID Entered");
                objectBuilder.add("status", 400);
                writer.print(objectBuilder.build());
            }
        } catch (SQLException e) {
            e.printStackTrace();

            resp.setStatus(200);
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("data", e.getLocalizedMessage());
            objectBuilder.add("message", "Error");
            objectBuilder.add("status", 500);
            writer.print(objectBuilder.build());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Update");

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String cusID = jsonObject.getString("id");
        String cusName = jsonObject.getString("name");
        String cusAddress = jsonObject.getString("address");
        String cusSalary = jsonObject.getString("salary");

        System.out.println(cusID + " " + cusName + " " + cusAddress + " " + cusSalary);
        PrintWriter writer = resp.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_system", "root", "1234");
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE customer SET cusName=?,cusAddress=?,cusSalary=? WHERE cusId=?");
            preparedStatement.setObject(4, cusID);
            preparedStatement.setObject(1, cusName);
            preparedStatement.setObject(2, cusAddress);
            preparedStatement.setObject(3, cusSalary);

            resp.setContentType("application/json");


            if (preparedStatement.executeUpdate() > 0) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("data", objectBuilder.build());
                objectBuilder.add("status", 200);
                objectBuilder.add("message", "Updated");

                writer.print(objectBuilder.build());
            } else {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("data", objectBuilder.build());
                objectBuilder.add("status", 400);
                objectBuilder.add("message", "Updated Unsuccessful");
                writer.print(objectBuilder.build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(200);
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("data", e.getLocalizedMessage());
            objectBuilder.add("message", "Error");
            objectBuilder.add("status", 500);
            writer.print(objectBuilder.build());


        } catch (ClassNotFoundException th) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("data", th.getLocalizedMessage());
            objectBuilder.add("message", "Update Fail");
            objectBuilder.add("status", 500);
            writer.print(objectBuilder.build());
        }
    }

}
