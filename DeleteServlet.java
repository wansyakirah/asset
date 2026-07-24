import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/DeleteServlet")
public class DeleteServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) {

        try {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            String rowID = request.getParameter("rowID");

            if (rowID == null || rowID.trim().isEmpty()) {
                out.println("<p style='color:red;'>❌ RowID parameter missing!</p>");
                out.println("<a href='ListServlet'>Back to List</a>");
                return;
            }

            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM weather_data WHERE rowID=?");
            ps.setString(1, rowID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                out.println("<html><body>");
                out.println("<h2>Confirm Delete</h2>");
                out.println("<p>Are You Sure Want To Delete This?</p>");

                out.println("<table border='1'>");
                out.println("<tr><th>RowID</th><th>Location</th><th>MinTemp</th><th>MaxTemp</th><th>Rainfall</th><th>Wind9am</th><th>Wind3pm</th><th>Humidity9am</th><th>Humidity3pm</th><th>Temp9am</th><th>Temp3pm</th><th>RainToday</th></tr>");
                out.println("<tr>");
                out.println("<td>" + rs.getString("rowID") + "</td>");
                out.println("<td>" + rs.getString("location") + "</td>");
                out.println("<td>" + rs.getDouble("minTemp") + "</td>");
                out.println("<td>" + rs.getDouble("maxTemp") + "</td>");
                out.println("<td>" + rs.getDouble("rainfall") + "</td>");
                out.println("<td>" + rs.getDouble("windSpeed9am") + "</td>");
                out.println("<td>" + rs.getDouble("windSpeed3pm") + "</td>");
                out.println("<td>" + rs.getDouble("humidity9am") + "</td>");
                out.println("<td>" + rs.getDouble("humidity3pm") + "</td>");
                out.println("<td>" + rs.getDouble("temp9am") + "</td>");
                out.println("<td>" + rs.getDouble("temp3pm") + "</td>");
                out.println("<td>" + rs.getString("rainToday") + "</td>");
                out.println("</tr>");
                out.println("</table>");

                out.println("<form action='ConfirmDeleteServlet' method='post'>");
                out.println("<input type='hidden' name='rowID' value='" + rs.getString("rowID") + "'/>");
                out.println("<input type='submit' value='Confirm Delete'/>");
                out.println("</form>");

                out.println("<a href='ListServlet'>Cancel</a>");
                out.println("</body></html>");
            } else {
                out.println("<p style='color:red;'>❌ No record found for RowID: " + rowID + "</p>");
                out.println("<a href='ListServlet'>Back to List</a>");
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
