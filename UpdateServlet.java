import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/UpdateServlet")
public class UpdateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "UPDATE weather_data SET location=?, minTemp=?, maxTemp=?, rainfall=?, windSpeed9am=?, windSpeed3pm=?, humidity9am=?, humidity3pm=?, temp9am=?, temp3pm=?, rainToday=? WHERE rowID=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            // Ambil semua parameter dari form EditServlet
            ps.setString(1, request.getParameter("location"));
            ps.setDouble(2, Double.parseDouble(request.getParameter("minTemp")));
            ps.setDouble(3, Double.parseDouble(request.getParameter("maxTemp")));
            ps.setDouble(4, Double.parseDouble(request.getParameter("rainfall")));
            ps.setDouble(5, Double.parseDouble(request.getParameter("windSpeed9am")));
            ps.setDouble(6, Double.parseDouble(request.getParameter("windSpeed3pm")));
            ps.setDouble(7, Double.parseDouble(request.getParameter("humidity9am")));
            ps.setDouble(8, Double.parseDouble(request.getParameter("humidity3pm")));
            ps.setDouble(9, Double.parseDouble(request.getParameter("temp9am")));
            ps.setDouble(10, Double.parseDouble(request.getParameter("temp3pm")));
            ps.setString(11, request.getParameter("rainToday"));
            ps.setString(12, request.getParameter("rowID"));

            ps.executeUpdate();
            ps.close();
            conn.close();

            // Lepas update, redirect balik ke senarai
            response.sendRedirect("ListServlet");

        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<p style='color:red;'>❌ Error: " + e.getMessage() + "</p>");
            out.println("<a href='ListServlet'>Back to List</a>");
        }
    }
}


