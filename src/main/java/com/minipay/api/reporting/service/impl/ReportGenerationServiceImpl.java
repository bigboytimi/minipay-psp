package com.minipay.api.reporting.service.impl;

import com.minipay.api.payment.domains.Payment;
import com.minipay.api.payment.enums.PaymentChannel;
import com.minipay.api.payment.enums.PaymentStatus;
import com.minipay.api.payment.service.PaymentService;
import com.minipay.api.reporting.service.ReportGenerationService;
import com.minipay.api.settlement.domains.SettlementBatch;
import com.minipay.api.settlement.enums.SettlementStatus;
import com.minipay.api.settlement.service.SettlementService;
import com.minipay.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportGenerationServiceImpl implements ReportGenerationService {

    private final SettlementService settlementService;
    private final PaymentService paymentService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @Override
    public ResponseEntity<?> getSettlementReport(String format, String batchReference, String merchantId, SettlementStatus status) throws Exception {
        if (isNotValidFormat(format)) {
            throw new ApiException("Invalid format. Supported formats: CSV, XLSX");
        }

        List<SettlementBatch> settlements = settlementService.getSettlementBatch(batchReference, merchantId, status);

        if (settlements == null || settlements.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        try {
            if ("CSV".equalsIgnoreCase(format)) {
                return generateSettlementCsvReport(settlements, batchReference);
            } else {
                return generateSettlementXlsxReport(settlements, batchReference);
            }
        } catch (IOException e) {
          throw new Exception(e);
        }
    }

    @Override
    public ResponseEntity<?> getTransactionReport(String format, LocalDateTime startDate, LocalDateTime endDate, PaymentChannel channel, String merchantId, PaymentStatus status) throws Exception {
        if (isNotValidFormat(format)) {
            throw new ApiException("Invalid format. Supported formats: CSV, XLSX");
        }

        List<Payment> payments = paymentService.getPayments(startDate, endDate, channel, merchantId, status);

        if (payments == null || payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        try {
            if ("CSV".equalsIgnoreCase(format)) {
                return generateTransactionCsvReport(payments, startDate, endDate);
            } else {
                return generateTransactionXlsxReport(payments, startDate, endDate);
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    private boolean isNotValidFormat(String format) {
        return format == null || (!format.equalsIgnoreCase("CSV") && !format.equalsIgnoreCase("XLSX"));
    }

    private ResponseEntity<ByteArrayResource> generateSettlementCsvReport(List<SettlementBatch> settlements, String batchReference) throws IOException {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("SettlementReference,MerchantId,Amount,Status,TransactionCount,SettlementDate\n");

        for (SettlementBatch settlement : settlements) {
            csvContent.append(String.format("%s,%s,%.2f,%s,%d,%s\n",
                    settlement.getSettlementReference(),
                    settlement.getMerchant().getMerchantId(),
                    settlement.getAmount(),
                    settlement.getStatus().name(),
                    settlement.getCount(),
                    formatDate(settlement.getCreatedAt())));
        }

        byte[] csvBytes = csvContent.toString().getBytes();
        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=settlement_report_" + batchReference + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvBytes.length)
                .body(resource);
    }

    private ResponseEntity<ByteArrayResource> generateSettlementXlsxReport(List<SettlementBatch> settlements, String batchReference) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Settlements");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"SettlementReference", "MerchantId", "Amount", "Status", "SettlementDate"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (SettlementBatch settlement : settlements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(settlement.getSettlementReference());
                row.createCell(1).setCellValue(settlement.getMerchant().getMerchantId());
                row.createCell(2).setCellValue(settlement.getAmount().toString());
                row.createCell(3).setCellValue(settlement.getStatus().name());
                row.createCell(4).setCellValue(formatDate(settlement.getCreatedAt()));
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] xlsxBytes = outputStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(xlsxBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=settlement_report_" + batchReference + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(xlsxBytes.length)
                    .body(resource);
        }
    }

    private ResponseEntity<ByteArrayResource> generateTransactionCsvReport(List<Payment> paymentTxns, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("PaymentReference,OrderId,CustomerId,Amount,Channel,Status,Currency,TransactionDate,Settled,SettlementDate\n");

        for (Payment payment : paymentTxns) {
            csvContent.append(String.format("%s,%s,%s,%.2f,%s,%s,%s,%s,%b,%s\n",
                    payment.getPaymentReference(),
                    payment.getOrderId(),
                    payment.getCustomerId(),
                    payment.getAmount(),
                    payment.getPaymentChannel().name(),
                    payment.getPaymentStatus().name(),
                    payment.getCurrency().name(),
                    formatDate(payment.getCreatedAt()),
                    payment.isSettled(),
                    formatDate(payment.getSettledAt())
            ));
        }

        byte[] csvBytes = csvContent.toString().getBytes();
        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transaction_report_" + startDate.toString() + "_to_" + endDate.toString() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvBytes.length)
                .body(resource);
    }

    private ResponseEntity<ByteArrayResource> generateTransactionXlsxReport(List<Payment> paymentTxns, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"PaymentReference", "OrderId", "CustomerId", "Amount", "Channel", "Status", "Currency", "TransactionDate", "Settled", "SettlementDate"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Payment payment : paymentTxns) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(payment.getPaymentReference());
                row.createCell(1).setCellValue(payment.getOrderId());
                row.createCell(2).setCellValue(payment.getCustomerId());
                row.createCell(3).setCellValue((RichTextString) payment.getAmount());
                row.createCell(4).setCellValue( payment.getPaymentChannel().name());
                row.createCell(5).setCellValue(payment.getPaymentStatus().name());
                row.createCell(6).setCellValue(payment.getCurrency().name());
                row.createCell(7).setCellValue(formatDate(payment.getCreatedAt()));
                row.createCell(8).setCellValue(payment.isSettled());
                row.createCell(9).setCellValue(formatDate(payment.getSettledAt()));
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] xlsxBytes = outputStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(xlsxBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transaction_report_" + startDate.toString() + "_to_" + endDate.toString() + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(xlsxBytes.length)
                    .body(resource);
        }
    }


    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FORMATTER) : null;
    }

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(FORMATTER) : null;
    }
}