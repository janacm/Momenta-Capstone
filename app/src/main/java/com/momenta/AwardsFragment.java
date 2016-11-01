package com.momenta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


/**
 * Created by Joe on 2016-02-01.
 * For Momenta
 */
public class AwardsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private RecyclerView mRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private String directory = "";
    private helperPreferences helperPreferences;
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
        mFirebaseDatabaseReference = FirebaseProvider.getInstance().getReference();
        mFirebaseAdapter = buildAdapter();
        helperPreferences = new helperPreferences(getContext());
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
                //Detremines the text for description of the award depending on how long it is(1 vs 2 parts desc)
                if (award.getMaxLevel() > 1) {
                    viewHolder.description.setText(getStringResourceByName(award.getDescription_1()) + " " +
                            award.getProgressLimitEachLevel().get(award.getCurrentLevel()) + " " +
                            getStringResourceByName(award.getDescription_2()));
                } else {
                    viewHolder.description.setText(getStringResourceByName(award.getDescription_1()) + " " +
                            getStringResourceByName(award.getDescription_2()));
                }
                viewHolder.awardImage.setImageDrawable(getResources().getDrawable(R.mipmap.momenta_icon));

                viewHolder.progressText.setText(award.getCurrentLevel() + 1 + "/" + award.getMaxLevel());

                //Draw progress bar's progress if award is not yet completed
                if (award.getCurrentLevel() != award.getMaxLevel()) {
                    viewHolder.progressBar.setMax(award.getProgressLimitEachLevel().get(award.getCurrentLevel()));
                    viewHolder.progressBar.setProgress((float) award.getCurrentProgress());
                    //string formatting for the hours
                    if (!award.getId().equals(helperPreferences.getPreferences(Constants.SHPREF_TREND_SETTER_AWARD_ID, "")) && !award.getId().equals(helperPreferences.getPreferences(Constants.SHPREF_PERFECTIONIST_AWARD_ID, ""))) {
                        viewHolder.progressBar.setProgressText(String.valueOf((int) award.getCurrentProgress()));
                    }else{
                        viewHolder.progressBar.setProgressText(String.format("%.2f",award.getCurrentProgress()));
                    }
                }
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
        public TextView progressText;
        public TextRoundCornerProgressBar progressBar;

        public AwardViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.award_title);
            description = (TextView) itemView.findViewById(R.id.award_description);
            awardImage = (ImageView) itemView.findViewById(R.id.award_image);
            progressText = (TextView) itemView.findViewById(R.id.award_level_progress);
            progressBar = (TextRoundCornerProgressBar) itemView.findViewById(R.id.award_progress_bar);

        }
    }

    private String getStringResourceByName(String aString) {
        if (aString != null) {
            int resId = getResources().getIdentifier(aString, "string", "com.momenta");
            if (resId != 0) {
                return getString(resId);
            } else
                return "";
        }
        return "";
    }
}
