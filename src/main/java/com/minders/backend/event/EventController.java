package com.minders.backend.event;

import com.minders.backend.event.dto.OrderCompletedRequest;
import com.minders.backend.event.dto.PageViewedRequest;
import com.minders.backend.event.dto.ProductAddedToCartRequest;
import com.minders.backend.event.dto.ProductViewedRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Puente entre el front de Minders Ecommerce y Braze.
 * Cada endpoint corresponde 1:1 a un evento de la taxonomía estricta de
 * Amplitude y recibe exactamente sus Event Properties, sin campos extra
 * inventados. El nombre del evento (tal como lo pide la taxonomía) se
 * fija del lado del backend en EventTrackingService, nunca lo arma el
 * front concatenando texto.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventTrackingService eventTrackingService;

    public EventController(EventTrackingService eventTrackingService) {
        this.eventTrackingService = eventTrackingService;
    }

    // Evento: "Page Viewed" -> { page_name }
    @PostMapping("/page-viewed")
    public ResponseEntity<Map<String, Object>> pageViewed(@Valid @RequestBody PageViewedRequest request) {
        boolean sent = eventTrackingService.trackPageViewed(request);
        return ack("Page Viewed", sent);
    }

    // Evento: "Product Viewed" -> { product_id, product_name, price }
    @PostMapping("/product-viewed")
    public ResponseEntity<Map<String, Object>> productViewed(@Valid @RequestBody ProductViewedRequest request) {
        boolean sent = eventTrackingService.trackProductViewed(request);
        return ack("Product Viewed", sent);
    }

    // Evento: "Product Added to Cart" -> { product_id, quantity }
    @PostMapping("/product-added-to-cart")
    public ResponseEntity<Map<String, Object>> productAddedToCart(@Valid @RequestBody ProductAddedToCartRequest request) {
        boolean sent = eventTrackingService.trackProductAddedToCart(request);
        return ack("Product Added to Cart", sent);
    }

    // Evento: "Order Completed" -> { order_id, revenue }
    // (además actualiza User Properties: lifetime_value, customer_type, first_purchase_date)
    @PostMapping("/order-completed")
    public ResponseEntity<Map<String, Object>> orderCompleted(@Valid @RequestBody OrderCompletedRequest request) {
        boolean sent = eventTrackingService.trackOrderCompleted(request);
        return ack("Order Completed", sent);
    }

    private ResponseEntity<Map<String, Object>> ack(String eventName, boolean sentToBraze) {
        return ResponseEntity.ok(Map.of(
                "event", eventName,
                "sentToBraze", sentToBraze
        ));
    }
}
