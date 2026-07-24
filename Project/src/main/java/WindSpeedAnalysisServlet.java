import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/WindSpeedAnalysisServlet")
public class WindSpeedAnalysisServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {

            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Batch Analysis
            String batchQuery =
                    "SELECT " +
                    "AVG(windSpeed9am) AS avg9am, " +
                    "AVG(windSpeed3pm) AS avg3pm, " +
                    "MAX(windSpeed9am) AS max9am, " +
                    "MAX(windSpeed3pm) AS max3pm, " +
                    "MIN(windSpeed9am) AS min9am, " +
                    "MIN(windSpeed3pm) AS min3pm " +
                    "FROM weather_data";

            ResultSet rs = stmt.executeQuery(batchQuery);

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Wind Speed Analysis</title>");
            out.println("<style>");
            out.println("body{font-family:Arial;padding:20px;}");
            out.println("table{border-collapse:collapse;width:60%;}");
            out.println("th,td{border:1px solid black;padding:10px;text-align:center;}");
            out.println("th{background-color:#87CEEB;}");
            out.println(".high{color:red;font-weight:bold;}");
            out.println(".normal{color:green;font-weight:bold;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h1>🌪 Wind Speed Analysis</h1>");

            // =========================
            // BATCH ANALYSIS
            // =========================
            out.println("<h2>📊 Batch Analysis</h2>");

            if (rs.next()) {

                out.println("<table>");

                out.println("<tr>");
                out.println("<th>Analysis</th>");
                out.println("<th>WindSpeed9am</th>");
                out.println("<th>WindSpeed3pm</th>");
                out.println("</tr>");

                out.println("<tr>");
                out.println("<td>Average</td>");
                out.println("<td>" + String.format("%.2f", rs.getDouble("avg9am")) + "</td>");
                out.println("<td>" + String.format("%.2f", rs.getDouble("avg3pm")) + "</td>");
                out.println("</tr>");

                out.println("<tr>");
                out.println("<td>Maximum</td>");
                out.println("<td>" + rs.getDouble("max9am") + "</td>");
                out.println("<td>" + rs.getDouble("max3pm") + "</td>");
                out.println("</tr>");

                out.println("<tr>");
                out.println("<td>Minimum</td>");
                out.println("<td>" + rs.getDouble("min9am") + "</td>");
                out.println("<td>" + rs.getDouble("min3pm") + "</td>");
                out.println("</tr>");

                out.println("</table>");
            }

            // =========================
            // REAL-TIME ANALYSIS
            // =========================
            out.println("<h2 style='margin-top:40px;'>⚡ Real-Time Analysis</h2>");

            String realtimeQuery =
                    "SELECT rowID, location, windSpeed9am, windSpeed3pm " +
                    "FROM weather_data " +
                    "ORDER BY rowID DESC LIMIT 10";

            ResultSet realtimeRS = stmt.executeQuery(realtimeQuery);

            out.println("<table>");

            out.println("<tr>");
            out.println("<th>Row ID</th>");
            out.println("<th>Location</th>");
            out.println("<th>WindSpeed9am</th>");
            out.println("<th>Status 9am</th>");
            out.println("<th>WindSpeed3pm</th>");
            out.println("<th>Status 3pm</th>");
            out.println("</tr>");

            while (realtimeRS.next()) {

                double ws9am = realtimeRS.getDouble("windSpeed9am");
                double ws3pm = realtimeRS.getDouble("windSpeed3pm");

                String status9am;
                String status3pm;

                // Real-time Logic
                if (ws9am >= 40) {
                    status9am = "<span class='high'>HIGH WIND</span>";
                } else {
                    status9am = "<span class='normal'>NORMAL</span>";
                }

                if (ws3pm >= 40) {
                    status3pm = "<span class='high'>HIGH WIND</span>";
                } else {
                    status3pm = "<span class='normal'>NORMAL</span>";
                }

                out.println("<tr>");

                out.println("<td>" + realtimeRS.getString("rowID") + "</td>");
                out.println("<td>" + realtimeRS.getString("location") + "</td>");

                out.println("<td>" + ws9am + "</td>");
                out.println("<td>" + status9am + "</td>");

                out.println("<td>" + ws3pm + "</td>");
                out.println("<td>" + status3pm + "</td>");

                out.println("</tr>");
            }

            out.println("</table>");

            out.println("<br><br>");
            out.println("<a href='ListServlet'>⬅ Back to Weather Records</a>");

            out.println("</body>");
            out.println("</html>");

            rs.close();
            realtimeRS.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {

            out.println("<h2 style='color:red;'>Error: "
                    + e.getMessage() + "</h2>");

            e.printStackTrace();
        }
    }
}