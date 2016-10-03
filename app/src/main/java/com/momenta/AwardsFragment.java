package com.momenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by Joe on 2016-02-01.
 * For Momenta
 */
public class AwardsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private RecyclerView mRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private String directory = "";

    private FirebaseRecyclerAdapter<Award, AwardsFragment.AwardViewHolder> mFirebaseAdapter;
    public static AwardsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        AwardsFragment fragment = new AwardsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            directory = mFirebaseUser.getUid() + "/awards";
        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = buildAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_awards, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        setLayoutManger();
        mRecyclerView.setAdapter(mFirebaseAdapter);

        return view;
    }

    /**
     * Helper method to build a recycler adapter
     * @return an adapter
     */
    private FirebaseRecyclerAdapter<Award, AwardsFragment.AwardViewHolder> buildAdapter() {
        return new FirebaseRecyclerAdapter<Award, AwardsFragment.AwardViewHolder>(
                Award.class,
                R.layout.award_card,
                AwardViewHolder.class,
                mFirebaseDatabaseReference.child(directory)) {

            @Override
            protected void populateViewHolder(AwardViewHolder viewHolder,
                                              Award award, int position) {

                viewHolder.name.setText(getStringResourceByName(award.getName()));
                if(award.getMaxLevel() > 1){
                    viewHolder.description.setText(getStringResourceByName(award.getDescription1())+" "+
                            award.getProgressLimitEachLevel().get(award.getCurrentLevel())+" "+
                            getStringResourceByName(award.getDescription2()));
                }else{
                    viewHolder.description.setText(getStringResourceByName(award.getDescription1())+" "+
                            getStringResourceByName(award.getDescription2()));
                }
                viewHolder.awardImage.setImageDrawable(getResources().getDrawable(R.mipmap.momenta_icon));

            }

            @Override
            public void onBindViewHolder(AwardViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);

                final Award award = getItem(position);
                //Set onClick listener for each award
                /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), TaskActivity.class);
                        intent.putExtra(Award.ID, award.getId());
                        getContext().startActivity(intent);
                    }
                });*/
            }

        };
    }

    /**
     * Used to order the layout of the recycler view.
     */
    private void setLayoutManger() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

    }

    /**
     * ViewHolder class for each item in the recycler view.
     */
    public static class AwardViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public ImageView awardImage;

        public AwardViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.award_title);
            description = (TextView) itemView.findViewById(R.id.award_description);
            awardImage = (ImageView) itemView.findViewById(R.id.award_image);
        }
    }
    private String getStringResourceByName(String aString) {
        int resId = getResources().getIdentifier(aString, "string", "com.momenta");
        if(resId!=0){
            return getString(resId);
        }else
            return "";
    }
}
