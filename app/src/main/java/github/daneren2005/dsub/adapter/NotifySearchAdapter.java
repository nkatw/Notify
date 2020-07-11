package github.daneren2005.dsub.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import github.daneren2005.dsub.R;

public class NotifySearchAdapter extends RecyclerView.Adapter<NotifySearchAdapter.NotifySearchViewHolder> {
    private static final String TAG = NotifySearchAdapter.class.getSimpleName();

    @NonNull
    @Override
    public NotifySearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.search_item_view, parent, false);
        return new NotifySearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifySearchViewHolder holder, int position) {
        // Override it on instantiation.
    }

    @Override
    public int getItemCount() {
        // Override it on instantiation.
        return 0;
    }


    protected class NotifySearchViewHolder extends RecyclerView.ViewHolder {
        // TODO: Find better way to encapsulate members
        public ViewGroup layout;
        public TextView title, subtitle;

        NotifySearchViewHolder(View itemView) {
            super(itemView);
            NotifySearchViewHolder.this.layout = itemView.findViewById(R.id.search_item_layout);
            NotifySearchViewHolder.this.title = itemView.findViewById(R.id.search_item_title);
            NotifySearchViewHolder.this.subtitle = itemView.findViewById(R.id.search_item_subtitle);
        }
    }
}
