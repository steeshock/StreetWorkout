package com.steeshock.streetworkout.presentation.adapters

/**
 * Define types of payloads for better UX
 * When update data - only certain views of
 * place card should redraw
 */
enum class PlacePayloadType {
    FAVORITE_PAYLOAD,
    IMAGES_PAYLOAD,
    ADDRESS_PAYLOAD,
    TITLE_PAYLOAD,
}