package mobile.smartteam.smartlight;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    protected void replaceFragment(Fragment fragment, String tag) {
        try {
            if (requireActivity().getSupportFragmentManager().findFragmentByTag(tag) != null) {
                return;
            }

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception ignore) {
        }
    }
}
