package com.minders.backend.event;

import com.minders.backend.braze.BrazeClient;
import com.minders.backend.braze.dto.BrazeAttributes;
import com.minders.backend.braze.dto.BrazeEvent;
import com.minders.backend.braze.dto.BrazePurchase;
import com.minders.backend.braze.dto.BrazeTrackRequest;
import com.minders.backend.event.dto.OrderCompletedRequest;
import com.minders.backend.event.dto.OrderItemDto;
import com.minders.backend.event.dto.PageViewedRequest;
import com.minders.backend.event.dto.ProductAddedToCartRequest;
import com.minders.backend.event.dto.ProductViewedRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Traduce los eventos "de negocio" que manda el front (nombres y
 * propiedades definidos por la taxonomía estricta de Amplitude) al
 * formato nativo de Braze (/users/track).
 *
 * IMPORTANTE sobre nombres de evento: los 4 nombres de evento
 * ("Page Viewed", "Product Viewed", "Product Added to Cart",
 * "Order Completed") y sus Event Properties están fijados por la
 * taxonomía y se usan tal cual, sin concatenar variables ni modificarlos.
 */
@Service
public class EventTrackingService {

    private final BrazeClient brazeClient;
    private final UserProfileStore userProfileStore;

    @Value("${braze.default-user-id}")
    private String defaultUserId;

    public EventTrackingService(BrazeClient brazeClient, UserProfileStore userProfileStore) {
        this.brazeClient = brazeClient;
        this.userProfileStore = userProfileStore;
    }

    // ---------------------------------------------------------------
    // Page Viewed -> Event Properties: page_name
    // ---------------------------------------------------------------
    public boolean trackPageViewed(PageViewedRequest req) {
        Map<String, Object> props = new HashMap<>();
        props.put("page_name", req.page_name());

        BrazeEvent event = new BrazeEvent(userIdOrDefault(req.userId()), "Page Viewed", now(), props);
        return brazeClient.track(BrazeTrackRequest.ofEvent(event));
    }

    // ---------------------------------------------------------------
    // Product Viewed -> Event Properties: product_id, product_name, price
    // ---------------------------------------------------------------
    public boolean trackProductViewed(ProductViewedRequest req) {
        Map<String, Object> props = new HashMap<>();
        props.put("product_id", req.product_id());
        props.put("product_name", req.product_name());
        props.put("price", req.price());

        BrazeEvent event = new BrazeEvent(userIdOrDefault(req.userId()), "Product Viewed", now(), props);
        return brazeClient.track(BrazeTrackRequest.ofEvent(event));
    }

    // ---------------------------------------------------------------
    // Product Added to Cart -> Event Properties: product_id, quantity
    // ---------------------------------------------------------------
    public boolean trackProductAddedToCart(ProductAddedToCartRequest req) {
        Map<String, Object> props = new HashMap<>();
        props.put("product_id", req.product_id());
        props.put("quantity", req.quantity());

        BrazeEvent event = new BrazeEvent(userIdOrDefault(req.userId()), "Product Added to Cart", now(), props);
        return brazeClient.track(BrazeTrackRequest.ofEvent(event));
    }

    // ---------------------------------------------------------------
    // Order Completed -> Event Properties: order_id, revenue
    //
    // Además de trackear el evento, este es el único hito donde se
    // actualizan las User Properties de la taxonomía:
    //   - lifetime_value: se acumula sumando el revenue de cada compra.
    //   - customer_type: "guest" | "registered", tal como lo informe el front.
    //   - first_purchase_date: se fija una sola vez, en la primera compra.
    //
    // Estas 3 no viajan como Event Properties del evento "Order Completed"
    // (la taxonomía solo pide order_id y revenue ahí); viajan en el array
    // "attributes" del mismo POST a /users/track, que es como Braze
    // representa User Properties. Si en el futuro se integra Amplitude
    // directamente, el equivalente es un identify() / setUserProperties()
    // separado del track() del evento, con esos mismos 3 campos.
    // ---------------------------------------------------------------
    public boolean trackOrderCompleted(OrderCompletedRequest req) {
        String externalId = userIdOrDefault(req.userId());
        String time = now();
        String today = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE);

        Map<String, Object> eventProps = new HashMap<>();
        eventProps.put("order_id", req.order_id());
        eventProps.put("revenue", req.revenue());

        BrazeEvent event = new BrazeEvent(externalId, "Order Completed", time, eventProps);

        // Actualiza lifetime_value (acumulado) y first_purchase_date (solo la 1ra vez)
        userProfileStore.registerPurchase(externalId, req.revenue(), today);

        String customerType = (req.customerType() == null || req.customerType().isBlank())
                ? "guest"
                : req.customerType();

        BrazeAttributes attributes = new BrazeAttributes(
                externalId,
                userProfileStore.lifetimeValueOf(externalId),
                customerType,
                userProfileStore.firstPurchaseDateOf(externalId)
        );

        // items es opcional y NO es parte de la taxonomía de Amplitude;
        // solo se usa para reportar purchases con granularidad de
        // producto a Braze (revenue/LTV por ítem, no solo por orden).
        List<BrazePurchase> purchases = (req.items() == null)
                ? List.of()
                : req.items().stream().map(item -> toPurchase(externalId, time, req.order_id(), item)).toList();

        return brazeClient.track(
                BrazeTrackRequest.ofEventPurchasesAndAttributes(event, purchases, attributes)
        );
    }

    private BrazePurchase toPurchase(String externalId, String time, String orderId, OrderItemDto item) {
        Map<String, Object> props = new HashMap<>();
        props.put("order_id", orderId);
        props.put("product_name", item.productName());

        return new BrazePurchase(
                externalId,
                item.productId(),
                "USD",
                item.price(),
                item.quantity(),
                time,
                props
        );
    }

    private String userIdOrDefault(String userId) {
        return (userId == null || userId.isBlank()) ? defaultUserId : userId;
    }

    private String now() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }
}
