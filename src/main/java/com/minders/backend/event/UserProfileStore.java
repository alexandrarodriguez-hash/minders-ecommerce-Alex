package com.minders.backend.event;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Almacén EN MEMORIA (no persiste entre reinicios ni escala a más de una
 * instancia) usado únicamente para poder calcular las User Properties
 * "lifetime_value" y "first_purchase_date" en esta demo sin base de datos.
 *
 * En un backend real esto se reemplaza por una tabla `user_profile`
 * (o se delega directamente en Braze/Amplitude, que ya acumulan estos
 * valores del lado de la plataforma). Se deja como un componente aislado
 * para que sea fácil de reemplazar por un repositorio JPA más adelante.
 */
@Component
public class UserProfileStore {

    private record Profile(double lifetimeValue, String firstPurchaseDate) {}

    private final ConcurrentHashMap<String, Profile> profiles = new ConcurrentHashMap<>();

    /**
     * Suma la compra actual al lifetime_value acumulado del usuario y,
     * si es su primera compra, fija first_purchase_date.
     * Tras llamar esto, usar lifetimeValueOf/firstPurchaseDateOf para
     * leer el estado actualizado y enviarlo a Braze.
     */
    public void registerPurchase(String userId, double orderRevenue, String todayIsoDate) {
        profiles.compute(userId, (id, existing) -> {
            double newLifetimeValue = (existing != null ? existing.lifetimeValue() : 0.0) + orderRevenue;
            String firstPurchaseDate = (existing != null) ? existing.firstPurchaseDate() : todayIsoDate;
            return new Profile(newLifetimeValue, firstPurchaseDate);
        });
    }

    public double lifetimeValueOf(String userId) {
        Profile p = profiles.get(userId);
        return p != null ? p.lifetimeValue() : 0.0;
    }

    public String firstPurchaseDateOf(String userId) {
        Profile p = profiles.get(userId);
        return p != null ? p.firstPurchaseDate() : null;
    }
}
