package model;

import model.common.CsvEntity;
import model.enums.Role;
import model.enums.StaffStatus;

// Nhân viên kế thừa Person và dùng enums cho role/status
public class Staff extends Person implements CsvEntity{
    // Tài khoản đăng nhập
    private String username;
    // Mật khẩu (gợi ý: nên mã hoá khi lưu thực tế)
    private String password;
    // Vai trò
    private Role role;
    // Lương cơ bản (đơn vị: VND)
    private double salary;
    // Trạng thái làm việc
    private StaffStatus status;

    public Staff() {
        this.status = StaffStatus.ACTIVE; // mặc định
        this.role = Role.WAITER; // mặc định là WAITER
    }

    public Staff(String id, String fullName, String phone, String address,
                 String username, String password, Role role, double salary, StaffStatus StaffStatus) {
        super(id, fullName, phone, address);
        this.username = username;
        this.password = password;
        this.role = role;
        this.salary = salary;
        this.status = StaffStatus;
    }

    // Getter/Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public StaffStatus getStatus() { return status; }
    public void setStatus(StaffStatus status) { this.status = status; }

    // Kiểm tra mật khẩu đơn giản (chỉ so chuỗi – demo)
    public boolean checkPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    @Override
    public void displayInfo() {
        System.out.printf("%-8s %-22s %-12s %-10s %-12s %-10s%n",
                id, fullName, phone, role.name(), String.format("%,.0f", salary), status.name());
    }


    // CsvEntity methods
    @Override
    public String[] csvHeader() {
        return new String[] {
                "ID", "FullName", "Phone", "Address",
                "Username", "Password", "Role", "Salary", "Status"
        };
    }

    @Override
    public String[] toCsvRow() {
        return new String[] {
                s(id),
                s(fullName),
                s(phone),
                s(address),
                s(username),
                s(password),
                s(role),
                String.valueOf(salary),
                s(status)
        };
    }

    private static String s(Object o) {
        return (o == null) ? "" : o.toString();
    }

}