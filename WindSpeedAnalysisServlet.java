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

        String action = request.getParameter("action");

        // =========================
        // STEP 1: SHOW BUTTON PAGE
        // =========================
        if (action == null) {

            out.println("<html><head><title>Wind Speed Analysis</title></head>");
            out.println("<body style='font-family:Arial;text-align:center;'>");

            out.println("<h1>🌪 Wind Speed Analysis System</h1>");
            out.println("<p>Please choose analysis type:</p>");

            out.println("<form action='WindSpeedAnalysisServlet' method='get'>");
            out.println("<button type='submit' name='action' value='batch' "
                    + "style='padding:10px 20px;margin:10px;'>Batch Analysis</button>");
            
            out.println("<a class='btn' href='ListServlet'>");
            out.println("3. BACK TO DATASET");
            out.println("</a>");

            out.println("<button type='submit' name='action' value='realtime' "
                    + "style='padding:10px 20px;margin:10px;'>Real-Time Analysis</button>");
            out.println("</form>");

            out.println("</body></html>");
            return;
        }

        // =========================
        // STEP 2: ANALYSIS PAGE
        // =========================
        try {

            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            response.setContentType("text/html");

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Wind Speed Analysis</title>");
            out.println("<style>");
            out.println("body{font-family:Arial;padding:20px;}");
            out.println("table{border-collapse:collapse;width:70%;margin:auto;}");
            out.println("th,td{border:1px solid black;padding:10px;text-align:center;}");
            out.println("th{background-color:#87CEEB;}");
            out.println(".high{color:red;font-weight:bold;}");
            out.println(".normal{color:green;font-weight:bold;}");
            out.println("h2{text-align:center;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h1 style='text-align:center;'>🌪 Wind Speed Analysis</h1>");

            // =========================
            // BATCH ANALYSIS
            // =========================
            if (action.equals("batch")) {

                out.println("<h2>📊 Batch Analysis</h2>");

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

                if (rs.next()) {

                    out.println("<table>");

                    out.println("<tr><th>Analysis</th><th>9 AM</th><th>3 PM</th></tr>");

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

                rs.close();
            }

            // =========================
            // REAL-TIME ANALYSIS
            // =========================
            else if (action.equals("realtime")) {

                out.println("<h2>⚡ Real-Time Analysis (Latest 10 Records)</h2>");

                String realtimeQuery =
                        "SELECT rowID, location, windSpeed9am, windSpeed3pm " +
                        "FROM weather_data ORDER BY rowID DESC LIMIT 10";

                ResultSet rs2 = stmt.executeQuery(realtimeQuery);

                out.println("<table>");

                out.println("<tr>");
                out.println("<th>ID</th>");
                out.println("<th>Location</th>");
                out.println("<th>9 AM</th>");
                out.println("<th>Status</th>");
                out.println("<th>3 PM</th>");
                out.println("<th>Status</th>");
                out.println("</tr>");

                while (rs2.next()) {

                    double ws9 = rs2.getDouble("windSpeed9am");
                    double ws3 = rs2.getDouble("windSpeed3pm");

                    String status9 = (ws9 >= 40)
                            ? "<span class='high'>HIGH</span>"
                            : "<span class='normal'>NORMAL</span>";

                    String status3 = (ws3 >= 40)
                            ? "<span class='high'>HIGH</span>"
                            : "<span class='normal'>NORMAL</span>";

                    out.println("<tr>");
                    out.println("<td>" + rs2.getString("rowID") + "</td>");
                    out.println("<td>" + rs2.getString("location") + "</td>");
                    out.println("<td>" + ws9 + "</td>");
                    out.println("<td>" + status9 + "</td>");
                    out.println("<td>" + ws3 + "</td>");
                    out.println("<td>" + status3 + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");

                rs2.close();
            }

            out.println("<br><div style='text-align:center; margin-top:20px;'>");
            out.println("<a href='WindSpeedAnalysisServlet'>⬅ Back</a>");
            out.println("</div>");

            out.println("</body></html>");

            stmt.close();
            conn.close();

        } catch (Exception e) {
            out.println("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace();
        }
    }
}