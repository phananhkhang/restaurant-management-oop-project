package util;

import model.Staff;
import model.enums.Role;
import model.enums.StaffStatus;
import service.StaffService;

public class Authentication {
    private static Staff currentUser = null;
    private static StaffService staffService;
    
    public static void setStaffService(StaffService service) {
        staffService = service;
    }
    
    // Đăng nhập
    public static boolean login(String username, String password) {
        if (staffService == null) return false;
        
        for (Staff staff : staffService.getAll()) {
            if (staff.getUsername().equals(username) && 
                staff.checkPassword(password) && 
                staff.getStatus() == StaffStatus.ACTIVE) {
                currentUser = staff;
                return true;
            }
        }
        return false;
    }
    
    // Đăng xuất
    public static void logout() {
        currentUser = null;
    }
    
    // Lấy user hiện tại
    public static Staff getCurrentUser() {
        return currentUser;
    }
    
    // Kiểm tra quyền
    public static boolean hasRole(Role... allowedRoles) {
        if (currentUser == null) return false;
        
        for (Role role : allowedRoles) {
            if (currentUser.getRole() == role) {
                return true;
            }
        }
        return false;
    }
    
    // Kiểm tra quyền quản lý nhân viên
    public static boolean canManageStaff() {
        return hasRole(Role.MANAGER);
    }
    
    // Kiểm tra quyền duyệt chi phí
    public static boolean canApproveExpense() {
        return hasRole(Role.MANAGER);
    }
    
    // Kiểm tra quyền xem báo cáo
    public static boolean canViewReports() {
        return hasRole(Role.MANAGER);
    }
    
    // Kiểm tra quyền quản lý thực đơn (Manager và Chef)
    public static boolean canManageMenu() {
        return hasRole(Role.MANAGER, Role.CHEF);
    }
    
    // Kiểm tra quyền quản lý đơn hàng (Manager, Waiter, Cashier)
    public static boolean canManageOrders() {
        return hasRole(Role.MANAGER, Role.WAITER, Role.CASHIER);
    }
    
    // Kiểm tra quyền quản lý bàn (Manager, Waiter, Host)
    public static boolean canManageTables() {
        return hasRole(Role.MANAGER, Role.WAITER, Role.HOST);
    }
    
    // Kiểm tra quyền quản lý kho (Manager, Chef)
    public static boolean canManageInventory() {
        return hasRole(Role.MANAGER, Role.CHEF);
    }
    
    // Kiểm tra quyền quản lý thanh toán (Manager, Cashier)
    public static boolean canManagePayments() {
        return hasRole(Role.MANAGER, Role.CASHIER);
    }
    
    // Kiểm tra có phải Manager không
    public static boolean isManager() {
        return hasRole(Role.MANAGER);
    }
    
    // Kiểm tra có phải Chef không
    public static boolean isChef() {
        return hasRole(Role.CHEF);
    }
    
    // Kiểm tra có phải Waiter không
    public static boolean isWaiter() {
        return hasRole(Role.WAITER);
    }
    
    // Kiểm tra có phải Cashier không
    public static boolean isCashier() {
        return hasRole(Role.CASHIER);
    }
}