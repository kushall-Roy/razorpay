package com.kushal.razorpay.payment.service.impl;
import com.kushal.razorpay.common.enums.OrderStatus;
import com.kushal.razorpay.common.exception.BusinessRuleViolationException;
import com.kushal.razorpay.common.exception.DuplicateResourceException;
import com.kushal.razorpay.common.exception.ResourceNotFoundException;
import com.kushal.razorpay.payment.dto.request.CreateOrderRequest;
import com.kushal.razorpay.payment.dto.response.OrderResponse;
import com.kushal.razorpay.payment.dto.response.PaymentResponse;
import com.kushal.razorpay.payment.entity.OrderRecord;
import com.kushal.razorpay.payment.entity.Payment;
import com.kushal.razorpay.payment.mapper.OrderMapper;
import com.kushal.razorpay.payment.mapper.PaymentMapper;
import com.kushal.razorpay.payment.repository.OrderRepository;
import com.kushal.razorpay.payment.repository.PaymentRepository;
import com.kushal.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {

        if(request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId,request.receipt())){
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE","Order with receipt already exists");
        }

        OrderRecord orderRecord = OrderRecord.builder()
                        .receipt(request.receipt())
                        .amount(request.amount())
                        .notes(request.notes())
                        .merchantId(merchantId)
                        .orderStatus(OrderStatus.CREATED)
                        .expiresAt(request.expiresAt() != null ? request.expiresAt() :
                        LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes)).build();

        orderRecord = orderRepository.save(orderRecord);

        // TODO: publish Kafka event about order creation

//        return new OrderResponse(orderRecord.getId(),
//                orderRecord.getMerchantId(),
//                orderRecord.getReceipt(),
//                orderRecord.getAmount(),
//                orderRecord.getOrderStatus(),
//                orderRecord.getAttempts(),
//                orderRecord.getNotes(),
//                orderRecord.getExpiresAt(),null);

        return orderMapper.toResponse(orderRecord); //Using mapStruct here
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord order =  orderRepository.findByIdAndMerchantId(orderId,merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("Order",orderId));

//        return new OrderResponse(order.getId(),
//                                 order.getMerchantId(),
//                                 order.getReceipt(),
//                                 order.getAmount(),
//                                 order.getOrderStatus(),
//                                 order.getAttempts(),
//                                 order.getNotes(),
//                                 order.getExpiresAt(),
//                        null);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord order =  orderRepository.findByIdAndMerchantId(orderId,merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("Order",orderId));

        if(order.getOrderStatus() == OrderStatus.CANCELLED || order.getOrderStatus() == OrderStatus.PAID){
            throw new BusinessRuleViolationException("ORDER_CANNOT_CANCEL",
                    "Cannot cancel order with status : "+order.getOrderStatus().name());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

//        return new OrderResponse(order.getId(),
//                order.getMerchantId(),
//                order.getReceipt(),
//                order.getAmount(),
//                order.getOrderStatus(),
//                order.getAttempts(),
//                order.getNotes(),
//                order.getExpiresAt(),
//                null);

        return orderMapper.toResponse(order);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        //first lets check if this order id belongs to this merchant id or not
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId,merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("Order",orderId));

        List<Payment> paymentList = paymentRepository.findByOrder_Id(order);

//        return paymentList.stream().map(
//                payment -> new PaymentResponse())  //here we can use Mapstruct
//                .collect()

//        return paymentList.stream().map(
//                        payment -> paymentMapper.toResponse(payment))
//                .collect(Collectors.toList());

        return paymentMapper.toResponseList(paymentList);
    }
}
