import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/ImportServlet")
@MultipartConfig
public class ImportServlet extends HttpServlet {

    private Double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // boleh tukar ke 0.0 kalau nak default
        }
        return Double.parseDouble(value);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Part filePart = request.getPart("csvfile");
        BufferedReader br = new BufferedReader(new InputStreamReader(filePart.getInputStream()));

        try {
            Connection conn = DBConnection.getConnection();
            String line;

            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO weather_data(rowID,location,minTemp,maxTemp,rainfall,windSpeed9am,windSpeed3pm,humidity9am,humidity3pm,temp9am,temp3pm,rainToday) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "location=VALUES(location), minTemp=VALUES(minTemp), maxTemp=VALUES(maxTemp), rainfall=VALUES(rainfall), " +
                    "windSpeed9am=VALUES(windSpeed9am), windSpeed3pm=VALUES(windSpeed3pm), humidity9am=VALUES(humidity9am), " +
                    "humidity3pm=VALUES(humidity3pm), temp9am=VALUES(temp9am), temp3pm=VALUES(temp3pm), rainToday=VALUES(rainToday)"
                );

                ps.setString(1, data[0]); // RowID
                ps.setString(2, data[1]); // Location
                ps.setObject(3, parseDoubleSafe(data[2])); // MinTemp
                ps.setObject(4, parseDoubleSafe(data[3])); // MaxTemp
                ps.setObject(5, parseDoubleSafe(data[4])); // Rainfall
                ps.setObject(6, parseDoubleSafe(data[5])); // WindSpeed9am
                ps.setObject(7, parseDoubleSafe(data[6])); // WindSpeed3pm
                ps.setObject(8, parseDoubleSafe(data[7])); // Humidity9am
                ps.setObject(9, parseDoubleSafe(data[8])); // Humidity3pm
                ps.setObject(10, parseDoubleSafe(data[9])); // Temp9am
                ps.setObject(11, parseDoubleSafe(data[10])); // Temp3pm
                ps.setString(12, data[11]); // RainToday

                ps.executeUpdate();
            }

            out.println("<h2>✅ Data imported successfully (duplicates updated)!</h2>");
            out.println("<a href='ListServlet'>View Records</a>");

        } catch (Exception e) {
            out.println("<h2>❌ Error: " + e.getMessage() + "</h2>");
            e.printStackTrace();
        }
    }
}
