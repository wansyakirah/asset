import java.sql.*;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/ConfirmDeleteServlet")
public class ConfirmServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) {

        try {
            String rowID = request.getParameter("rowID");
            Connection conn = DBConnection.getConnection();

            // Soft delete (tandakan is_deleted=1)
            PreparedStatement ps = conn.prepareStatement("UPDATE weather_data SET is_deleted=1 WHERE rowID=?");
            ps.setString(1, rowID);
            ps.executeUpdate();

            response.sendRedirect("ListServlet");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
