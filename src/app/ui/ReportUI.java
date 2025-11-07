package app.ui;

import java.time.LocalDate;
import java.util.Scanner;

import service.ReportService;
import service.StaffService;
import service.MenuItemService;
import service.InventoryItemService;
import service.PaymentService;
import service.ExpenseService;
import service.TableEntityService;
import service.BillService;
import util.Authentication;

public class ReportUI {
    private static final Scanner scanner = new Scanner(System.in);
    private static ReportService reportService;

    private static int getIntInput() {
        try {
            int v = Integer.parseInt(scanner.nextLine().trim());
            return v;
        } catch (Exception e) {
            return -1;
        }
    }

    private static void pause() {
        System.out.print("\nNhấn Enter để tiếp tục...");
        scanner.nextLine();
    }

    private static LocalDate[] chooseTimeRange() {
        System.out.println("\nChọn khoảng thời gian:");
        System.out.println(" 1. Ngày (YYYY-MM-DD)");
        System.out.println(" 2. Tháng (MM-YYYY)");
        System.out.println(" 3. Quý (Q-YYYY)");
        System.out.println(" 4. Năm (YYYY)");
        System.out.println(" 5. Tùy chọn (Từ ngày -> Đến ngày)");
        System.out.println(" 0. Hủy (dùng mặc định)");
        System.out.print("Chọn: ");
        int opt = getIntInput();
        try {
            switch (opt) {
                case 1: {
                    System.out.print("Ngày (YYYY-MM-DD): ");
                    LocalDate d = LocalDate.parse(scanner.nextLine().trim());
                    return new LocalDate[]{d, d};
                }
                case 2: {
                    System.out.print("Tháng-năm (MM-YYYY): ");
                    String[] p = scanner.nextLine().trim().split("-");
                    int m = Integer.parseInt(p[0]); int y = Integer.parseInt(p[1]);
                    LocalDate s = LocalDate.of(y, m, 1);
                    LocalDate e = s.withDayOfMonth(s.lengthOfMonth());
                    return new LocalDate[]{s, e};
                }
                case 3: {
                    System.out.print("Quý-năm (Q-YYYY): ");
                    String[] q = scanner.nextLine().trim().split("-");
                    int quarter = Integer.parseInt(q[0]); int yq = Integer.parseInt(q[1]);
                    int startMonth = (quarter - 1) * 3 + 1;
                    LocalDate s = LocalDate.of(yq, startMonth, 1);
                    LocalDate e = s.plusMonths(2).withDayOfMonth(s.plusMonths(2).lengthOfMonth());
                    return new LocalDate[]{s, e};
                }
                case 4: {
                    System.out.print("Năm (YYYY): ");
                    int y = Integer.parseInt(scanner.nextLine().trim());
                    LocalDate s = LocalDate.of(y, 1, 1);
                    LocalDate e = LocalDate.of(y, 12, 31);
                    return new LocalDate[]{s, e};
                }
                case 5: {
                    System.out.print("Từ ngày (YYYY-MM-DD): ");
                    LocalDate s = LocalDate.parse(scanner.nextLine().trim());
                    System.out.print("Đến ngày (YYYY-MM-DD): ");
                    LocalDate e = LocalDate.parse(scanner.nextLine().trim());
                    return new LocalDate[]{s, e};
                }
                default:
                    return null;
            }
        } catch (Exception ex) {
            System.out.println("Định dạng không hợp lệ. Hủy.");
            return null;
        }
    }

    public static void reportMenu() {
        if (!Authentication.canViewReports()) {
            System.out.println("Bạn không có quyền xem báo cáo.");
            pause();
            return;
        }
        if (reportService == null) {
            System.out.println("ReportService chưa khởi tạo.");
            pause();
            return;
        }

        while (true) {
            System.out.println("\n--- BÁO CÁO---");
            System.out.println("1. Tổng quan (Overview)");
            System.out.println("2. Doanh thu");
            System.out.println("3. Full (chạy tất cả cho 1 khoảng)");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");
            int c = getIntInput();
            if (c == 0) return;
            switch (c) {
                case 1: {
                    LocalDate[] r = chooseTimeRange();
                    if (r != null) reportService.printSystemOverview(r[0], r[1]);
                    else reportService.printSystemOverview();
                    pause();
                    break;
                }
                case 2: {
                    LocalDate[] r = chooseTimeRange();
                    if (r != null) reportService.printRevenueReport(r[0], r[1]);
                    else reportService.printRevenueReport();
                    pause();
                    break;
                }
                case 3: {
                    LocalDate[] r = chooseTimeRange();
                    if (r != null) reportService.printFullReport(r[0], r[1]);
                    else reportService.printFullReport();
                    pause();
                    break;
                }
                default:
                    System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    public static void initializeReportService(StaffService staffService,
                                               MenuItemService menuService,
                                               InventoryItemService inventoryService,
                                               PaymentService paymentService,
                                               ExpenseService expenseService,
                                               TableEntityService tableService,
                                               BillService billService) {
        reportService = new ReportService(
            staffService, menuService,
            inventoryService, paymentService, expenseService, tableService,
            billService
        );
    }

    public static void main(String[] args) {
        reportMenu();
    }
}