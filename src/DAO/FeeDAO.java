package DAO;

import Model.Fee;
import Model.FeeType;
import Model.Student;
import connectionDB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeeDAO implements Search{
    public static Connection connection;

    public FeeDAO(){
        connection = DBConnection.getConnection();
    }

    public List<Fee> getAllFees() {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM fees";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Fee fee = new Fee();
                fee.setFeeId(resultSet.getInt("fee_id"));

                // Chuyển đổi String từ database thành Enum FeeType
                String feeTypeStr = resultSet.getString("fee_type");
                FeeType feeType = FeeType.valueOf(feeTypeStr.toUpperCase());
                fee.setFeeType(feeType);

                fee.setFeeAmount(resultSet.getDouble("amount"));
                fee.setPaymentDate(resultSet.getString("payment_date"));

                // Thiết lập thông tin sinh viên
                Student student = new Student();
                student.setStudentId(resultSet.getInt("student_id"));
                fee.setStudent(student);

                fee.setIs_paid(resultSet.getInt("is_paid"));

                fees.add(fee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid fee type found in database.");
            e.printStackTrace();
        }

        return fees;
    }



    @Override
    public List<Fee> searchByName(String feeTypeName) {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM fees WHERE fee_type = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Chuyển feeTypeName sang định dạng phù hợp với enum
            FeeType feeType = FeeType.valueOf(feeTypeName.toUpperCase());
            statement.setString(1, feeType.name()); // Ghi tên enum vào query

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Fee fee = new Fee();
                    fee.setFeeId(resultSet.getInt("fee_id"));
                    fee.setFeeType(feeType); // Trực tiếp sử dụng enum FeeType
                    fee.setFeeAmount(resultSet.getDouble("amount"));
                    fee.setPaymentDate(resultSet.getString("payment_date"));

                    Student student = new Student();
                    student.setStudentId(resultSet.getInt("student_id"));
                    fee.setStudent(student);

                    fee.setIs_paid(resultSet.getInt("is_paid"));
                    fees.add(fee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid fee type input: " + feeTypeName);
        }
        return fees;
    }

    public boolean addNewFee(Fee fee) {
        String sql = "INSERT INTO fees (fee_type, amount, payment_date, student_id, is_paid) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fee.getFeeType().name()); // Chuyển enum FeeType sang String
            statement.setDouble(2, fee.getFeeAmount());

            // Kiểm tra nếu chưa trả phí (is_paid == 0), để payment_date là NULL
            if (fee.getIs_paid() == 1) {
                statement.setString(3, fee.getPaymentDate()); // Đặt payment_date nếu đã trả
            } else {
                statement.setNull(3, java.sql.Types.DATE); // Đặt payment_date là NULL nếu chưa trả
            }

            statement.setInt(4, fee.getStudent().getStudentId());
            statement.setInt(5, fee.getIs_paid());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0; // Trả về true nếu thêm thành công
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateFee(Fee fee) {
        System.out.println("Accessed updateFee method");

        // SQL query cập nhật thông tin khoản phí
        String sql = "UPDATE fees SET fee_type = ?, amount = ?, payment_date = ?, student_id = ?, is_paid = ? WHERE fee_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            // In thông tin để kiểm tra
            System.out.println("Fee Type: " + fee.getFeeType()); // Kiểm tra Fee Type
            statement.setString(1, fee.getFeeType().name()); // Chuyển enum FeeType thành String

            System.out.println("Fee Amount: " + fee.getFeeAmount()); // Kiểm tra Fee Amount
            statement.setDouble(2, fee.getFeeAmount()); // Cập nhật số tiền

            // Kiểm tra nếu chưa trả phí (is_paid == 0), đặt payment_date là NULL
            if (fee.getIs_paid() == 1) {
                System.out.println("Payment Date: " + fee.getPaymentDate()); // Kiểm tra Payment Date
                statement.setString(3, fee.getPaymentDate()); // Nếu đã trả phí, sử dụng ngày thanh toán
            } else {
                System.out.println("Setting Payment Date to NULL as fee is not paid.");
                statement.setNull(3, java.sql.Types.DATE); // Nếu chưa trả phí, đặt payment_date là NULL
            }

            System.out.println("Student ID: " + fee.getStudent().getStudentId()); // Kiểm tra Student ID
            statement.setInt(4, fee.getStudent().getStudentId()); // Cập nhật Student ID

            System.out.println("Is Paid: " + fee.getIs_paid()); // Kiểm tra trạng thái đã trả phí
            statement.setInt(5, fee.getIs_paid()); // Cập nhật trạng thái thanh toán (is_paid)

            System.out.println("Fee ID: " + fee.getFeeId()); // Kiểm tra Fee ID
            statement.setInt(6, fee.getFeeId()); // Cập nhật ID của khoản phí

            // Thực thi câu lệnh cập nhật
            int rowsUpdated = statement.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated); // Kiểm tra số hàng đã được cập nhật

            return rowsUpdated > 0; // Trả về true nếu cập nhật thành công (số hàng cập nhật > 0)

        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi nếu có
        }

        return false; // Trả về false nếu có lỗi
    }



    public boolean deleteFee(int feeId) {
        String sql = "DELETE FROM fees WHERE fee_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, feeId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
//    public Fee getFeeById(int feeId) {
//        String sql = "SELECT * FROM fees WHERE fee_id = ?";
//        try (PreparedStatement statement = connection.prepareStatement(sql)) {
//
//            statement.setInt(1, feeId);
//            try (ResultSet resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    Fee fee = new Fee();
//                    fee.setFeeId(resultSet.getInt("fee_id"));
//                    fee.setFeeType(resultSet.getString("fee_type"));
//                    fee.setFeeAmount(resultSet.getDouble("fee_amount"));
//                    fee.setPaymentDate(resultSet.getString("payment_date"));
//
//                    Student student = new Student();
//                    student.setStudentId(resultSet.getInt("student_id"));
//                    fee.setStudent(student);
//
//                    fee.setIs_paid(resultSet.getInt("is_paid"));
//
//                    return fee;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
