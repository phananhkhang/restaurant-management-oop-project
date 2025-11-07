package app.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import model.Bill;
import model.Payment;
import model.common.Money;
import model.enums.BillStatus;
import model.enums.PaymentMethod;
import model.enums.PaymentStatus;
import service.BillService;
import service.PaymentService;
import util.IdGenerator;
import app.MainApp;

public class BillUI {
    // ===== KHỞI TẠO =====
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static BillService getBillService() {
        return MainApp.getBillService();
    }

    private static PaymentService getPaymentService() {
        return MainApp.getPaymentService();
    }

    // ===== TIỆN ÍCH =====
    private static String getStringInput() {
        try {
            return scanner.nextLine().trim();
        } catch (Exception e) {
            scanner.nextLine();
            return "";
        }
    }

    private static int getIntInput() {
        try {
            String s = scanner.nextLine().trim();
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    private static void pause() {
        System.out.print("\n⏸️  Nhấn Enter để tiếp tục...");
        scanner.nextLine();
    }

    private static String repeat(String str, int count) {
        return str.repeat(count);
    }

    private static String formatMoney(Money m) {
        if (m == null || m.getAmount() == null) return "0 VND";
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(m.getAmount()) + " VND";
    }

    // ===== MENU CHUNG (HÓA ĐƠN/THANH TOÁN) =====
    public static void billOrderMenu() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║        🧾 QUẢN LÝ HÓA ĐƠN              ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println(" 1. Hóa đơn & Chi tiết (xem / cập nhật / xóa / in / thanh toán)");
            System.out.println(" 0. Quay lại");
            System.out.print("👉 Chọn: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    invoiceMenu();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    // ===== MENU HÓA ĐƠN (KHÔNG CHO TẠO Ở ĐÂY) =====
    private static void invoiceMenu() {
        while (true) {
            System.out.println("\n--- HÓA ĐƠN & CHI TIẾT ---");
            System.out.println("1. Liệt kê tất cả hóa đơn");
            System.out.println("2. Xem chi tiết hóa đơn");
            System.out.println("3. Thanh toán hóa đơn");
            System.out.println("4. Cập nhật hóa đơn");
            System.out.println("5. Xóa hóa đơn");
            System.out.println("6. In hóa đơn");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");

            int c = getIntInput();
            switch (c) {
                case 1:
                    listAllBills();
                    pause();
                    break;
                case 2:
                    viewBillDetail();
                    break;
                case 3:
                    payBill();
                    break;
                case 4:
                    updateBill();
                    break;
                case 5:
                    deleteBill();
                    break;
                case 6:
                    printBill();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    // ===== DANH SÁCH =====
    private static void listAllBills() {
        List<Bill> bills = getBillService().getAll();
        System.out.println("\n📋 DANH SÁCH HÓA ĐƠN (" + bills.size() + ")");
        System.out.println(repeat("─", 90));
        System.out.printf("%-15s %-10s %-20s %20s %-15s%n", 
            "Mã HĐ", "Bàn", "Ngày tạo", "Tổng tiền", "Trạng thái");
        System.out.println(repeat("─", 90));
        
        DecimalFormat df = new DecimalFormat("#,##0");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Bill b : bills) {
            String table = b.getTableId() != null ? b.getTableId() : "-";
            String total = b.getTotalAmount() != null ? df.format(b.getTotalAmount().getAmount()) : "0";
            String shortId = b.getId().length() > 12 ? b.getId().substring(0, 12) + "..." : b.getId();
            String formattedDate = b.getCreatedAt() != null ? b.getCreatedAt().format(dateFormatter) : "-";
            
            System.out.printf("%-15s %-10s %-20s %20s %-15s%n",
                shortId, table, formattedDate, total + " VND", b.getStatus());
        }
        System.out.println(repeat("─", 90));
    }

    // ===== XEM CHI TIẾT =====
    private static void viewBillDetail() {
        System.out.print("\n🔍 Nhập mã hóa đơn: ");
        String billId = getStringInput();
        Optional<Bill> opt = getBillService().getById(billId);
        if (opt.isEmpty()) {
            System.out.println("❌ Không tìm thấy hóa đơn!");
            pause();
            return;
        }
        Bill bill = opt.get();
        
        DecimalFormat df = new DecimalFormat("#,##0");
        System.out.println("\n" + repeat("═", 70));
        System.out.println("           CHI TIẾT HÓA ĐƠN");
        System.out.println(repeat("═", 70));
        System.out.println("Mã hóa đơn      : " + bill.getId());
        System.out.println("Bàn             : " + (bill.getTableId() != null ? bill.getTableId() : "-"));
        System.out.println("Ngày tạo        : " + bill.getCreatedAt().format(DATE_TIME_FORMATTER));
        System.out.println("Cập nhật lần cuối: " + bill.getUpdatedAt().format(DATE_TIME_FORMATTER));
        System.out.println("Trạng thái      : " + bill.getStatus());
        System.out.println(repeat("─", 70));
        
        if (bill.getItems() != null && !bill.getItems().isEmpty()) {
            System.out.println("DANH SÁCH MÓN (" + bill.getItems().size() + " món):");
            for (int i = 0; i < bill.getItems().size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, bill.getItems().get(i));
            }
            System.out.println(repeat("─", 70));
        } else {
            System.out.println("⚠️  Chưa có món nào!");
            System.out.println(repeat("─", 70));
        }
        
        System.out.println("Tạm tính        : " + formatMoney(bill.getSubTotal()));
        System.out.println("Giảm giá        : " + formatMoney(bill.getDiscountAmount()));
        System.out.println("TỔNG CỘNG       : " + formatMoney(bill.getTotalAmount()));
        System.out.println("Đã thanh toán   : " + formatMoney(bill.getPaidAmount()));
        
        BigDecimal remaining = BigDecimal.ZERO;
        if (bill.getTotalAmount() != null && bill.getPaidAmount() != null) {
            remaining = bill.getTotalAmount().getAmount().subtract(bill.getPaidAmount().getAmount());
        }
        System.out.println("Còn lại         : " + df.format(remaining) + " VND");
        System.out.println(repeat("─", 70));
        System.out.println("Ghi chú         : " + (bill.getNote() != null ? bill.getNote() : "-"));
        System.out.println(repeat("═", 70));
        pause();
    }

    // ===== THANH TOÁN =====
    private static void payBill() {
        System.out.print("\n🔍 Nhập mã hóa đơn cần thanh toán: ");
        String id = getStringInput();
        Optional<Bill> opt = getBillService().getById(id);
        if (opt.isEmpty()) {
            System.out.println("❌ Không tìm thấy hóa đơn!");
            pause();
            return;
        }
        Bill bill = opt.get();

        // Kiểm tra bàn có đang sử dụng không (chỉ cho thanh toán khi bàn đang OCCUPIED)
        if (bill.getTableId() != null && !bill.getTableId().isEmpty()) {
            try {
                var tableOpt = MainApp.getTableService().getById(bill.getTableId());
                if (tableOpt.isEmpty()) {
                    System.out.println("❌ Không tìm thấy bàn " + bill.getTableId() + "!");
                    pause();
                    return;
                }
                var table = tableOpt.get();
                if (table.getStatus().toString().equals("AVAILABLE")) {
                    System.out.println("❌ Bàn " + bill.getTableId() + " đã trống rồi!");
                    System.out.println("   Không thể thanh toán hóa đơn cho bàn không sử dụng.");
                    pause();
                    return;
                }
            } catch (Exception e) {
                System.out.println("⚠️  Lỗi khi kiểm tra trạng thái bàn: " + e.getMessage());
            }
        }

        BigDecimal total = bill.getTotalAmount() != null ? bill.getTotalAmount().getAmount() : BigDecimal.ZERO;
        DecimalFormat df = new DecimalFormat("#,##0");
        
        // Hiển thị hóa đơn chi tiết
        System.out.println("\n" + repeat("═", 70));
        System.out.println("           🧾 CHI TIẾT HÓA ĐƠN THANH TOÁN");
        System.out.println(repeat("═", 70));
        System.out.println("Mã hóa đơn      : " + bill.getId());
        System.out.println("Bàn             : " + (bill.getTableId() != null ? bill.getTableId() : "-"));
        System.out.println("Ngày tạo        : " + bill.getCreatedAt().format(DATE_TIME_FORMATTER));
        System.out.println("Trạng thái      : " + bill.getStatus());
        System.out.println(repeat("─", 70));
        
        if (bill.getItems() != null && !bill.getItems().isEmpty()) {
            System.out.println("DANH SÁCH MÓN (" + bill.getItems().size() + " món):");
            for (int i = 0; i < bill.getItems().size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, bill.getItems().get(i));
            }
            System.out.println(repeat("─", 70));
        } else {
            System.out.println("⚠️  Chưa có món nào!");
            System.out.println(repeat("─", 70));
        }
        
        System.out.println("Tạm tính        : " + formatMoney(bill.getSubTotal()));
        System.out.println("Giảm giá        : " + formatMoney(bill.getDiscountAmount()));
        System.out.println("TỔNG CỘNG       : " + formatMoney(bill.getTotalAmount()));
        System.out.println(repeat("═", 70));
        
        // Vòng lặp thanh toán - lặp cho đến khi thanh toán đủ
        boolean isFullyPaid = false;
        while (!isFullyPaid) {
            // Load lại bill để có số tiền đã thanh toán mới nhất
            opt = getBillService().getById(id);
            if (opt.isEmpty()) break;
            bill = opt.get();
            
            BigDecimal paid = bill.getPaidAmount() != null ? bill.getPaidAmount().getAmount() : BigDecimal.ZERO;
            BigDecimal remaining = total.subtract(paid);
            
            System.out.println("\n💰 TÌNH TRẠNG THANH TOÁN:");
            System.out.println("   Đã thanh toán   : " + df.format(paid) + " VND");
            System.out.println("   Còn lại         : " + df.format(remaining) + " VND");
            System.out.println(repeat("─", 70));
            
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("✅ Hóa đơn đã thanh toán đầy đủ!");
                isFullyPaid = true;
                break;
            }

            System.out.print("\n💵 Nhập số tiền thu (0 để thoát): ");
            String amtInput = getStringInput();
            
            // Cho phép thoát nếu nhập 0
            if (amtInput.equals("0")) {
                System.out.println("⚠️  Thoát thanh toán. Hóa đơn vẫn chưa thanh toán đủ.");
                break;
            }
            
            // Yêu cầu phải nhập số tiền, không cho phép để trống
            if (amtInput.isEmpty()) {
                System.out.println("❌ Vui lòng nhập số tiền. Nhập 0 để thoát.");
                continue;
            }
            
            BigDecimal amountBD;
            try {
                amountBD = BigDecimal.valueOf(Double.parseDouble(amtInput));
            } catch (NumberFormatException ex) {
                System.out.println("❌ Giá trị không hợp lệ. Vui lòng thử lại.");
                continue;
            }
            
            if (amountBD.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("❌ Số tiền phải lớn hơn 0. Vui lòng thử lại.");
                continue;
            }
            
            if (amountBD.compareTo(remaining) > 0) {
                System.out.print("⚠️  Số tiền thu lớn hơn phần còn lại. Xác nhận vẫn thu (y/n): ");
                if (!getStringInput().equalsIgnoreCase("y")) {
                    System.out.println("❌ Hủy thanh toán lần này. Vui lòng nhập lại.");
                    continue;
                }
            }

            System.out.println("\n💳 CHỌN PHƯƠNG THỨC THANH TOÁN:");
            System.out.println("   1. 💵 Tiền mặt (CASH)");
            System.out.println("   2. 💳 Thẻ (CARD)");
            System.out.println("   3. 🏦 Chuyển khoản (BANK_TRANSFER)");
            System.out.print("➤ Nhập lựa chọn (1-3): ");
            int m = getIntInput();
            PaymentMethod method = PaymentMethod.values()[Math.max(0, Math.min(PaymentMethod.values().length - 1, m - 1))];

            // Tạo Payment
            Payment payment = new Payment();
            String paymentId = IdGenerator.generatePaymentId(getPaymentService().getAll());
            payment.setPaymentId(paymentId);
            payment.setOrderId(bill.getId());
            payment.setPaymentMethod(method);
            payment.setAmount(amountBD.doubleValue());
            payment.setPaymentDate(LocalDate.now().toString());
            payment.setPaymentTime(LocalTime.now().toString().substring(0, 5));
            payment.setStatus(PaymentStatus.PENDING);

            try {
                getPaymentService().create(payment);

                // Cập nhật hóa đơn
                Money newPaid = (bill.getPaidAmount() == null ? new Money(BigDecimal.ZERO) : bill.getPaidAmount()).add(new Money(amountBD));
                bill.setPaidAmount(newPaid);
                
                if (newPaid.getAmount().compareTo(total) >= 0) {
                    bill.setStatus(BillStatus.PAID);
                    isFullyPaid = true;
                } else {
                    bill.setStatus(BillStatus.OPEN);
                }
                getBillService().update(bill);
                
                // 💾 Tự động lưu sau thanh toán
                MainApp.autoSave();

                // Hiển thị kết quả thanh toán lần này
                System.out.println("\n" + repeat("═", 70));
                System.out.println("           ✅ THANH TOÁN THÀNH CÔNG");
                System.out.println(repeat("═", 70));
                System.out.println("Mã thanh toán   : " + paymentId);
                System.out.println("Số tiền nhận    : " + df.format(amountBD) + " VND");
                
                if (amountBD.compareTo(remaining) > 0) {
                    BigDecimal change = amountBD.subtract(remaining);
                    System.out.println("💵 TIỀN THỪA    : " + df.format(change) + " VND");
                }
                
                System.out.println("Phương thức     : " + method);
                System.out.println(repeat("═", 70));
                
                // Nếu thanh toán đủ
                if (isFullyPaid) {
                    System.out.println("\n🎉 HÓA ĐƠN ĐÃ THANH TOÁN ĐẦY ĐỦ!");
                    System.out.println("   Trạng thái HĐ: PAID");
                    
                    // Nếu có bàn, cập nhật trạng thái bàn
                    if (bill.getTableId() != null && !bill.getTableId().isEmpty()) {
                        try {
                            MainApp.getTableService().releaseTable(bill.getTableId());
                            System.out.println("   🚪 Bàn " + bill.getTableId() + " đã được trả (chuyển sang trạng thái TRỐNG)");
                            
                            // 💾 Tự động lưu sau khi trả bàn
                            MainApp.autoSave();
                        } catch (Exception e) {
                            System.out.println("   ⚠️  Không thể cập nhật trạng thái bàn: " + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("\n⚠️  HÓA ĐƠN CHƯA THANH TOÁN ĐỦ - Tiếp tục thu tiền...");
                }
                
            } catch (Exception e) {
                System.out.println("❌ Lỗi khi lưu thanh toán: " + e.getMessage());
            }
        }
        
        pause();
    }

    // ===== CẬP NHẬT HÓA ĐƠN (CHỈ GHI CHÚ / ĐIỀU CHỈNH PAID) =====
    private static void updateBill() {
        System.out.print("\n✏️  Nhập mã hóa đơn cần cập nhật: ");
        String id = getStringInput();
        Optional<Bill> opt = getBillService().getById(id);
        if (opt.isEmpty()) {
            System.out.println("❌ Không tìm thấy hóa đơn!");
            pause();
            return;
        }
        Bill bill = opt.get();
        System.out.println("Đã thanh toán hiện tại: " + formatMoney(bill.getPaidAmount()) + " | Ghi chú: " + (bill.getNote() == null ? "-" : bill.getNote()));
        System.out.print("Nhập giá trị đã thanh toán mới (Enter để giữ nguyên): ");
        String paidStr = getStringInput();
        if (!paidStr.isEmpty()) {
            try {
                BigDecimal paidBD = BigDecimal.valueOf(Double.parseDouble(paidStr));
                bill.setPaidAmount(new Money(paidBD));
            } catch (NumberFormatException ex) {
                System.out.println("Giá trị không hợp lệ, bỏ qua cập nhật số tiền.");
            }
        }
        System.out.print("Ghi chú (Enter để giữ nguyên): ");
        String note = getStringInput();
        if (!note.isEmpty()) bill.setNote(note);

        try {
            getBillService().update(bill);
            System.out.println("✅ Cập nhật thành công.");
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi cập nhật: " + e.getMessage());
        }
        pause();
    }

    // ===== XÓA HÓA ĐƠN =====
    private static void deleteBill() {
        System.out.print("\n🗑️  Nhập mã hóa đơn cần xóa: ");
        String id = getStringInput();
        Optional<Bill> opt = getBillService().getById(id);
        if (opt.isEmpty()) {
            System.out.println("❌ Không tìm thấy hóa đơn!");
            pause();
            return;
        }
        System.out.print("Xác nhận xóa hóa đơn " + id + " (y/n): ");
        if (getStringInput().equalsIgnoreCase("y")) {
            try {
                boolean ok = getBillService().deleteById(id);
                System.out.println(ok ? "✅ Đã xóa." : "❌ Xóa thất bại.");
            } catch (Exception e) {
                System.out.println("❌ Lỗi khi xóa: " + e.getMessage());
            }
        } else {
            System.out.println("Hủy xóa.");
        }
        pause();
    }

    // ===== IN HÓA ĐƠN =====
    private static void printBill() {
        System.out.print("\n🖨️ Nhập mã hóa đơn cần in: ");
        String billId = getStringInput();
        Optional<Bill> opt = getBillService().getById(billId);
        if (opt.isEmpty()) {
            System.out.println("❌ Không tìm thấy hóa đơn!");
            pause();
            return;
        }
        Bill bill = opt.get();
        System.out.println("\n" + repeat("═", 60));
        System.out.println("              HÓA ĐƠN THANH TOÁN");
        System.out.println(repeat("═", 60));
        System.out.println("Mã HĐ: " + bill.getId());
        System.out.println("Ngày: " + bill.getCreatedAt().format(DATE_TIME_FORMATTER));
        System.out.println(repeat("─", 60));
        if (bill.getItems() != null) {
            bill.getItems().forEach(it -> System.out.println("  " + it));
        }
        System.out.println(repeat("─", 60));
        System.out.println("Tổng cộng: " + formatMoney(bill.getSubTotal()));
        System.out.println("Giảm giá: " + formatMoney(bill.getDiscountAmount()));
        System.out.println("THÀNH TIỀN: " + formatMoney(bill.getTotalAmount()));
        System.out.println("Đã thanh toán: " + formatMoney(bill.getPaidAmount()));
        System.out.println(repeat("═", 60));
        System.out.println("          Cảm ơn quý khách!");
        System.out.println(repeat("═", 60));
        pause();
    }
}
