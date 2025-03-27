package com.example.Hotel_booking.controller;

import com.example.Hotel_booking.config.Config;
import com.example.Hotel_booking.model.BookedHotel;
import com.example.Hotel_booking.model.PaymentStatus;
import com.example.Hotel_booking.repository.BookingRepository;
import com.example.Hotel_booking.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingRepository bookingRepository;

    @PostMapping("/create-payment/{bookingId}")
    public ResponseEntity<?> createPayment(@PathVariable Long bookingId, HttpServletRequest request) {
        try {
            BookedHotel bookedHotel = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

            // Kiểm tra trạng thái thanh toán hiện tại
            if (PaymentStatus.PAID.equals(bookedHotel.getPaymentStatus())) {
                return ResponseEntity.badRequest().body("Booking này đã được thanh toán!");
            }

            long amount = bookedHotel.getTotalAmount().multiply(new BigDecimal("100")).longValue();
            String vnp_TxnRef = Config.getRandomNumber(8);
            String vnp_TmnCode = Config.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", Config.vnp_Version);
            vnp_Params.put("vnp_Command", Config.vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_BankCode", "NCB");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + bookingId);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
            
            // Thêm IP address của người dùng
            String ipAddress = getIpAddress(request);
            vnp_Params.put("vnp_IpAddr", ipAddress);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

            // Cập nhật trạng thái thanh toán sang PROCESSING
            bookedHotel.setPaymentStatus(PaymentStatus.PROCESSING);
            bookingRepository.save(bookedHotel);

            return ResponseEntity.ok(paymentUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @GetMapping("/vnpay-payment-callback")
    public ResponseEntity<?> paymentCallback(@RequestParam Map<String, String> params) {
        try {
            // Log toàn bộ params để debug
            System.out.println("VNPay callback params: " + params);
            
            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String orderInfo = params.get("vnp_OrderInfo");
            
            if (orderInfo == null || !orderInfo.startsWith("Thanh toan don hang:")) {
                return ResponseEntity.badRequest().body("Thông tin đơn hàng không hợp lệ");
            }
            
            // Lấy bookingId từ orderInfo
            String bookingId = orderInfo.replace("Thanh toan don hang:", "").trim();
            
            // Tìm booking theo ID
            BookedHotel bookedHotel = bookingRepository.findById(Long.parseLong(bookingId))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

            if ("00".equals(vnp_ResponseCode)) {
                // Thanh toán thành công
                bookedHotel.setPaymentStatus(PaymentStatus.PAID);
                bookedHotel.setPaymentDate(LocalDateTime.now());
                bookingRepository.save(bookedHotel);
                
                // Trả về trang HTML thông báo thành công
                String successHtml = "<html><body><h1>Thanh toán thành công!</h1>" +
                        "<p>Mã đặt phòng của bạn: " + bookedHotel.getBookingConfirmationCode() + "</p>" +
                        "<p>Vui lòng kiểm tra email để xem chi tiết đặt phòng.</p>" +
                        "<script>window.location.href = 'http://localhost:3000/booking-success';</script>" +
                        "</body></html>";
                
                return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(successHtml);
            } else {
                // Thanh toán thất bại
                bookedHotel.setPaymentStatus(PaymentStatus.FAILED);
                bookingRepository.save(bookedHotel);
                
                // Trả về trang HTML thông báo thất bại
                String failureHtml = "<html><body><h1>Thanh toán thất bại!</h1>" +
                        "<p>Vui lòng thử lại hoặc liên hệ hỗ trợ.</p>" +
                        "<script>window.location.href = 'http://localhost:3000/booking-failed';</script>" +
                        "</body></html>";
                
                return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(failureHtml);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi để debug
            return ResponseEntity.badRequest()
                .contentType(MediaType.TEXT_HTML)
                .body("<html><body><h1>Lỗi xử lý thanh toán</h1>" +
                        "<p>Vui lòng liên hệ hỗ trợ.</p>" +
                        "<script>window.location.href = 'http://localhost:3000/booking-error';</script>" +
                        "</body></html>");
        }
    }

    // Phương thức lấy IP address của người dùng
    private String getIpAddress(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAddress = "127.0.0.1";
        }
        return ipAddress;
    }
}
