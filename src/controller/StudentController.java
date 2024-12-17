package controller;

import Model.Student;
import service.StudentService;
import view.StudentPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StudentController {
    private final StudentService studentService; // Dịch vụ xử lý logic nghiệp vụ
    private final StudentPanel studentPanel;    // Giao diện hiển thị quản lý sinh viên

    public StudentController(StudentPanel studentPanel) {
        this.studentPanel = studentPanel;
        this.studentService = new StudentService(); // Khởi tạo đối tượng service

        studentPanel.getAddStudentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("ello");
            }
        });
        loadStudentTable();
    }

    private void initController() {
        // Gán sự kiện cho các nút trên StudentPanel
        studentPanel.getAddStudentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Hello");
            }
        });

        studentPanel.getEditStudentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditStudent();
            }
        });

        studentPanel.getDeleteStudentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteStudent();
            }
        });

        studentPanel.getSearchButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSearchStudent();
            }
        });
    }

    private void loadStudentTable() {
        // Lấy danh sách sinh viên từ service và đổ dữ liệu vào bảng
        List<Student> students = studentService.getAllStudents();
        DefaultTableModel model = (DefaultTableModel) studentPanel.getStudentTable().getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        for (Student student : students) {
            model.addRow(new Object[]{
                    student.getStudentId(),
                    student.getFullName(),
                    student.getDateOfBirth(),
                    student.getGender(),
                    student.getAddress(),
                    student.getEmail(),
                    student.getRoom().getRoomId(),
                    student.getContract().getContractId()
            });
        }
    }

    private void onAddStudent() {
        // Hiển thị hộp thoại nhập thông tin sinh viên mới
        try {
            System.out.println("Add button clicked");
            Student student = studentPanel.showStudentDialog(null);
            if (student != null) {
                boolean isAdded = studentService.addStudent(student);
                if (isAdded) {
                    JOptionPane.showMessageDialog(studentPanel, "Thêm sinh viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadStudentTable();
                } else {
                    JOptionPane.showMessageDialog(studentPanel, "Thêm sinh viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();  // In ra lỗi nếu có
        }
    }

    private void onEditStudent() {
        // Lấy sinh viên đang được chọn trong bảng
        int selectedRow = studentPanel.getStudentTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(studentPanel, "Vui lòng chọn sinh viên cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin sinh viên từ bảng
        DefaultTableModel model = (DefaultTableModel) studentPanel.getStudentTable().getModel();
        int studentId = (int) model.getValueAt(selectedRow, 0);
        Student student = studentService.getStudentById(studentId);

        // Hiển thị hộp thoại chỉnh sửa thông tin sinh viên
        Student updatedStudent = studentPanel.showStudentDialog(student);
        if (updatedStudent != null) {
            boolean isUpdated = studentService.updateStudent(updatedStudent);
            if (isUpdated) {
                JOptionPane.showMessageDialog(studentPanel, "Cập nhật sinh viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadStudentTable();
            } else {
                JOptionPane.showMessageDialog(studentPanel, "Cập nhật sinh viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDeleteStudent() {
        // Lấy sinh viên đang được chọn trong bảng
        int selectedRow = studentPanel.getStudentTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(studentPanel, "Vui lòng chọn sinh viên cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin sinh viên từ bảng
        DefaultTableModel model = (DefaultTableModel) studentPanel.getStudentTable().getModel();
        int studentId = (int) model.getValueAt(selectedRow, 0);

        // Xác nhận xóa
        int confirm = JOptionPane.showConfirmDialog(studentPanel, "Bạn có chắc chắn muốn xóa sinh viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean isDeleted = studentService.deleteStudent(studentId);
            if (isDeleted) {
                JOptionPane.showMessageDialog(studentPanel, "Xóa sinh viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadStudentTable();
            } else {
                JOptionPane.showMessageDialog(studentPanel, "Xóa sinh viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onSearchStudent() {
        // Tìm kiếm sinh viên theo tên
        String searchKeyword = studentPanel.getSearchField().getText();
        List<Student> students = studentService.searchStudentsByName(searchKeyword);

        // Cập nhật bảng với kết quả tìm kiếm
        DefaultTableModel model = (DefaultTableModel) studentPanel.getStudentTable().getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        for (Student student : students) {
            model.addRow(new Object[]{
                    student.getStudentId(),
                    student.getFullName(),
                    student.getDateOfBirth(),
                    student.getGender(),
                    student.getAddress(),
                    student.getEmail(),
                    student.getRoom().getRoomId(),
                    student.getContract().getContractId()
            });
        }
    }
}
