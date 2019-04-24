package amit.apps.aurora_raw3.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import amit.apps.aurora_raw3.R;
import amit.apps.aurora_raw3.VideolyricsActivity;
import amit.apps.aurora_raw3.datainitializers.PlaylistsHolder;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> {
    private VideosAdapter.VideosPojo[] itemsvideopojo;
    private String playlisttitle;

    public ItemsAdapter(String ptitle) {
        itemsvideopojo= PlaylistsHolder.getPojosfromtitle(ptitle);
        playlisttitle=ptitle;

    }

    @Override
    public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.itemscard, parent, false);
        return new ItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemsViewHolder holder, int position) {
        holder.setSerialnum((position+1)+"");
        holder.setVideotitle(itemsvideopojo[position].getTitle());
        holder.setVideoduration("Duration · " + itemsvideopojo[position].getDuration());

        holder.removevideobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("remove button clicked :", "tryna remove item at position = "+holder.getAdapterPosition());
                PlaylistsHolder.removefromplaylist(playlisttitle, itemsvideopojo[holder.getAdapterPosition()].getTitle());
                itemsvideopojo=PlaylistsHolder.getPojosfromtitle(playlisttitle);

                StringBuilder allvideos= new StringBuilder();
                for(VideosAdapter.VideosPojo x: itemsvideopojo) {
                    allvideos.append(x.getTitle()).append("\n");
                }
                Log.e("new list :", allvideos.toString());

                notifyDataSetChanged();
            }
        });

        View.OnClickListener itemClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.videotitle.getContext(), VideolyricsActivity.class);
                VideolyricsActivity.itemsvideopojo=itemsvideopojo;
                VideolyricsActivity.currentpojo=holder.getAdapterPosition();
                VideolyricsActivity.createPlaylist();
                holder.videotitle.getContext().startActivity(intent);
            }
        };
        holder.serialnum.setOnClickListener(itemClickListener);
        holder.videotitle.setOnClickListener(itemClickListener);
        holder.videoduration.setOnClickListener(itemClickListener);
    }

    @Override
    public int getItemCount() {
        return itemsvideopojo.length;
    }

    static class ItemsViewHolder extends RecyclerView.ViewHolder {
        TextView serialnum, videotitle, videoduration;
        ImageButton removevideobutton;

        ItemsViewHolder(View itemView) {
            super(itemView);

            serialnum=itemView.findViewById(R.id.serialnum);
            videotitle=itemView.findViewById(R.id.videotitle);
            videoduration=itemView.findViewById(R.id.videoduration);
            removevideobutton=itemView.findViewById(R.id.removevideobutton);
        }

        void setSerialnum(String num) {
            serialnum.setText(num);
        }

        void setVideotitle(String title) {
            videotitle.setText(title);
        }

        void setVideoduration(String duration) {
            videoduration.setText(duration);
        }
    }
}
