import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SummaryStatisticsServlet")
public class SummaryStatisticsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = DBConnection.getConnection();

            String sql =
                    "SELECT location, minTemp, maxTemp, temp9am, temp3pm " +
                    "FROM weather_data";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Temperature Summary Statistics</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h2>Summary Statistics of Temperature (Average)</h2>");
            out.println("<hr>");

            int recordNo = 1;

            while (rs.next()) {

                String location = rs.getString("location");
                double minTemp = rs.getDouble("minTemp");
                double maxTemp = rs.getDouble("maxTemp");
                double temp9am = rs.getDouble("temp9am");
                double temp3pm = rs.getDouble("temp3pm");

                double average =
                        (minTemp + maxTemp + temp9am + temp3pm) / 4.0;

                out.println("<h3>Record " + recordNo + "</h3>");

                out.println("<p>");
                out.println("Location : " + location + "<br>");
                out.println("MinTemp : " + minTemp + "<br>");
                out.println("MaxTemp : " + maxTemp + "<br>");
                out.println("Temp9am : " + temp9am + "<br>");
                out.println("Temp3pm : " + temp3pm + "<br>");
                out.println("<b>Average Temperature : "
                        + String.format("%.2f", average)
                        + " °C</b>");
                out.println("</p>");

                out.println("<hr>");

                recordNo++;
            }

            out.println("<br>");
            out.println("<a href='ListServlet'>Back to Records</a>");

            out.println("</body>");
            out.println("</html>");

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {

            out.println("<h3 style='color:red;'>Error: "
                    + e.getMessage() + "</h3>");

            e.printStackTrace();
        }
    }
}