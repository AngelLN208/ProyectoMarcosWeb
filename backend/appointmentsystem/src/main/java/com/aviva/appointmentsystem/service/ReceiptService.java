package com.aviva.appointmentsystem.service;

import com.aviva.appointmentsystem.dto.ReceiptResponse;
import com.aviva.appointmentsystem.entity.Receipt;
import com.aviva.appointmentsystem.entity.Payment;
import com.aviva.appointmentsystem.exception.ResourceNotFoundException;
import com.aviva.appointmentsystem.exception.ValidationException;
import com.aviva.appointmentsystem.repository.ReceiptRepository;
import com.aviva.appointmentsystem.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar comprobantes de pago
 */
@Service
@Transactional
public class ReceiptService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptService.class);

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Crear un comprobante
     */
    public ReceiptResponse createReceipt(com.aviva.appointmentsystem.dto.ReceiptRequest request) {
        logger.info("Creando comprobante para pago ID={}", request.paymentId());

        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Pago", request.paymentId()));

        if (receiptRepository.findByReceiptNumber(request.receiptNumber()).isPresent()) {
            throw new ValidationException("El número de comprobante ya existe");
        }

        Receipt receipt = new Receipt();
        receipt.setPayment(payment);

        if (request.receiptNumber() != null && !request.receiptNumber().isBlank()) {
            receipt.setReceiptNumber(request.receiptNumber());
        } else {
            receipt.setReceiptNumber("REC-" + System.currentTimeMillis());
        }

        if (request.amount() != null) {
            receipt.setTotalAmount(request.amount());
        } else {
            receipt.setTotalAmount(payment.getAmount());
        }

        receipt.setDescription(request.description() != null ? request.description() : "Comprobante de pago generado");
        receipt.setCreatedAt(java.time.LocalDateTime.now());

        Receipt saved = receiptRepository.save(receipt);
        return mapToResponse(saved);
    }

    /**
     * Obtiene un comprobante por ID
     */
    @Transactional(readOnly = true)
    public ReceiptResponse getById(Long id) {
        logger.debug("Obteniendo comprobante ID={}", id);

        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante", id));

        return mapToResponse(receipt);
    }

    /**
     * Obtiene un comprobante por número de comprobante
     */
    @Transactional(readOnly = true)
    public ReceiptResponse getByReceiptNumber(String receiptNumber) {
        logger.debug("Obteniendo comprobante por número: {}", receiptNumber);

        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado: " + receiptNumber));

        return mapToResponse(receipt);
    }

    /**
     * Lista todos los comprobantes
     */
    @Transactional(readOnly = true)
    public List<ReceiptResponse> getAll() {
        logger.debug("Obteniendo todos los comprobantes");

        return receiptRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Mapea entidad a DTO
     */
    private ReceiptResponse mapToResponse(Receipt receipt) {
        return new ReceiptResponse(
            receipt.getId(),
            receipt.getReceiptNumber(),
            receipt.getDescription(),
            receipt.getTotalAmount(),
            receipt.getCreatedAt()
        );
    }
}
