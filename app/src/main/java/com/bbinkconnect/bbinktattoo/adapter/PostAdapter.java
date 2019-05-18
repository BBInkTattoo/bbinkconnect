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

    public PostAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        if (position == 1 || (position + 2) % 7 == 1) {

// Werbung
            final Post post = mPosts.get(position);

            Glide.with(mContext).load("https://www.lenaheel.de/wp-content/uploads/2014/12/CSLH_Spruch1-1.jpg")
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(holder.post_image);

            holder.like.setVisibility(View.GONE);
            holder.save.setVisibility(View.GONE);
            holder.likes.setVisibility(View.GONE);
            holder.comment.setVisibility(View.GONE);
            holder.comments.setVisibility(View.GONE);
            holder.description.setText("Hier könnte ihre Werbung stehen.");
            holder.username.setText("BB Ink Connect");
            holder.publisher.setText("BB Ink Connect");
            Glide.with(mContext).load("data:image/jpeg;base64,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")
                    .into(holder.image_profile);

            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(mContext, view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.ad_info:
                                    Toast.makeText(mContext, "Info clicked!", Toast.LENGTH_SHORT).show();
                                    return true;

                                case R.id.report_ad:
                                    Toast.makeText(mContext, "Reported clicked!", Toast.LENGTH_SHORT).show();

                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.inflate(R.menu.post_menu);
                    if (!post.getPublisher().equals(firebaseUser.getUid())){
                        popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.ad_info).setVisible(true);
                        popupMenu.getMenu().findItem(R.id.report_ad).setVisible(true);
                    }
                    popupMenu.show();
                }
            });


        } else {

// Post Image
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final Post post = mPosts.get(position);

            Glide.with(mContext).load(post.getPostimage())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(holder.post_image);

            if (post.getDescription().equals("")){
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
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).setValue(true);
                        addNotification(post.getPublisher(), post.getPostid());
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).removeValue();
                    }
                }
            });

            holder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.save.getTag().equals("save")){
                        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                                .child(post.getPostid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                                .child(post.getPostid()).removeValue();
                    }
                }
            });

            holder.image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", post.getPublisher());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                }
            });

            holder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", post.getPublisher());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                }
            });

            holder.publisher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", post.getPublisher());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
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

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PostDetailFragment()).commit();
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
                            switch (menuItem.getItemId()){
                                case R.id.edit:
                                    editPost(post.getPostid());
                                    return true;
                                case R.id.delete:
                                    final String id = post.getPostid();
                                    FirebaseDatabase.getInstance().getReference("Posts")
                                            .child(post.getPostid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        deleteNotifications(id, firebaseUser.getUid());
                                                    }
                                                }
                                            });
                                    return true;
                                case R.id.report:
                                    Toast.makeText(mContext, "Reported clicked!", Toast.LENGTH_SHORT).show();

                                    final String id_2 = post.getPostid();
                                    FirebaseDatabase.getInstance().getReference("Posts")
                                            .child(post.getPostid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        deleteNotifications(id_2, firebaseUser.getUid());
                                                    }
                                                }
                                            });
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
                    }else{
                        popupMenu.getMenu().findItem(R.id.report_ad).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.ad_info).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.edit).setVisible(true);
                        popupMenu.getMenu().findItem(R.id.delete).setVisible(true);
                        popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                    }
                    popupMenu.show();
                }
            });
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
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (Objects.requireNonNull(snapshot.child("postid").getValue()).equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(final TextView likes, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCommetns(String postId, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.setText("View All "+dataSnapshot.getChildrenCount()+" Comments");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(Objects.requireNonNull(user).getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Objects.requireNonNull(firebaseUser).getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_heart_red);
                    imageView.setTag("liked");
                } else{
                    imageView.setImageResource(R.drawable.ic_heart);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Saves").child(Objects.requireNonNull(firebaseUser).getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_bookmark);
                    imageView.setTag("saved");
                } else{
                    // TODO: 18.05.2019 Ic bookmark black
                    imageView.setImageResource(R.drawable.ic_bookmark);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description", editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postid).updateChildren(hashMap);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialog.show();
    }

    private void getText(String postid, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editText.setText(Objects.requireNonNull(dataSnapshot.getValue(Post.class)).getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}