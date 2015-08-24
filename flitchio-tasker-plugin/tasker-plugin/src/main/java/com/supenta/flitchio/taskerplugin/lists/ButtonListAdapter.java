package com.supenta.flitchio.taskerplugin.lists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.supenta.flitchio.taskerplugin.R;

/**
 * This Adapter is responsible for displaying the available Flitchio buttons as well as returning
 * the selected one.
 */
public class ButtonListAdapter extends TrackSelectionAdapter<ButtonListAdapter.ButtonViewHolder> {

    /**
     * We choose an array here because no conversion is needed from Enum.values() and because the
     * amount of buttons is known therefore we save up on some memory and method calls (even though
     * the second might as well be optimized by JIT).
     */
    private final FlitchioButton[] buttons;

    public ButtonListAdapter() {
        this.buttons = FlitchioButton.values();
    }

    public boolean hasAtLeastOneSelection() {
        return selectedItem != -1;
    }

    public String getSelectedButtonId() {
        return hasAtLeastOneSelection() ? getSelected().getFlitchioButtonId() : null;
    }

    public int getSelectedButtonLabelId() {
        return hasAtLeastOneSelection() ? getSelected().getLabelResourceId() : -1;
    }

    private FlitchioButton getSelected() {
        return hasAtLeastOneSelection() ? buttons[selectedItem] : null;
    }

    @Override
    public ButtonListAdapter.ButtonViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flitchio_button, parent, false);

        return new ButtonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ButtonListAdapter.ButtonViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);

        viewHolder.update(buttons[i]);
    }

    @Override
    public int getItemCount() {
        return buttons.length;
    }

    class ButtonViewHolder extends TrackSelectionAdapter.ViewHolder {
        private final TextView txtButtonLabel;

        ButtonViewHolder(View itemView) {
            super(itemView);

            this.txtButtonLabel = (TextView) itemView.findViewById(R.id.txt_flitchio_button_label);
        }

        void update(FlitchioButton button) {
            txtButtonLabel.setText(button.getLabelResourceId());
        }
    }
}
