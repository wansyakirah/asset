import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/EditServlet")
public class EditServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            String rowID = request.getParameter("rowID");

            // Kalau rowID kosong → redirect balik ke senarai
            if (rowID == null || rowID.trim().isEmpty()) {
                response.sendRedirect("ListServlet");
                return;
            }

            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM weather_data WHERE rowID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, rowID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                out.println("<html><head><title>Edit Weather Record</title></head><body>");
                out.println("<h2>Edit Weather Record</h2>");
                out.println("<form action='UpdateServlet' method='post'>");

                // Hidden field untuk rowID
                out.println("<input type='hidden' name='rowID' value='" + rs.getString("rowID") + "'/>");

                // Semua field editable
                out.println("Location:<br>");
                out.println("<input type='text' name='location' value='" + rs.getString("location") + "'><br><br>");

                out.println("MinTemp:<br>");
                out.println("<input type='text' name='minTemp' value='" + rs.getDouble("minTemp") + "'><br><br>");

                out.println("MaxTemp:<br>");
                out.println("<input type='text' name='maxTemp' value='" + rs.getDouble("maxTemp") + "'><br><br>");

                out.println("Rainfall:<br>");
                out.println("<input type='text' name='rainfall' value='" + rs.getDouble("rainfall") + "'><br><br>");

                out.println("WindSpeed9am:<br>");
                out.println("<input type='text' name='windSpeed9am' value='" + rs.getDouble("windSpeed9am") + "'><br><br>");

                out.println("WindSpeed3pm:<br>");
                out.println("<input type='text' name='windSpeed3pm' value='" + rs.getDouble("windSpeed3pm") + "'><br><br>");

                out.println("Humidity9am:<br>");
                out.println("<input type='text' name='humidity9am' value='" + rs.getDouble("humidity9am") + "'><br><br>");

                out.println("Humidity3pm:<br>");
                out.println("<input type='text' name='humidity3pm' value='" + rs.getDouble("humidity3pm") + "'><br><br>");

                out.println("Temp9am:<br>");
                out.println("<input type='text' name='temp9am' value='" + rs.getDouble("temp9am") + "'><br><br>");

                out.println("Temp3pm:<br>");
                out.println("<input type='text' name='temp3pm' value='" + rs.getDouble("temp3pm") + "'><br><br>");

                out.println("RainToday:<br>");
                out.println("<input type='text' name='rainToday' value='" + rs.getString("rainToday") + "'><br><br>");

                // Butang update
                out.println("<input type='submit' value='Update'>");
                out.println("</form>");

                // Link balik ke senarai
                out.println("<br><a href='ListServlet'>Back to List</a>");

                out.println("</body></html>");
            } else {
                out.println("<html><body>");
                out.println("<p style='color:red;'>❌ No record found for RowID: " + rowID + "</p>");
                out.println("<a href='ListServlet'>Back to List</a>");
                out.println("</body></html>");
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            out.println("<html><body>");
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
            out.println("<a href='ListServlet'>Back to List</a>");
            out.println("</body></html>");
        }
    }
}
