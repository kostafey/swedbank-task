package com.kostafey.swedbanktest.dto;

public record PickUpResult(
    Boolean carReturned,
    ResultState resultState,
    Long orderId
) {
    public static enum ResultState {
        OK,
        PICK_UP_REQUEST_NOT_RECEIVED,
        PAYMENT_TIMEOUT,
        NO_SUCH_ORDER,
        NO_CAR,
        NOT_ENOUGH_MONEY,
        OVERPAID_CREDIT,
        ERROR;
    }
} 
