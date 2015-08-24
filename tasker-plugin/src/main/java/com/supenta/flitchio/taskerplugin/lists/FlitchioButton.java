package com.supenta.flitchio.taskerplugin.lists;


import com.supenta.flitchio.sdk.InputElement;
import com.supenta.flitchio.taskerplugin.R;

/**
 * Represents the data needed to identify a {@link com.supenta.flitchio.sdk.InputElement.Button}
 * both visually and structurally (from the id).
 */
public enum FlitchioButton {
    BUTTON_TOP(InputElement.BUTTON_TOP, R.string.button_top_label),
    BUTTON_BOTTOM(InputElement.BUTTON_BOTTOM, R.string.button_bottom_label),
    DPAD_TOP_UP(InputElement.DPAD_TOP_UP, R.string.dpad_top_up_label),
    DPAD_TOP_DOWN(InputElement.DPAD_TOP_DOWN, R.string.dpad_top_down_label),
    DPAD_TOP_LEFT(InputElement.DPAD_TOP_LEFT, R.string.dpad_top_left_label),
    DPAD_TOP_RIGHT(InputElement.DPAD_TOP_RIGHT, R.string.dpad_top_right_label),
    DPAD_BOTTOM_UP(InputElement.DPAD_BOTTOM_UP, R.string.dpad_bottom_up_label),
    DPAD_BOTTOM_DOWN(InputElement.DPAD_BOTTOM_DOWN, R.string.dpad_bottom_down_label),
    DPAD_BOTTOM_LEFT(InputElement.DPAD_BOTTOM_LEFT, R.string.dpad_bottom_left_label),
    DPAD_BOTTOM_RIGHT(InputElement.DPAD_BOTTOM_RIGHT, R.string.dpad_bottom_right_label);

    private final String buttonId;
    private final int labelResourceId;

    FlitchioButton(InputElement.Button button, int labelResourceId) {
        this.buttonId = button.name;
        this.labelResourceId = labelResourceId;
    }

    public String getFlitchioButtonId() {
        return buttonId;
    }

    public int getLabelResourceId() {
        return labelResourceId;
    }
}