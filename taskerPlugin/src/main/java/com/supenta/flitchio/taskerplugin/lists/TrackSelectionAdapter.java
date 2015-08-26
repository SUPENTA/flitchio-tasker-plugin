package com.supenta.flitchio.taskerplugin.lists;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import timber.log.Timber;

/**
 * Taken from http://goo.gl/vkm1b1 and slightly modified this provides us with an Adapter which holds
 * the currently selected item. This can also easily be transformed to multi-selection.
 *
 * @param <VH> The ViewHolder which needs to be extending
 *             {@link com.supenta.flitchio.taskerplugin.lists.TrackSelectionAdapter.ViewHolder}
 */
@SuppressWarnings("WeakerAccess")
public abstract class TrackSelectionAdapter<VH extends TrackSelectionAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected int selectedItem = -1;

    /**
     * This provides keyboard support.
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // Handle key up and key down and attempt to move selection
        recyclerView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

                // Return false if scrolled to the bounds and allow focus to move off the list
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return tryMoveSelection(lm, 1);
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        return tryMoveSelection(lm, -1);
                    }
                }

                return false;
            }
        });
    }

    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
        int nextSelectItem = selectedItem + direction;

        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (nextSelectItem >= 0 && nextSelectItem < getItemCount()) {
            changeSelectionAndNotify(nextSelectItem);
            lm.scrollToPosition(selectedItem);
            return true;
        }

        return false;
    }

    private void changeSelectionAndNotify(final int nextSelectedItem) {
        Timber.d("Previous selection: %d. Next selection: %d ", selectedItem, nextSelectedItem);

        notifyItemChanged(selectedItem);
        selectedItem = nextSelectedItem;
        notifyItemChanged(selectedItem);
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        // Set selected state; use a state list drawable to style the view
        viewHolder.itemView.setSelected(selectedItem == position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);

            // Handle item click and set the selection
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Redraw the old selection and the new
                    changeSelectionAndNotify(getAdapterPosition());
                }
            });
        }
    }
}