package com.bbinkconnect.bbinktattoo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bbinkconnect.bbinktattoo.CommentsActivity;
import com.bbinkconnect.bbinktattoo.FollowersActivity;
import com.bbinkconnect.bbinktattoo.R;
import com.bbinkconnect.bbinktattoo.fragments.PostDetailFragment;
import com.bbinkconnect.bbinktattoo.fragments.ProfileFragment;
import com.bbinkconnect.bbinktattoo.model.Post;
import com.bbinkconnect.bbinktattoo.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder> {

    private final Context mContext;
    private final List<Post> mPosts;
    private FirebaseUser firebaseUser;
    private static final int post = 0;
    private static final int werbung = 1;


    public PostAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || (position + 1) % 7 == 0) {
          return werbung;
        } else {
            return post;
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
        case werbung:
            // write ads code

        case post:
            View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
            return new ImageViewHolder(view);
        default:

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) { // Werbung
        int viewType  = getItemViewType(position);
        switch (viewType) {
            case werbung:
                // write ads code

            case post:
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // Post Image
                final Post post = mPosts.get(position);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

                Glide.with(mContext)
                        .load(post.getPostimage())
                        .apply(requestOptions)
                        .into(holder.post_image);

                if (post.getDescription().equals("")) {
                    holder.description.setVisibility(View.GONE);
                } else {
                    holder.description.setVisibility(View.VISIBLE);
                    holder.description.setText(post.getDescription());
                }

                publisherInfo(holder.image_profile, holder.username, holder.publisher, post.getPublisher());
                isLiked(post.getPostid(), holder.like);
                isSaved(post.getPostid(), holder.save);
                nrLikes(holder.likes, post.getPostid());
                getCommetns(post.getPostid(), holder.comments);

                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.like.getTag().equals("like")) {

                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                            addNotification(post.getPublisher(), post.getPostid());
                        } else {

                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                        }
                    }
                });

                holder.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.save.getTag().equals("save")) {
                            FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                        }
                    }
                });

                holder.image_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", post.getPublisher());
                        editor.apply();
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    }
                });

                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", post.getPublisher());
                        editor.apply();
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    }
                });

                holder.publisher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", post.getPublisher());
                        editor.apply();

                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    }
                });

                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, CommentsActivity.class);
                        intent.putExtra("postid", post.getPostid());
                        intent.putExtra("publisherid", post.getPublisher());
                        mContext.startActivity(intent);
                    }
                });

                holder.comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, CommentsActivity.class);
                        intent.putExtra("postid", post.getPostid());
                        intent.putExtra("publisherid", post.getPublisher());
                        mContext.startActivity(intent);
                    }
                });

                holder.post_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("postid", post.getPostid());
                        editor.apply();

                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
                    }
                });

                holder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, FollowersActivity.class);
                        intent.putExtra("id", post.getPostid());
                        intent.putExtra("title", "likes");
                        mContext.startActivity(intent);
                    }
                });

                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(mContext, view);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.edit:
                                        editPost(post.getPostid());
                                        return true;
                                    case R.id.delete:
                                        final String id = post.getPostid();

                                        FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    deleteNotifications(id, firebaseUser.getUid());
                                                }
                                            }
                                        });
                                        return true;
                                    case R.id.report:
                                        Toast.makeText(mContext, "Reported clicked!", Toast.LENGTH_SHORT).show(); // Übersetzen
                                    default:
                                        return false;
                                }
                            }
                        });
                        popupMenu.inflate(R.menu.post_menu);
                        if (!post.getPublisher().equals(firebaseUser.getUid())) {
                            popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.ad_info).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.report_ad).setVisible(false);
                        } else {
                            popupMenu.getMenu().findItem(R.id.report_ad).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.ad_info).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.edit).setVisible(true);
                            popupMenu.getMenu().findItem(R.id.delete).setVisible(true);
                            popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                        }
                        popupMenu.show();
                    }
                });
            default:
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        final ImageView image_profile;
        final ImageView post_image;
        final ImageView like;
        final ImageView comment;
        final ImageView save;
        final ImageView more;
        final TextView username;
        final TextView likes;
        final TextView publisher;
        final TextView description;
        final TextView comments;

        ImageViewHolder(View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            more = itemView.findViewById(R.id.more);
        }
    }

    private void addNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        reference.push().setValue(hashMap);
    }

    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if (Objects.requireNonNull(snapshot.child("postid").getValue()).equals(postid)){

                        snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show(); // Übersetzten
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(final TextView likes, String postId){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getCommetns(String postId, final TextView comments){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View All "+dataSnapshot.getChildrenCount()+" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        reference.keepSynced(true);
        //stürzt hier ab
        ValueEventListener listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                //stürzt hier ab
                Glide.with(mContext)
                        .load(Objects.requireNonNull(user).getImageurl())
                        .apply(requestOptions)
                        .into(image_profile);

                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void isLiked(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Objects.requireNonNull(firebaseUser).getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_heart_red);
                    imageView.setTag("liked");
                } else{
                    imageView.setImageResource(R.drawable.ic_heart);
                    imageView.setTag("like");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(Objects.requireNonNull(firebaseUser).getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_bookmark_orange);
                    imageView.setTag("saved");
                } else{
                    imageView.setImageResource(R.drawable.ic_bookmark);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post"); 									// Übersetzten
        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() { // Übersetzten
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("description", editText.getText().toString());
                FirebaseDatabase.getInstance().getReference("Posts").child(postid).updateChildren(hashMap);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // Übersetzten
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    private void getText(String postid, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        //  reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(Objects.requireNonNull(dataSnapshot.getValue(Post.class)).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}