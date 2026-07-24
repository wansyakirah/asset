import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/ListServlet")
public class ListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM weather_data WHERE is_deleted=0");

            out.println("<html><head><title>Weather Records</title></head><body>");
            out.println("<h2>🌤 Weather Dataset</h2>");
            out.println("<table border='1' cellpadding='5' cellspacing='0'>");
            
            out.println("<br><br>");

            out.println("<a href='SummaryStatisticsServlet'>View Temperature Analysis</a>");
            
            out.println("<br><br>");

            out.println("<a href='WindSpeedAnalysisServlet'>View Wind Speed Analysis</a>");
            
            out.println("<br><br>");

            out.println("<a href='upload.html'>Import Another Dataset</a>");

            out.println("<tr>"
                    + "<th>RowID</th>"
                    + "<th>Location</th>"
                    + "<th>MinTemp</th>"
                    + "<th>MaxTemp</th>"
                    + "<th>Rainfall</th>"
                    + "<th>WindSpeed9am</th>"
                    + "<th>WindSpeed3pm</th>"
                    + "<th>Humidity9am</th>"
                    + "<th>Humidity3pm</th>"
                    + "<th>Temp9am</th>"
                    + "<th>Temp3pm</th>"
                    + "<th>RainToday</th>"
                    + "<th>Actions</th>"
                    + "</tr>");

            while (rs.next()) {
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

                // Link Edit & Delete sahaja
                out.println("<td>"
                        + "<a href='EditServlet?rowID=" + rs.getString("rowID") + "'>Edit</a> | "
                        + "<a href='DeleteServlet?rowID=" + rs.getString("rowID") + "'>Delete</a>"
                        + "</td>");

                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body></html>");

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
