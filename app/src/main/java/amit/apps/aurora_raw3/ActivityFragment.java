package amit.apps.aurora_raw3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ActivityFragment extends Fragment {
    public static int tabtoload=0;
    private FragmentTabHost tabHost;

    public ActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_activity, container, false);
        MainActivity.fragmenttoload=0;

        android.support.v7.app.ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Activity");
        }

        addTabs(layout);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        tabHost.setCurrentTab(tabtoload);
    }

    private void addTabs(View layout) {
        tabHost = layout.findViewById(R.id.tabhost_activity);
        tabHost.setup(getContext(), getChildFragmentManager(), R.id.container_activity);

        tabHost.addTab(tabHost.newTabSpec("facebook").setIndicator("Facebook"),
                ActivityFacebookFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("instagram").setIndicator("Instagram"),
                ActivityInstagramFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("twitter").setIndicator("Twitter"),
                ActivityTwitterFragment.class, null);
    }
}
