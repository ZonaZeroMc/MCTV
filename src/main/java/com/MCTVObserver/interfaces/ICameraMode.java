package com.MCTVObserver.interfaces;

import java.util.UUID;

public interface ICameraMode {

    /**
     * Activa este modo de cámara para el jugador especificado.
     *
     * @param observerUUID El UUID del jugador que actuará como observador.
     * @param targetUUID   El UUID del jugador o punto de interés objetivo.
     */
    void activate(UUID observerUUID, UUID targetUUID);

    /**
     * Desactiva este modo de cámara para el jugador especificado.
     *
     * @param observerUUID El UUID del jugador que actuará como observador.
     */
    void deactivate(UUID observerUUID);

    /**
     * Actualiza este modo de cámara (por ejemplo, si el jugador objetivo se mueve).
     *
     * @param observerUUID El UUID del jugador que actuará como observador.
     */
    void update(UUID observerUUID);

    /**
     * Devuelve el nombre de este modo de cámara.
     *
     * @return El nombre del modo de cámara.
     */
    String getName(UUID observerUUID);

    /**
     * Indica si el modo de cámara está actualmente activo.
     *
     * @param observerUUID El UUID del jugador que actuará como observador.
     * @return Verdadero si el modo está activo, falso en caso contrario.
     */
    boolean isActive(UUID observerUUID);

    /**
     * Devuelve el UUID del jugador que está siendo observado en este modo.
     *
     * @return El UUID del jugador objetivo.
     */
    UUID getTarget(UUID observerUUID);
}
