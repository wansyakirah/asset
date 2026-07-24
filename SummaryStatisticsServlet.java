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

@WebServlet("/SummaryStatisticsServlet")
public class SummaryStatisticsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // =========================
        // SSE STREAM
        // =========================
        if ("stream".equals(action)) {

            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();

            try {

                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();

                String sql =
                        "SELECT rowID, location, minTemp, maxTemp, temp9am, temp3pm " +
                        "FROM weather_data " +
                        "WHERE is_active = 1 " +
                        "ORDER BY rowID DESC";

                ResultSet rs = stmt.executeQuery(sql);

                int recordNo = 1;

                while (rs.next()) {

                    double min = rs.getDouble("minTemp");
                    double max = rs.getDouble("maxTemp");
                    double t9 = rs.getDouble("temp9am");
                    double t3 = rs.getDouble("temp3pm");

                    double avg =
                            (min + max + t9 + t3) / 4.0;

                    String html =
                            "<div style='width:700px;"
                                    + "margin:15px auto;"
                                    + "padding:20px;"
                                    + "border:3px solid #2196F3;"
                                    + "border-radius:12px;"
                                    + "background:#f8fbff;"
                                    + "font-size:22px;'>"

                                    + "<h2 style=\"color:#2196F3;\">Record "
                                    + recordNo + "</h2>"

                                    + "<b>Location :</b> "
                                    + rs.getString("location") + "<br><br>"

                                    + "<b>MinTemp :</b> "
                                    + min + "<br>"

                                    + "<b>MaxTemp :</b> "
                                    + max + "<br>"

                                    + "<b>Temp9am :</b> "
                                    + t9 + "<br>"

                                    + "<b>Temp3pm :</b> "
                                    + t3 + "<br><br>"

                                    + "<b>Average Temperature :</b> "
                                    + String.format("%.2f", avg)
                                    + " °C"

                                    + "</div>";

                    html = html.replace("\n", "");

                    out.write("data: " + html + "\n\n");
                    out.flush();

                    Thread.sleep(2000);

                    recordNo++;
                }

                rs.close();
                stmt.close();
                conn.close();

            } catch (Exception e) {

                out.write("event:error\n");
                out.write("data:" + e.getMessage() + "\n\n");
                out.flush();
            }

            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // =========================
        // CONTROL PANEL
        // =========================
        if (action == null) {

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Temperature Analysis System</title>");

            out.println("<style>");

            out.println("body{");
            out.println("margin:0;");
            out.println("height:100vh;");
            out.println("display:flex;");
            out.println("justify-content:center;");
            out.println("align-items:center;");
            out.println("background:#f3f3f5;");
            out.println("font-family:Arial;");
            out.println("}");

            out.println(".container{");
            out.println("width:750px;");
            out.println("padding:60px;");
            out.println("background:#fafafa;");
            out.println("border:1px solid #ddd;");
            out.println("border-radius:10px;");
            out.println("box-shadow:0 0 15px rgba(0,0,0,0.08);");
            out.println("}");

            out.println(".btn{");
            out.println("display:block;");
            out.println("width:500px;");
            out.println("margin:25px auto;");
            out.println("padding:25px;");
            out.println("font-size:28px;");
            out.println("text-decoration:none;");
            out.println("text-align:center;");
            out.println("color:#666;");
            out.println("background:white;");
            out.println("border:1px solid #ccc;");
            out.println("border-radius:8px;");
            out.println("box-shadow:0 2px 8px rgba(0,0,0,0.12);");
            out.println("}");

            out.println("</style>");

            out.println("</head>");
            out.println("<body>");

            out.println("<div class='container'>");

            out.println("<a class='btn' href='SummaryStatisticsServlet?action=realtime'>");
            out.println("1. VIEW REAL-TIME RECORDS");
            out.println("</a>");

            out.println("<a class='btn' href='SummaryStatisticsServlet?action=batch'>");
            out.println("2. GENERATE SUMMARY AVERAGE");
            out.println("</a>");

            out.println("<a class='btn' href='ListServlet'>");
            out.println("3. BACK TO DATASET");
            out.println("</a>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

            return;
        }

        try {

            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            out.println("<html><body style='font-family:Arial;'>");

            // =========================
            // BATCH ANALYSIS
            // =========================
            if ("batch".equals(action)) {

                out.println("<div style='text-align:center;margin:20px;'>");
                out.println("<a href='SummaryStatisticsServlet' "
                        + "style='font-size:24px;padding:12px 30px;"
                        + "background:#4CAF50;color:white;"
                        + "text-decoration:none;'>⬅ Back</a>");
                out.println("</div>");

                out.println("<h1 style='text-align:center;'>📊 Batch Analysis</h1>");

                String sql =
                        "SELECT location,minTemp,maxTemp,temp9am,temp3pm " +
                        "FROM weather_data " +
                        "WHERE is_active = 1";

                ResultSet rs = stmt.executeQuery(sql);

                int totalRecords = 0;
                double totalAverage = 0;

                StringBuilder rows = new StringBuilder();

                while (rs.next()) {

                    double avg =
                            (rs.getDouble("minTemp")
                                    + rs.getDouble("maxTemp")
                                    + rs.getDouble("temp9am")
                                    + rs.getDouble("temp3pm")) / 4.0;

                    totalAverage += avg;
                    totalRecords++;

                    rows.append("<tr>");
                    rows.append("<td>").append(rs.getString("location")).append("</td>");
                    rows.append("<td>").append(rs.getDouble("minTemp")).append("</td>");
                    rows.append("<td>").append(rs.getDouble("maxTemp")).append("</td>");
                    rows.append("<td>").append(rs.getDouble("temp9am")).append("</td>");
                    rows.append("<td>").append(rs.getDouble("temp3pm")).append("</td>");
                    rows.append("<td><b>")
                            .append(String.format("%.2f", avg))
                            .append("</b></td>");
                    rows.append("</tr>");
                }

                out.println("<div style='width:500px;"
                        + "margin:auto;"
                        + "padding:20px;"
                        + "border:3px solid green;"
                        + "border-radius:12px;"
                        + "text-align:center;'>");

                out.println("<h2>📈 Summary Card</h2>");
                out.println("<p>Total Records : " + totalRecords + "</p>");

                if (totalRecords > 0) {

                    out.println("<p>Overall Average Temperature : "
                            + String.format("%.2f",
                            totalAverage / totalRecords)
                            + " °C</p>");
                }

                out.println("</div><br>");

                out.println("<table border='1' "
                        + "style='width:90%;margin:auto;border-collapse:collapse;'>");

                out.println("<tr>");
                out.println("<th>Location</th>");
                out.println("<th>MinTemp</th>");
                out.println("<th>MaxTemp</th>");
                out.println("<th>Temp9am</th>");
                out.println("<th>Temp3pm</th>");
                out.println("<th>Average</th>");
                out.println("</tr>");

                out.println(rows.toString());

                out.println("</table>");

                rs.close();
            }

            // =========================
            // REAL TIME PAGE
            // =========================
            else if ("realtime".equals(action)) {

                out.println("<div style='text-align:center;margin:20px;'>");

                out.println("<a href='SummaryStatisticsServlet' "
                        + "style='font-size:24px;padding:12px 30px;"
                        + "background:#2196F3;color:white;"
                        + "text-decoration:none;border-radius:8px;'>⬅ Back</a>");

                out.println("&nbsp;&nbsp;");

                out.println("<button onclick='startStream()' "
                        + "style='font-size:24px;padding:12px 30px;'>Start Stream</button>");

                out.println("&nbsp;&nbsp;");

                out.println("<button onclick='stopStream()' "
                        + "style='font-size:24px;padding:12px 30px;'>Stop Stream</button>");

                out.println("</div>");

                out.println("<h1 style='text-align:center;'>⚡ Real-Time Analysis</h1>");

                out.println("<div id='records'></div>");

                out.println("<script>");

                out.println("let source=null;");

                out.println("function startStream(){");

                out.println("document.getElementById('records').innerHTML='';");

                out.println("source=new EventSource('SummaryStatisticsServlet?action=stream');");

                out.println("source.onmessage=function(event){");

                out.println("document.getElementById('records').innerHTML="
                        + "event.data + document.getElementById('records').innerHTML;");

                out.println("};");

                out.println("source.onerror=function(){");
                out.println("if(source){source.close();}");
                out.println("};");

                out.println("}");

                out.println("function stopStream(){");
                out.println("if(source){");
                out.println("source.close();");
                out.println("alert('Streaming Stopped');");
                out.println("}");
                out.println("}");

                out.println("</script>");
            }

            out.println("</body></html>");

            stmt.close();
            conn.close();

        } catch (Exception e) {

            out.println("<h2 style='color:red;'>Error: "
                    + e.getMessage()
                    + "</h2>");
        }
    }
}